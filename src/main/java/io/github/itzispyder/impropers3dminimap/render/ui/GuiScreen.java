package io.github.itzispyder.impropers3dminimap.render.ui;

import io.github.itzispyder.impropers3dminimap.Global;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.Typeable;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive.ScrollPanelElement;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import io.github.itzispyder.impropers3dminimap.util.misc.Pair;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;
import java.util.function.Consumer;

public abstract class GuiScreen extends Screen implements Global {

    public final List<GuiElement> children;
    public GuiElement selected, hovered, mostRecentlyAdded;
    public long lastHover;
    public boolean shiftKeyPressed, altKeyPressed, ctrlKeyPressed;
    public Pair<Integer, Integer> cursor;

    public GuiScreen(String title) {
        super(Text.literal(title));

        this.lastHover = System.currentTimeMillis();
        this.children = new ArrayList<>();
        this.selected = null;
        this.mostRecentlyAdded = null;
        this.cursor = Pair.of(0, 0);
    }

    public static boolean matchCurrent(Class<? extends GuiScreen> type) {
        return mc.currentScreen != null && mc.currentScreen.getClass() == type;
    }

    public abstract void baseRender(DrawContext context, int mouseX, int mouseY, float delta);

    @Override
    public void tick() {
        new ArrayList<>(this.children).forEach(GuiElement::onTick);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        if (selected != null && selected.isDraggable()) {
            int dx = mouseX - cursor.left;
            int dy = mouseY - cursor.right;
            selected.move(dx, dy);
            selected.boundIn(context.getScaledWindowWidth(), context.getScaledWindowHeight());
            this.cursor = Pair.of(mouseX, mouseY);
        }

        super.render(context, mouseX, mouseY, delta);
        this.baseRender(context, mouseX, mouseY, delta);

        try {
            var children = new ArrayList<>(this.children);
            for (GuiElement guiElement : children) {
                guiElement.render(context, mouseX, mouseY);
            }
        }
        catch (ConcurrentModificationException ignore) {}

        GuiElement element = getHoveredElement(mouseX, mouseY);

        if (hovered != element) {
            hovered = element;
            lastHover = System.currentTimeMillis();
        }
    }

    @Override
    public void renderBackground(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean isDown = button == 0;
        for (int i = children.size() - 1; i >= 0; i--) {
            GuiElement child = children.get(i);
            if (child.isMouseOver((int)mouseX, (int)mouseY)) {
                if (isDown) {
                    this.selected = child;
                    this.cursor = Pair.of((int)mouseX, (int)mouseY);
                }
                child.mouseClicked(mouseX, mouseY, button);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);

        if (!(selected instanceof Typeable)) {
            this.selected = null;
        }

        for (int i = children.size() - 1; i >= 0; i--) {
            GuiElement child = children.get(i);
            if (child.isMouseOver((int)mouseX, (int)mouseY)) {
                child.mouseReleased(mouseX, mouseY, button);
                break;
            }
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);

        for (GuiElement child : children) {
            if (child.isMouseOver((int)mouseX, (int)mouseY)) {
                child.mouseScrolled(mouseX, mouseY, (int)verticalAmount);
                if (child instanceof ScrollPanelElement panel) {
                    panel.onScroll((int)verticalAmount);
                }
            }
            scrollAt(child, (int)mouseX, (int)mouseY, verticalAmount);
        }

        return true;
    }

    private void scrollAt(GuiElement element, int mouseX, int mouseY, double amount) {
        if (element instanceof ScrollPanelElement panel && panel.isMouseOver(mouseX, mouseY)) {
            panel.onScroll((int)amount);
            return;
        }

        for (GuiElement child : element.getChildren()) {
            scrollAt(child, mouseX, mouseY, amount);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.shiftKeyPressed = true;
        }
        else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            this.altKeyPressed = true;
        }
        else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            this.ctrlKeyPressed = true;
        }

        super.keyPressed(keyCode, scanCode, modifiers);

        if (selected instanceof Typeable typeable) {
            typeable.onKey(keyCode, scanCode);
        }

        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT_SHIFT || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            this.shiftKeyPressed = false;
        }
        else if (keyCode == GLFW.GLFW_KEY_LEFT_ALT || keyCode == GLFW.GLFW_KEY_RIGHT_ALT) {
            this.altKeyPressed = false;
        }
        else if (keyCode == GLFW.GLFW_KEY_LEFT_CONTROL || keyCode == GLFW.GLFW_KEY_RIGHT_CONTROL) {
            this.ctrlKeyPressed = false;
        }

        super.keyReleased(keyCode, scanCode, modifiers);
        return true;
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return !(selected instanceof Typeable);
    }

    public List<GuiElement> getChildren() {
        return children;
    }

    public void clearChildren() {
        children.clear();
    }

    public void forEachChild(Consumer<GuiElement> action) {
        children.forEach(action);
    }

    public void addChild(GuiElement child) {
        if (child != null) {
            mostRecentlyAdded = child;
            children.add(child);
        }
    }

    public void removeChild(GuiElement child) {
        children.remove(child);
    }

    public void tagGuiElement(DrawContext context, int mouseX, int mouseY, GuiElement element) {
        String name = element.getClass().getSimpleName();
        double textScale = 0.7;
        int width = mc.textRenderer.getWidth(name) + 2;
        RenderUtils.fillRect(context, mouseX, mouseY, (int)(width * textScale), 9, 0xFF000000);
        RenderUtils.drawText(context, name, mouseX + 2, mouseY + (int)(9 * 0.33), 0.7F, true);
    }

    public GuiElement getHoveredElement(double mouseX, double mouseY) {
        for (int i = children.size() - 1; i >= 0; i--) {
            GuiElement child = children.get(i);
            if (child.isContainer() ? child.isMouseOver((int)mouseX, (int)mouseY) : child.isHovered((int)mouseX, (int)mouseY)) {
                if (child.isContainer()) {
                    GuiElement deepChild = child.getHoveredElement(mouseX, mouseY);
                    return deepChild != null ? deepChild : child;
                }
                return child;
            }
        }
        return null;
    }
}
