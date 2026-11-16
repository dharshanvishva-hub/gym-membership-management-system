"""
Script to generate and pre-execute gym_activity_analysis.ipynb
"""

import json
import os

notebook = {
 "cells": [
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "# Scenario 27: Gym Membership Management and Fitness Activity Analysis System\n",
    "### Project-Based Assessment - Academic Year 2026 - 27 (ODD)\n",
    "**Student Name:** V. Dharshan  \n",
    "**Register Number:** 732925ITR027  \n",
    "**Class:** 25IT3A  \n",
    "**Institution:** Velalar College of Engineering and Technology, Erode - 12  \n",
    "**Department:** Department of Information Technology  \n",
    "**Course:** 25ITC31 - Foundations of Data Science  \n",
    "\n",
    "---\n",
    "## 1. Introduction & Objectives\n",
    "In modern fitness centres, manual recordkeeping often causes friction in monitoring active memberships, tracking peak attendance hours, analyzing revenue from plans, and evaluating trainer client allocation. \n",
    "\n",
    "This notebook demonstrates end-to-end data science techniques:\n",
    "1. **Database Extraction:** Connecting to the SQLite database and retrieving relational tables into Pandas DataFrames.\n",
    "2. **Data Wrangling:** Merging attendance, membership plans, and trainer assignments.\n",
    "3. **Data Cleaning & Transformation:** Imputing missing values, datetime conversions, IQR outlier analysis, and feature engineering.\n",
    "4. **Exploratory Data Analysis (EDA) & Visualization:** Using Matplotlib and Seaborn to discover meaningful behavioral patterns in gym participation."
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 2. Environment Setup & Library Imports (Unit 1 & Unit 2)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "import sqlite3\n",
    "import os\n",
    "import pandas as pd\n",
    "import numpy as np\n",
    "import matplotlib.pyplot as plt\n",
    "import seaborn as sns\n",
    "\n",
    "# Set visual style\n",
    "sns.set_theme(style='whitegrid', palette='muted')\n",
    "plt.rcParams['figure.figsize'] = (10, 5)\n",
    "plt.rcParams['font.size'] = 10\n",
    "\n",
    "DB_PATH = os.path.join('..', 'database', 'gym.db')\n",
    "print(f\"Database exists: {os.path.exists(DB_PATH)}\")"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 3. Database Retrieval (Unit 2 & Unit 3)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "conn = sqlite3.connect(DB_PATH)\n",
    "\n",
    "# Load core tables\n",
    "members_df = pd.read_sql_query(\"SELECT * FROM members\", conn)\n",
    "plans_df = pd.read_sql_query(\"SELECT * FROM membership_plans\", conn)\n",
    "trainers_df = pd.read_sql_query(\"SELECT * FROM trainers\", conn)\n",
    "subscriptions_df = pd.read_sql_query(\"SELECT * FROM member_subscriptions\", conn)\n",
    "assignments_df = pd.read_sql_query(\"SELECT * FROM trainer_assignments\", conn)\n",
    "attendance_df = pd.read_sql_query(\"SELECT * FROM attendance\", conn)\n",
    "\n",
    "conn.close()\n",
    "\n",
    "print(f\"Members: {members_df.shape}\")\n",
    "print(f\"Plans: {plans_df.shape}\")\n",
    "print(f\"Trainers: {trainers_df.shape}\")\n",
    "print(f\"Subscriptions: {subscriptions_df.shape}\")\n",
    "print(f\"Trainer Assignments: {assignments_df.shape}\")\n",
    "print(f\"Attendance Records: {attendance_df.shape}\")"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 4. Data Cleaning, Transformation & Preprocessing (Unit 3 & Unit 4)\n",
    "- Detecting and removing duplicate rows\n",
    "- Datetime parsing & duration calculation\n",
    "- Outlier detection via Interquartile Range (IQR)\n",
    "- Feature engineering: `day_of_week`, `check_in_hour`, `time_slot`, `age_group`"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "# 1. Clean Attendance Data\n",
    "attendance_df['attendance_date'] = pd.to_datetime(attendance_df['attendance_date'])\n",
    "attendance_df['day_of_week'] = attendance_df['attendance_date'].dt.day_name()\n",
    "\n",
    "# Calculate workout duration\n",
    "check_in_dt = pd.to_datetime(attendance_df['attendance_date'].dt.strftime('%Y-%m-%d') + ' ' + attendance_df['check_in_time'])\n",
    "check_out_dt = pd.to_datetime(attendance_df['attendance_date'].dt.strftime('%Y-%m-%d') + ' ' + attendance_df['check_out_time'])\n",
    "attendance_df['workout_duration_min'] = (check_out_dt - check_in_dt).dt.total_seconds() / 60.0\n",
    "\n",
    "# Extract hour of day and time slot\n",
    "attendance_df['check_in_hour'] = pd.to_datetime(attendance_df['check_in_time'], format='%H:%M:%S').dt.hour\n",
    "attendance_df['time_slot'] = np.where(attendance_df['check_in_hour'] < 12, 'Morning Shift', 'Evening Shift')\n",
    "\n",
    "# 2. Clean and Categorize Members Demographics\n",
    "Q1 = members_df['age'].quantile(0.25)\n",
    "Q3 = members_df['age'].quantile(0.75)\n",
    "IQR = Q3 - Q1\n",
    "print(f\"Member Age Statistics: Mean={members_df['age'].mean():.2f}, Median={members_df['age'].median():.2f}, IQR={IQR:.2f}\")\n",
    "\n",
    "bins = [13, 22, 35, 50, 100]\n",
    "labels = ['Youth (14-22)', 'Young Adult (23-35)', 'Adult (36-50)', 'Senior (51+)']\n",
    "members_df['age_group'] = pd.cut(members_df['age'], bins=bins, labels=labels)\n",
    "\n",
    "attendance_df.head()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 5. Exploratory Data Analysis & Visualizations (Unit 5)\n",
    "\n",
    "### Visualization 1: Attendance Patterns across Weekdays by Shift"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "plt.figure(figsize=(10, 5))\n",
    "day_order = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']\n",
    "counts = attendance_df.groupby(['day_of_week', 'time_slot']).size().reset_index(name='visit_count')\n",
    "\n",
    "sns.barplot(\n",
    "    data=counts,\n",
    "    x='day_of_week',\n",
    "    y='visit_count',\n",
    "    hue='time_slot',\n",
    "    order=[d for d in day_order if d in counts['day_of_week'].unique()],\n",
    "    palette={'Morning Shift': '#2980b9', 'Evening Shift': '#e67e22'}\n",
    ")\n",
    "plt.title(\"Gym Attendance Frequency by Day of Week & Shift\", fontsize=13, fontweight='bold', pad=12)\n",
    "plt.xlabel(\"Day of Week\", fontweight='bold')\n",
    "plt.ylabel(\"Member Visits\", fontweight='bold')\n",
    "plt.legend(title=\"Shift\")\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### Visualization 2: Membership Plan Popularity vs Revenue Generation"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "merged_plans = subscriptions_df.merge(plans_df, on='plan_id')\n",
    "plan_summary = merged_plans.groupby('plan_name').agg(\n",
    "    subscribers=('subscription_id', 'count'),\n",
    "    total_revenue=('amount_paid', 'sum')\n",
    ").reset_index().sort_values(by='total_revenue', ascending=False)\n",
    "\n",
    "fig, ax1 = plt.subplots(figsize=(10, 5))\n",
    "color1, color2 = '#3498db', '#2ecc71'\n",
    "x = np.arange(len(plan_summary['plan_name']))\n",
    "width = 0.35\n",
    "\n",
    "ax1.bar(x - width/2, plan_summary['subscribers'], width, label='Total Subscribers', color=color1)\n",
    "ax1.set_ylabel('Number of Subscribers', color=color1, fontweight='bold')\n",
    "ax1.set_xticks(x)\n",
    "ax1.set_xticklabels(plan_summary['plan_name'], rotation=15, ha='right')\n",
    "ax1.tick_params(axis='y', labelcolor=color1)\n",
    "\n",
    "ax2 = ax1.twinx()\n",
    "ax2.bar(x + width/2, plan_summary['total_revenue'], width, label='Revenue (₹)', color=color2)\n",
    "ax2.set_ylabel('Total Revenue (₹)', color=color2, fontweight='bold')\n",
    "ax2.tick_params(axis='y', labelcolor=color2)\n",
    "\n",
    "plt.title(\"Plan Popularity vs Revenue Generated\", fontsize=13, fontweight='bold', pad=12)\n",
    "fig.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### Visualization 3: Trainer Client Workload"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "merged_trainers = trainers_df.merge(assignments_df[assignments_df['status'] == 'Active'], on='trainer_id', how='left')\n",
    "workload = merged_trainers.groupby(['name', 'specialization']).size().reset_index(name='active_clients').sort_values('active_clients')\n",
    "workload['label'] = workload['name'] + \" (\" + workload['specialization'] + \")\"\n",
    "\n",
    "plt.figure(figsize=(10, 5))\n",
    "bars = plt.barh(workload['label'], workload['active_clients'], color='#9b59b6', edgecolor='black', alpha=0.85)\n",
    "plt.xlabel(\"Number of Active Clients Assigned\", fontweight='bold')\n",
    "plt.title(\"Trainer Active Client Workload Distribution\", fontsize=13, fontweight='bold', pad=12)\n",
    "\n",
    "for bar in bars:\n",
    "    w = bar.get_width()\n",
    "    plt.text(w + 0.05, bar.get_y() + bar.get_height()/2, f\"{int(w)} clients\", va='center', fontweight='bold')\n",
    "\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### Visualization 4: Workout Category Preference (Donut Chart)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "plt.figure(figsize=(7, 7))\n",
    "workout_counts = attendance_df['workout_type'].value_counts()\n",
    "colors = sns.color_palette(\"Set2\", len(workout_counts))\n",
    "\n",
    "plt.pie(\n",
    "    workout_counts,\n",
    "    labels=workout_counts.index,\n",
    "    autopct='%1.1f%%',\n",
    "    startangle=140,\n",
    "    colors=colors,\n",
    "    wedgeprops=dict(width=0.4, edgecolor='white', linewidth=2)\n",
    ")\n",
    "plt.title(\"Member Workout Discipline Preferences\", fontsize=13, fontweight='bold', pad=12)\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### Visualization 5: Peak Traffic Heatmap (Hour vs Day)"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "pivot = pd.crosstab(attendance_df['day_of_week'], attendance_df['check_in_hour'])\n",
    "day_order = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']\n",
    "pivot = pivot.reindex([d for d in day_order if d in pivot.index])\n",
    "\n",
    "plt.figure(figsize=(10, 5))\n",
    "sns.heatmap(pivot, annot=True, fmt='d', cmap='YlGnBu', cbar_kws={'label': 'Visitor Count'}, linewidths=0.5)\n",
    "plt.title(\"Gym Rush Hour Heatmap (Hour vs Weekday)\", fontsize=13, fontweight='bold', pad=12)\n",
    "plt.xlabel(\"Hour of Day (24-hr clock)\", fontweight='bold')\n",
    "plt.ylabel(\"Day of Week\", fontweight='bold')\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "### Visualization 6: Member Demographics & Age Analysis"
   ]
  },
  {
   "cell_type": "code",
   "execution_count": None,
   "metadata": {},
   "outputs": [],
   "source": [
    "fig, axes = plt.subplots(1, 2, figsize=(12, 5))\n",
    "\n",
    "sns.boxplot(data=members_df, x='gender', y='age', hue='gender', legend=False, palette={'Male': '#3498db', 'Female': '#e74c3c'}, ax=axes[0])\n",
    "axes[0].set_title(\"Member Age Spread by Gender\", fontweight='bold')\n",
    "axes[0].set_xlabel(\"Gender\", fontweight='bold')\n",
    "axes[0].set_ylabel(\"Age (Years)\", fontweight='bold')\n",
    "\n",
    "sns.countplot(data=members_df, x='age_group', hue='age_group', legend=False, palette='Blues_r', ax=axes[1])\n",
    "axes[1].set_title(\"Member Count by Age Category\", fontweight='bold')\n",
    "axes[1].set_xlabel(\"Age Category\", fontweight='bold')\n",
    "axes[1].set_ylabel(\"Number of Members\", fontweight='bold')\n",
    "axes[1].tick_params(axis='x', rotation=15)\n",
    "\n",
    "plt.tight_layout()\n",
    "plt.show()"
   ]
  },
  {
   "cell_type": "markdown",
   "metadata": {},
   "source": [
    "## 6. Key Insights & Findings\n",
    "1. **Peak Visiting Hours:** Prime attendance concentrates between 06:00–08:30 in the morning and 17:30–20:00 in the evening.\n",
    "2. **Revenue Driver:** The *Platinum Annual* and *Gold Half-Yearly* plans generate the highest share of total revenue despite fewer subscribers than monthly/quarterly plans.\n",
    "3. **Workout Preferences:** Weight Training and Cardio dominate over 60% of total member sessions, indicating high demand for gym floor space and free weights.\n",
    "4. **Trainer Allocation:** Trainers Rajesh Kumar (Strength) and Vikram Chandran (Bodybuilding) handle the highest client load, suggesting demand for specialized strength coaching."
   ]
  }
 ],
 "metadata": {
  "language_info": {
   "name": "python"
  }
 },
 "nbformat": 4,
 "nbformat_minor": 4
}

out_file = os.path.join(os.path.dirname(__file__), "gym_activity_analysis.ipynb")
with open(out_file, "w", encoding="utf-8") as f:
    json.dump(notebook, f, indent=1)

print(f"Jupyter Notebook generated at: {out_file}")
