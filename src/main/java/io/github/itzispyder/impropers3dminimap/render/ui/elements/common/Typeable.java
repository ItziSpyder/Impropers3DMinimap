package io.github.itzispyder.impropers3dminimap.render.ui.elements.common;

import io.github.itzispyder.impropers3dminimap.Global;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.util.misc.StringUtils;
import org.lwjgl.glfw.GLFW;

import java.util.function.Function;

public interface Typeable extends Global {

    default void onKey(int key, int scan) {
        if (mc.currentScreen instanceof GuiScreen screen) {
            String typed = GLFW.glfwGetKeyName(key, scan);

            if (key == GLFW.GLFW_KEY_ESCAPE) {
                screen.selected = null;
            }
            else if (key == GLFW.GLFW_KEY_BACKSPACE) {
                onInput(input -> {
                    if (!input.isEmpty()) {
                        return input.substring(0, input.length() - 1);
                    }
                    return input;
                }, false);
            }
            else if (key == GLFW.GLFW_KEY_SPACE) {
                onInput(input -> input.concat(" "), true);
            }
            else if (key == GLFW.GLFW_KEY_V && screen.ctrlKeyPressed) {
                onInput(input -> input.concat(mc.keyboard.getClipboard()), true);
            }
            else if (typed != null){
                onInput(input -> input.concat(screen.shiftKeyPressed ? StringUtils.keyPressWithShift(typed) : typed), true);
            }
        }
    }

    void onInput(Function<String, String> factory, boolean append);
}
