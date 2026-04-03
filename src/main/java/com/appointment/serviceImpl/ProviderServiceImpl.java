package com.appointment.serviceImpl;

import com.appointment.dto.provider.ProviderDto;
import com.appointment.dto.provider.ProviderResponseDto;
import com.appointment.entity.Provider;
import com.appointment.exception.ResourceNotFoundException;
import com.appointment.repository.ProviderRepository;
import com.appointment.service.ProviderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProviderServiceImpl implements ProviderService {

    private final ProviderRepository providerRepository;

    @Override
    @Transactional
    public ProviderResponseDto createProvider(ProviderDto dto, String email) {
        Provider provider = Provider.builder()
                .createdBy(email)
                .firstName(dto.getFirstName())
                .lastName(dto.getLastName())
                .specialization(dto.getSpecialization())
                .licenseNumber(dto.getLicenseNumber())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        provider = providerRepository.save(provider);
        return toProviderResponseDto(provider);
    }

    @Override
    @Transactional(readOnly = true)
    public ProviderResponseDto getProviderByUuid(UUID uuid) {
        Provider provider = providerRepository.findByUuid(uuid)
                .orElseThrow(() -> new ResourceNotFoundException("Provider", "uuid", uuid));
        return toProviderResponseDto(provider);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProviderResponseDto> getAllProviders(Pageable pageable) {
        return providerRepository.findAll(pageable)
                .map(this::toProviderResponseDto);
    }

    private ProviderResponseDto toProviderResponseDto(Provider provider) {
        return ProviderResponseDto.builder()
                .uuid(provider.getUuid())
                .createdBy(provider.getCreatedBy())
                .firstName(provider.getFirstName())
                .lastName(provider.getLastName())
                .specialization(provider.getSpecialization())
                .licenseNumber(provider.getLicenseNumber())
                .phoneNumber(provider.getPhoneNumber())
                .createdAt(provider.getCreatedAt())
                .build();
    }
}
