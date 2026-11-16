package com.gym.model;

import java.time.LocalDate;

public class TrainerAssignment extends BaseEntity {
    private int memberId;
    private int trainerId;
    private LocalDate assignedDate;
    private String fitnessGoal;
    private String status; // 'Active', 'Completed', 'Cancelled'

    // Joined fields
    private String memberName;
    private String trainerName;
    private String specialization;

    public TrainerAssignment() {
        super();
        this.assignedDate = LocalDate.now();
        this.status = "Active";
    }

    public TrainerAssignment(int id, int memberId, int trainerId, LocalDate assignedDate, String fitnessGoal, String status) {
        super(id);
        this.memberId = memberId;
        this.trainerId = trainerId;
        this.assignedDate = assignedDate != null ? assignedDate : LocalDate.now();
        this.fitnessGoal = fitnessGoal;
        this.status = status != null ? status : "Active";
    }

    public int getAssignmentId() {
        return getId();
    }

    public void setAssignmentId(int assignmentId) {
        setId(assignmentId);
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getTrainerId() {
        return trainerId;
    }

    public void setTrainerId(int trainerId) {
        this.trainerId = trainerId;
    }

    public LocalDate getAssignedDate() {
        return assignedDate;
    }

    public void setAssignedDate(LocalDate assignedDate) {
        this.assignedDate = assignedDate;
    }

    public String getFitnessGoal() {
        return fitnessGoal;
    }

    public void setFitnessGoal(String fitnessGoal) {
        this.fitnessGoal = fitnessGoal;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getTrainerName() {
        return trainerName;
    }

    public void setTrainerName(String trainerName) {
        this.trainerName = trainerName;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    @Override
    public String toString() {
        return String.format("Assignment [ID=%d, Member=%s, Trainer=%s, Goal=%s, Status=%s]",
                getId(), memberName != null ? memberName : memberId,
                trainerName != null ? trainerName : trainerId, fitnessGoal, status);
    }
}
