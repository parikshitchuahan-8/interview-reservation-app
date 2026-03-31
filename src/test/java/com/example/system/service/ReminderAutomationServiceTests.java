package com.example.system.service;

import com.example.system.entity.Reservation;
import com.example.system.repository.ReservationRepository;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReminderAutomationServiceTests {

    @Test
    void sendsReminderWhenConfigured() {
        ReservationRepository repository = mock(ReservationRepository.class);
        JavaMailSender mailSender = mock(JavaMailSender.class);
        MimeMessage mimeMessage = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        ReminderAutomationService service = new ReminderAutomationService(repository, mailSender);
        ReflectionTestUtils.setField(service, "remindersEnabled", true);
        ReflectionTestUtils.setField(service, "fromEmail", "noreply@test.dev");

        Reservation reservation = Reservation.builder()
                .companyName("Acme")
                .roleTitle("Backend Engineer")
                .recruiterName("Priya")
                .recruiterEmail("priya@example.com")
                .nextMilestone("Follow up")
                .prepFocus("Spring Boot")
                .followUpDate(LocalDate.now())
                .build();

        boolean result = service.sendReminderIfPossible(reservation);

        assertTrue(result);
        assertTrue(Boolean.TRUE.equals(reservation.getReminderSent()));
        verify(mailSender).send(mimeMessage);
    }

    @Test
    void skipsReminderWithoutSmtpOrRecipient() {
        ReminderAutomationService service = new ReminderAutomationService(mock(ReservationRepository.class), mock(JavaMailSender.class));
        ReflectionTestUtils.setField(service, "remindersEnabled", false);

        Reservation reservation = Reservation.builder()
                .companyName("Acme")
                .roleTitle("Backend Engineer")
                .build();

        assertFalse(service.sendReminderIfPossible(reservation));
    }
}
