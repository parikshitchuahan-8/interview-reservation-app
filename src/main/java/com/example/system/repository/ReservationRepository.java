package com.example.system.repository;

import com.example.system.entity.Reservation;
import com.example.system.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserOrderByAppliedOnDesc(User user);

    List<Reservation> findByUserAndStatusOrderByAppliedOnDesc(User user, String status);

    List<Reservation> findByUserOrderByUpdatedOnDesc(User user);

    List<Reservation> findTop3ByUserAndInterviewDateGreaterThanEqualOrderByInterviewDateAsc(User user, LocalDate interviewDate);

    List<Reservation> findByFollowUpDateLessThanEqualAndStatusNotIn(LocalDate followUpDate, Collection<String> statuses);

    long countByUser(User user);

    long countByUserAndInterviewDateGreaterThanEqual(User user, LocalDate interviewDate);

    long countByUserAndStatus(User user, String status);

    long countByUserAndStatusIn(User user, Collection<String> statuses);
}
