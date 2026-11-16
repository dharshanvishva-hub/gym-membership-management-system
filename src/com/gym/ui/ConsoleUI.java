package com.gym.ui;

import com.gym.exception.GymException;
import com.gym.model.*;
import com.gym.service.GymManagementService;
import com.gym.service.ReportGeneratorService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Interactive Console Interface for Gym Membership Management & Activity Analysis.
 * Implements clean input handling, formatting, and all functional requirements.
 */
public class ConsoleUI {
    private final GymManagementService gymService;
    private final ReportGeneratorService reportService;
    private final Scanner scanner;

    public ConsoleUI() {
        this.gymService = new GymManagementService();
        this.reportService = new ReportGeneratorService(gymService);
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        boolean running = true;
        while (running) {
            printHeader();
            System.out.println(" 1. Manage Member Details (View, Add, Search, Update)");
            System.out.println(" 2. Manage Membership Plans (View, Add)");
            System.out.println(" 3. Register Memberships & Renewals");
            System.out.println(" 4. Record Attendance (Check-in, Check-out, View)");
            System.out.println(" 5. Manage Trainers & Member Assignments");
            System.out.println(" 6. Track Membership Status (Active, Expiring Soon, Expired)");
            System.out.println(" 7. Generate Business & Activity Reports");
            System.out.println(" 8. Run Background Multithreaded Audit Simulation");
            System.out.println(" 9. Launch Desktop GUI Mode");
            System.out.println(" 0. Exit System");
            System.out.println("================================================================================");
            System.out.print("Enter your choice (0-9): ");

            String input = scanner.nextLine().trim();
            switch (input) {
                case "1": handleMemberMenu(); break;
                case "2": handlePlanMenu(); break;
                case "3": handleSubscriptionMenu(); break;
                case "4": handleAttendanceMenu(); break;
                case "5": handleTrainerMenu(); break;
                case "6": handleStatusTracking(); break;
                case "7": handleReportsMenu(); break;
                case "8": handleMultithreadedAudit(); break;
                case "9":
                    System.out.println("\n[INFO] Launching Swing GUI in parallel...");
                    GymAppGUI.launch();
                    break;
                case "0":
                    running = false;
                    System.out.println("\nThank you for using Gym Management & Activity Analysis System. Goodbye!");
                    break;
                default:
                    System.out.println("\n[ERROR] Invalid choice. Please enter a number between 0 and 9.");
            }
        }
    }

    private void printHeader() {
        System.out.println("\n================================================================================");
        System.out.println("   GYM MEMBERSHIP MANAGEMENT & FITNESS ACTIVITY ANALYSIS SYSTEM");
        System.out.println("   Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A");
        System.out.println("   Velalar College of Engineering and Technology, Erode - 12");
        System.out.println("================================================================================");
    }

