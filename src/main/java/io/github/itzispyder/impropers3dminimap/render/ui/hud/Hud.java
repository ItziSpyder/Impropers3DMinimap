package io.github.itzispyder.impropers3dminimap.render.ui.hud;

import io.github.itzispyder.impropers3dminimap.Global;
import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.render.simulation.SimulationRadar;
import io.github.itzispyder.impropers3dminimap.render.ui.Positionable;
import io.github.itzispyder.impropers3dminimap.util.minecraft.PlayerUtils;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.Window;

import java.util.HashMap;
import java.util.Map;

@Environment(EnvType.CLIENT)
public abstract class Hud implements Positionable, Global {

    private static final Map<Class<? extends Hud>, Hud> registry = new HashMap<>();

    public static Map<Class<? extends Hud>, Hud> huds() {
        return new HashMap<>(registry);
    }

    public static void addHud(Hud hud) {
        if (hud != null)
            registry.put(hud.getClass(), hud);
    }

    public static void removeHud(Hud hud) {
        if (hud != null)
            registry.remove(hud.getClass());
    }

    @SuppressWarnings("unchecked")
    public static <T extends Hud> T get(Class<T> hudClass) {
        return (T)huds().get(hudClass);
    }

    private int x, y, width, height;
    private final String id;

    public Hud(String id, int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.id = id;
    }

    public Hud(String id, int x, int width, int height) {
        this(id, x, 0, width, height);
    }

    public abstract void onRender(DrawContext context);

    public void render(DrawContext context) {
        if (canRender()) {
            this.onRender(context);
        }
    }

    public void renderBackground(DrawContext context) {
        SimulationRadar radar = Impropers3DMinimap.radar;
        int r = radar.borderRadius.getVal();
        RenderUtils.fillRoundRect(context, getX(), getY(), getWidth(), getHeight(), r, 0xFF000000);
    }

    public void renderForeground(DrawContext context) {
        SimulationRadar radar = Impropers3DMinimap.radar;
        int r = radar.borderRadius.getVal();
        RenderUtils.fillRoundRect(context, getX(), getY(), getWidth(), getHeight(), r, system.background.getHex());

        int cx = x + width / 2;
        int cy = y + height / 2;
        RenderUtils.drawLine(context, cx - 5, cy - 5, cx + 5, cy + 5, 0xFFFF0000);
        RenderUtils.drawLine(context, cx + 5, cy - 5, cx - 5, cy + 5, 0xFFFF0000);
    }

    public boolean canRender() {
        if (this instanceof SettingDependent<?> sd && !sd.passesCheck())
            return false;
        return Impropers3DMinimap.radar.isEnabled() && mc.currentScreen == null && PlayerUtils.valid();
    }

    public String getId() {
        return id;
    }

    @Override
    public int getX() {
        return x;
    }

    @Override
    public void setX(int x) {
        this.x = x;
    }

    @Override
    public int getY() {
        return y;
    }

    @Override
    public void setY(int y) {
        this.y = y;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public void setWidth(int width) {
        this.width = width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public Window getWindow() {
        return mc.getWindow();
    }

    @Override
    public void setHeight(int height) {
        this.height = height;
    }
}
