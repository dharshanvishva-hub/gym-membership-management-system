"""
Flask Web Application for Scenario 27:
Gym Membership Management and Fitness Activity Analysis System

Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A
Velalar College of Engineering and Technology, Erode - 12
"""

from flask import Flask, render_template, request, jsonify, redirect, url_for, flash, send_from_directory
import sqlite3
import os
from datetime import datetime, date

app = Flask(__name__)
app.secret_key = "fitzone-secret-key-2026"

BASE_DIR = os.path.abspath(os.path.join(os.path.dirname(__file__), ".."))
DB_PATH = os.path.join(BASE_DIR, "database", "gym.db")
VIZ_DIR = os.path.join(BASE_DIR, "visualizations")

def get_db():
    conn = sqlite3.connect(DB_PATH)
    conn.row_factory = sqlite3.Row
    # Enable foreign keys
    conn.execute("PRAGMA foreign_keys = ON;")
    return conn

@app.route("/visualizations/<path:filename>")
def serve_visualization(filename):
    return send_from_directory(VIZ_DIR, filename)

# -------------------------------------------------------------
# 1. Dashboard Route
# -------------------------------------------------------------
@app.route("/")
def dashboard():
    conn = get_db()
    c = conn.cursor()

    # KPI counts
    total_members = c.execute("SELECT COUNT(*) FROM members").fetchone()[0]
    total_plans = c.execute("SELECT COUNT(*) FROM membership_plans").fetchone()[0]
    total_trainers = c.execute("SELECT COUNT(*) FROM trainers").fetchone()[0]
    total_revenue = c.execute("SELECT COALESCE(SUM(amount_paid), 0) FROM member_subscriptions").fetchone()[0]
    
    # Active vs Expired vs Expiring Soon
    expiring_count = c.execute("""
        SELECT COUNT(*) FROM member_subscriptions 
        WHERE date(expiry_date) BETWEEN date('now') AND date('now', '+7 days')
    """).fetchone()[0]

    # Recent attendance
    recent_attendance = c.execute("""
        SELECT a.*, m.name as member_name 
        FROM attendance a
        JOIN members m ON a.member_id = m.member_id
        ORDER BY a.attendance_date DESC, a.check_in_time DESC
        LIMIT 6
    """).fetchall()

    # Active trainer workloads
    trainer_workload = c.execute("SELECT * FROM vw_trainer_workload").fetchall()

    conn.close()

    return render_template(
        "index.html",
        total_members=total_members,
        total_plans=total_plans,
        total_trainers=total_trainers,
        total_revenue=total_revenue,
        expiring_count=expiring_count,
        recent_attendance=recent_attendance,
        trainer_workload=trainer_workload
    )

# -------------------------------------------------------------
# 2. Members Management
# -------------------------------------------------------------
@app.route("/members", methods=["GET", "POST"])
def members():
    conn = get_db()
    c = conn.cursor()

    if request.method == "POST":
        name = request.form.get("name", "").strip()
        age = request.form.get("age", type=int)
        gender = request.form.get("gender", "Male")
        contact = request.form.get("contact", "").strip()
        email = request.form.get("email", "").strip()

        # Validations
        if not name or len(name) < 2:
            flash("Member name must be at least 2 characters.", "danger")
        elif not age or age < 14 or age > 90:
            flash("Member age must be between 14 and 90.", "danger")
        elif not contact or len(contact) != 10 or not contact.isdigit():
            flash("Contact number must be exactly 10 digits.", "danger")
        else:
            try:
                c.execute("""
                    INSERT INTO members (name, age, gender, contact_number, email, registration_date, status)
                    VALUES (?, ?, ?, ?, ?, date('now'), 'Active')
                """, (name, age, gender, contact, email))
                conn.commit()
                flash(f"Member '{name}' registered successfully!", "success")
            except sqlite3.IntegrityError as e:
                flash("Contact number or Email already exists in the database.", "warning")
            except Exception as e:
                flash(f"Error registering member: {str(e)}", "danger")

        return redirect(url_for("members"))

    search_query = request.args.get("search", "").strip()
    if search_query:
        members_list = c.execute("""
            SELECT * FROM members 
            WHERE name LIKE ? OR contact_number LIKE ? 
            ORDER BY member_id DESC
        """, (f"%{search_query}%", f"%{search_query}%")).fetchall()
    else:
        members_list = c.execute("SELECT * FROM members ORDER BY member_id DESC").fetchall()

    conn.close()
    return render_template("members.html", members=members_list, search_query=search_query)

