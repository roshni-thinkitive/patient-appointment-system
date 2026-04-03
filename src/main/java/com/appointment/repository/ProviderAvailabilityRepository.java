package com.appointment.repository;

import com.appointment.entity.Provider;
import com.appointment.entity.ProviderAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProviderAvailabilityRepository extends JpaRepository<ProviderAvailability, Long> {

    List<ProviderAvailability> findAllByProvider(Provider provider);

    List<ProviderAvailability> findAllByProviderAndDayOfWeek(Provider provider, DayOfWeek day);

    List<ProviderAvailability> findByProviderAndIsActiveTrue(Provider provider);

    Optional<ProviderAvailability> findByUuid(UUID uuid);

    void deleteAllByProvider(Provider provider);
}
