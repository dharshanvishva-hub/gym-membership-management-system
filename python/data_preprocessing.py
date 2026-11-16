"""
Scenario 27: Gym Membership Management and Fitness Activity Analysis System
Data Preprocessing & Transformation Module (Foundations of Data Science - 25ITC31)
Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A
Velalar College of Engineering and Technology, Erode - 12
"""

import pandas as pd
import numpy as np
from db_retrieval import fetch_full_gym_dataset, fetch_joined_activity_data

def preprocess_attendance_data(df):
    """
    Cleans and enriches attendance records.
    - Converts date/time strings to datetime objects
    - Computes workout duration in minutes
    - Extracts day_of_week, hour_of_day, and time_slot (Morning/Evening)
    """
    df = df.copy()

    # 1. Check for duplicates
    initial_count = len(df)
    df = df.drop_duplicates()
    duplicates_removed = initial_count - len(df)
    if duplicates_removed > 0:
        print(f"[Preprocessing] Removed {duplicates_removed} duplicate attendance rows.")

    # 2. Date and Time parsing
    df['attendance_date'] = pd.to_datetime(df['attendance_date'])
    df['day_of_week'] = df['attendance_date'].dt.day_name()
    df['is_weekend'] = df['day_of_week'].isin(['Saturday', 'Sunday'])

    # Parse check-in and check-out to calculate duration
    check_in_dt = pd.to_datetime(df['attendance_date'].dt.strftime('%Y-%m-%d') + ' ' + df['check_in_time'])
    
    # Handle possible missing check_out_time by forward filling or default 60 min session
    missing_checkouts = df['check_out_time'].isna().sum()
    if missing_checkouts > 0:
        print(f"[Preprocessing] Imputing {missing_checkouts} missing check-outs with estimated 60-min session.")
        check_out_dt = check_in_dt + pd.Timedelta(minutes=60)
    else:
        check_out_dt = pd.to_datetime(df['attendance_date'].dt.strftime('%Y-%m-%d') + ' ' + df['check_out_time'])

    df['workout_duration_min'] = (check_out_dt - check_in_dt).dt.total_seconds() / 60.0
    
    # Extract check-in hour
    df['check_in_hour'] = pd.to_datetime(df['check_in_time'], format='%H:%M:%S').dt.hour
    
    # Time slot classification
    df['time_slot'] = np.where(df['check_in_hour'] < 12, 'Morning Shift (6AM-11AM)', 'Evening Shift (4PM-9PM)')

    return df

def preprocess_member_demographics(members_df):
    """
    Preprocess member demographics, detects age outliers, and bins into age categories.
    """
    df = members_df.copy()

    # Age Outlier Detection using IQR method (Unit 4)
    Q1 = df['age'].quantile(0.25)
    Q3 = df['age'].quantile(0.75)
    IQR = Q3 - Q1
    lower_bound = Q1 - 1.5 * IQR
    upper_bound = Q3 + 1.5 * IQR
    outliers = df[(df['age'] < lower_bound) | (df['age'] > upper_bound)]
    print(f"[Preprocessing] Member Age Stats - Mean: {df['age'].mean():.1f}, Median: {df['age'].median():.1f}, IQR Outliers count: {len(outliers)}")

    # Age categorization
    bins = [13, 22, 35, 50, 100]
    labels = ['Youth (14-22)', 'Young Adult (23-35)', 'Adult (36-50)', 'Senior (51+)']
    df['age_group'] = pd.cut(df['age'], bins=bins, labels=labels, right=True)

    return df

def generate_preprocessed_datasets():
    """
    Main preprocessing pipeline returning enriched DataFrames ready for EDA and Visualization.
    """
    raw_data = fetch_full_gym_dataset()
    members_cleaned = preprocess_member_demographics(raw_data['members'])
    attendance_cleaned = preprocess_attendance_data(raw_data['attendance'])
    activity_joined = fetch_joined_activity_data()
    activity_enriched = preprocess_attendance_data(activity_joined)

    print("[Preprocessing] All datasets cleaned and feature engineered successfully.")
    return {
        'members': members_cleaned,
        'plans': raw_data['membership_plans'],
        'trainers': raw_data['trainers'],
        'subscriptions': raw_data['member_subscriptions'],
        'trainer_assignments': raw_data['trainer_assignments'],
        'attendance': attendance_cleaned,
        'activity_enriched': activity_enriched
    }

if __name__ == "__main__":
    print("Testing Preprocessing Pipeline...")
    data = generate_preprocessed_datasets()
    print("\nPreprocessed Attendance Preview:")
    print(data['attendance'][['member_id', 'attendance_date', 'day_of_week', 'check_in_hour', 'workout_duration_min', 'time_slot']].head(5))