# -------------------------------------------------------------
# 3. Plans & Subscriptions
# -------------------------------------------------------------
@app.route("/plans", methods=["GET", "POST"])
def plans():
    conn = get_db()
    c = conn.cursor()

    if request.method == "POST":
        action = request.form.get("action")
        
        if action == "subscribe":
            member_id = request.form.get("member_id", type=int)
            plan_id = request.form.get("plan_id", type=int)

            plan = c.execute("SELECT * FROM membership_plans WHERE plan_id = ?", (plan_id,)).fetchone()
            if plan and member_id:
                duration = plan["duration_months"]
                fee = plan["fee"]
                start_date = date.today()
                
                # Expiry date calculation
                # SQLite date calculation helper
                c.execute("""
                    INSERT INTO member_subscriptions (member_id, plan_id, start_date, expiry_date, amount_paid, payment_status)
                    VALUES (?, ?, date('now'), date('now', '+' || ? || ' months'), ?, 'Paid')
                """, (member_id, plan_id, duration, fee))
                conn.commit()
                flash("Subscription successfully registered!", "success")
            else:
                flash("Invalid Member or Plan selected.", "danger")

        elif action == "renew":
            sub_id = request.form.get("subscription_id", type=int)
            plan_id = request.form.get("plan_id", type=int)

            sub = c.execute("SELECT * FROM member_subscriptions WHERE subscription_id = ?", (sub_id,)).fetchone()
            plan = c.execute("SELECT * FROM membership_plans WHERE plan_id = ?", (plan_id,)).fetchone()

            if sub and plan:
                duration = plan["duration_months"]
                fee = plan["fee"]
                # extend from current expiry date or today if expired
                c.execute("""
                    INSERT INTO member_subscriptions (member_id, plan_id, start_date, expiry_date, amount_paid, payment_status)
                    VALUES (?, ?, date('now'), date('now', '+' || ? || ' months'), ?, 'Paid')
                """, (sub["member_id"], plan_id, duration, fee))
                conn.commit()
                flash("Membership renewed successfully!", "success")

        return redirect(url_for("plans"))

    plans_list = c.execute("SELECT * FROM membership_plans ORDER BY plan_id ASC").fetchall()
    subs_list = c.execute("SELECT * FROM vw_active_memberships ORDER BY subscription_id DESC").fetchall()
    members_list = c.execute("SELECT member_id, name FROM members WHERE status = 'Active' ORDER BY name ASC").fetchall()

    conn.close()
    return render_template("plans.html", plans=plans_list, subscriptions=subs_list, members=members_list)

# -------------------------------------------------------------
# 4. Attendance Desk
# -------------------------------------------------------------
@app.route("/attendance", methods=["GET", "POST"])
def attendance():
    conn = get_db()
    c = conn.cursor()

    if request.method == "POST":
        action = request.form.get("action")

        if action == "checkin":
            member_id = request.form.get("member_id", type=int)
            workout_type = request.form.get("workout_type", "General Workout")

            # Check if open check-in exists today
            open_checkin = c.execute("""
                SELECT * FROM attendance 
                WHERE member_id = ? AND attendance_date = date('now') AND check_out_time IS NULL
            """, (member_id,)).fetchone()

            if open_checkin:
                flash("This member already has an active check-in today!", "warning")
            else:
                c.execute("""
                    INSERT INTO attendance (member_id, attendance_date, check_in_time, workout_type)
                    VALUES (?, date('now'), time('now', 'localtime'), ?)
                """, (member_id, workout_type))
                conn.commit()
                flash("Check-in logged successfully!", "success")

        elif action == "checkout":
            attendance_id = request.form.get("attendance_id", type=int)
            c.execute("""
                UPDATE attendance 
                SET check_out_time = time('now', 'localtime') 
                WHERE attendance_id = ?
            """, (attendance_id,))
            conn.commit()
            flash("Check-out recorded!", "success")

        return redirect(url_for("attendance"))

    # Fetch recent attendance
    attendance_records = c.execute("""
        SELECT a.*, m.name AS member_name 
        FROM attendance a
        JOIN members m ON a.member_id = m.member_id
        ORDER BY a.attendance_date DESC, a.check_in_time DESC
        LIMIT 50
    """).fetchall()

    active_members = c.execute("SELECT member_id, name FROM members WHERE status = 'Active' ORDER BY name ASC").fetchall()

    conn.close()
    return render_template("attendance.html", records=attendance_records, members=active_members)

