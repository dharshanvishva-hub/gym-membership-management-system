"""
Scenario 27: Gym Membership Management and Fitness Activity Analysis System
Data Visualization & Exploratory Data Analysis (Foundations of Data Science - 25ITC31)
Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A
Velalar College of Engineering and Technology, Erode - 12
"""

import os
import matplotlib.pyplot as plt
import seaborn as sns
import pandas as pd
import numpy as np

from data_preprocessing import generate_preprocessed_datasets

# Setup visualization directory
OUTPUT_DIR = os.path.join(os.path.dirname(__file__), "..", "visualizations")
os.makedirs(OUTPUT_DIR, exist_ok=True)

# Styling configuration
sns.set_theme(style="whitegrid", palette="muted")
plt.rcParams['font.sans-serif'] = 'DejaVu Sans'
plt.rcParams['font.size'] = 10

def plot_attendance_trends(attendance_df):
    """
    Visualization 1: Attendance Trends across Days of the Week by Time Slot.
    """
    plt.figure(figsize=(10, 5))
    day_order = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
    
    # Filter to existing days and reindex
    counts = attendance_df.groupby(['day_of_week', 'time_slot']).size().reset_index(name='visit_count')
    
    ax = sns.barplot(
        data=counts,
        x='day_of_week',
        y='visit_count',
        hue='time_slot',
        order=[d for d in day_order if d in counts['day_of_week'].unique()],
        palette={'Morning Shift (6AM-11AM)': '#2980b9', 'Evening Shift (4PM-9PM)': '#e67e22'}
    )
    plt.title("Gym Attendance Frequency by Day of Week & Shift", fontsize=13, fontweight='bold', pad=12)
    plt.xlabel("Day of the Week", fontweight='bold')
    plt.ylabel("Total Member Visits", fontweight='bold')
    plt.legend(title="Shift Time")
    plt.tight_layout()
    
    out_path = os.path.join(OUTPUT_DIR, "01_attendance_by_day_of_week.png")
    plt.savefig(out_path, dpi=300)
    plt.close()
    print(f"[Chart Saved] -> {out_path}")

def plot_plan_popularity_and_revenue(plans_df, subs_df):
    """
    Visualization 2: Membership Plan Popularity vs Revenue Generated.
    """
    merged = subs_df.merge(plans_df, on='plan_id')
    stats = merged.groupby('plan_name').agg(
        subscribers=('subscription_id', 'count'),
        total_revenue=('amount_paid', 'sum')
    ).reset_index().sort_values(by='total_revenue', ascending=False)

    fig, ax1 = plt.subplots(figsize=(11, 5))

    color1 = '#3498db'
    color2 = '#2ecc71'

    x = np.arange(len(stats['plan_name']))
    width = 0.35

    bars1 = ax1.bar(x - width/2, stats['subscribers'], width, label='Total Subscribers', color=color1)
    ax1.set_ylabel('Number of Subscribers', color=color1, fontweight='bold')
    ax1.set_xticks(x)
    ax1.set_xticklabels(stats['plan_name'], rotation=15, ha='right')
    ax1.tick_params(axis='y', labelcolor=color1)

    ax2 = ax1.twinx()
    bars2 = ax2.bar(x + width/2, stats['total_revenue'], width, label='Revenue (₹)', color=color2)
    ax2.set_ylabel('Total Revenue (₹)', color=color2, fontweight='bold')
    ax2.tick_params(axis='y', labelcolor=color2)
    ax2.yaxis.set_major_formatter('₹{x:,.0f}')

    plt.title("Membership Plan Popularity vs. Revenue Generation", fontsize=13, fontweight='bold', pad=12)
    fig.tight_layout()

    out_path = os.path.join(OUTPUT_DIR, "02_plan_popularity_and_revenue.png")
    plt.savefig(out_path, dpi=300)
    plt.close()
    print(f"[Chart Saved] -> {out_path}")

def plot_trainer_workload(trainers_df, assignments_df):
    """
    Visualization 3: Active Client Workload per Trainer.
    """
    merged = trainers_df.merge(assignments_df[assignments_df['status'] == 'Active'], on='trainer_id', how='left')
    workload = merged.groupby(['name', 'specialization']).size().reset_index(name='active_clients')
    workload['label'] = workload['name'] + "\n(" + workload['specialization'] + ")"
    workload = workload.sort_values(by='active_clients', ascending=True)

    plt.figure(figsize=(10, 5))
    bars = plt.barh(workload['label'], workload['active_clients'], color='#9b59b6', edgecolor='black', alpha=0.85)
    plt.xlabel("Active Assigned Members", fontweight='bold')
    plt.title("Trainer Workload Distribution (Active Client Assignments)", fontsize=13, fontweight='bold', pad=12)

    for bar in bars:
        w = bar.get_width()
        plt.text(w + 0.05, bar.get_y() + bar.get_height()/2, f"{int(w)} clients", va='center', fontweight='bold')

    plt.tight_layout()
    out_path = os.path.join(OUTPUT_DIR, "03_trainer_workload_distribution.png")
    plt.savefig(out_path, dpi=300)
    plt.close()
    print(f"[Chart Saved] -> {out_path}")

