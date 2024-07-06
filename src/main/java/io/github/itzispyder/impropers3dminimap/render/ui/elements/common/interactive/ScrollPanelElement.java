package io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive;

import io.github.itzispyder.impropers3dminimap.render.animation.Animator;
import io.github.itzispyder.impropers3dminimap.render.animation.PollingAnimator;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.util.minecraft.InteractionUtils;
import io.github.itzispyder.impropers3dminimap.util.minecraft.PlayerUtils;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public class ScrollPanelElement extends GuiElement {

    public static final int SCROLL_MULTIPLIER = 15;
    private int remainingUp, remainingDown, limitTop, limitBottom;
    private final Animator topShadow, interpolation;
    private int interpolationLength;

    public ScrollPanelElement(int x, int y, int width, int height) {
        super(x, y, width, height);
        super.setContainer(true);
        remainingUp = remainingDown = 0;
        limitTop = y;
        limitBottom = y + height;
        this.topShadow = new PollingAnimator(150, this::canScrollUp);
        this.interpolation = new Animator(100);
    }

    public boolean canScrollDown() {
        return remainingDown > 0;
    }

    public boolean canScrollUp() {
        return remainingUp > 0;
    }

    public boolean canScroll() {
        return canScrollUp() || canScrollDown();
    }

    public boolean canScrollInDirection(int amount) {
        if (amount >= 0) {
            return canScrollUp();
        }
        else {
            return canScrollDown();
        }
    }

    @Override
    public void addChild(GuiElement child) {
        super.addChild(child);
        updateBounds(child);
        child.scrollOnPanel(this, 0);
    }

    public void updateBounds(GuiElement child) {
        if (child.y < limitTop) {
            limitTop = child.y;
        }
        if (child.y + child.height > limitBottom) {
            limitBottom = child.y + child.height;
        }
        remainingUp = y - limitTop;
        remainingDown = limitBottom - (y + height);
    }

    public void recalculatePositions() {
        remainingUp = remainingDown = 0;
        limitTop = y;
        limitBottom = y + height;

        for (GuiElement child : getChildren()) {
            updateBounds(child);
        }
    }

    @Override
    public void onRender(DrawContext context, int mouseX, int mouseY) {

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY) {
        context.enableScissor(x, y, x + width, y + height);

        float interpolatedDelta = (float)(interpolationLength * interpolation.getProgressClampedReversed());
        context.getMatrices().push();
        context.getMatrices().translate(0, -interpolatedDelta, 0);
        super.render(context, mouseX, mouseY);
        context.getMatrices().pop();

        int gradientHeight = 10;
        int upH = (int)(gradientHeight * topShadow.getProgressClamped());
        RenderUtils.fillVerticalGradient(context, x, y, width, upH, 0x80000000, 0x00000000);

        context.disableScissor();
    }

    @Override
    public void mouseClicked(double mouseX, double mouseY, int button) {
        if (isMouseOver((int)mouseX, (int)mouseY)) {
            onClick(mouseX, mouseY, button);
        }

        for (int i = getChildren().size() - 1; i >= 0; i--) {
            GuiElement child = getChildren().get(i);
            child.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void onScroll(int amount) {
        interpolationLength = scrollWithMultiplier(amount, SCROLL_MULTIPLIER);
        interpolation.reset();
    }

    private int scrollWithMultiplier(int amount, int multiplier) {
        int total = 0;
        for (int i = 0; i < multiplier; i++) {
            total += scrollWithoutMultiplier(amount);
        }
        return total;
    }

    private int scrollWithoutMultiplier(int amount) {
        var c = InteractionUtils.getCursor();
        boolean hovered = isHovered(c.x, c.y) && mc.currentScreen instanceof GuiScreen screen && (PlayerUtils.invalid() || screen.hovered == getParent());

        if (canScrollInDirection(amount) && hovered) {
            for (GuiElement child : getChildren()) {
                child.scrollOnPanel(this, amount);
            }

            remainingDown = remainingDown + amount;
            remainingUp = remainingUp - amount;
            return amount;
        }
        return 0;
    }
}
