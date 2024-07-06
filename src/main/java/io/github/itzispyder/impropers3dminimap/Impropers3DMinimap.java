package io.github.itzispyder.impropers3dminimap;

import io.github.itzispyder.impropers3dminimap.config.Config;
import io.github.itzispyder.impropers3dminimap.render.simulation.SimulationRadar;
import io.github.itzispyder.impropers3dminimap.render.ui.screens.ConfigScreen;
import io.github.itzispyder.impropers3dminimap.util.math.Color;
import io.github.itzispyder.impropers3dminimap.util.misc.JsonSerializable;
import io.github.itzispyder.impropers3dminimap.util.misc.Scheduler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Impropers3DMinimap implements ModInitializer {

    public static final Config config = JsonSerializable.load(Config.PATH, Config.class, new Config());
    public static final SimulationRadar radar = new SimulationRadar();
    public static final Scheduler scheduler = new Scheduler();
    public static final Color accent = new Color(0xFFA434EB);

    public static final KeyBinding BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "binds.impropers3dminimap.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "binds.impropers3dminimap"
    ));

    @Override
    public void onInitialize() {
        config.save();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BIND.wasPressed()) {
                client.setScreen(new ConfigScreen());
            }
            radar.onTick();
        });
    }
}
