package com.gym.ui;

import com.gym.model.*;
import com.gym.service.GymManagementService;
import com.gym.service.ReportGeneratorService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

/**
 * Modern Swing Graphical User Interface for Gym Management.
 * Features multi-tab navigation, data tables, and quick action dialogues.
 */
public class GymAppGUI extends JFrame {
    private final GymManagementService gymService;
    private final ReportGeneratorService reportService;

    public GymAppGUI() {
        this.gymService = new GymManagementService();
        this.reportService = new ReportGeneratorService(gymService);

        setTitle("Gym Membership Management & Fitness Activity Analysis System - V. Dharshan (732925ITR027)");
        setSize(1000, 680);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        initComponents();
    }

    private void initComponents() {
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Segoe UI", Font.BOLD, 13));

        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Members", createMembersPanel());
        tabbedPane.addTab("Plans & Subscriptions", createSubscriptionsPanel());
        tabbedPane.addTab("Attendance Logs", createAttendancePanel());
        tabbedPane.addTab("Trainers & Assignments", createTrainersPanel());
        tabbedPane.addTab("Business Reports", createReportsPanel());

        add(tabbedPane);
    }

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Top summary cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, 4, 15, 15));
        try {
            int totalMembers = gymService.getAllMembers().size();
            int totalPlans = gymService.getAllPlans().size();
            int totalSubs = gymService.getAllSubscriptions().size();
            double totalRevenue = gymService.calculateTotalRevenue();

            cardsPanel.add(createCard("Total Members", String.valueOf(totalMembers), new Color(41, 128, 185)));
            cardsPanel.add(createCard("Active Plans", String.valueOf(totalPlans), new Color(39, 174, 96)));
            cardsPanel.add(createCard("Subscriptions", String.valueOf(totalSubs), new Color(142, 68, 173)));
            cardsPanel.add(createCard("Total Revenue", String.format("₹%,.0f", totalRevenue), new Color(211, 84, 0)));
        } catch (Exception e) {
            cardsPanel.add(new JLabel("Error loading stats: " + e.getMessage()));
        }

        panel.add(cardsPanel, BorderLayout.NORTH);

        // Center: About & System Info
        JTextArea infoText = new JTextArea();
        infoText.setEditable(false);
        infoText.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        infoText.setText(
                "========================================================================================\n" +
                "  GYM MEMBERSHIP MANAGEMENT AND FITNESS ACTIVITY ANALYSIS SYSTEM\n" +
                "  Project-Based Assessment - Academic Year 2026 - 27 (ODD)\n" +
                "  Velalar College of Engineering and Technology, Erode - 12 | Dept. of Information Technology\n" +
                "  Student: V. Dharshan | Reg No: 732925ITR027 | Class: 25IT3A\n" +
                "========================================================================================\n\n" +
                "Integrated Subject Modules:\n" +
                " • 25ITT31 - Database Management Systems (SQLite Schema, Relational Model, 3NF Normalization)\n" +
                " • 25ITT32 - Java Programming (OOP, JDBC, Collections, Streams, Multithreading, Exceptions)\n" +
                " • 25ITT33 - Software Engineering (SRS IEEE 830, Use Case / Class Models, Test Cases)\n" +
                " • 25ITC31 - Foundations of Data Science (Python EDA, Preprocessing, Matplotlib & Seaborn Charts)\n\n" +
                "Use the tabs above to navigate through Members, Plans, Attendance, Trainers, and Reports."
        );
        panel.add(new JScrollPane(infoText), BorderLayout.CENTER);

        return panel;
    }

    private JPanel createCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout(5, 5));
        card.setBackground(color);
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLbl.setForeground(Color.WHITE);

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(new Font("Segoe UI", Font.BOLD, 22));
        valLbl.setForeground(Color.WHITE);

        card.add(titleLbl, BorderLayout.NORTH);
        card.add(valLbl, BorderLayout.CENTER);
        return card;
    }

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Member ID", "Name", "Age", "Gender", "Contact Number", "Email", "Reg Date", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);
        refreshMembersTable(model);

        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> refreshMembersTable(model));
        btnPanel.add(refreshBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshMembersTable(DefaultTableModel model) {
        model.setRowCount(0);
        try {
            List<Member> members = gymService.getAllMembers();
            for (Member m : members) {
                model.addRow(new Object[]{
                        m.getMemberId(), m.getName(), m.getAge(), m.getGender(),
                        m.getContactNumber(), m.getEmail(), m.getRegistrationDate(), m.getStatus()
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error fetching members: " + e.getMessage());
        }
    }

    private JPanel createSubscriptionsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Sub ID", "Member Name", "Plan Name", "Start Date", "Expiry Date", "Amount (₹)", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        try {
            List<Subscription> subs = gymService.getAllSubscriptions();
            for (Subscription s : subs) {
                model.addRow(new Object[]{
                        s.getSubscriptionId(), s.getMemberName(), s.getPlanName(),
                        s.getStartDate(), s.getExpiryDate(), String.format("%.2f", s.getAmountPaid()), s.getStatus()
                });
            }
        } catch (Exception ignored) {}

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createAttendancePanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Attendance ID", "Member Name", "Date", "Check-in Time", "Check-out Time", "Workout Type"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        try {
            List<Attendance> list = gymService.getAllAttendance();
            for (Attendance a : list) {
                model.addRow(new Object[]{
                        a.getAttendanceId(), a.getMemberName(), a.getAttendanceDate(),
                        a.getCheckInTime(), a.getCheckOutTime() != null ? a.getCheckOutTime() : "In Progress",
                        a.getWorkoutType()
                });
            }
        } catch (Exception ignored) {}

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createTrainersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Assignment ID", "Member Name", "Trainer Name", "Specialization", "Fitness Goal", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        JTable table = new JTable(model);

        try {
            List<TrainerAssignment> list = gymService.getAllTrainerAssignments();
            for (TrainerAssignment ta : list) {
                model.addRow(new Object[]{
                        ta.getAssignmentId(), ta.getMemberName(), ta.getTrainerName(),
                        ta.getSpecialization(), ta.getFitnessGoal(), ta.getStatus()
                });
            }
        } catch (Exception ignored) {}

        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JTextArea reportArea = new JTextArea();
        reportArea.setFont(new Font("Consolas", Font.PLAIN, 12));
        reportArea.setEditable(false);

        reportArea.setText(reportService.generateRevenueReport() + "\n\n" +
                reportService.generateExpiringMembershipsReport(7) + "\n\n" +
                reportService.generateTrainerWorkloadReport());

        panel.add(new JScrollPane(reportArea), BorderLayout.CENTER);

        JButton refreshBtn = new JButton("Refresh Reports");
        refreshBtn.addActionListener(e -> {
            reportArea.setText(reportService.generateRevenueReport() + "\n\n" +
                    reportService.generateExpiringMembershipsReport(7) + "\n\n" +
                    reportService.generateTrainerWorkloadReport());
        });
        panel.add(refreshBtn, BorderLayout.SOUTH);

        return panel;
    }

    public static void launch() {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}
            GymAppGUI gui = new GymAppGUI();
            gui.setVisible(true);
        });
    }
}
