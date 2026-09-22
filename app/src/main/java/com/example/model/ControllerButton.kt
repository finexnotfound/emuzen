package com.example.model

enum class ControllerButton(val label: String) {
    // D-Pad
    DPAD_UP("▲"),
    DPAD_DOWN("▼"),
    DPAD_LEFT("◀"),
    DPAD_RIGHT("▶"),

    // Face Buttons
    A("A"),
    B("B"),
    X("X"),
    Y("Y"),

    // Shoulders & Triggers
    L("L"),
    R("R"),
    Z("Z"),
    ZL("ZL"),
    ZR("ZR"),
    L1("L1"),
    L2("L2"),
    R1("R1"),
    R2("R2"),

    // PlayStation Symbols
    PS_TRIANGLE("△"),
    PS_CIRCLE("○"),
    PS_CROSS("✕"),
    PS_SQUARE("□"),

    // N64 C-Buttons
    C_UP("▲"),
    C_DOWN("▼"),
    C_LEFT("◀"),
    C_RIGHT("▶"),

    // Atari & Game Gear
    ATARI_FIRE("FIRE"),
    GG_1("1"),
    GG_2("2"),

    // System
    START("START"),
    SELECT("SELECT"),
    HOME("HOME")
}

enum class AnalogStickType {
    LEFT,
    RIGHT,
    CIRCLE_PAD
}
