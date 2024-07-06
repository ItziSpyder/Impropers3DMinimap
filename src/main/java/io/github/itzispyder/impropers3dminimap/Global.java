package io.github.itzispyder.impropers3dminimap;

import io.github.itzispyder.impropers3dminimap.config.Config;
import net.minecraft.client.MinecraftClient;

public interface Global {

    MinecraftClient mc = MinecraftClient.getInstance();

    default Config getConfig() {
        return Impropers3DMinimap.config;
    }
}
