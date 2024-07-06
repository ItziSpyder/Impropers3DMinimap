package io.github.itzispyder.impropers3dminimap.render.ui.elements.common;

import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;

@FunctionalInterface
public interface PressAction<T extends GuiElement> {

    void onPress(T button);
}
