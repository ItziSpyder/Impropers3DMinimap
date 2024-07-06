package io.github.itzispyder.coherent.gui;

public enum ClickType {

    RELEASE,
    CLICK,
    HOLD,
    UNKNOWN;

    public static ClickType of(int action) {
        ClickType r;
        switch (action) {
            case 0 -> r = RELEASE;
            case 1 -> r = CLICK;
            case 2 -> r = HOLD;
            default -> r = UNKNOWN;
        }
        return r;
    }

    public boolean isRelease() {
        return this == RELEASE;
    }

    public boolean isClick() {
        return this == CLICK;
    }

    public boolean isHold() {
        return this == HOLD;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }

    public boolean isUp() {
        return this == RELEASE || this == UNKNOWN;
    }

    public boolean isDown() {
        return this == CLICK || this == HOLD;
    }
}
