package io.github.itzispyder.impropers3dminimap.render.simulation;

import com.mojang.blaze3d.systems.RenderSystem;
import io.github.itzispyder.impropers3dminimap.render.animation.Animator;
import io.github.itzispyder.impropers3dminimap.util.math.Color;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.*;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.lwjgl.opengl.GL11;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimulationRenderer {

    private final Simulation simulation;
    private final Map<ChunkPos, Map<BlockPos, SimulatedBlock>> blocks, highlightedBlocks;
    private final Map<Integer, Entity> entities;
    private final Animator radarPingAnimator;

    public SimulationRenderer(Simulation simulation) {
        this.simulation = simulation;
        this.blocks = new ConcurrentHashMap<>();
        this.highlightedBlocks = new ConcurrentHashMap<>();
        this.entities = new HashMap<>();
        this.radarPingAnimator = new Animator(1000);
    }

    public void render(DrawContext context, Vec3d camera, Quaternionf rotation, float scale) {
        Vec3d focalPoint = simulation.getFocalPoint();
        int originX = (int) focalPoint.x;
        int originY = (int) focalPoint.y;

        MatrixStack matrices = context.getMatrices();

        matrices.push();
        matrices.translate(originX, originY, 0);
        matrices.scale(scale, -scale, scale);

        renderWorld(context.getMatrices(), camera, rotation);
        renderWorldHighlighted(context.getMatrices(), camera, rotation);
        renderEntities(context, camera, rotation);

        matrices.pop();
    }

    public void renderWorld(MatrixStack matrices, Vec3d camera, Quaternionf rotation) {
        BufferBuilder buf = RenderUtils.getBuffer(simulation.getMethod().drawMode, VertexFormats.POSITION_COLOR);
        Matrix4f mat = matrices.peek().getPositionMatrix();

        for (var chunk : blocks.values())
            for (var block : chunk.values())
                block.render(mat, buf, simulation, camera, rotation);

        BuiltBuffer draw = buf.endNullable();
        boolean empty = draw == null;

        if (empty)
            return;

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        BufferRenderer.drawWithGlobalProgram(draw);

        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    }

    public void renderWorldHighlighted(MatrixStack matrices, Vec3d camera, Quaternionf rotation) {
        BufferBuilder buf = RenderUtils.getBuffer(VertexFormat.DrawMode.DEBUG_LINES, VertexFormats.POSITION_COLOR);
        Matrix4f mat = matrices.peek().getPositionMatrix();

        for (var chunk : highlightedBlocks.values())
            for (var block : chunk.values())
                block.render(mat, buf, simulation, camera, rotation);

        BuiltBuffer draw = buf.endNullable();
        boolean empty = draw == null;

        if (empty)
            return;

        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.setShader(GameRenderer::getPositionColorProgram);
        RenderSystem.setShaderColor(1, 1, 1, 1);
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        BufferRenderer.drawWithGlobalProgram(draw);

        RenderSystem.disableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShader(GameRenderer::getPositionTexProgram);
    }

    public void renderEntities(DrawContext context, Vec3d camera, Quaternionf rotation) {
        if (radarPingAnimator.isFinished()) {
            radarPingAnimator.reverse();
            radarPingAnimator.reset();
        }

        float animation = (float)radarPingAnimator.getAnimation();
        float radius = 1.0F;

        for (Entity ent : entities.values()) {
            Vec2f pos = simulation.projectVector(ent.getPos().subtract(camera), rotation);
            Color color = simulation.outOfBounds(pos.x, pos.y) ? Color.ORANGE : Color.RED;
            fillCircle(context, pos.x, pos.y, 0.5F + radius * animation, color.getHexCustomAlpha(0.5));
            fillCircle(context, pos.x, pos.y, 0.333F, color.getHex());
        }
    }

    public static void fillCircle(DrawContext context, float cX, float cY, float radius, int color) {
        BufferBuilder buf = RenderUtils.getBuffer(VertexFormat.DrawMode.TRIANGLE_FAN, VertexFormats.POSITION_COLOR);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, cX, cY, 0).color(color);

        for (int i = 0; i <= 360; i ++) {
            double angle = Math.toRadians(i);
            float x = (float)(Math.cos(angle) * radius) + cX;
            float y = (float)(Math.sin(angle) * radius) + cY;
            buf.vertex(mat, x, y, 0).color(color);
        }

        RenderUtils.beginRendering();
        RenderUtils.drawBuffer(buf);
        RenderUtils.finishRendering();
    }

    public synchronized void update(BlockView world, BlockPos pos, boolean useMapColors, boolean highlight) {
        if (pos != null && world != null) {
            SimulatedBlock block = new SimulatedBlock(world, pos, useMapColors, simulation.getMethod(), highlight);
            if (!block.isValid())
                return;

            ChunkPos chunkPos = ChunkSectionPos.from(pos).toChunkPos();
            var chunk = highlight ? highlightedBlocks : blocks;
            var blocks = chunk.computeIfAbsent(chunkPos, cp -> new ConcurrentHashMap<>());
            blocks.put(pos, block);
        }
    }

    public synchronized void update(BlockView world, BlockPos pos, BlockState state, boolean useMapColors, boolean highlight) {
        if (pos != null && world != null && state != null) {
            SimulatedBlock block = new SimulatedBlock(world, pos, state, useMapColors, simulation.getMethod(), highlight);
            if (!block.isValid())
                return;

            ChunkPos chunkPos = ChunkSectionPos.from(pos).toChunkPos();
            var chunk = highlight ? highlightedBlocks : blocks;
            var blocks = chunk.computeIfAbsent(chunkPos, cp -> new ConcurrentHashMap<>());
            blocks.put(pos, block);
        }
    }

    public synchronized void update(Entity entity) {
        if (entity != null && entity.isAlive() && !entity.isSpectator())
            entities.put(entity.getId(), entity);
    }

    public void clearWorld() {
        blocks.clear();
        highlightedBlocks.clear();
    }

    public void clearEntities() {
        entities.clear();
    }

    public void clear() {
        clearEntities();
        clearWorld();
    }

    public int worldSize() {
        return blocks.size() + highlightedBlocks.size();
    }

    public int entityCount() {
        return entities.size();
    }
}