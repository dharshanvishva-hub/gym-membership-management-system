package com.gym.model;

import java.time.LocalDate;

public class Member extends BaseEntity {
    private String name;
    private int age;
    private String gender;
    private String contactNumber;
    private String email;
    private LocalDate registrationDate;
    private String status; // 'Active', 'Inactive', 'Suspended'

    public Member() {
        super();
        this.registrationDate = LocalDate.now();
        this.status = "Active";
    }

    public Member(int id, String name, int age, String gender, String contactNumber, String email, LocalDate registrationDate, String status) {
        super(id);
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.contactNumber = contactNumber;
        this.email = email;
        this.registrationDate = registrationDate != null ? registrationDate : LocalDate.now();
        this.status = status != null ? status : "Active";
    }

    public Member(String name, int age, String gender, String contactNumber, String email) {
        this(0, name, age, gender, contactNumber, email, LocalDate.now(), "Active");
    }

    public int getMemberId() {
        return getId();
    }

    public void setMemberId(int memberId) {
        setId(memberId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(LocalDate registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return String.format("Member [ID=%d, Name=%s, Age=%d, Gender=%s, Contact=%s, Status=%s]",
                getId(), name, age, gender, contactNumber, status);
    }
}
