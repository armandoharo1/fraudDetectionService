package com.armando.frauddetection.api.controller.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Login response with simple token")
public record LoginResponse(String token, String username, String role) {}

