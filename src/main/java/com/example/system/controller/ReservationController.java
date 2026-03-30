package com.example.system.controller;

import com.example.system.entity.Reservation;
import com.example.system.entity.User;
import com.example.system.repository.ReservationRepository;
import com.example.system.repository.UserRepository;
import com.example.system.service.ReminderAutomationService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@RequiredArgsConstructor
@Controller
public class ReservationController {

    private final ReservationRepository reserveRepo;
    private final UserRepository userRepo;
    private final ReminderAutomationService reminderAutomationService;

    @GetMapping("/applications/new")
    public String showReservationForm(HttpSession session, Model model) {
        User user = requireLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }
        model.addAttribute("application", Reservation.builder()
                .status("Applied")
                .workMode("On-site")
                .priority("Medium")
                .confidenceLevel("Medium")
                .build());
        model.addAttribute("formMode", "create");
        model.addAttribute("formAction", "/applications");
        return "reserve";
    }

    @GetMapping("/applications/{id}/edit")
    public String editReservationForm(@PathVariable Long id,
                                      HttpSession session,
                                      Model model,
                                      RedirectAttributes redirectAttributes) {
        User user = requireLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Reservation reservation = reserveRepo.findById(id).orElse(null);
        if (!isOwnedByUser(user, reservation)) {
            redirectAttributes.addFlashAttribute("message", "Unable to edit that application.");
            return "redirect:/dashboard";
        }

        model.addAttribute("application", reservation);
        model.addAttribute("formMode", "edit");
        model.addAttribute("formAction", "/applications/" + reservation.getId());
        return "reserve";
    }

    @PostMapping("/applications")
    public String createReservation(@RequestParam String companyName,
                                    @RequestParam String roleTitle,
                                    @RequestParam String jobLocation,
                                    @RequestParam String applicationLink,
                                    @RequestParam String appliedOn,
                                    @RequestParam(required = false) String interviewDate,
                                    @RequestParam(required = false) String followUpDate,
                                    @RequestParam String status,
                                    @RequestParam String workMode,
                                    @RequestParam String priority,
                                    @RequestParam String recruiterName,
                                    @RequestParam String recruiterEmail,
                                    @RequestParam String resumeVersion,
                                    @RequestParam(required = false) Integer mockInterviewScore,
                                    @RequestParam String confidenceLevel,
                                    @RequestParam String nextMilestone,
                                    @RequestParam String prepFocus,
                                    @RequestParam String compensationRange,
                                    @RequestParam String notes,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        User user = requireLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Reservation reservation = populateReservation(Reservation.builder().user(user).build(),
                companyName, roleTitle, jobLocation, applicationLink, appliedOn, interviewDate, followUpDate,
                status, workMode, priority, recruiterName, recruiterEmail, resumeVersion, mockInterviewScore,
                confidenceLevel, nextMilestone, prepFocus, compensationRange, notes);

        reserveRepo.save(reservation);

        redirectAttributes.addFlashAttribute("message", "Application added to your placement tracker.");
        return "redirect:/dashboard";
    }

    @PostMapping("/applications/{id}")
    public String updateReservation(@PathVariable Long id,
                                    @RequestParam String companyName,
                                    @RequestParam String roleTitle,
                                    @RequestParam String jobLocation,
                                    @RequestParam String applicationLink,
                                    @RequestParam String appliedOn,
                                    @RequestParam(required = false) String interviewDate,
                                    @RequestParam(required = false) String followUpDate,
                                    @RequestParam String status,
                                    @RequestParam String workMode,
                                    @RequestParam String priority,
                                    @RequestParam String recruiterName,
                                    @RequestParam String recruiterEmail,
                                    @RequestParam String resumeVersion,
                                    @RequestParam(required = false) Integer mockInterviewScore,
                                    @RequestParam String confidenceLevel,
                                    @RequestParam String nextMilestone,
                                    @RequestParam String prepFocus,
                                    @RequestParam String compensationRange,
                                    @RequestParam String notes,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User user = requireLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Reservation reservation = reserveRepo.findById(id).orElse(null);
        if (!isOwnedByUser(user, reservation)) {
            redirectAttributes.addFlashAttribute("message", "Unable to update that application.");
            return "redirect:/dashboard";
        }

        populateReservation(reservation, companyName, roleTitle, jobLocation, applicationLink, appliedOn, interviewDate, followUpDate,
                status, workMode, priority, recruiterName, recruiterEmail, resumeVersion, mockInterviewScore,
                confidenceLevel, nextMilestone, prepFocus, compensationRange, notes);
        reserveRepo.save(reservation);
        redirectAttributes.addFlashAttribute("message", "Application updated successfully.");
        return "redirect:/dashboard";
    }

    @PostMapping("/applications/delete")
    public String cancelReservation(@RequestParam Long reservationId,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {
        User user = requireLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Reservation reservation = reserveRepo.findById(reservationId).orElse(null);
        if (!isOwnedByUser(user, reservation)) {
            redirectAttributes.addFlashAttribute("message", "Unable to remove that application.");
            return "redirect:/dashboard";
        }

        reserveRepo.delete(reservation);
        redirectAttributes.addFlashAttribute("message", "Application removed from your tracker.");
        return "redirect:/dashboard";
    }

    @PostMapping("/applications/{id}/stage")
    public String updateStage(@PathVariable Long id,
                              @RequestParam String status,
                              @RequestHeader(value = "HX-Request", required = false) String htmxRequest,
                              HttpSession session,
                              Model model,
                              RedirectAttributes redirectAttributes) {
        User user = requireLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Reservation reservation = reserveRepo.findById(id).orElse(null);
        if (!isOwnedByUser(user, reservation)) {
            redirectAttributes.addFlashAttribute("message", "Unable to update that application.");
            return "redirect:/dashboard";
        }

        reservation.setStatus(status);
        reservation.setUpdatedOn(LocalDate.now());
        reserveRepo.save(reservation);

        if (htmxRequest != null) {
            model.addAttribute("application", reservation);
            return "fragments/application-row :: row";
        }

        redirectAttributes.addFlashAttribute("message", "Stage updated for " + reservation.getCompanyName() + ".");
        return "redirect:/dashboard";
    }

    @PostMapping("/applications/{id}/send-reminder")
    public String sendReminder(@PathVariable Long id,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        User user = requireLoggedInUser(session);
        if (user == null) {
            return "redirect:/login";
        }

        Reservation reservation = reserveRepo.findById(id).orElse(null);
        if (!isOwnedByUser(user, reservation)) {
            redirectAttributes.addFlashAttribute("message", "Unable to send a reminder for that application.");
            return "redirect:/dashboard";
        }

        boolean delivered = reminderAutomationService.sendReminderIfPossible(reservation);
        reserveRepo.save(reservation);
        redirectAttributes.addFlashAttribute("message", delivered
                ? "Reminder sent for " + reservation.getCompanyName() + "."
                : "Reminder prepared for " + reservation.getCompanyName() + ". Configure SMTP to send emails automatically.");
        return "redirect:/dashboard";
    }

    private User requireLoggedInUser(HttpSession session) {
        String username = (String) session.getAttribute("username");
        if (username == null) {
            return null;
        }

        User user = userRepo.findByUsername(username);
        if (user == null) {
            session.invalidate();
        }
        return user;
    }

    private boolean isOwnedByUser(User user, Reservation reservation) {
        return user != null && reservation != null && reservation.getUser() != null
                && reservation.getUser().getId().equals(user.getId());
    }

    private Reservation populateReservation(Reservation reservation,
                                            String companyName,
                                            String roleTitle,
                                            String jobLocation,
                                            String applicationLink,
                                            String appliedOn,
                                            String interviewDate,
                                            String followUpDate,
                                            String status,
                                            String workMode,
                                            String priority,
                                            String recruiterName,
                                            String recruiterEmail,
                                            String resumeVersion,
                                            Integer mockInterviewScore,
                                            String confidenceLevel,
                                            String nextMilestone,
                                            String prepFocus,
                                            String compensationRange,
                                            String notes) {
        reservation.setCompanyName(companyName.trim());
        reservation.setRoleTitle(roleTitle.trim());
        reservation.setJobLocation(jobLocation.trim());
        reservation.setApplicationLink(applicationLink.trim());
        reservation.setAppliedOn(LocalDate.parse(appliedOn));
        reservation.setInterviewDate(interviewDate == null || interviewDate.isBlank() ? null : LocalDate.parse(interviewDate));
        reservation.setFollowUpDate(followUpDate == null || followUpDate.isBlank() ? null : LocalDate.parse(followUpDate));
        reservation.setStatus(status);
        reservation.setWorkMode(workMode);
        reservation.setPriority(priority);
        reservation.setRecruiterName(recruiterName.trim());
        reservation.setRecruiterEmail(recruiterEmail.trim());
        reservation.setResumeVersion(resumeVersion.trim());
        reservation.setMockInterviewScore(mockInterviewScore);
        reservation.setConfidenceLevel(confidenceLevel);
        reservation.setNextMilestone(nextMilestone.trim());
        reservation.setPrepFocus(prepFocus.trim());
        reservation.setCompensationRange(compensationRange.trim());
        reservation.setNotes(notes.trim());
        reservation.setUpdatedOn(LocalDate.now());
        if (Boolean.TRUE.equals(reservation.getReminderSent())
                && reservation.getFollowUpDate() != null
                && reservation.getLastReminderSentOn() != null
                && reservation.getFollowUpDate().isAfter(reservation.getLastReminderSentOn())) {
            reservation.setReminderSent(false);
        }
        if (reservation.getReminderSent() == null) {
            reservation.setReminderSent(false);
        }
        return reservation;
    }

}
