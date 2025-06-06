package com.hitachi.digital.drone.dto.request;

import jakarta.validation.constraints.*;

import lombok.Data;

@Data
public class LoadMedicationRequest {

    @Pattern(regexp = "^[a-zA-Z0-9_-]+$", message = "Only letters, numbers, hyphens, and underscores are allowed")
    private String name;

    @Min(value = 1, message = "Weight must be at least 1")
    @Max(value = 1000, message = "Weight Limit must not exceed 1000")
    private Integer weight;

    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Only uppercase letters, numbers, and underscores are allowed")
    private String code;
    private String imageUrl;
}
