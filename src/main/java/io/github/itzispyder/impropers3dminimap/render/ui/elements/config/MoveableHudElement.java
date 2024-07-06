package io.github.itzispyder.impropers3dminimap.render.ui.elements.config;

import io.github.itzispyder.impropers3dminimap.config.types.BooleanSetting;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.Hud;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.SettingDependent;
import net.minecraft.client.gui.DrawContext;

public class MoveableHudElement extends GuiElement {

    private final Hud hud;

    public MoveableHudElement(Hud hud) {
        super(hud.getX(), hud.getY(), hud.getWidth(), hud.getHeight());
        this.hud = hud;
        this.setDraggable(true);
    }

    @Override
    public void onRender(DrawContext context, int mouseX, int mouseY) {
        hud.setX(x);
        hud.setY(y);
        hud.onRender(context);
        this.setHeight(hud.getHeight());
        this.setWidth(hud.getWidth());
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        if (hud instanceof SettingDependent<?> sd && button == 1) {
            var setting = sd.provideSetting();
            if (setting instanceof BooleanSetting bs)
                bs.setVal(!bs.getVal());
        }
    }

    public Hud getHud() {
        return hud;
    }
}
