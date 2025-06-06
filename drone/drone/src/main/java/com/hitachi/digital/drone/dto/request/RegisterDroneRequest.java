package com.hitachi.digital.drone.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import static com.hitachi.digital.drone.constant.DroneConstants.MODEL_REGEX;
import static jakarta.validation.constraints.Pattern.Flag.CASE_INSENSITIVE;

@Data
public class RegisterDroneRequest {

    @NotBlank(message = "Serial Number is mandatory")
    @Size(min = 1, max = 100, message = "Serial Number must be between 1 and 100 characters")
    private String serialNumber;

    @Pattern(regexp = MODEL_REGEX, flags = { CASE_INSENSITIVE })
    private String model;

    @Min(value = 1, message = "Weight must be at least 1")
    @Max(value = 1000, message = "Weight Limit must not exceed 1000")
    private Integer weightLimit;
}
