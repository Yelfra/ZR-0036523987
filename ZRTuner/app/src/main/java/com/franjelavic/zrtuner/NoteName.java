package com.franjelavic.zrtuner;

public enum NoteName {

    C("C"),
    D("D"),
    E("E"),
    F("F"),
    G("G"),
    A("A"),
    B("B");

    private final String scientificPitchNotation;

    NoteName(String scientificPitchNotation) {
        this.scientificPitchNotation = scientificPitchNotation;
    }

    public String getNotation() {
        return scientificPitchNotation;
    }
}
