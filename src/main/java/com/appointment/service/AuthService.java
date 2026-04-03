package com.appointment.service;

import com.appointment.dto.LoginRequestDto;
import com.appointment.dto.LoginResponseDto;
import com.appointment.dto.RegisterRequestDto;

public interface AuthService {

    LoginResponseDto login(LoginRequestDto loginRequest);

    String register(RegisterRequestDto registerRequest);
}
