package com.appointment.controller;

import com.appointment.dto.common.ResponseHelper;
import com.appointment.dto.provider.ProviderDto;
import com.appointment.dto.provider.ProviderResponseDto;
import com.appointment.service.ProviderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/providers")
@RequiredArgsConstructor
public class ProviderController {

    private final ProviderService providerService;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createProvider(@Valid @RequestBody ProviderDto dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        ProviderResponseDto result = providerService.createProvider(dto, email);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseHelper.created("Provider created successfully", result));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<Map<String, Object>> getProviderByUuid(@PathVariable UUID uuid) {
        ProviderResponseDto result = providerService.getProviderByUuid(uuid);
        return ResponseEntity.ok(ResponseHelper.success("Provider fetched successfully", result));
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllProviders(
            @PageableDefault(page = 0, size = 10, sort = "createdAt",
                    direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(
                ResponseHelper.success("Providers fetched successfully",
                        providerService.getAllProviders(pageable)));
    }
}
