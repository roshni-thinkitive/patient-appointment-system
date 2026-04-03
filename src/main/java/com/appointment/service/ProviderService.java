package com.appointment.service;

import com.appointment.dto.provider.ProviderDto;
import com.appointment.dto.provider.ProviderResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface ProviderService {

    ProviderResponseDto createProvider(ProviderDto dto, String email);

    ProviderResponseDto getProviderByUuid(UUID uuid);

    Page<ProviderResponseDto> getAllProviders(Pageable pageable);
}
