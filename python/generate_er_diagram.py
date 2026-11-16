"""
Generate a professional, high-resolution ER Diagram image for GitHub display.
"""

import matplotlib.pyplot as plt
import matplotlib.patches as patches
import os

fig, ax = plt.subplots(figsize=(16, 11), dpi=300)
ax.set_xlim(0, 16)
ax.set_ylim(0, 11)
ax.axis('off')

# Background
fig.patch.set_facecolor('#f8f9fa')
ax.set_facecolor('#f8f9fa')

# Title
ax.text(8, 10.5, "ENTITY-RELATIONSHIP (ER) DIAGRAM", ha='center', va='center',
        fontsize=18, fontweight='bold', color='#2c3e50')
ax.text(8, 10.15, "Scenario 27: Gym Membership Management and Fitness Activity Analysis System",
        ha='center', va='center', fontsize=12, color='#7f8c8d')
ax.text(8, 9.85, "Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A | VCET Erode",
        ha='center', va='center', fontsize=10, fontstyle='italic', color='#34495e')

def draw_entity(ax, x, y, width, height, title, attributes, header_color='#2980b9'):
    header_h = 0.55
    
    # Shadow
    shadow = patches.FancyBboxPatch((x+0.06, y-height-0.06), width, height,
                                    boxstyle="round,pad=0.03,rounding_size=0.1",
                                    facecolor='#d0d7de', edgecolor='none', zorder=1)
    ax.add_patch(shadow)

    # Main body box
    body = patches.FancyBboxPatch((x, y-height), width, height,
                                  boxstyle="round,pad=0.03,rounding_size=0.1",
                                  facecolor='#ffffff', edgecolor='#bdc3c7', linewidth=1.5, zorder=2)
    ax.add_patch(body)

    # Header patch
    header = patches.Rectangle((x, y-header_h), width, header_h,
                               facecolor=header_color, edgecolor='none', zorder=3)
    ax.add_patch(header)

    # Title text
    ax.text(x + width/2, y - header_h/2, title, ha='center', va='center',
            fontsize=11, fontweight='bold', color='white', zorder=4)

    # Attribute text
    line_y = y - header_h - 0.28
    for attr in attributes:
        is_pk = "[PK]" in attr
        is_fk = "[FK]" in attr
        
        if is_pk:
            text_color = '#c0392b'
            weight = 'bold'
        elif is_fk:
            text_color = '#8e44ad'
            weight = 'bold'
        else:
            text_color = '#2c3e50'
            weight = 'normal'
            
        ax.text(x + 0.2, line_y, attr, ha='left', va='center',
                fontsize=8.5, color=text_color, fontweight=weight, zorder=4)
        line_y -= 0.28

# 1. MEMBERS Entity
members_attrs = [
    "* member_id : INT [PK]",
    "- name : VARCHAR(100)",
    "- age : INT",
    "- gender : VARCHAR(10)",
    "- contact_number : VARCHAR(15) [UK]",
    "- email : VARCHAR(100) [UK]",
    "- registration_date : DATE",
    "- status : VARCHAR(20)"
]
draw_entity(ax, 0.8, 8.8, 3.8, 3.2, "MEMBERS", members_attrs, header_color='#1f618d')

# 2. MEMBERSHIP_PLANS Entity
plans_attrs = [
    "* plan_id : INT [PK]",
    "- plan_name : VARCHAR(50) [UK]",
    "- duration_months : INT",
    "- fee : DECIMAL(10,2)",
    "- facilities_offered : TEXT"
]
draw_entity(ax, 11.4, 8.8, 3.8, 2.4, "MEMBERSHIP_PLANS", plans_attrs, header_color='#27ae60')

# 3. TRAINERS Entity
trainers_attrs = [
    "* trainer_id : INT [PK]",
    "- name : VARCHAR(100)",
    "- specialization : VARCHAR(100)",
    "- contact_number : VARCHAR(15) [UK]",
    "- email : VARCHAR(100) [UK]",
    "- experience_years : INT",
    "- hourly_rate : DECIMAL(10,2)"
]
draw_entity(ax, 11.4, 4.8, 3.8, 2.9, "TRAINERS", trainers_attrs, header_color='#8e44ad')

# 4. MEMBER_SUBSCRIPTIONS (Associative Entity)
subs_attrs = [
    "* subscription_id : INT [PK]",
    "# member_id : INT [FK]",
    "# plan_id : INT [FK]",
    "- start_date : DATE",
    "- expiry_date : DATE",
    "- amount_paid : DECIMAL(10,2)",
    "- payment_status : VARCHAR(20)"
]
draw_entity(ax, 6.1, 8.8, 3.8, 2.9, "MEMBER_SUBSCRIPTIONS", subs_attrs, header_color='#d35400')

