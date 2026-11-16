package com.gym.model;

public class Trainer extends BaseEntity {
    private String name;
    private String specialization;
    private String contactNumber;
    private String email;
    private int experienceYears;
    private double hourlyRate;

    public Trainer() {
        super();
    }

    public Trainer(int id, String name, String specialization, String contactNumber, String email, int experienceYears, double hourlyRate) {
        super(id);
        this.name = name;
        this.specialization = specialization;
        this.contactNumber = contactNumber;
        this.email = email;
        this.experienceYears = experienceYears;
        this.hourlyRate = hourlyRate;
    }

    public int getTrainerId() {
        return getId();
    }

    public void setTrainerId(int trainerId) {
        setId(trainerId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
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

    public int getExperienceYears() {
        return experienceYears;
    }

    public void setExperienceYears(int experienceYears) {
        this.experienceYears = experienceYears;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    @Override
    public String toString() {
        return String.format("Trainer [ID=%d, Name=%s, Specialization=%s, Exp=%d yrs, Contact=%s]",
                getId(), name, specialization, experienceYears, contactNumber);
    }
}
