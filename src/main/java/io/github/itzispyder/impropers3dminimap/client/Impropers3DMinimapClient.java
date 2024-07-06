package io.github.itzispyder.impropers3dminimap.client;

import io.github.itzispyder.impropers3dminimap.util.math.Color;
import io.github.itzispyder.impropers3dminimap.util.misc.Scheduler;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.font.TextRenderer;

public class Impropers3DMinimapClient implements ClientModInitializer {

    private static final Impropers3DMinimapClient system = new Impropers3DMinimapClient();
    public static Impropers3DMinimapClient getInstance() {
        return system;
    }

    public TextRenderer textRenderer;
    public final Scheduler scheduler;
    public final Color accent;
    public final Color background;

    public Impropers3DMinimapClient() {
        this.scheduler = new Scheduler();
        this.accent = new Color(0xFF0080B3);
        this.background = new Color(0xB2000000);
    }

    @Override
    public void onInitializeClient() {

    }
}
