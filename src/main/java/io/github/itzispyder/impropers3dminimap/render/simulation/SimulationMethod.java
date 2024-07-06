package io.github.itzispyder.impropers3dminimap.render.simulation;

import net.minecraft.client.render.VertexFormat;

public enum SimulationMethod {

    LINES(VertexFormat.DrawMode.DEBUG_LINES, 0xFF),
    QUADS(VertexFormat.DrawMode.QUADS, 0x50);

    public final VertexFormat.DrawMode drawMode;
    public final int transparency;

    SimulationMethod(VertexFormat.DrawMode drawMode, int transparency) {
        this.drawMode = drawMode;
        this.transparency = transparency;
    }
}