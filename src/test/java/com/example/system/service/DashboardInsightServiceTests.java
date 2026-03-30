package com.example.system.service;

import com.example.system.entity.Reservation;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DashboardInsightServiceTests {

    private final DashboardInsightService service = new DashboardInsightService();

    @Test
    void buildsStatusSummaryAndFocusApplication() {
        Reservation applied = Reservation.builder()
                .companyName("Alpha")
                .roleTitle("Backend Engineer")
                .status("Applied")
                .priority("Medium")
                .confidenceLevel("Medium")
                .resumeVersion("Backend-v1")
                .appliedOn(LocalDate.now().minusDays(10))
                .prepFocus("Spring Boot")
                .notes("Submitted through careers portal.")
                .updatedOn(LocalDate.now())
                .build();

        Reservation interview = Reservation.builder()
                .companyName("Beta")
                .roleTitle("SDE")
                .status("Interview Scheduled")
                .priority("High")
                .confidenceLevel("High")
                .resumeVersion("Backend-v2")
                .mockInterviewScore(78)
                .appliedOn(LocalDate.now().minusDays(5))
                .prepFocus("DSA and LLD")
                .notes("Interview panel includes engineering manager.")
                .interviewDate(LocalDate.now().plusDays(2))
                .updatedOn(LocalDate.now())
                .build();

        List<Reservation> applications = List.of(applied, interview);

        Map<String, Long> summary = service.buildStatusSummary(applications);
        Reservation focusApplication = service.findFocusApplication(applications);

        assertEquals(1L, summary.get("Applied"));
        assertEquals(1L, summary.get("Interview Scheduled"));
        assertNotNull(focusApplication);
        assertEquals("Beta", focusApplication.getCompanyName());
        assertEquals(78.0, service.calculateAverageMockScore(applications));
        assertEquals(1L, service.buildConfidenceSummary(applications).get("High"));
        assertFalse(service.buildMonthlyProgress(applications).isEmpty());
        assertTrue(service.findPeakMonthCount(service.buildMonthlyProgress(applications)) >= 1);
    }
}
