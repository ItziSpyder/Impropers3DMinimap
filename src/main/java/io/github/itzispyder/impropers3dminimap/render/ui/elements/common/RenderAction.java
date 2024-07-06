package io.github.itzispyder.impropers3dminimap.render.ui.elements.common;

import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;
import net.minecraft.client.gui.DrawContext;

@FunctionalInterface
public interface RenderAction<T extends GuiElement> {

    void onRender(DrawContext context, int mouseX, int mouseY, T button);
}