# 5. TRAINER_ASSIGNMENTS Entity
ta_attrs = [
    "* assignment_id : INT [PK]",
    "# member_id : INT [FK]",
    "# trainer_id : INT [FK]",
    "- assigned_date : DATE",
    "- fitness_goal : VARCHAR(100)",
    "- status : VARCHAR(20)"
]
draw_entity(ax, 6.1, 4.8, 3.8, 2.6, "TRAINER_ASSIGNMENTS", ta_attrs, header_color='#b7950b')

# 6. ATTENDANCE Entity
att_attrs = [
    "* attendance_id : INT [PK]",
    "# member_id : INT [FK]",
    "- attendance_date : DATE",
    "- check_in_time : TIME",
    "- check_out_time : TIME",
    "- workout_type : VARCHAR(50)"
]
draw_entity(ax, 0.8, 4.2, 3.8, 2.6, "ATTENDANCE", att_attrs, header_color='#2c3e50')

# Relationship Connectors
# Members to Subscriptions (1 to N)
ax.annotate("", xy=(6.1, 7.35), xytext=(4.6, 7.35),
            arrowprops=dict(arrowstyle="->", lw=2, color="#2980b9"))
ax.text(4.7, 7.5, "1", fontsize=10, fontweight='bold', color='#1f618d')
ax.text(5.8, 7.5, "N", fontsize=10, fontweight='bold', color='#d35400')
ax.text(5.35, 7.15, "subscribes", ha='center', fontsize=8.5, fontstyle='italic', color='#7f8c8d')

# Plans to Subscriptions (1 to N)
ax.annotate("", xy=(9.9, 7.35), xytext=(11.4, 7.35),
            arrowprops=dict(arrowstyle="->", lw=2, color="#27ae60"))
ax.text(11.2, 7.5, "1", fontsize=10, fontweight='bold', color='#27ae60')
ax.text(10.1, 7.5, "N", fontsize=10, fontweight='bold', color='#d35400')
ax.text(10.65, 7.15, "defines", ha='center', fontsize=8.5, fontstyle='italic', color='#7f8c8d')

# Members to Attendance (1 to N)
ax.annotate("", xy=(2.7, 4.2), xytext=(2.7, 5.6),
            arrowprops=dict(arrowstyle="->", lw=2, color="#2c3e50"))
ax.text(2.85, 5.3, "1", fontsize=10, fontweight='bold', color='#1f618d')
ax.text(2.85, 4.4, "N", fontsize=10, fontweight='bold', color='#2c3e50')
ax.text(3.3, 4.85, "logs daily", ha='center', fontsize=8.5, fontstyle='italic', color='#7f8c8d')

# Members to Trainer Assignments (1 to N)
ax.annotate("", xy=(6.1, 3.6), xytext=(4.6, 6.2),
            arrowprops=dict(arrowstyle="->", lw=2, color="#b7950b"))
ax.text(4.7, 6.0, "1", fontsize=10, fontweight='bold', color='#1f618d')
ax.text(5.8, 3.8, "N", fontsize=10, fontweight='bold', color='#b7950b')
ax.text(4.9, 4.6, "assigned", ha='center', fontsize=8.5, fontstyle='italic', color='#7f8c8d')

# Trainers to Trainer Assignments (1 to N)
ax.annotate("", xy=(9.9, 3.6), xytext=(11.4, 3.6),
            arrowprops=dict(arrowstyle="->", lw=2, color="#8e44ad"))
ax.text(11.2, 3.8, "1", fontsize=10, fontweight='bold', color='#8e44ad')
ax.text(10.1, 3.8, "N", fontsize=10, fontweight='bold', color='#b7950b')
ax.text(10.65, 3.35, "guides", ha='center', fontsize=8.5, fontstyle='italic', color='#7f8c8d')

# Legend
legend_box = patches.FancyBboxPatch((1.0, 0.4), 14.0, 0.7,
                                   boxstyle="round,pad=0.03,rounding_size=0.08",
                                   facecolor='#ecf0f1', edgecolor='#bdc3c7', linewidth=1)
ax.add_patch(legend_box)
ax.text(8.0, 0.75, "RELATIONAL LEGEND:  * [PK] = Primary Key  |  # [FK] = Foreign Key  |  [UK] = Unique Key  |  Cardinality: 1:N (One-to-Many)",
        ha='center', va='center', fontsize=9.5, fontweight='bold', color='#2c3e50')

out_path = os.path.join(os.path.dirname(__file__), "..", "docs", "er_diagram.png")
plt.tight_layout()
plt.savefig(out_path, dpi=300, facecolor=fig.get_facecolor(), edgecolor='none')
plt.close()
print(f"ER Diagram generated at: {out_path}")
