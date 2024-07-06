package io.github.itzispyder.impropers3dminimap.util.minecraft;

import io.github.itzispyder.impropers3dminimap.Global;
import net.minecraft.client.util.Window;
import org.lwjgl.glfw.GLFW;

import java.awt.*;

public final class InteractionUtils implements Global {

    public static void setCursor(int x, int y) {
        Window win = mc.getWindow();
        int w1 = win.getWidth();
        int w2 = win.getScaledWidth();
        int h1 = win.getHeight();
        int h2 = win.getScaledHeight();
        double ratW = (double)w2 / (double)w1;
        double ratH = (double)h2 / (double)h1;
        GLFW.glfwSetCursorPos(win.getHandle(), x / ratW, y / ratH);
    }

    public static Point getCursor() {
        Window win = mc.getWindow();
        int w1 = win.getWidth();
        int w2 = win.getScaledWidth();
        int h1 = win.getHeight();
        int h2 = win.getScaledHeight();
        double rW = (double)w2 / (double)w1;
        double rH = (double)h2 / (double)h1;
        return new Point((int)(rW * mc.mouse.getX()), (int)(rH * mc.mouse.getY()));
    }
}
