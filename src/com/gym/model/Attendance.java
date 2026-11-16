package com.gym.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Attendance extends BaseEntity {
    private int memberId;
    private LocalDate attendanceDate;
    private LocalTime checkInTime;
    private LocalTime checkOutTime;
    private String workoutType;

    // Joined fields
    private String memberName;

    public Attendance() {
        super();
        this.attendanceDate = LocalDate.now();
        this.checkInTime = LocalTime.now();
        this.workoutType = "General Workout";
    }

    public Attendance(int id, int memberId, LocalDate attendanceDate, LocalTime checkInTime, LocalTime checkOutTime, String workoutType) {
        super(id);
        this.memberId = memberId;
        this.attendanceDate = attendanceDate != null ? attendanceDate : LocalDate.now();
        this.checkInTime = checkInTime != null ? checkInTime : LocalTime.now();
        this.checkOutTime = checkOutTime;
        this.workoutType = workoutType != null ? workoutType : "General Workout";
    }

    public int getAttendanceId() {
        return getId();
    }

    public void setAttendanceId(int attendanceId) {
        setId(attendanceId);
    }

    public int getMemberId() {
        return memberId;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalTime getCheckInTime() {
        return checkInTime;
    }

    public void setCheckInTime(LocalTime checkInTime) {
        this.checkInTime = checkInTime;
    }

    public LocalTime getCheckOutTime() {
        return checkOutTime;
    }

    public void setCheckOutTime(LocalTime checkOutTime) {
        this.checkOutTime = checkOutTime;
    }

    public String getWorkoutType() {
        return workoutType;
    }

    public void setWorkoutType(String workoutType) {
        this.workoutType = workoutType;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    @Override
    public String toString() {
        return String.format("Attendance [ID=%d, Member=%s, Date=%s, In=%s, Out=%s, Workout=%s]",
                getId(), memberName != null ? memberName : memberId,
                attendanceDate, checkInTime, checkOutTime != null ? checkOutTime : "In-Progress", workoutType);
    }
}