    private void handleMemberMenu() {
        System.out.println("\n--- MEMBER MANAGEMENT ---");
        System.out.println("1. List All Members");
        System.out.println("2. Add New Member");
        System.out.println("3. Search Member by Name/Contact");
        System.out.println("4. Update Member Status");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        try {
            switch (c) {
                case "1":
                    List<Member> members = gymService.getAllMembers();
                    System.out.println("\n--------------------------------------------------------------------------------");
                    System.out.printf("%-5s | %-20s | %-4s | %-7s | %-12s | %-10s\n", "ID", "Name", "Age", "Gender", "Contact", "Status");
                    System.out.println("--------------------------------------------------------------------------------");
                    for (Member m : members) {
                        System.out.printf("%-5d | %-20s | %-4d | %-7s | %-12s | %-10s\n",
                                m.getMemberId(), m.getName(), m.getAge(), m.getGender(), m.getContactNumber(), m.getStatus());
                    }
                    System.out.println("Total: " + members.size() + " members.");
                    break;

                case "2":
                    System.out.print("Enter Member Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Age: ");
                    int age = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Gender (Male/Female/Other): ");
                    String gender = scanner.nextLine().trim();
                    System.out.print("Enter Contact Number (10 digits): ");
                    String contact = scanner.nextLine().trim();
                    System.out.print("Enter Email Address: ");
                    String email = scanner.nextLine().trim();

                    Member saved = gymService.registerMember(name, age, gender, contact, email);
                    System.out.println("\n[SUCCESS] Member registered with ID: " + saved.getMemberId());
                    break;

                case "3":
                    System.out.print("Enter name or contact number to search: ");
                    String keyword = scanner.nextLine().trim();
                    List<Member> results = gymService.searchMembers(keyword);
                    if (results.isEmpty()) {
                        System.out.println("No matching members found.");
                    } else {
                        System.out.println("\nSearch Results (" + results.size() + " matches):");
                        for (Member m : results) {
                            System.out.println(" - " + m);
                        }
                    }
                    break;

                case "4":
                    System.out.print("Enter Member ID to update: ");
                    int id = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter New Status (Active / Inactive / Suspended): ");
                    String status = scanner.nextLine().trim();
                    boolean updated = gymService.updateMemberStatus(id, status);
                    if (updated) {
                        System.out.println("[SUCCESS] Member status updated successfully.");
                    }
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number format entered.");
        } catch (GymException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handlePlanMenu() {
        System.out.println("\n--- MEMBERSHIP PLANS ---");
        System.out.println("1. List All Plans");
        System.out.println("2. Add New Plan");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        try {
            switch (c) {
                case "1":
                    List<MembershipPlan> plans = gymService.getAllPlans();
                    System.out.println("\n--------------------------------------------------------------------------------");
                    System.out.printf("%-4s | %-20s | %-10s | %-10s | %-25s\n", "ID", "Plan Name", "Duration", "Fee (₹)", "Facilities");
                    System.out.println("--------------------------------------------------------------------------------");
                    for (MembershipPlan p : plans) {
                        System.out.printf("%-4d | %-20s | %-2d months  | %-10.2f | %-25s\n",
                                p.getPlanId(), p.getPlanName(), p.getDurationMonths(), p.getFee(), p.getFacilitiesOffered());
                    }
                    break;

                case "2":
                    System.out.print("Enter Plan Name: ");
                    String name = scanner.nextLine();
                    System.out.print("Enter Duration (in months): ");
                    int duration = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Fee (₹): ");
                    double fee = Double.parseDouble(scanner.nextLine().trim());
                    System.out.print("Enter Facilities Offered: ");
                    String facilities = scanner.nextLine();

                    MembershipPlan p = gymService.createPlan(name, duration, fee, facilities);
                    System.out.println("\n[SUCCESS] Membership plan created with ID: " + p.getPlanId());
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handleSubscriptionMenu() {
        System.out.println("\n--- MEMBERSHIP REGISTRATION & RENEWAL ---");
        System.out.println("1. Register New Subscription (Assign Plan)");
        System.out.println("2. Renew Existing Membership");
        System.out.println("3. View All Subscriptions");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        try {
            switch (c) {
                case "1":
                    System.out.print("Enter Member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Plan ID: ");
                    int planId = Integer.parseInt(scanner.nextLine().trim());

                    Subscription sub = gymService.subscribeMember(memberId, planId, LocalDate.now());
                    System.out.printf("\n[SUCCESS] Subscription registered! Expiry Date: %s | Amount: ₹%.2f\n",
                            sub.getExpiryDate(), sub.getAmountPaid());
                    break;

                case "2":
                    System.out.print("Enter Member ID to Renew: ");
                    int rMemberId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Plan ID for Renewal: ");
                    int rPlanId = Integer.parseInt(scanner.nextLine().trim());

                    Subscription renewed = gymService.renewMembership(rMemberId, rPlanId);
                    System.out.printf("\n[SUCCESS] Membership renewed! New Expiry Date: %s | Amount: ₹%.2f\n",
                            renewed.getExpiryDate(), renewed.getAmountPaid());
                    break;

                case "3":
                    List<Subscription> list = gymService.getAllSubscriptions();
                    System.out.println("\n--------------------------------------------------------------------------------");
                    System.out.printf("%-5s | %-18s | %-16s | %-11s | %-11s | %-10s\n",
                            "SubID", "Member Name", "Plan Name", "Start", "Expiry", "Status");
                    System.out.println("--------------------------------------------------------------------------------");
                    for (Subscription s : list) {
                        System.out.printf("%-5d | %-18s | %-16s | %-11s | %-11s | %-10s\n",
                                s.getSubscriptionId(), s.getMemberName(), s.getPlanName(),
                                s.getStartDate(), s.getExpiryDate(), s.getStatus());
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handleAttendanceMenu() {
        System.out.println("\n--- ATTENDANCE MANAGEMENT ---");
        System.out.println("1. Member Check-in");
        System.out.println("2. Member Check-out");
        System.out.println("3. View Member Attendance History");
        System.out.println("4. View All Recent Attendance Logs");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        try {
            switch (c) {
                case "1":
                    System.out.print("Enter Member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Workout Type (e.g. Weight Training, Cardio, Yoga, CrossFit, HIIT): ");
                    String workout = scanner.nextLine().trim();
                    if (workout.isEmpty()) workout = "General Workout";

                    Attendance a = gymService.recordCheckIn(memberId, workout);
                    System.out.printf("\n[SUCCESS] Check-in logged at %s for %s\n", a.getCheckInTime(), a.getMemberName());
                    break;

                case "2":
                    System.out.print("Enter Member ID: ");
                    int outMemberId = Integer.parseInt(scanner.nextLine().trim());
                    boolean checkedOut = gymService.recordCheckOut(outMemberId);
                    if (checkedOut) {
                        System.out.println("[SUCCESS] Check-out recorded successfully.");
                    }
                    break;

                case "3":
                    System.out.print("Enter Member ID: ");
                    int histMemberId = Integer.parseInt(scanner.nextLine().trim());
                    List<Attendance> hist = gymService.getMemberAttendance(histMemberId);
                    if (hist.isEmpty()) {
                        System.out.println("No attendance records found for this member.");
                    } else {
                        System.out.println("\nAttendance History (" + hist.size() + " sessions):");
                        for (Attendance att : hist) {
                            System.out.printf(" - Date: %s | Check-in: %s | Check-out: %s | Workout: %s\n",
                                    att.getAttendanceDate(), att.getCheckInTime(),
                                    att.getCheckOutTime() != null ? att.getCheckOutTime() : "In Progress",
                                    att.getWorkoutType());
                        }
                    }
                    break;

                case "4":
                    List<Attendance> all = gymService.getAllAttendance();
                    System.out.println("\n--------------------------------------------------------------------------------");
                    System.out.printf("%-5s | %-18s | %-11s | %-8s | %-8s | %-15s\n",
                            "ID", "Member Name", "Date", "In", "Out", "Workout");
                    System.out.println("--------------------------------------------------------------------------------");
                    for (Attendance att : all) {
                        System.out.printf("%-5d | %-18s | %-11s | %-8s | %-8s | %-15s\n",
                                att.getAttendanceId(), att.getMemberName(), att.getAttendanceDate(),
                                att.getCheckInTime(), att.getCheckOutTime() != null ? att.getCheckOutTime() : "--:--",
                                att.getWorkoutType());
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handleTrainerMenu() {
        System.out.println("\n--- TRAINERS & ASSIGNMENTS ---");
        System.out.println("1. List All Trainers");
        System.out.println("2. Assign Member to Trainer");
        System.out.println("3. View All Trainer Assignments");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        try {
            switch (c) {
                case "1":
                    List<Trainer> trainers = gymService.getAllTrainers();
                    System.out.println("\n--------------------------------------------------------------------------------");
                    System.out.printf("%-4s | %-18s | %-25s | %-6s | %-12s\n", "ID", "Trainer Name", "Specialization", "Exp(yr)", "Contact");
                    System.out.println("--------------------------------------------------------------------------------");
                    for (Trainer t : trainers) {
                        System.out.printf("%-4d | %-18s | %-25s | %-6d | %-12s\n",
                                t.getTrainerId(), t.getName(), t.getSpecialization(), t.getExperienceYears(), t.getContactNumber());
                    }
                    break;

                case "2":
                    System.out.print("Enter Member ID: ");
                    int memberId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Trainer ID: ");
                    int trainerId = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Enter Member Fitness Goal (e.g. Weight Loss, Muscle Hypertrophy, Mobility): ");
                    String goal = scanner.nextLine().trim();

                    TrainerAssignment ta = gymService.assignTrainer(memberId, trainerId, goal);
                    System.out.printf("\n[SUCCESS] Trainer %s assigned to %s for '%s'\n",
                            ta.getTrainerName(), ta.getMemberName(), ta.getFitnessGoal());
                    break;

                case "3":
                    List<TrainerAssignment> assignments = gymService.getAllTrainerAssignments();
                    System.out.println("\n--------------------------------------------------------------------------------");
                    System.out.printf("%-4s | %-18s | %-18s | %-25s | %-10s\n",
                            "ID", "Member Name", "Trainer Name", "Fitness Goal", "Status");
                    System.out.println("--------------------------------------------------------------------------------");
                    for (TrainerAssignment a : assignments) {
                        System.out.printf("%-4d | %-18s | %-18s | %-25s | %-10s\n",
                                a.getAssignmentId(), a.getMemberName(), a.getTrainerName(), a.getFitnessGoal(), a.getStatus());
                    }
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handleStatusTracking() {
        System.out.println("\n--- MEMBERSHIP STATUS TRACKING ---");
        try {
            Map<String, List<Subscription>> categorized = gymService.getSubscriptionsCategorized();
            System.out.println("\n1. Active Memberships: " + categorized.getOrDefault("Active", List.of()).size());
            System.out.println("2. Expiring Soon (<= 7 days): " + categorized.getOrDefault("Expiring Soon", List.of()).size());
            System.out.println("3. Expired Memberships: " + categorized.getOrDefault("Expired", List.of()).size());

            System.out.println("\nDisplaying Expiring Soon Memberships:");
            System.out.println(reportService.generateExpiringMembershipsReport(7));
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handleReportsMenu() {
        System.out.println("\n--- BUSINESS & ACTIVITY REPORTS ---");
        System.out.println("1. Revenue and Subscriptions Summary");
        System.out.println("2. Trainer Workload Summary");
        System.out.println("3. Workout Preference Breakdown");
        System.out.print("Choice: ");
        String c = scanner.nextLine().trim();

        try {
            switch (c) {
                case "1":
                    System.out.println(reportService.generateRevenueReport());
                    break;
                case "2":
                    System.out.println(reportService.generateTrainerWorkloadReport());
                    break;
                case "3":
                    Map<String, Long> workouts = gymService.getWorkoutDistribution();
                    System.out.println("\nWorkout Distribution Across Sessions:");
                    workouts.forEach((type, count) -> System.out.printf(" - %-20s : %d sessions\n", type, count));
                    break;
            }
        } catch (Exception e) {
            System.out.println("[ERROR] " + e.getMessage());
        }
    }

    private void handleMultithreadedAudit() {
        System.out.println("\n[Multithreading Demo] Spawning background worker thread for audit compilation...");
        reportService.generateAsyncAuditReport(() -> {
            System.out.println("[Multithreaded Worker] Audit callback received successfully!");
        });
        try {
            Thread.sleep(1500); // give thread time to print in console
        } catch (InterruptedException ignored) {}
    }
}
