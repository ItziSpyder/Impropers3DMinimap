package io.github.itzispyder.impropers3dminimap.render.ui.hud.moveables;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.config.Setting;
import io.github.itzispyder.impropers3dminimap.render.simulation.Simulation;
import io.github.itzispyder.impropers3dminimap.render.simulation.SimulationRadar;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.Hud;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.SettingDependent;
import io.github.itzispyder.impropers3dminimap.util.minecraft.PlayerUtils;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.gui.DrawContext;

public class SimulationHud extends Hud implements SettingDependent<Boolean> {

    public SimulationHud() {
        super("simulation-hud", 100, 10, 200, 100);
    }

    @Override
    public void onRender(DrawContext context) {
        SimulationRadar radar = Impropers3DMinimap.radar;
        Simulation simulation = radar.getSimulation();

        if (simulation == null || mc.currentScreen != null) {
            RenderUtils.fillRoundRect(context, getX(), getY(), getWidth(), getHeight(), 5, 0xFF000000);
            RenderUtils.drawRoundRect(context, getX(), getY(), getWidth(), getHeight(), 5, 0xFFFFFFFF);
        }
        else {
            radar.renderHud(context);
        }

        if (!passesCheck())
            renderForeground(context);
    }

    @Override
    public Setting<Boolean> provideSetting() {
        return Impropers3DMinimap.radar.enabled;
    }

    @Override
    public boolean checkSetting(Setting<Boolean> setting) {
        return setting.getVal();
    }

    @Override
    public boolean canRender() {
        return mc.currentScreen == null && PlayerUtils.valid() && passesCheck();
    }
}
