package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types;

import io.github.itzispyder.impropers3dminimap.config.types.IntegerSetting;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive.TextBoxElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.SettingElement;
import io.github.itzispyder.impropers3dminimap.util.math.MathUtils;
import net.minecraft.client.gui.DrawContext;

import static io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils.*;

public class IntegerSettingElement extends SettingElement<IntegerSetting> {

    private int fillEnd;
    private final int sliderWidth;
    private final TextBoxElement textBox;

    public IntegerSettingElement(IntegerSetting setting, int x, int y, int width) {
        super(setting, x, y, width);
        this.setHeight(16);
        this.sliderWidth = width / 4 * 3;
        this.fillEnd = x + sliderWidth;

        this.textBox = new TextBoxElement(fillEnd + 7, y + 8, width / 4 - 7) {
            {
                this.setPattern("^-?\\d+$");
                this.setQuery(String.valueOf(setting.getVal()));
                this.keyPressCallbacks.add((key, click, scancode, modifiers) -> {
                    if (this.queryMatchesPattern() && canParseInt())
                        setting.setVal(Integer.parseInt(this.getQuery()));
                });
            }

            @Override
            public boolean queryMatchesPattern() {
                boolean bl = super.queryMatchesPattern();
                return bl && canParseInt();
            }

            private boolean canParseInt() {
                try {
                    Integer.parseInt(this.getQuery());
                    return true;
                }
                catch (Exception ex) {
                    return false;
                }
            }
        };
        this.addChild(textBox);
    }

    @Override
    public void onRender(DrawContext context, int mx, int my) {
        int settingMin = setting.getMin();
        int settingMax = setting.getMax();

        if (mc.currentScreen instanceof GuiScreen screen && screen.selected == this) {
            this.fillEnd = MathUtils.clamp(mx, x, x + sliderWidth);
            int range = settingMax - settingMin;
            double ratio = (double)(fillEnd - x) / (double)sliderWidth;
            double value = range * ratio;
            int result = (int)(value + settingMin);

            setting.setVal(result);
            textBox.setQuery(String.valueOf(result));
        }

        double range = settingMax - settingMin;
        double value = setting.getVal() - settingMin;
        double ratio = value / range;

        setting.setVal((int)(range * ratio + settingMin));

        int len = (int)(sliderWidth * MathUtils.clamp(ratio, 0, 1));
        this.fillEnd = x + len;
        int color = system.accent.getHex();

        drawText(context, "§o" + setting.getName(), x, y + 10 / 3, 0.9F, false);
        fillRect(context, x, y + 10 + 10 / 3, sliderWidth, 2, 0xFF808080);
        fillRect(context, x, y + 10 + 10 / 3, len, 2, color);
        fillCircle(context, fillEnd, y + 10 + 10 / 3 + 1, 4, color);
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        return rendering && mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height + 5;
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        if (!(mc.currentScreen instanceof GuiScreen screen))
            return;

        boolean hoveringSlider = rendering && mouseX > x && mouseX < x + sliderWidth && mouseY > y && mouseY < y + height + 5;
        if (hoveringSlider) {
            screen.selected = this;
        }
        else {
            screen.selected = getChildren().get(0);
        }
    }
}