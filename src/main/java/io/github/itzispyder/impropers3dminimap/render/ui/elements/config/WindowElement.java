package io.github.itzispyder.impropers3dminimap.render.ui.elements.config;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.render.animation.Animations;
import io.github.itzispyder.impropers3dminimap.render.animation.Animator;
import io.github.itzispyder.impropers3dminimap.render.simulation.SimulationRadar;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.util.math.Color;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public abstract class WindowElement extends GuiElement {

    protected final Animator animator = new Animator(200, Animations.UPWARDS_BOUNCE);
    private final String title;
    private boolean canAnimate;

    public WindowElement(String title, int x, int y, int width, int height) {
        super(x, y, width, height);
        this.title = title;
        this.setDraggable(true);
        this.canAnimate = true;
    }

    @Override
    public void render(DrawContext context, int mx, int my) {
        boolean canAnimate = canAnimate();

        if (canAnimate) {
            float scale = (float) animator.getAnimation();
            int x = this.x + this.width / 2;
            int y = this.y + this.height / 2;
            context.getMatrices().push();
            context.getMatrices().scale(scale, scale, 1);
            context.getMatrices().translate(x / scale - x, y / scale - y, 0);
        }

        SimulationRadar radar = Impropers3DMinimap.radar;

        int r = radar.borderRadius.getDef();
        RenderUtils.fillRoundTabBottom(context, x, y + 16, width, height - 16, r, system.background.getHex());

        renderTitleBar(context, mx, my);
        super.render(context, mx, my);

        Color color1 = Color.AQUA;
        Color color2 = Color.MAGENTA;
        int c11 = color1.getHex();
        int c12 = color2.getHex();
        int c01 = color1.getHexCustomAlpha(0.0);
        int c02 = color2.getHexCustomAlpha(0.0);
        RenderUtils.fillRoundShadowGradient(context, x, y, width, height, r, 3, c11, c02, c12, c01, c11, c02, c12, c01);

        if (canAnimate)
            context.getMatrices().pop();
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        int mx = (int)mouseX;
        int my = (int)mouseY;

        if (mc.currentScreen instanceof GuiScreen screen) { // bring forward
            screen.removeChild(this);
            screen.addChild(this);
        }
        if (isHoveringClose(mx, my)) {
            close();
        }
    }

    public abstract void onClose();

    public void close() {
        animator.reverse();
        animator.reset();
        system.scheduler.runDelayedTask(this::onClose, animator.getLength());
    }

    public boolean canAnimate() {
        return canAnimate;
    }

    public void setCanAnimate(boolean canAnimate) {
        this.canAnimate = canAnimate;
    }

    public Animator getAnimator() {
        return animator;
    }

    public void renderTitleBar(DrawContext context, int mx, int my) {
        int r = Impropers3DMinimap.radar.borderRadius.getVal();
        int accent = system.accent.getHex();
        RenderUtils.fillRoundTabTop(context, x, y, width, 16 - r, r, accent);
        RenderUtils.fillRect(context, x, y + r, width, 16 - r, accent);

        if (isHoveringClose(mx, my) && mc.currentScreen instanceof GuiScreen screen && screen.hovered == this) {
            RenderUtils.fillArc(context, x + width - r, y + r, r, 0, 90, 0xFFFF0000);
            RenderUtils.fillRect(context, x + width - 16, y, 16 - r, 16 - r, 0xFFFF0000);
            RenderUtils.fillRect(context, x + width - 16, y + r, 16, 16 - r, 0xFFFF0000);
        }

        RenderUtils.drawText(context, title, x + 10, y + 16 / 3, false);

        int cx = x + width - 16 / 2;
        int cy = y + 16 / 2;
        RenderUtils.drawLine(context, cx - 3, cy - 3, cx + 3, cy + 3, 0xFFFFFFFF);
        RenderUtils.drawLine(context, cx + 3, cy - 3, cx - 3, cy + 3, 0xFFFFFFFF);
    }

    public boolean isHoveringTitle(int mx, int my) {
        int tx = x;
        int ty = y;
        int tw = width;
        int th = 16;
        return mx > tx && mx < tx + tw && my > ty && my < ty + th;
    }

    public boolean isHoveringClose(int mx, int my) {
        int tx = x + width - 16;
        int ty = y;
        int tw = 16;
        int th = 16;
        return mx > tx && mx < tx + tw && my > ty && my < ty + th;
    }

    public String getTitle() {
        return title;
    }
}
