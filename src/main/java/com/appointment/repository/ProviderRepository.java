package com.appointment.repository;

import com.appointment.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    Optional<Provider> findByUuid(UUID uuid);

    List<Provider> findAllByCreatedBy(String email);
}
