package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types;

import io.github.itzispyder.impropers3dminimap.config.types.DictionarySetting;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.DictionaryLookupWindow;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.SettingElement;
import net.minecraft.client.gui.DrawContext;

import static io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils.*;

public class DictionarySettingElement extends SettingElement<DictionarySetting<?>> {

    private boolean editing;
    private int count;

    public DictionarySettingElement(DictionarySetting<?> setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.editing = false;
        this.count = setting.getVal().getTruesCount();
    }

    @Override
    public void onRender(DrawContext context, int mouseX, int mouseY) {
        fillRoundRect(context, x, y, 20, height, 3, 0xFF404040);
        int cX = x + 20 / 2;
        int cY = y + height / 3;
        drawCenteredText(context, "{...}", cX, cY, 0.9F, false);
        drawText(context, "§o" + setting.getName() + ": §r(" + count + ")", x + 22, y + height / 3, 0.9F, false);
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        return rendering && mouseX > x && mouseX < x + 20 && mouseY > y && mouseY < y + 10;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX;
        int my = (int)mouseY;

        if (mc.currentScreen instanceof GuiScreen screen && !editing) {
            editing = true;
            DictionaryLookupWindow window = new DictionaryLookupWindow(mx, my, setting) {
                @Override
                public void onClose() {
                    super.onClose();
                    editing = false;
                    count = setting.getVal().getTrues().size();
                }
            };
            window.boundIn(width(), height());
            screen.addChild(window);
        }
    }
}