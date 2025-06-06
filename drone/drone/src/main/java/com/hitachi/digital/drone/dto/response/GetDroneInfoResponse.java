package com.hitachi.digital.drone.dto.response;

import lombok.Data;

@Data
public class GetDroneInfoResponse {

    private String serialNumber;
    private String model;
    private Integer weightLimit;
    private Double batteryCapacity;
    private String state;
}
