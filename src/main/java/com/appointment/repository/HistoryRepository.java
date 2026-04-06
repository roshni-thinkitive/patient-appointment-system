package com.appointment.repository;

import com.appointment.entity.History;
import com.appointment.entity.Patient;
import com.appointment.enums.HistoryType;
import com.appointment.enums.Relationship;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {

    Optional<History> findByUuidAndIsDeletedFalse(UUID uuid);

    @Query("SELECT h FROM History h WHERE h.isDeleted = false " +
           "AND (:patient IS NULL OR h.patient = :patient) " +
           "AND (:historyType IS NULL OR h.historyType = :historyType) " +
           "AND (:relation IS NULL OR h.relation = :relation) " +
           "AND (:searchString IS NULL OR LOWER(h.condition) LIKE LOWER(CONCAT('%', CAST(:searchString AS string), '%')))")
    Page<History> findAllFiltered(@Param("patient") Patient patient,
                                  @Param("historyType") HistoryType historyType,
                                  @Param("relation") Relationship relation,
                                  @Param("searchString") String searchString,
                                  Pageable pageable);

    List<History> findByPatientAndHistoryTypeAndIsDeletedFalseOrderByCreatedAtDesc(
            Patient patient, HistoryType historyType);

    Optional<History> findFirstByPatientAndHistoryTypeAndIsDeletedFalseOrderByCreatedAtDesc(
            Patient patient, HistoryType historyType);
}
