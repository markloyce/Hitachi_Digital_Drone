package com.hitachi.digital.drone.enumeration;

public enum ModelEnum {

    LIGHTWEIGHT ("Lightweight"),
    MIDDLEWEIGHT("Middleweight"),
    CRUISERWEIGHT("Cruiserweight"),
    HEAVYWEIGHT("Heavyweight");

    private String value;

    private ModelEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    public static ModelEnum getValue(String value) {
        for (ModelEnum model : values()) {
            if (model.getValue().equalsIgnoreCase(value)) {
                return model;
            }
        }
        
        throw new IllegalArgumentException("Invalid ColorEnum value");
    }
}
