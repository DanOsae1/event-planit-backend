package com.osaebros.eventplanner.model;

import java.util.Arrays;

public enum Offering {
    PHOTOGRAPHER, DJ, DECORATIONS, VIDEOGRAPHY, EQUIPMENT, VENUE, CATERING, OTHER;

    public static Offering getOffering(String offering) {
        return Arrays.stream(values()).filter(o -> o.name().equalsIgnoreCase(offering))
                .findFirst()
                .orElse(OTHER);
    }
}
