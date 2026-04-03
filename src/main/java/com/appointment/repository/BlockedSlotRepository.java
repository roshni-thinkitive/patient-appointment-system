package com.appointment.repository;

import com.appointment.entity.BlockedSlot;
import com.appointment.entity.Provider;
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
public interface BlockedSlotRepository extends JpaRepository<BlockedSlot, Long> {

    List<BlockedSlot> findAllByProvider(Provider provider);

    List<BlockedSlot> findByProviderAndBlockedDate(Provider provider, LocalDate date);

    Optional<BlockedSlot> findByUuid(UUID uuid);

    @Query("SELECT COUNT(b) > 0 FROM BlockedSlot b WHERE b.provider = :provider " +
           "AND b.blockedDate = :date " +
           "AND b.blockStartTime < :endTime " +
           "AND b.blockEndTime > :startTime")
    boolean existsOverlap(@Param("provider") Provider provider,
                          @Param("date") LocalDate date,
                          @Param("startTime") LocalTime startTime,
                          @Param("endTime") LocalTime endTime);
}
