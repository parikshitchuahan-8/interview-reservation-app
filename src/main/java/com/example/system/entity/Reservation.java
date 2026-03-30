package com.example.system.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "applications")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String companyName;
    private String roleTitle;
    private String jobLocation;
    private String applicationLink;
    private LocalDate appliedOn;
    private LocalDate interviewDate;
    private String status;
    private String workMode;
    private String priority;
    private String prepFocus;
    private String compensationRange;
    private String recruiterName;
    private String recruiterEmail;
    private String resumeVersion;
    private Integer mockInterviewScore;
    private String confidenceLevel;
    private String nextMilestone;
    private Boolean reminderSent;
    private LocalDate lastReminderSentOn;
    private LocalDate followUpDate;
    private LocalDate updatedOn;

    @Column(length = 1500)
    private String notes;

    @ManyToOne
    private User user;

    @Transient
    public boolean hasUpcomingInterview() {
        return interviewDate != null && !interviewDate.isBefore(LocalDate.now());
    }

    @Transient
    public long getDaysUntilInterview() {
        if (interviewDate == null) {
            return -1;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), interviewDate);
    }

    @Transient
    public int getReadinessScore() {
        int score = 30;

        if (status != null) {
            score += switch (status) {
                case "Assessment" -> 10;
                case "Interview Scheduled", "Interviewing" -> 20;
                case "HR Round" -> 25;
                case "Offer" -> 35;
                default -> 0;
            };
        }

        if ("High".equalsIgnoreCase(priority)) {
            score += 15;
        } else if ("Medium".equalsIgnoreCase(priority)) {
            score += 8;
        }

        if (prepFocus != null && !prepFocus.isBlank()) {
            score += 10;
        }

        if (notes != null && notes.length() > 40) {
            score += 10;
        }

        if (interviewDate != null && !interviewDate.isBefore(LocalDate.now())) {
            score += 10;
        }

        if (mockInterviewScore != null) {
            score += Math.min(mockInterviewScore / 10, 10);
        }

        return Math.min(score, 100);
    }

    @Transient
    public String getSuggestedNextAction() {
        if (followUpDate != null && !followUpDate.isAfter(LocalDate.now())
                && !"Offer".equalsIgnoreCase(status)
                && !"Rejected".equalsIgnoreCase(status)) {
            return "Follow up with " + (recruiterName == null || recruiterName.isBlank() ? "the recruiter" : recruiterName) + " today.";
        }
        if ("Applied".equalsIgnoreCase(status)) {
            return "Follow up with recruiter and revise the job description.";
        }
        if ("Assessment".equalsIgnoreCase(status)) {
            return "Block out a timed mock test window.";
        }
        if ("Interview Scheduled".equalsIgnoreCase(status) || "Interviewing".equalsIgnoreCase(status)) {
            return "Prepare examples and revise " + prepFocus + ".";
        }
        if ("HR Round".equalsIgnoreCase(status)) {
            return "Prepare salary expectation, relocation, and availability answers.";
        }
        if ("Offer".equalsIgnoreCase(status)) {
            return "Compare offer details and decide on negotiation points.";
        }
        return "Review notes and decide the next follow-up step.";
    }

    @Transient
    public boolean isFollowUpDue() {
        return followUpDate != null
                && !followUpDate.isAfter(LocalDate.now())
                && !"Offer".equalsIgnoreCase(status)
                && !"Rejected".equalsIgnoreCase(status);
    }

    @Transient
    public long getDaysUntilFollowUp() {
        if (followUpDate == null) {
            return Long.MAX_VALUE;
        }
        return ChronoUnit.DAYS.between(LocalDate.now(), followUpDate);
    }

    @Transient
    public String getRecruiterMessageDraft() {
        String person = recruiterName == null || recruiterName.isBlank() ? "Hiring Team" : recruiterName;
        return "Hi " + person + ", I wanted to follow up on my application for the "
                + roleTitle + " role at " + companyName
                + ". I remain very interested and would be glad to share any additional details. Thank you.";
    }

    @Transient
    public String getPrepIntensity() {
        if (mockInterviewScore == null) {
            return "Calibration needed";
        }
        if (mockInterviewScore >= 80) {
            return "Interview ready";
        }
        if (mockInterviewScore >= 60) {
            return "Sharpening";
        }
        return "Deep practice needed";
    }
}
