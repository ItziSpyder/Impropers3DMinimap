package io.github.itzispyder.impropers3dminimap.render.simulation;

import io.github.itzispyder.impropers3dminimap.util.minecraft.PlayerUtils;
import net.minecraft.block.BlockState;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.BlockView;
import org.joml.Matrix4f;
import org.joml.Quaternionf;

import java.util.List;

public class SimulatedBlock {

    private final List<Box> collisions;
    private final BlockView world;
    private final BlockState block;
    private final BlockPos pos;
    private final int color;
    private final boolean cullUp, cullDown, cullWest, cullEast, cullNorth, cullSouth;
    private final boolean highlight;

    public SimulatedBlock(BlockView world, BlockPos pos, boolean useMapColors, SimulationMethod method, boolean highlight) {
        this(world, pos, world.getBlockState(pos), useMapColors, method, highlight);
    }

    public SimulatedBlock(BlockView world, BlockPos pos, BlockState state, boolean useMapColors, SimulationMethod method, boolean highlight) {
        this.world = world;
        this.pos = pos;
        this.block = state;
        this.collisions = state.getCollisionShape(world, pos).getBoundingBoxes();
        this.highlight = highlight;

        boolean fullBlock = collisions.size() == 1 && collisions.getFirst().getAverageSideLength() == 1;

        this.cullUp = !highlight && fullBlock && shouldCullNeighbor(world, pos.add(0, 1, 0));
        this.cullDown = !highlight && fullBlock && shouldCullNeighbor(world, pos.add(0, -1, 0));
        this.cullWest = !highlight && fullBlock && shouldCullNeighbor(world, pos.add(-1, 0, 0));
        this.cullEast = !highlight && fullBlock && shouldCullNeighbor(world, pos.add(1, 0, 0));
        this.cullNorth = !highlight && fullBlock && shouldCullNeighbor(world, pos.add(0, 0, 1));
        this.cullSouth = !highlight && fullBlock && shouldCullNeighbor(world, pos.add(0, 0, -1));

        if (highlight) {
            this.color = 0xFFFC552B;
            return;
        }
        if (useMapColors) {
            this.color = method.transparency << 24 | state.getMapColor(world, pos).color;
            return;
        }

        if (block.hasBlockEntity())
            this.color = 0xFF03FC0F;
        else if (fullBlock)
            this.color = PlayerUtils.valid() && pos.getY() == PlayerUtils.player().getBlockY() ? 0xFFC0C0C0 : 0xFF808080;
        else
            this.color = 0xFFFFFFFF;
    }

    public void render(Matrix4f position, BufferBuilder vertexConsumer, Simulation simulation, Vec3d camera, Quaternionf rotation) {
        Vec3d pos = getOffsetPos(camera);

        for (Box box : collisions) {
            SimulationMethod method = simulation.getMethod();
            if (method == SimulationMethod.LINES || highlight)
                boxLines(position, vertexConsumer, simulation, box, pos, rotation);
            else if (method == SimulationMethod.QUADS)
                boxQuads(position, vertexConsumer, simulation, box, pos, rotation);
        }
    }

    private void boxLines(Matrix4f position, BufferBuilder vertexConsumer, Simulation simulation, Box box, Vec3d offset, Quaternionf rotation) {
        float x1 = (float) (box.minX + offset.x);
        float y1 = (float) (box.minY + offset.y);
        float z1 = (float) (box.minZ + offset.z);
        float x2 = (float) (box.maxX + offset.x);
        float y2 = (float) (box.maxY + offset.y);
        float z2 = (float) (box.maxZ + offset.z);

        line(position, vertexConsumer, simulation, rotation,   x1, y1, z1,   x2, y1, z1); // bottom 4
        line(position, vertexConsumer, simulation, rotation,   x2, y1, z1,   x2, y1, z2);
        line(position, vertexConsumer, simulation, rotation,   x2, y1, z2,   x1, y1, z2);
        line(position, vertexConsumer, simulation, rotation,   x1, y1, z2,   x1, y1, z1);

        line(position, vertexConsumer, simulation, rotation,   x1, y2, z1,   x2, y2, z1); // top 4
        line(position, vertexConsumer, simulation, rotation,   x2, y2, z1,   x2, y2, z2);
        line(position, vertexConsumer, simulation, rotation,   x2, y2, z2,   x1, y2, z2);
        line(position, vertexConsumer, simulation, rotation,   x1, y2, z2,   x1, y2, z1);

        line(position, vertexConsumer, simulation, rotation,   x1, y1, z1,   x1, y2, z1); // pillars
        line(position, vertexConsumer, simulation, rotation,   x2, y1, z1,   x2, y2, z1);
        line(position, vertexConsumer, simulation, rotation,   x2, y1, z2,   x2, y2, z2);
        line(position, vertexConsumer, simulation, rotation,   x1, y1, z2,   x1, y2, z2);
    }

