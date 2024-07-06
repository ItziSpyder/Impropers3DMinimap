package io.github.itzispyder.impropers3dminimap;

import io.github.itzispyder.impropers3dminimap.config.Config;
import io.github.itzispyder.impropers3dminimap.render.simulation.SimulationRadar;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.Hud;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.moveables.SimulationHud;
import io.github.itzispyder.impropers3dminimap.render.ui.screens.ConfigScreen;
import io.github.itzispyder.impropers3dminimap.util.minecraft.PlayerUtils;
import io.github.itzispyder.impropers3dminimap.util.misc.JsonSerializable;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class Impropers3DMinimap implements ModInitializer, Global {

    public static final Config config = JsonSerializable.load(Config.PATH, Config.class, new Config());
    public static final SimulationRadar radar = new SimulationRadar();

    public static final KeyBinding BIND = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "binds.impropers3dminimap.menu",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_M,
            "binds.impropers3dminimap"
    ));

    @Override
    public void onInitialize() {
        Hud.addHud(new SimulationHud());

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (BIND.wasPressed())
                client.setScreen(new ConfigScreen());
            radar.onTick();
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> {
            if (PlayerUtils.invalid())
                return;
            for (Hud hud : Hud.huds().values())
                hud.render(drawContext);
        });

        config.load();
        config.save();
    }
}
