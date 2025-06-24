package io.github.itzispyder.impropers3dminimap.render.simulation;

import com.mojang.blaze3d.vertex.VertexFormat;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderConstants;
import net.minecraft.client.render.RenderLayer;

public enum SimulationMethod {

    LINES(VertexFormat.DrawMode.DEBUG_LINES, 0xFF, RenderConstants.LINES),
    QUADS(VertexFormat.DrawMode.QUADS, 0x50, RenderConstants.QUADS);

    public final VertexFormat.DrawMode drawMode;
    public final int transparency;
    private final RenderLayer layer;

    SimulationMethod(VertexFormat.DrawMode drawMode, int transparency, RenderLayer layer) {
        this.drawMode = drawMode;
        this.transparency = transparency;
        this.layer = layer;
    }

    public RenderLayer getLayer() {
        return layer;
    }
}