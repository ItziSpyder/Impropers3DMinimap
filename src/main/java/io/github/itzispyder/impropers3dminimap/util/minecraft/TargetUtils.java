package io.github.itzispyder.impropers3dminimap.util.minecraft;

import io.github.itzispyder.impropers3dminimap.Global;
import io.github.itzispyder.impropers3dminimap.util.misc.Pair;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class TargetUtils implements Global {

    public static final Predicate<Entity> VALID_ENTITY = e -> e != null && !e.isSpectator() && e.isAlive() && e.isAttackable();
    public static final Predicate<Entity> VALID_LIVING_ENTITY = e -> e instanceof LivingEntity && VALID_ENTITY.test(e);
    public static final Predicate<Entity> VALID_ATTACKABLE_ENTITY = e -> e instanceof LivingEntity l && VALID_ENTITY.test(e) && l.hurtTime <= 0;
    public static final Predicate<Entity> VALID_PLAYER = e -> e instanceof PlayerEntity p && VALID_ENTITY.test(p) && PlayerUtils.playerValid(p);
    public static final Predicate<Entity> VALID_ATTACKABLE_PLAYER = e -> e instanceof PlayerEntity p && VALID_ENTITY.test(p) && PlayerUtils.playerValid(p) && p.hurtTime <= 0 && !p.getAbilities().creativeMode;

    public static boolean isInRange(Entity entity, double radius) {
        return isInRange(entity, radius, radius);
    }

    public static boolean isInRange(Entity entity, double horRadius, double verRadius) {
        if (PlayerUtils.invalid()) {
            return false;
        }
        return isInRange(PlayerUtils.getPos(), entity.getPos(), horRadius, verRadius);
    }

    public static boolean isInRange(Vec3d from, Vec3d to, double horRadius, double verRadius) {
        double x = to.x - from.x;
        double y = to.y - from.y;
        double z = to.z - from.z;
        boolean withinHor = x * x + z * z < horRadius * horRadius;
        boolean withinVer = y * y < verRadius * verRadius;
        return withinHor && withinVer;
    }

    public static List<Entity> getEntities(Entity except, Vec3d pos, double range, Predicate<Entity> filter) {
        if (PlayerUtils.invalid()) {
            return new ArrayList<>();
        }

        var world = PlayerUtils.getClientWorld();
        List<Entity> result = new ArrayList<>();

        for (Entity ent : world.getEntities()) {
            if (ent.equals(except)) {
                continue;
            }
            if (!isInRange(ent.getPos(), pos, range, range)) {
                continue;
            }
            if (filter.test(ent)) {
                result.add(ent);
            }
        }
        return result;
    }

    public static Entity getNearest(Entity except, Vec3d pos, double range, Predicate<Entity> filter) {
        if (PlayerUtils.invalid()) {
            return null;
        }

        var all = getEntities(except, pos, range, filter)
                .stream()
                .sorted(Comparator.comparing(e -> e.getPos().distanceTo(pos)))
                .toList();

        if (all.isEmpty()) {
            return null;
        }
        return all.get(0);
    }

    public static List<Entity> getEntities(double range, Predicate<Entity> filter) {
        if (PlayerUtils.invalid()) {
            return new ArrayList<>();
        }
        return getEntities(PlayerUtils.player(), PlayerUtils.getPos(), range, filter);
    }

    public static Entity getNearest(double range, Predicate<Entity> filter) {
        if (PlayerUtils.invalid()) {
            return null;
        }
        return getNearest(PlayerUtils.player(), PlayerUtils.getPos(), range, filter);
    }

    public static Pair<BlockPos, BlockState> getNearestBlock(World world, BlockPos pos, double horRadius, double verRadius, BiPredicate<BlockPos, BlockState> condition) {
        var center = pos.toCenterPos();

        int x1 = (int)(center.x - horRadius);
        int y1 = (int)(center.y - verRadius);
        int z1 = (int)(center.z - horRadius);
        int x2 = (int)(center.x + horRadius);
        int y2 = (int)(center.y + verRadius);
        int z2 = (int)(center.z + horRadius);

        Pair<BlockPos, BlockState> result = null;
        double dist = Math.sqrt(horRadius * horRadius + verRadius * verRadius + horRadius * horRadius);

        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    var b = new BlockPos(x, y, z);

                    var state = world.getBlockState(b);
                    if (!condition.test(b, state))
                        continue;

                    double compare = b.toCenterPos().distanceTo(center);
                    if (compare >= dist)
                        continue;

                    dist = compare;
                    result = Pair.of(b, state);
                }
            }
        }
        return result;
    }

    public static Pair<BlockPos, BlockState> getNearestBlock(World world, BlockPos pos, double radius, BiPredicate<BlockPos, BlockState> condition) {
        return getNearestBlock(world, pos, radius, radius, condition);
    }
}
