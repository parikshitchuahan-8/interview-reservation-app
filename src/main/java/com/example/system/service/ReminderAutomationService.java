package com.example.system.service;

import com.example.system.entity.Reservation;
import com.example.system.repository.ReservationRepository;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReminderAutomationService {

    private final ReservationRepository reservationRepository;
    private final JavaMailSender mailSender;

    @Value("${careersprint.reminders.enabled:false}")
    private boolean remindersEnabled;

    @Value("${careersprint.reminders.from:no-reply@careersprint.local}")
    private String fromEmail;

    @Scheduled(cron = "${careersprint.reminders.cron:0 0 9 * * *}")
    public void sendDueReminderDigest() {
        if (!remindersEnabled) {
            return;
        }

        List<Reservation> dueApplications = reservationRepository.findByFollowUpDateLessThanEqualAndStatusNotIn(
                LocalDate.now(), List.of("Offer", "Rejected"));

        for (Reservation reservation : dueApplications) {
            if (Boolean.TRUE.equals(reservation.getReminderSent())
                    && LocalDate.now().equals(reservation.getLastReminderSentOn())) {
                continue;
            }

            boolean sent = sendReminderIfPossible(reservation);
            if (sent) {
                reservationRepository.save(reservation);
            }
        }
    }

    public boolean sendReminderIfPossible(Reservation reservation) {
        if (reservation == null) {
            return false;
        }

        reservation.setLastReminderSentOn(LocalDate.now());
        reservation.setReminderSent(false);

        if (!remindersEnabled || reservation.getRecruiterEmail() == null || reservation.getRecruiterEmail().isBlank()) {
            return false;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false);
            helper.setFrom(fromEmail);
            helper.setTo(reservation.getRecruiterEmail());
            helper.setSubject("Follow-up for " + reservation.getRoleTitle() + " application");
            helper.setText(buildEmailBody(reservation));
            mailSender.send(message);
            reservation.setReminderSent(true);
            return true;
        } catch (Exception exception) {
            log.warn("Unable to send reminder email for application {}", reservation.getId(), exception);
            return false;
        }
    }

    private String buildEmailBody(Reservation reservation) {
        return reservation.getRecruiterMessageDraft()
                + System.lineSeparator()
                + System.lineSeparator()
                + "Next milestone: " + safeValue(reservation.getNextMilestone())
                + System.lineSeparator()
                + "Prep focus: " + safeValue(reservation.getPrepFocus());
    }

    private String safeValue(String value) {
        return value == null || value.isBlank() ? "Not specified" : value;
    }
}
