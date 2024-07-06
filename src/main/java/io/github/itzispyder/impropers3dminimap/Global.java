package io.github.itzispyder.impropers3dminimap;

import io.github.itzispyder.impropers3dminimap.client.Impropers3DMinimapClient;
import io.github.itzispyder.impropers3dminimap.config.Config;
import net.minecraft.client.MinecraftClient;

public interface Global {

    MinecraftClient mc = MinecraftClient.getInstance();
    Impropers3DMinimapClient system = Impropers3DMinimapClient.getInstance();

    String modId = "impropers3dminimap";

    default Config getConfig() {
        return Impropers3DMinimap.config;
    }
}
