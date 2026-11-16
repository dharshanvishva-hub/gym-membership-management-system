package com.gym.service;

import com.gym.dao.*;
import com.gym.exception.*;
import com.gym.model.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Core Service Layer orchestrating business logic and database interactions.
 * Demonstrates Unit 3 (Exceptions), Unit 4 (Strings/Regex), and Unit 5 (Streams & Lambdas).
 */
public class GymManagementService {
    private final MemberDAO memberDAO;
    private final PlanDAO planDAO;
    private final TrainerDAO trainerDAO;
    private final SubscriptionDAO subscriptionDAO;
    private final TrainerAssignmentDAO trainerAssignmentDAO;
    private final AttendanceDAO attendanceDAO;

    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{10}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");

    public GymManagementService() {
        this.memberDAO = new MemberDAO();
        this.planDAO = new PlanDAO();
        this.trainerDAO = new TrainerDAO();
        this.subscriptionDAO = new SubscriptionDAO();
        this.trainerAssignmentDAO = new TrainerAssignmentDAO();
        this.attendanceDAO = new AttendanceDAO();
    }

    // ==========================================
    // 1. Member Management Operations
    // ==========================================

    public Member registerMember(String name, int age, String gender, String contact, String email) throws GymException {
        validateMemberInput(name, age, contact, email);
        Member member = new Member(name.trim(), age, gender, contact.trim(), email != null ? email.trim() : "");
        try {
            boolean success = memberDAO.save(member);
            if (!success) {
                throw new DatabaseException("Failed to register member in database.");
            }
            return member;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating member: " + e.getMessage(), e);
        }
    }

    public List<Member> getAllMembers() throws DatabaseException {
        try {
            return memberDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving members: " + e.getMessage(), e);
        }
    }

    public Member getMemberById(int id) throws GymException {
        try {
            return memberDAO.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Member", id));
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving member: " + e.getMessage(), e);
        }
    }

    public List<Member> searchMembers(String keyword) throws DatabaseException {
        try {
            return memberDAO.searchByNameOrContact(keyword);
        } catch (SQLException e) {
            throw new DatabaseException("Error searching members: " + e.getMessage(), e);
        }
    }

    public boolean updateMemberStatus(int memberId, String status) throws GymException {
        Member member = getMemberById(memberId);
        member.setStatus(status);
        try {
            return memberDAO.update(member);
        } catch (SQLException e) {
            throw new DatabaseException("Error updating member status: " + e.getMessage(), e);
        }
    }

    private void validateMemberInput(String name, int age, String contact, String email) throws ValidationException {
        if (name == null || name.trim().length() < 2) {
            throw new ValidationException("Member name must be at least 2 characters.");
        }
        if (age < 14 || age > 90) {
            throw new ValidationException("Member age must be between 14 and 90.");
        }
        if (contact == null || !PHONE_PATTERN.matcher(contact.trim()).matches()) {
            throw new ValidationException("Contact number must be a valid 10-digit number.");
        }
        if (email != null && !email.trim().isEmpty() && !EMAIL_PATTERN.matcher(email.trim()).matches()) {
            throw new ValidationException("Invalid email address format.");
        }
    }

    // ==========================================
    // 2. Membership Plans Operations
    // ==========================================

    public List<MembershipPlan> getAllPlans() throws DatabaseException {
        try {
            return planDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving plans: " + e.getMessage(), e);
        }
    }

    public MembershipPlan getPlanById(int id) throws GymException {
        try {
            return planDAO.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Membership Plan", id));
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving plan: " + e.getMessage(), e);
        }
    }

