package com.appointment.repository;

import com.appointment.entity.Provider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUuid(UUID uuid);

    Optional<Provider> findByUuidAndIsDeletedFalse(UUID uuid);

    Page<Provider> findAllByIsDeletedFalse(Pageable pageable);

    List<Provider> findAllByCreatedBy(String email);
}
