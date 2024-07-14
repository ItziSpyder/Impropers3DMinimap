package io.github.itzispyder.impropers3dminimap.render.simulation;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.util.math.Color;
import io.github.itzispyder.impropers3dminimap.util.math.MathUtils;
import io.github.itzispyder.impropers3dminimap.util.minecraft.PlayerUtils;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import io.github.itzispyder.impropers3dminimap.util.misc.Dictionary;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class Simulation {

    private final ClientPlayerEntity player;
    private final SimulationRenderer renderer;
    private final BlockView world;
    private Vec3d focalPoint;
    private int x, y, width, height;
    private float mapScale;
    private SimulationMethod method;
    public double zoomDelta;

    public Simulation(ClientPlayerEntity player, int x, int y, int w, int h, float mapScale, SimulationMethod method, long focalLength) {
        this.focalPoint = new Vec3d(x + w / 2.0, y + h / 2.0, focalLength);
        this.player = player;
        this.world = player.getWorld();
        this.renderer = new SimulationRenderer(this);
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.mapScale = mapScale;
        this.method = method;
    }


    public void render(DrawContext context, Vec3d camera, Quaternionf rotation, boolean renderBackground, int borderRadius, int accentColor) {
        int r = borderRadius;
        zoomDelta = Impropers3DMinimap.radar.zoomAnimator.getAnimation();

        int x = this.x;
        int y = this.y;
        int width = this.width;
        int height = this.height;
        int winW = RenderUtils.width();
        int winH = RenderUtils.height();

        if (zoomDelta > 0) {
            int destW = 420;
            int destH = 240;
            int destX = (winW - destW) / 2;
            int destY = (winH - destH) / 2;

            x = (int) MathUtils.lerpClamped(x, destX, zoomDelta);
            y = (int) MathUtils.lerpClamped(y, destY, zoomDelta);
            width = (int) MathUtils.lerpClamped(width, destW, zoomDelta);
            height = (int) MathUtils.lerpClamped(height, destH, zoomDelta);
        }

        if (renderBackground) {
            RenderUtils.fillRoundRect(context, x, y, width, height, r, accentColor);
            RenderUtils.fillRect(context, x + r, y + r, width - r * 2, height - r * 2, Color.BLACK.getHex());
        }

        if (renderer.worldSize() > 0) {
            Vec3d focal = this.focalPoint;
            if (zoomDelta > 0)
                focal = new Vec3d(MathUtils.lerpClamped(focal.x, winW / 2F, zoomDelta), MathUtils.lerpClamped(focal.y, winH / 2F, zoomDelta), focal.z);

            context.enableScissor(x + r, y + r, x + width - r, y + height - r);
            renderer.render(context, camera, focal, rotation, mapScale, zoomDelta);
            context.disableScissor();
        }

        renderMapViewer(context, x + width / 2, y + height / 2 + 2, 12);
    }

    public void renderMapViewer(DrawContext context, int x, int y, int size) {
        BufferBuilder buf = RenderUtils.getBuffer(VertexFormat.DrawMode.TRIANGLE_STRIP, VertexFormats.POSITION_COLOR);
        Matrix4f mat = context.getMatrices().peek().getPositionMatrix();

        buf.vertex(mat, x, y + 3, 0).color(0xFFFF0000); // center
        buf.vertex(mat, x - size / 2F, y + size / 2F, 0).color(0xFFFF0000); // bottom left
        buf.vertex(mat, x, y - size / 2F, 0).color(0xFFFF0000); // top center
        buf.vertex(mat, x, y + 3, 0).color(0xFF800000); // center
        buf.vertex(mat, x + size / 2F, y + size / 2F, 0).color(0xFF800000); // bottom right

        RenderUtils.beginRendering();
        RenderUtils.drawBuffer(buf);
        RenderUtils.finishRendering();
    }

    public void update(int radius, boolean useMapColors, Dictionary<Block> targets) {
        renderer.clear();
        Box box = player.getBoundingBox().expand(radius);

        for (double x = box.minX; x <= box.maxX; x++) {
            for (double y = box.minY; y <= box.maxY; y++) {
                for (double z = box.minZ; z <= box.maxZ; z++) {
                    BlockPos pos = new BlockPos((int)Math.floor(x), (int)Math.floor(y), (int)Math.floor(z));
                    update(pos, world.getBlockState(pos), useMapColors, targets);
                }
            }
        }
    }

    public void updateEntities(int radius, Dictionary<EntityType<?>> targets) {
        for (Entity ent : player.clientWorld.getEntities())
            if (ent != null && ent.isAlive() && !ent.isSpectator() && ent.distanceTo(player) <= radius)
                if (ent != PlayerUtils.player() && targets.lookup(ent.getType()))
                    update(ent);
    }

    public void update(BlockPos pos, BlockState state, boolean useMapColors, Dictionary<Block> targets) {
        renderer.update(world, pos, state, useMapColors, targets.lookup(state.getBlock()));
    }

    public void update(Entity ent) {
        renderer.update(ent);
    }

    public Vec2f projectVector(Vec3d vec) {
        return projectVector(vec.x, vec.y, vec.z);
    }

    public Vec2f projectVector(Vec3d vec, Quaternionf rotation, Vec3d origin) {
        Vector3f transform = vec.subtract(origin).toVector3f();
        transform = rotation.transform(transform).add(origin.toVector3f());
        return projectVector(transform.x, transform.y, transform.z);
    }

    public Vec2f projectVector(Vec3d vec, Quaternionf rotation) {
        Vector3f transform = rotation.transform(vec.toVector3f());
        return projectVector(transform.x, transform.y, transform.z);
    }

    public Vec2f projectVector(double x, double y, double z) {
        double focal = -MathUtils.lerpClamped(focalPoint.z, 10, zoomDelta);
        double depth = focal + z;
        if (depth >= -0.1)
            depth = -0.1;

        float px = (float)(focal * x / depth);
        float py = (float)(focal * y / depth);
        //System.out.printf("PROJECT [%s, %s, %s] -> [%s, %s]%n".formatted((int)x, (int)y, (int)z, (int)px, (int)py));
        return new Vec2f(px, py);
    }

    public Vec3d getFocalPoint() {
        return focalPoint;
    }

    public void setFocalLength(double length) {
        focalPoint = new Vec3d(focalPoint.x, focalPoint.y, length);
    }

    public SimulationRenderer getRenderer() {
        return renderer;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
        this.focalPoint = new Vec3d(x + width / 2.0, y + height / 2.0, focalPoint.z);
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
        this.focalPoint = new Vec3d(x + width / 2.0, y + height / 2.0, focalPoint.z);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
        this.focalPoint = new Vec3d(x + width / 2.0, y + height / 2.0, focalPoint.z);
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
        this.focalPoint = new Vec3d(x + width / 2.0, y + height / 2.0, focalPoint.z);
    }

    public void setDimensions(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.focalPoint = new Vec3d(x + width / 2.0, y + height / 2.0, focalPoint.z);
    }

    public SimulationMethod getMethod() {
        return method;
    }

    public void setMethod(SimulationMethod method) {
        this.method = method;
    }

    public float getMapScale() {
        return mapScale;
    }

    public void setMapScale(float mapScale) {
        this.mapScale = mapScale;
    }

    public boolean outOfBounds(Vec2f v1, Vec2f v2) {
        return outOfBounds(v1.x, v1.y, v2.x, v2.y);
    }

    public boolean outOfBounds(Vec2f v1, Vec2f v2, Vec2f v3, Vec2f v4) {
        return outOfBounds(v1) && outOfBounds(v2) && outOfBounds(v3) && outOfBounds(v4);
    }

    public boolean outOfBounds(float x1, float y1, float x2, float y2) {
        return outOfBounds(x1, y1) && outOfBounds(x2, y2);
    }

    public boolean outOfBounds(Vec2f v) {
        return outOfBounds(v.x, v.y);
    }

    public boolean outOfBounds(float x, float y) {
        int ox = this.x + this.width / 2;
        int oy = this.y + this.height / 2;
        return x < this.x - ox || y < this.y - oy || x > this.x + this.width - ox || y > this.y + this.height - oy;
    }

    public float withScale(float value, float scale) {
        return value / scale - value;
    }
}