# -------------------------------------------------------------
# 5. Trainers & Assignments
# -------------------------------------------------------------
@app.route("/trainers", methods=["GET", "POST"])
def trainers():
    conn = get_db()
    c = conn.cursor()

    if request.method == "POST":
        member_id = request.form.get("member_id", type=int)
        trainer_id = request.form.get("trainer_id", type=int)
        goal = request.form.get("fitness_goal", "").strip()

        if member_id and trainer_id and goal:
            c.execute("""
                INSERT INTO trainer_assignments (member_id, trainer_id, assigned_date, fitness_goal, status)
                VALUES (?, ?, date('now'), ?, 'Active')
            """, (member_id, trainer_id, goal))
            conn.commit()
            flash("Trainer assigned to member successfully!", "success")
        else:
            flash("Please fill all required assignment fields.", "danger")

        return redirect(url_for("trainers"))

    trainers_list = c.execute("SELECT * FROM trainers ORDER BY trainer_id ASC").fetchall()
    assignments = c.execute("""
        SELECT ta.*, m.name as member_name, t.name as trainer_name, t.specialization
        FROM trainer_assignments ta
        JOIN members m ON ta.member_id = m.member_id
        JOIN trainers t ON ta.trainer_id = t.trainer_id
        ORDER BY ta.assignment_id DESC
    """).fetchall()

    active_members = c.execute("SELECT member_id, name FROM members WHERE status = 'Active' ORDER BY name ASC").fetchall()

    conn.close()
    return render_template("trainers.html", trainers=trainers_list, assignments=assignments, members=active_members)

# -------------------------------------------------------------
# 6. Analytics & Reports
# -------------------------------------------------------------
@app.route("/analytics")
def analytics():
    conn = get_db()
    c = conn.cursor()

    revenue_by_plan = c.execute("SELECT * FROM vw_revenue_by_plan").fetchall()
    trainer_workload = c.execute("SELECT * FROM vw_trainer_workload").fetchall()
    expiring_soon = c.execute("SELECT * FROM vw_expiring_soon").fetchall()

    conn.close()
    return render_template(
        "analytics.html",
        revenue_by_plan=revenue_by_plan,
        trainer_workload=trainer_workload,
        expiring_soon=expiring_soon
    )

# -------------------------------------------------------------
# API Route for Chart.js in Dashboard
# -------------------------------------------------------------
@app.route("/api/chart-data")
def chart_data():
    conn = get_db()
    c = conn.cursor()

    # Workout distribution
    workouts = c.execute("SELECT workout_type, COUNT(*) as count FROM attendance GROUP BY workout_type").fetchall()
    workout_labels = [w["workout_type"] for w in workouts]
    workout_counts = [w["count"] for w in workouts]

    # Revenue by plan
    rev_plans = c.execute("SELECT plan_name, total_revenue, total_subscriptions FROM vw_revenue_by_plan").fetchall()
    plan_names = [p["plan_name"] for p in rev_plans]
    plan_revenues = [p["total_revenue"] for p in rev_plans]
    plan_subs = [p["total_subscriptions"] for p in rev_plans]

    conn.close()
    return jsonify({
        "workout_labels": workout_labels,
        "workout_counts": workout_counts,
        "plan_names": plan_names,
        "plan_revenues": plan_revenues,
        "plan_subs": plan_subs
    })

if __name__ == "__main__":
    print("=" * 70)
    print("Starting Gym Management Web Application...")
    print("Scenario 27: Gym Membership Management & Fitness Activity Analysis System")
    print("Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A")
    print("Access URL: http://127.0.0.1:5000")
    print("=" * 70)
    app.run(debug=True, port=5000)
