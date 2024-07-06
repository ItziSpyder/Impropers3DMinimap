package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types;

import io.github.itzispyder.impropers3dminimap.config.types.EnumSetting;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.ModeSelectionWindow;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.SettingElement;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import io.github.itzispyder.impropers3dminimap.util.misc.StringUtils;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import static io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils.height;
import static io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils.width;

public class EnumSettingElement<T extends Enum<?>> extends SettingElement<EnumSetting<T>> {

    private boolean editing;

    public EnumSettingElement(EnumSetting<T> setting, int x, int y, int w) {
        super(setting, x, y, w);
        this.editing = false;
    }

    @Override
    public void onRender(DrawContext context, int mx, int my) {
        boolean hover = isHovered(mx, my) && mc.currentScreen instanceof GuiScreen screen && screen.hovered == getParent().getParent().getParent();
        int fill = hover ? 0xFF808080 : 0xFF404040;
        int width = this.width / 3;
        Text name = Text.of(StringUtils.capitalizeWords(setting.getVal().name()));

        RenderUtils.fillRoundRect(context, x, y, width, height, 3, fill);
        RenderUtils.drawDefaultCenteredScaledText(context, name, x + width / 2, y + height / 3, 0.9F, false, 0xFFFFFFFF);
        RenderUtils.drawText(context, "§o" + setting.getName(), x + width + 2, y + height / 3, 0.9F, false);
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        return rendering && mouseX > x && mouseX < x + width / 3 && mouseY > y && mouseY < y + 10;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX;
        int my = (int)mouseY;

        if (mc.currentScreen instanceof GuiScreen screen && !editing) {
            editing = true;
            ModeSelectionWindow window = new ModeSelectionWindow(mx, my, setting) {
                @Override
                public void onClose() {
                    super.onClose();
                    editing = false;
                }
            };
            window.boundIn(width(), height());
            screen.addChild(window);
        }
    }
}
