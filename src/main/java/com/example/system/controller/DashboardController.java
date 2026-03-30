package com.example.system.controller;


import com.example.system.entity.Reservation;
import com.example.system.entity.User;
import com.example.system.repository.ReservationRepository;
import com.example.system.repository.UserRepository;
import com.example.system.service.DashboardInsightService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

@Controller
@RequiredArgsConstructor
public class DashboardController {
    private final UserRepository userRepo;
    private final ReservationRepository reserveRepo;
    private final DashboardInsightService dashboardInsightService;

    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam(defaultValue = "All") String status,
                                HttpSession session,
                                Model model) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return "redirect:/login";
        }

        User user = userRepo.findByUsername(username);
        if (user == null) {
            session.invalidate();
            return "redirect:/login";
        }

        LocalDate today = LocalDate.now();
        List<Reservation> allApplications = reserveRepo.findByUserOrderByAppliedOnDesc(user);
        List<Reservation> applications = "All".equalsIgnoreCase(status)
                ? allApplications
                : reserveRepo.findByUserAndStatusOrderByAppliedOnDesc(user, status);
        List<Reservation> upcomingInterviews = reserveRepo.findTop3ByUserAndInterviewDateGreaterThanEqualOrderByInterviewDateAsc(user, today);
        Map<String, Long> statusSummary = dashboardInsightService.buildStatusSummary(allApplications);
        Map<String, Long> monthlyProgress = dashboardInsightService.buildMonthlyProgress(allApplications);
        Reservation focusApplication = dashboardInsightService.findFocusApplication(allApplications);

        model.addAttribute("username", username);
        model.addAttribute("applications", applications);
        model.addAttribute("totalApplications", allApplications.size());
        model.addAttribute("activePipelines", reserveRepo.countByUserAndStatusIn(user, List.of("Applied", "Assessment", "Interview Scheduled", "Interviewing", "HR Round")));
        model.addAttribute("offersCount", reserveRepo.countByUserAndStatus(user, "Offer"));
        model.addAttribute("upcomingCount", reserveRepo.countByUserAndInterviewDateGreaterThanEqual(user, today));
        model.addAttribute("upcomingInterviews", upcomingInterviews);
        model.addAttribute("today", today);
        model.addAttribute("selectedStatus", status);
        model.addAttribute("statusSummary", statusSummary);
        model.addAttribute("focusApplication", focusApplication);
        model.addAttribute("coachTips", dashboardInsightService.buildCoachTips(allApplications));
        model.addAttribute("followUpsDue", dashboardInsightService.findFollowUpsDue(allApplications));
        model.addAttribute("resumeVariantCount", dashboardInsightService.countResumeVariants(allApplications));
        model.addAttribute("averageMockScore", Math.round(dashboardInsightService.calculateAverageMockScore(allApplications)));
        model.addAttribute("confidenceSummary", dashboardInsightService.buildConfidenceSummary(allApplications));
        model.addAttribute("monthlyProgress", monthlyProgress);
        model.addAttribute("recentActivity", dashboardInsightService.buildRecentActivity(allApplications));
        model.addAttribute("peakMonthCount", dashboardInsightService.findPeakMonthCount(monthlyProgress));
        return "dashboard";
    }

    @GetMapping("/applications/export")
    public ResponseEntity<String> exportApplications(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/login")
                    .build();
        }

        User user = userRepo.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(302)
                    .header(HttpHeaders.LOCATION, "/login")
                    .build();
        }

        List<Reservation> applications = reserveRepo.findByUserOrderByAppliedOnDesc(user);
        StringJoiner csv = new StringJoiner(System.lineSeparator());
        csv.add("Company,Role,Location,Status,Priority,Applied On,Interview Date,Work Mode,Package,Prep Focus");

        for (Reservation application : applications) {
            csv.add(csvCell(application.getCompanyName()) + ","
                    + csvCell(application.getRoleTitle()) + ","
                    + csvCell(application.getJobLocation()) + ","
                    + csvCell(application.getStatus()) + ","
                    + csvCell(application.getPriority()) + ","
                    + csvCell(application.getAppliedOn() != null ? application.getAppliedOn().toString() : "") + ","
                    + csvCell(application.getInterviewDate() != null ? application.getInterviewDate().toString() : "") + ","
                    + csvCell(application.getWorkMode()) + ","
                    + csvCell(application.getCompensationRange()) + ","
                    + csvCell(application.getPrepFocus()));
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=careersprint-applications.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csv.toString());
    }

    private String csvCell(String value) {
        String safeValue = value == null ? "" : value.replace("\"", "\"\"");
        return "\"" + safeValue + "\"";
    }
}
