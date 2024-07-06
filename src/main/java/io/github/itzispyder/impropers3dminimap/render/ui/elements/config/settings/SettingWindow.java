package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.config.SettingContainer;
import io.github.itzispyder.impropers3dminimap.config.SettingSection;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive.ScrollPanelElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.WindowElement;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import io.github.itzispyder.impropers3dminimap.util.minecraft.TextUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SettingWindow extends WindowElement {

    private final SettingContainer module;
    private final String name, description;

    public SettingWindow(SettingContainer module, int x, int y, String name, String description) {
        super(name, x, y, 200, 250);
        this.module = module;
        this.description = description;
        this.name = name;

        int width = this.width - 20;
        int margin = x + 10;
        int caret = y + 20 + (TextUtils.wordWrap(description, width).size() * 10) + 10;
        int height = y + this.height - caret - 8;

        ScrollPanelElement panel = new ScrollPanelElement(x, caret, this.width, height) {
            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                if (mc.currentScreen instanceof GuiScreen screen) {
                    screen.selected = this;
                }
            }
        };

        caret += 2;
        for (SettingSection section : module.getContents()) {
            if (section.getContents().isEmpty()) {
                continue;
            }
            SettingSectionElement element = new SettingSectionElement(section, margin, caret, width);
            panel.addChild(element);
            caret += element.height + 10;
        }
        this.addChild(panel);
        this.setHeight(Math.min(caret - y, this.height) + 5);
    }

    @Override
    public void onRender(DrawContext context, int mx, int my) {
        int margin = x + 10;
        int caret = y + 20;
        int width = this.width - 20;

        for (String line : TextUtils.wordWrap(description, width)) {
            RenderUtils.drawDefaultText(context, Text.of("§r§o" + line), margin, caret, false, 0xFFD0D0D0);
            caret += 10;
        }
    }

    @Override
    public void onClose() {
        if (mc.currentScreen instanceof GuiScreen screen) {
            screen.removeChild(this);
            Impropers3DMinimap.config.save();
        }
    }

    public SettingContainer getModule() {
        return module;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