    private void boxQuads(Matrix4f position, BufferBuilder vertexConsumer, Simulation simulation, Box box, Vec3d offset, Quaternionf rotation) {
        float x1 = (float) (box.minX + offset.x);
        float y1 = (float) (box.minY + offset.y);
        float z1 = (float) (box.minZ + offset.z);
        float x2 = (float) (box.maxX + offset.x);
        float y2 = (float) (box.maxY + offset.y);
        float z2 = (float) (box.maxZ + offset.z);

        if (!cullDown)
            quad(position, vertexConsumer, simulation, rotation,
                    new Vec3d(x1, y1, z1),
                    new Vec3d(x2, y1, z1),
                    new Vec3d(x2, y1, z2),
                    new Vec3d(x1, y1, z2)
            ); // bottom

        if (!cullUp)
            quad(position, vertexConsumer, simulation, rotation,
                    new Vec3d(x1, y2, z1),
                    new Vec3d(x2, y2, z1),
                    new Vec3d(x2, y2, z2),
                    new Vec3d(x1, y2, z2)
            ); // top

        if (!cullSouth)
            quad(position, vertexConsumer, simulation, rotation,
                    new Vec3d(x1, y1, z1),
                    new Vec3d(x2, y1, z1),
                    new Vec3d(x2, y2, z1),
                    new Vec3d(x1, y2, z1)
            ); // front

        if (!cullNorth)
            quad(position, vertexConsumer, simulation, rotation,
                    new Vec3d(x1, y1, z2),
                    new Vec3d(x2, y1, z2),
                    new Vec3d(x2, y2, z2),
                    new Vec3d(x1, y2, z2)
            ); // back

        if (!cullWest)
            quad(position, vertexConsumer, simulation, rotation,
                    new Vec3d(x1, y1, z1),
                    new Vec3d(x1, y2, z1),
                    new Vec3d(x1, y2, z2),
                    new Vec3d(x1, y1, z2)
            ); // left

        if (!cullEast)
            quad(position, vertexConsumer, simulation, rotation,
                    new Vec3d(x2, y1, z1),
                    new Vec3d(x2, y2, z1),
                    new Vec3d(x2, y2, z2),
                    new Vec3d(x2, y1, z2)
            ); // right
    }

    private void quad(Matrix4f position, BufferBuilder vertexConsumer, Simulation simulation, Quaternionf rotation, Vec3d v1, Vec3d v2, Vec3d v3, Vec3d v4) {
        Vec2f pix1 = simulation.projectVector(v1, rotation);
        Vec2f pix2 = simulation.projectVector(v2, rotation);
        Vec2f pix3 = simulation.projectVector(v3, rotation);
        Vec2f pix4 = simulation.projectVector(v4, rotation);

        if (simulation.outOfBounds(pix1, pix2, pix3, pix4))
            return;

        vertexConsumer.vertex(position, pix1.x, pix1.y, 0).color(color);
        vertexConsumer.vertex(position, pix2.x, pix2.y, 0).color(color);
        vertexConsumer.vertex(position, pix3.x, pix3.y, 0).color(color);
        vertexConsumer.vertex(position, pix4.x, pix4.y, 0).color(color);
    }

    private void line(Matrix4f position, BufferBuilder vertexConsumer, Simulation simulation, Quaternionf rotation, double x1, double y1, double z1, double x2, double y2, double z2) {
        Vec2f pix1 = simulation.projectVector(new Vec3d(x1, y1, z1), rotation);
        Vec2f pix2 = simulation.projectVector(new Vec3d(x2, y2, z2), rotation);

        if (simulation.outOfBounds(pix1, pix2))
            return;

        vertexConsumer.vertex(position, pix1.x, pix1.y, 0).color(color);
        vertexConsumer.vertex(position, pix2.x, pix2.y, 0).color(color);
    }

    public BlockPos getPos() {
        return pos;
    }

    public Vec3d getOffsetPos(Vec3d camera) {
        double x = pos.getX() - camera.x;
        double y = pos.getY() - camera.y;
        double z = pos.getZ() - camera.z;
        return new Vec3d(x, y, z);
    }

    public BlockView getWorld() {
        return world;
    }

    public BlockState getBlock() {
        return block;
    }

    public List<Box> getCollisions() {
        return collisions;
    }

    public boolean isSurrounded() {
        return cullUp && cullDown && cullWest && cullEast && cullNorth && cullSouth;
    }

    public boolean isValid() {
        return !collisions.isEmpty() && !isSurrounded();
    }

    private static boolean shouldCullNeighbor(BlockView world, BlockPos neighbor, BlockState state) {
        List<Box> collisions = state.getCollisionShape(world, neighbor).getBoundingBoxes();
        return collisions.size() == 1 && collisions.getFirst().getAverageSideLength() == 1;
    }

    private static boolean shouldCullNeighbor(BlockView world, BlockPos neighbor) {
        return shouldCullNeighbor(world, neighbor, world.getBlockState(neighbor));
    }
}