package io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive;

import io.github.itzispyder.coherent.gui.ClickType;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiElement;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.KeyPressCallback;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.Typeable;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TextBoxElement extends GuiElement implements Typeable {

    public final List<KeyPressCallback> keyPressCallbacks = new ArrayList<>();
    private String query, defaultText, pattern;
    private boolean selectionBlinking;
    private int selectionBlink;

    public TextBoxElement(int x, int y, int width) {
        super(x, y, width, 12);
        this.query = "";
    }

    public TextBoxElement(int x, int y) {
        this(x, y, 90);
    }

    @Override
    public void onRender(DrawContext context, int mouseX, int mouseY) {
        if (!(mc.currentScreen instanceof GuiScreen screen)) {
           return;
        }

        RenderUtils.fillRect(context, x, y, width, height, 0x20FFFFFF);
        if (screen.selected == this) {
            RenderUtils.drawBox(context, x, y, width, height, 0xFFFFFFFF);
        }

        String text = query;
        while (!text.isEmpty() && mc.textRenderer.getWidth(text) * 0.9F > width - height - 4) {
            text = text.substring(1);
        }

        if (!queryMatchesPattern())
            RenderUtils.drawText(context, "§6" + text, x + height / 2 + 2, y + height / 3, 0.9F, false);
        else if (screen.selected == this && !text.isEmpty())
            RenderUtils.drawText(context, "§f" + text, x + height / 2 + 2, y + height / 3, 0.9F, false);
        else if (!text.isEmpty())
            RenderUtils.drawText(context, "§7" + text, x + height / 2 + 2, y + height / 3, 0.9F, false);
        else {
            String defaultText = getDefaultText();
            StringBuilder sb = new StringBuilder();
            int i = 0;
            while (i < defaultText.length() && mc.textRenderer.getWidth(sb.toString()) * 0.9F < width - height - 4) {
                sb.append(defaultText.charAt(i++));
            }
            RenderUtils.drawText(context, sb.toString(), x + height / 2 + 2, y + height / 3, 0.9F, false);
        }

        if (selectionBlinking) {
            int tx = (int)(x + height / 2 + 2 + mc.textRenderer.getWidth(text) * 0.9);
            int ty = y + 2;
            RenderUtils.drawVerLine(context, tx, ty, height - 4, 0xE0FFFFFF);
        }
    }

    @Override
    public void onKey(int key, int scancode) {
        Typeable.super.onKey(key, scancode);
        for (KeyPressCallback callback : keyPressCallbacks) {
            callback.handleKey(key, ClickType.CLICK, scancode, -1);
        }
    }

    @Override
    public void onInput(Function<String, String> factory, boolean append) {
        query = factory.apply(query);
    }

    @Override
    public void onTick() {
        super.onTick();

        if (mc.currentScreen instanceof GuiScreen screen) {
            if (screen.selected != this) {
                selectionBlinking = false;
                return;
            }

            if (selectionBlink++ >= 20) {
                selectionBlink = 0;
            }
            if (selectionBlink % 10 == 0 && selectionBlink > 0) {
                selectionBlinking = !selectionBlinking;
            }
        }
    }

    public String getQuery() {
        return query;
    }

    public String getLowercaseQuery() {
        return query.toLowerCase();
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getDefaultText() {
        return defaultText == null ? "" : defaultText;
    }

    public void setDefaultText(String defaultText) {
        this.defaultText = defaultText;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public boolean queryMatchesPattern() {
        return pattern == null || query.matches(pattern);
    }
}
