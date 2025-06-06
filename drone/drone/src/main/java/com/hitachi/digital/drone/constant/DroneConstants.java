package com.hitachi.digital.drone.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DroneConstants {

    public static final String API_BASE_PATH = "/api/v1/arch/global";

    public static final String LIGHTWEIGHT = "Lightweight";
    public static final String MIDDLEWEIGHT = "Middleweight";
    public static final String CRUISERWEIGHT = "Cruiserweight";
    public static final String HEAVYWEIGHT = "Heavyweight";

    public static final String MODEL_REGEX = LIGHTWEIGHT + "|" + MIDDLEWEIGHT + "|" + CRUISERWEIGHT
            + "|" + HEAVYWEIGHT;
}
