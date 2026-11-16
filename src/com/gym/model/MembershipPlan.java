package com.gym.model;

public class MembershipPlan extends BaseEntity {
    private String planName;
    private int durationMonths;
    private double fee;
    private String facilitiesOffered;

    public MembershipPlan() {
        super();
    }

    public MembershipPlan(int id, String planName, int durationMonths, double fee, String facilitiesOffered) {
        super(id);
        this.planName = planName;
        this.durationMonths = durationMonths;
        this.fee = fee;
        this.facilitiesOffered = facilitiesOffered;
    }

    public int getPlanId() {
        return getId();
    }

    public void setPlanId(int planId) {
        setId(planId);
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public int getDurationMonths() {
        return durationMonths;
    }

    public void setDurationMonths(int durationMonths) {
        this.durationMonths = durationMonths;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public String getFacilitiesOffered() {
        return facilitiesOffered;
    }

    public void setFacilitiesOffered(String facilitiesOffered) {
        this.facilitiesOffered = facilitiesOffered;
    }

    @Override
    public String toString() {
        return String.format("Plan [ID=%d, Name=%s, Duration=%d mo, Fee=₹%.2f, Facilities=%s]",
                getId(), planName, durationMonths, fee, facilitiesOffered);
    }
}
