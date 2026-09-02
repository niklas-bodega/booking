package com.lasias.hostelbookingbackend.dtos;

import java.time.LocalDateTime;

public record UserDto(String email, String name, String role, LocalDateTime createdAt, Boolean userHasAPassword) {
    }

