package com.gym.service;

import com.gym.model.*;

import java.util.List;
import java.util.Map;

/**
 * Generates formatted text reports and demonstrates Unit 3 Multithreading.
 */
public class ReportGeneratorService {
    private final GymManagementService gymService;

    public ReportGeneratorService(GymManagementService gymService) {
        this.gymService = gymService;
    }

    public String generateRevenueReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                        FITNESS CENTRE REVENUE & SUBSCRIPTION REPORT                    \n");
        sb.append("========================================================================================\n");
        try {
            List<Subscription> subscriptions = gymService.getAllSubscriptions();
            double totalRevenue = gymService.calculateTotalRevenue();

            sb.append(String.format("%-6s | %-20s | %-18s | %-12s | %-12s | %-10s\n",
                    "SubID", "Member Name", "Plan Name", "Start Date", "Expiry Date", "Amount (₹)"));
            sb.append("----------------------------------------------------------------------------------------\n");

            for (Subscription s : subscriptions) {
                sb.append(String.format("%-6d | %-20s | %-18s | %-12s | %-12s | %10.2f\n",
                        s.getSubscriptionId(),
                        s.getMemberName() != null ? s.getMemberName() : "ID:" + s.getMemberId(),
                        s.getPlanName() != null ? s.getPlanName() : "ID:" + s.getPlanId(),
                        s.getStartDate(),
                        s.getExpiryDate(),
                        s.getAmountPaid()));
            }
            sb.append("----------------------------------------------------------------------------------------\n");
            sb.append(String.format("TOTAL ACCUMULATED REVENUE: ₹%,.2f across %d active/recorded subscriptions.\n",
                    totalRevenue, subscriptions.size()));
            sb.append("========================================================================================\n");
        } catch (Exception e) {
            sb.append("Error compiling report: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public String generateExpiringMembershipsReport(int days) {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append(String.format("                MEMBERSHIPS EXPIRING WITHIN %d DAYS (RENEWAL ALERTS)             \n", days));
        sb.append("========================================================================================\n");
        try {
            List<Subscription> expiring = gymService.getExpiringSoon(days);
            if (expiring.isEmpty()) {
                sb.append("No memberships expiring in the next ").append(days).append(" days.\n");
            } else {
                sb.append(String.format("%-6s | %-20s | %-18s | %-12s | %-12s\n",
                        "SubID", "Member Name", "Plan", "Expiry Date", "Days Left"));
                sb.append("----------------------------------------------------------------------------------------\n");
                for (Subscription s : expiring) {
                    sb.append(String.format("%-6d | %-20s | %-18s | %-12s | %-12d\n",
                            s.getSubscriptionId(),
                            s.getMemberName(),
                            s.getPlanName(),
                            s.getExpiryDate(),
                            s.getDaysRemaining()));
                }
            }
            sb.append("========================================================================================\n");
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    public String generateTrainerWorkloadReport() {
        StringBuilder sb = new StringBuilder();
        sb.append("========================================================================================\n");
        sb.append("                         TRAINER CLIENT WORKLOAD SUMMARY                                \n");
        sb.append("========================================================================================\n");
        try {
            Map<String, Long> workload = gymService.getTrainerWorkloadCounts();
            sb.append(String.format("%-30s | %-15s\n", "Trainer Name", "Active Clients"));
            sb.append("----------------------------------------------------------------------------------------\n");
            workload.forEach((trainer, count) ->
                    sb.append(String.format("%-30s | %-15d\n", trainer, count)));
            sb.append("========================================================================================\n");
        } catch (Exception e) {
            sb.append("Error: ").append(e.getMessage());
        }
        return sb.toString();
    }

    /**
     * Demonstrates Unit 3 Multithreading by executing report generation asynchronously.
     */
    public void generateAsyncAuditReport(Runnable onComplete) {
        Thread worker = new Thread(() -> {
            try {
                System.out.println("\n[Multithreaded Worker] Starting background audit & revenue report task...");
                Thread.sleep(1200); // simulate analytical aggregation workload
                String report = generateRevenueReport();
                System.out.println("[Multithreaded Worker] Background report ready!");
                System.out.println(report);
                if (onComplete != null) {
                    onComplete.run();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.err.println("[Multithreaded Worker] Task interrupted.");
            }
        }, "AuditReport-WorkerThread");
        worker.setDaemon(true);
        worker.start();
    }
}