    public MembershipPlan createPlan(String name, int durationMonths, double fee, String facilities) throws GymException {
        if (name == null || name.trim().isEmpty()) {
            throw new ValidationException("Plan name cannot be empty.");
        }
        if (durationMonths <= 0) {
            throw new ValidationException("Duration must be at least 1 month.");
        }
        if (fee < 0) {
            throw new ValidationException("Fee cannot be negative.");
        }
        MembershipPlan plan = new MembershipPlan(0, name.trim(), durationMonths, fee, facilities);
        try {
            planDAO.save(plan);
            return plan;
        } catch (SQLException e) {
            throw new DatabaseException("Error creating plan: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 3. Subscription & Renewal Operations
    // ==========================================

    public Subscription subscribeMember(int memberId, int planId, LocalDate startDate) throws GymException {
        Member member = getMemberById(memberId);
        MembershipPlan plan = getPlanById(planId);

        LocalDate start = startDate != null ? startDate : LocalDate.now();
        LocalDate expiry = start.plusMonths(plan.getDurationMonths());

        Subscription subscription = new Subscription(0, member.getMemberId(), plan.getPlanId(), start, expiry, plan.getFee(), "Paid");
        try {
            boolean success = subscriptionDAO.save(subscription);
            if (!success) {
                throw new DatabaseException("Failed to save subscription.");
            }
            subscription.setMemberName(member.getName());
            subscription.setPlanName(plan.getPlanName());
            return subscription;
        } catch (SQLException e) {
            throw new DatabaseException("Error processing subscription: " + e.getMessage(), e);
        }
    }

    public Subscription renewMembership(int memberId, int planId) throws GymException {
        // Find existing subscriptions to set new start date
        try {
            List<Subscription> existing = subscriptionDAO.findByMemberId(memberId);
            LocalDate newStart = LocalDate.now();
            if (!existing.isEmpty()) {
                LocalDate lastExpiry = existing.get(0).getExpiryDate();
                if (lastExpiry.isAfter(newStart)) {
                    newStart = lastExpiry.plusDays(1);
                }
            }
            return subscribeMember(memberId, planId, newStart);
        } catch (SQLException e) {
            throw new DatabaseException("Error renewing subscription: " + e.getMessage(), e);
        }
    }

    public List<Subscription> getAllSubscriptions() throws DatabaseException {
        try {
            return subscriptionDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving subscriptions: " + e.getMessage(), e);
        }
    }

    // Using Java 8+ Streams (Unit 5) to categorize memberships
    public Map<String, List<Subscription>> getSubscriptionsCategorized() throws DatabaseException {
        List<Subscription> all = getAllSubscriptions();
        return all.stream().collect(Collectors.groupingBy(Subscription::getStatus));
    }

    public List<Subscription> getExpiringSoon(int days) throws DatabaseException {
        try {
            return subscriptionDAO.findExpiringSoon(days);
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching expiring subscriptions: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 4. Attendance Management Operations
    // ==========================================

    public Attendance recordCheckIn(int memberId, String workoutType) throws GymException {
        Member member = getMemberById(memberId);
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        try {
            Optional<Attendance> openCheckIn = attendanceDAO.findOpenCheckIn(memberId, today);
            if (openCheckIn.isPresent()) {
                throw new ValidationException("Member is already checked in today at " + openCheckIn.get().getCheckInTime());
            }

            Attendance attendance = new Attendance(0, member.getMemberId(), today, now, null, workoutType);
            attendanceDAO.save(attendance);
            attendance.setMemberName(member.getName());
            return attendance;
        } catch (SQLException e) {
            throw new DatabaseException("Error recording check-in: " + e.getMessage(), e);
        }
    }

    public boolean recordCheckOut(int memberId) throws GymException {
        LocalDate today = LocalDate.now();
        try {
            Optional<Attendance> openCheckIn = attendanceDAO.findOpenCheckIn(memberId, today);
            if (!openCheckIn.isPresent()) {
                throw new ValidationException("No active check-in found for Member ID " + memberId + " today.");
            }
            return attendanceDAO.recordCheckOut(openCheckIn.get().getAttendanceId(), LocalTime.now());
        } catch (SQLException e) {
            throw new DatabaseException("Error recording check-out: " + e.getMessage(), e);
        }
    }

    public List<Attendance> getAllAttendance() throws DatabaseException {
        try {
            return attendanceDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching attendance: " + e.getMessage(), e);
        }
    }

    public List<Attendance> getMemberAttendance(int memberId) throws DatabaseException {
        try {
            return attendanceDAO.findByMemberId(memberId);
        } catch (SQLException e) {
            throw new DatabaseException("Error fetching member attendance: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 5. Trainer Management & Assignment
    // ==========================================

    public List<Trainer> getAllTrainers() throws DatabaseException {
        try {
            return trainerDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving trainers: " + e.getMessage(), e);
        }
    }

    public Trainer getTrainerById(int id) throws GymException {
        try {
            return trainerDAO.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Trainer", id));
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving trainer: " + e.getMessage(), e);
        }
    }

    public TrainerAssignment assignTrainer(int memberId, int trainerId, String fitnessGoal) throws GymException {
        Member member = getMemberById(memberId);
        Trainer trainer = getTrainerById(trainerId);

        if (fitnessGoal == null || fitnessGoal.trim().isEmpty()) {
            throw new ValidationException("Fitness goal cannot be empty.");
        }

        TrainerAssignment ta = new TrainerAssignment(0, member.getMemberId(), trainer.getTrainerId(),
                LocalDate.now(), fitnessGoal.trim(), "Active");
        try {
            boolean success = trainerAssignmentDAO.save(ta);
            if (!success) {
                throw new DatabaseException("Failed to save trainer assignment.");
            }
            ta.setMemberName(member.getName());
            ta.setTrainerName(trainer.getName());
            ta.setSpecialization(trainer.getSpecialization());
            return ta;
        } catch (SQLException e) {
            throw new DatabaseException("Error assigning trainer: " + e.getMessage(), e);
        }
    }

    public List<TrainerAssignment> getAllTrainerAssignments() throws DatabaseException {
        try {
            return trainerAssignmentDAO.findAll();
        } catch (SQLException e) {
            throw new DatabaseException("Error retrieving trainer assignments: " + e.getMessage(), e);
        }
    }

    // ==========================================
    // 6. Analytics & Aggregations using Java Streams
    // ==========================================

    public double calculateTotalRevenue() throws DatabaseException {
        List<Subscription> subscriptions = getAllSubscriptions();
        return subscriptions.stream()
                .mapToDouble(Subscription::getAmountPaid)
                .sum();
    }

    public Map<String, Long> getWorkoutDistribution() throws DatabaseException {
        List<Attendance> attendances = getAllAttendance();
        return attendances.stream()
                .collect(Collectors.groupingBy(Attendance::getWorkoutType, Collectors.counting()));
    }

    public Map<String, Long> getTrainerWorkloadCounts() throws DatabaseException {
        List<TrainerAssignment> assignments = getAllTrainerAssignments();
        return assignments.stream()
                .filter(a -> "Active".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.groupingBy(TrainerAssignment::getTrainerName, Collectors.counting()));
    }
}
