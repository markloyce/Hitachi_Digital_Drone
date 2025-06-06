package com.hitachi.digital.drone.dto.response;

import lombok.Data;

@Data
public class LoadedMedicationResponse {

    private String name;
    private String code;
    private Integer weight;
    private String imageUrl;
}
