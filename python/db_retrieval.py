"""
Scenario 27: Gym Membership Management and Fitness Activity Analysis System
Database Retrieval Module (Foundations of Data Science - 25ITC31)
Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A
Velalar College of Engineering and Technology, Erode - 12
"""

import sqlite3
import pandas as pd
import os

DB_PATH = os.path.join(os.path.dirname(__file__), "..", "database", "gym.db")

def get_connection():
    """Establish and return SQLite connection."""
    if not os.path.exists(DB_PATH):
        raise FileNotFoundError(f"Database file not found at: {DB_PATH}")
    return sqlite3.connect(DB_PATH)

def fetch_table(table_name):
    """Retrieve an entire table into a pandas DataFrame."""
    conn = get_connection()
    query = f"SELECT * FROM {table_name}"
    df = pd.read_sql_query(query, conn)
    conn.close()
    return df

def fetch_full_gym_dataset():
    """
    Retrieve all core tables and return as a dictionary of DataFrames.
    """
    tables = [
        "members",
        "membership_plans",
        "trainers",
        "member_subscriptions",
        "trainer_assignments",
        "attendance"
    ]
    data = {}
    conn = get_connection()
    for table in tables:
        df = pd.read_sql_query(f"SELECT * FROM {table}", conn)
        data[table] = df
        print(f"[DB Retrieval] Loaded '{table}': {df.shape[0]} rows, {df.shape[1]} columns")
    conn.close()
    return data

def fetch_joined_activity_data():
    """
    Execute comprehensive SQL join query for attendance, member profile, and plan.
    """
    conn = get_connection()
    sql = """
    SELECT 
        a.attendance_id,
        a.attendance_date,
        a.check_in_time,
        a.check_out_time,
        a.workout_type,
        m.member_id,
        m.name AS member_name,
        m.age,
        m.gender,
        p.plan_name,
        p.duration_months,
        p.fee,
        s.payment_status
    FROM attendance a
    JOIN members m ON a.member_id = m.member_id
    LEFT JOIN member_subscriptions s ON m.member_id = s.member_id
    LEFT JOIN membership_plans p ON s.plan_id = p.plan_id
    ORDER BY a.attendance_date ASC, a.check_in_time ASC
    """
    df = pd.read_sql_query(sql, conn)
    conn.close()
    return df

if __name__ == "__main__":
    print("Testing Database Retrieval...")
    dataset = fetch_full_gym_dataset()
    print("\nSample Members Data:")
    print(dataset["members"].head(3))
    print("\nSample Attendance Data:")
    print(dataset["attendance"].head(3))
