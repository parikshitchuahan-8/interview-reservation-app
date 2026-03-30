package com.example.system.service;

import com.example.system.entity.Reservation;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardInsightService {

    public Map<String, Long> buildStatusSummary(List<Reservation> applications) {
        Map<String, Long> summary = new LinkedHashMap<>();
        List<String> orderedStatuses = List.of("Applied", "Assessment", "Interview Scheduled", "Interviewing", "HR Round", "Offer", "Rejected");

        for (String status : orderedStatuses) {
            long count = applications.stream()
                    .filter(application -> status.equalsIgnoreCase(application.getStatus()))
                    .count();
            summary.put(status, count);
        }

        return summary;
    }

    public Reservation findFocusApplication(List<Reservation> applications) {
        LocalDate today = LocalDate.now();
        return applications.stream()
                .filter(application -> !"Rejected".equalsIgnoreCase(application.getStatus()))
                .max(Comparator
                        .comparingInt(Reservation::getReadinessScore)
                        .thenComparing(application -> application.getInterviewDate() != null
                                ? Math.abs(application.getDaysUntilInterview())
                                : Long.MAX_VALUE, Comparator.reverseOrder())
                        .thenComparing(application -> application.getUpdatedOn() != null ? application.getUpdatedOn() : today))
                .orElse(null);
    }

    public List<String> buildCoachTips(List<Reservation> applications) {
        long highPriorityCount = applications.stream()
                .filter(application -> "High".equalsIgnoreCase(application.getPriority()))
                .count();
        long interviewCount = applications.stream()
                .filter(Reservation::hasUpcomingInterview)
                .count();
        long offerCount = applications.stream()
                .filter(application -> "Offer".equalsIgnoreCase(application.getStatus()))
                .count();

        String firstTip = interviewCount > 0
                ? "You have " + interviewCount + " interview-driven pipeline" + (interviewCount > 1 ? "s" : "") + ". Protect prep slots on your calendar."
                : "No interviews are scheduled yet. Use notes and follow-ups to move high-priority applications forward.";

        String secondTip = highPriorityCount > 2
                ? "You are tracking several high-priority roles. Pick one company for deep prep instead of spreading effort too thin."
                : "Your high-priority queue looks manageable. Keep updating notes after each recruiter touchpoint.";

        String thirdTip = offerCount > 0
                ? "Offer-stage applications deserve a negotiation checklist: compensation, role scope, joining timeline, and growth."
                : "A strong prep loop is visible in interviews. Use the tracker after each mock or real round to record learning quickly.";

        return List.of(firstTip, secondTip, thirdTip);
    }

    public List<Reservation> findFollowUpsDue(List<Reservation> applications) {
        return applications.stream()
                .filter(Reservation::isFollowUpDue)
                .sorted(Comparator.comparing(Reservation::getFollowUpDate))
                .limit(4)
                .collect(Collectors.toList());
    }

    public long countResumeVariants(List<Reservation> applications) {
        return applications.stream()
                .map(Reservation::getResumeVersion)
                .filter(version -> version != null && !version.isBlank())
                .distinct()
                .count();
    }

    public double calculateAverageMockScore(List<Reservation> applications) {
        return applications.stream()
                .map(Reservation::getMockInterviewScore)
                .filter(score -> score != null)
                .mapToInt(Integer::intValue)
                .average()
                .orElse(0);
    }

    public Map<String, Long> buildConfidenceSummary(List<Reservation> applications) {
        Map<String, Long> summary = new LinkedHashMap<>();
        List<String> levels = List.of("High", "Medium", "Low");
        for (String level : levels) {
            long count = applications.stream()
                    .filter(application -> level.equalsIgnoreCase(application.getConfidenceLevel()))
                    .count();
            summary.put(level, count);
        }
        return summary;
    }

    public Map<String, Long> buildMonthlyProgress(List<Reservation> applications) {
        Map<String, Long> progress = new LinkedHashMap<>();
        LocalDate now = LocalDate.now().withDayOfMonth(1);

        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            String label = month.getMonth().name().substring(0, 3) + " " + month.getYear();
            long count = applications.stream()
                    .filter(application -> application.getAppliedOn() != null
                            && application.getAppliedOn().getMonth() == month.getMonth()
                            && application.getAppliedOn().getYear() == month.getYear())
                    .count();
            progress.put(label, count);
        }
        return progress;
    }

    public List<Reservation> buildRecentActivity(List<Reservation> applications) {
        return applications.stream()
                .filter(application -> application.getUpdatedOn() != null)
                .sorted(Comparator.comparing(Reservation::getUpdatedOn).reversed())
                .limit(5)
                .toList();
    }

    public long findPeakMonthCount(Map<String, Long> monthlyProgress) {
        return monthlyProgress.values().stream().mapToLong(Long::longValue).max().orElse(1);
    }
}
