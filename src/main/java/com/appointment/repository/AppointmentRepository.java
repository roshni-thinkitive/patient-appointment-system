package com.appointment.repository;

import com.appointment.entity.Appointment;
import com.appointment.entity.Patient;
import com.appointment.entity.Provider;
import com.appointment.enums.AppointmentStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    Optional<Appointment> findByUuid(UUID uuid);

    Page<Appointment> findAllByPatient(Patient patient, Pageable pageable);

    Page<Appointment> findAllByProvider(Provider provider, Pageable pageable);

    List<Appointment> findAllByProviderAndAppointmentDate(Provider provider, LocalDate date);

    List<Appointment> findAllByStatus(AppointmentStatus status);

    @Query("SELECT COUNT(a) > 0 FROM Appointment a " +
           "WHERE a.provider = :provider " +
           "AND a.appointmentDate = :date " +
           "AND a.status = 'SCHEDULED' " +
           "AND a.startTime < :endTime " +
           "AND a.endTime > :startTime")
    boolean existsOverlappingAppointment(@Param("provider") Provider provider,
                                         @Param("date") LocalDate date,
                                         @Param("startTime") LocalTime startTime,
                                         @Param("endTime") LocalTime endTime);
}