def plot_workout_type_preference(attendance_df):
    """
    Visualization 4: Donut Chart of Workout Category Preferences.
    """
    plt.figure(figsize=(8, 6))
    counts = attendance_df['workout_type'].value_counts()
    colors = sns.color_palette("Set2", len(counts))

    plt.pie(
        counts,
        labels=counts.index,
        autopct='%1.1f%%',
        startangle=140,
        colors=colors,
        wedgeprops=dict(width=0.4, edgecolor='white', linewidth=2)
    )
    plt.title("Member Workout Category Preference Distribution", fontsize=13, fontweight='bold', pad=12)
    plt.tight_layout()

    out_path = os.path.join(OUTPUT_DIR, "04_workout_type_preference_pie.png")
    plt.savefig(out_path, dpi=300)
    plt.close()
    print(f"[Chart Saved] -> {out_path}")

def plot_hourly_traffic_heatmap(attendance_df):
    """
    Visualization 5: Heatmap of Gym Traffic by Hour and Day of Week.
    """
    pivot = pd.crosstab(
        attendance_df['day_of_week'],
        attendance_df['check_in_hour']
    )
    day_order = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
    pivot = pivot.reindex([d for d in day_order if d in pivot.index])

    plt.figure(figsize=(10, 5))
    sns.heatmap(pivot, annot=True, fmt='d', cmap='YlGnBu', cbar_kws={'label': 'Visitor Count'}, linewidths=0.5)
    plt.title("Peak Gym Activity Heatmap: Hour of Day vs. Weekday", fontsize=13, fontweight='bold', pad=12)
    plt.xlabel("Hour of Day (24-hr clock)", fontweight='bold')
    plt.ylabel("Day of the Week", fontweight='bold')
    plt.tight_layout()

    out_path = os.path.join(OUTPUT_DIR, "05_hourly_checkin_heatmap.png")
    plt.savefig(out_path, dpi=300)
    plt.close()
    print(f"[Chart Saved] -> {out_path}")

def plot_member_demographics(members_df):
    """
    Visualization 6: Member Age Demographics by Gender (Box Plot & Distribution).
    """
    fig, axes = plt.subplots(1, 2, figsize=(12, 5))

    # Boxplot
    sns.boxplot(data=members_df, x='gender', y='age', palette={'Male': '#3498db', 'Female': '#e74c3c'}, ax=axes[0])
    axes[0].set_title("Member Age Distribution by Gender", fontweight='bold')
    axes[0].set_xlabel("Gender", fontweight='bold')
    axes[0].set_ylabel("Age (Years)", fontweight='bold')

    # Count by Age Group
    sns.countplot(data=members_df, x='age_group', palette='Blues_r', ax=axes[1])
    axes[1].set_title("Member Count by Age Category", fontweight='bold')
    axes[1].set_xlabel("Age Category", fontweight='bold')
    axes[1].set_ylabel("Number of Members", fontweight='bold')
    axes[1].tick_params(axis='x', rotation=15)

    plt.tight_layout()
    out_path = os.path.join(OUTPUT_DIR, "06_member_age_distribution_boxplot.png")
    plt.savefig(out_path, dpi=300)
    plt.close()
    print(f"[Chart Saved] -> {out_path}")

def generate_all_visualizations():
    print("=" * 60)
    print("Generating Visualizations for Gym Activity Analysis...")
    print("=" * 60)
    data = generate_preprocessed_datasets()
    
    plot_attendance_trends(data['attendance'])
    plot_plan_popularity_and_revenue(data['plans'], data['subscriptions'])
    plot_trainer_workload(data['trainers'], data['trainer_assignments'])
    plot_workout_type_preference(data['attendance'])
    plot_hourly_traffic_heatmap(data['attendance'])
    plot_member_demographics(data['members'])
    
    print("\n[SUCCESS] All 6 visualizations created successfully in 'visualizations/' folder.")

if __name__ == "__main__":
    generate_all_visualizations()
