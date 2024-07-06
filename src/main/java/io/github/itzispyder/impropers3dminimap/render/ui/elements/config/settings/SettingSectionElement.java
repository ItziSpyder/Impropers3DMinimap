package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings;

import io.github.itzispyder.impropers3dminimap.config.Setting;
import io.github.itzispyder.impropers3dminimap.config.SettingSection;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public class SettingSectionElement extends GuiElement {

    private final SettingSection section;

    public SettingSectionElement(SettingSection section, int x, int y, int width) {
        super(x, y, width, 10);
        this.section = section;

        int caret = y + 10;
        for (Setting<?> setting : section.getSettings()) {
            GuiElement settingElement = setting.toGuiElement(x, caret);
            this.addChild(settingElement);
            caret += settingElement.height + 5;
        }
        this.setHeight(caret - y);
    }

    @Override
    public void onRender(DrawContext context, int mx, int my) {
        String text = section.getName();
        int tw = system.textRenderer.getWidth(text);
        int tx = x + (width - tw) / 2;
        int ty = y + 2;
        int lw = x + width - (tx + tw + 3);

        RenderUtils.drawText(context, text, tx, y, false);
        RenderUtils.fillSidewaysGradient(context, tx + tw + 3, ty, lw, 1, 0xFFFFFFFF, 0x00FFFFFF);
        RenderUtils.fillSidewaysGradient(context, x, ty, lw, 1, 0x00FFFFFF, 0xFFFFFFFF);
    }

    public SettingSection getSection() {
        return section;
    }
}
