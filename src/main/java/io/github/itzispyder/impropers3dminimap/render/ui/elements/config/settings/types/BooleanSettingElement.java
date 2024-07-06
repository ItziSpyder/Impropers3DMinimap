package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types;

import io.github.itzispyder.impropers3dminimap.config.types.BooleanSetting;
import io.github.itzispyder.impropers3dminimap.render.animation.Animator;
import io.github.itzispyder.impropers3dminimap.render.animation.PollingAnimator;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.SettingElement;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public class BooleanSettingElement extends SettingElement<BooleanSetting> {

    private final Animator ani = new PollingAnimator(150, setting::getVal);
    private boolean round;

    public BooleanSettingElement(BooleanSetting setting, int x, int y, int width, boolean round) {
        super(setting, x, y, width);
        this.round = round;
    }

    public BooleanSettingElement(BooleanSetting setting, int x, int y, int width) {
        this(setting, x, y, width, false);
    }

    @Override
    public void onRender(DrawContext context, int mx, int my) {
        int color = system.accent.getHex();

        if (round) {
            RenderUtils.fillAnnulus(context, x + 4, y + 5, 4, 1, color);
            RenderUtils.fillCircle(context, x + 4, y + 5, 3, Animator.transformColorOpacity(ani, color));
        }
        else {
            RenderUtils.drawRect(context, x, y, 10, 10, color);
            RenderUtils.fillRect(context, x + 2, y + 2, 6, 6, Animator.transformColorOpacity(ani, color));
        }

        RenderUtils.drawText(context, "§o" + setting.getName(), x + 12, y + height / 3, 0.9F, false);
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        setting.setVal(!setting.getVal());
    }

    @Override
    public boolean isHovered(int mouseX, int mouseY) {
        return rendering && mouseX > x && mouseX < x + 10 && mouseY > y && mouseY < y + 10;
    }

    public boolean isRound() {
        return round;
    }

    public void setRound(boolean round) {
        this.round = round;
    }
}
