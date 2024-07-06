package io.github.itzispyder.impropers3dminimap.render.ui.elements.common;

import io.github.itzispyder.coherent.gui.ClickType;

@FunctionalInterface
public interface KeyPressCallback {

    void handleKey(int key, ClickType click, int scancode, int modifiers);
}
