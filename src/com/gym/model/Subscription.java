package com.gym.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Subscription extends BaseEntity {
    private int memberId;
    private int planId;
    private LocalDate startDate;
    private LocalDate expiryDate;
    private double amountPaid;
    private String paymentStatus;

    // Joined fields for display
    private String memberName;
    private String planName;

    public Subscription() {
        super();
        this.paymentStatus = "Paid";
    }

    public Subscription(int id, int memberId, int planId, LocalDate startDate, LocalDate expiryDate, double amountPaid, String paymentStatus) {
        super(id);
        this.memberId = memberId;
        this.planId = planId;
        this.startDate = startDate;
        this.expiryDate = expiryDate;
        this.amountPaid = amountPaid;
        this.paymentStatus = paymentStatus != null ? paymentStatus : "Paid";
    }

    public int getSubscriptionId() {
        return getId();
    }

    public void setSubscriptionId(int subscriptionId) {
        setId(subscriptionId);
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getPlanId() {
        return planId;
    }

    public void setPlanId(int planId) {
        this.planId = planId;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    public double getAmountPaid() {
        return amountPaid;
    }

    public void setAmountPaid(double amountPaid) {
        this.amountPaid = amountPaid;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getPlanName() {
        return planName;
    }

    public void setPlanName(String planName) {
        this.planName = planName;
    }

    public long getDaysRemaining() {
        if (expiryDate == null) return 0;
        return ChronoUnit.DAYS.between(LocalDate.now(), expiryDate);
    }

    public String getStatus() {
        if (expiryDate == null) return "Unknown";
        LocalDate today = LocalDate.now();
        if (expiryDate.isBefore(today)) {
            return "Expired";
        } else if (!expiryDate.isAfter(today.plusDays(7))) {
            return "Expiring Soon";
        } else {
            return "Active";
        }
    }

    @Override
    public String toString() {
        return String.format("Subscription [ID=%d, MemberID=%d, PlanID=%d, Start=%s, Expiry=%s, Status=%s]",
                getId(), memberId, planId, startDate, expiryDate, getStatus());
    }
}
