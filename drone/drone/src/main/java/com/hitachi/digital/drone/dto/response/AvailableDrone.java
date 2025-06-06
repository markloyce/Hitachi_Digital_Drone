package com.hitachi.digital.drone.dto.response;

import lombok.Data;

import java.util.UUID;

@Data
public class AvailableDrone {

    private UUID id;
    private String serialNumber;
    private String model;
    private Integer weightAvailable;
    private String state;
    private Integer weightLimit;
    private Double batteryPercentage;
}
