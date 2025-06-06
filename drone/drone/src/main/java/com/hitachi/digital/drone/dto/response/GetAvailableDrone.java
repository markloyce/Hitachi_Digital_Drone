package com.hitachi.digital.drone.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class GetAvailableDrone {

    List<AvailableDrone> availableDrones;
}
