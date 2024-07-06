package io.github.itzispyder.impropers3dminimap;

import io.github.itzispyder.impropers3dminimap.config.Config;
import io.github.itzispyder.impropers3dminimap.render.simulation.SimulationRadar;
import io.github.itzispyder.impropers3dminimap.util.math.Color;
import io.github.itzispyder.impropers3dminimap.util.misc.JsonSerializable;
import io.github.itzispyder.impropers3dminimap.util.misc.Scheduler;
import net.fabricmc.api.ModInitializer;

public class Impropers3DMinimap implements ModInitializer {

    public static final Config config = JsonSerializable.load(Config.PATH, Config.class, new Config());
    public static final SimulationRadar radar = new SimulationRadar();
    public static final Scheduler scheduler = new Scheduler();
    public static final Color accent = new Color(0xFFA434EB);

    @Override
    public void onInitialize() {
        config.save();
    }
}
