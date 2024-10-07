package net.arna.jcraft.common.splatter;

import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

@Data
public class SplatterSection {
    private final Level world;
    private final Direction direction;
    private final @NonNull Vector3f minPos, maxPos;
    private final Vector3f center;
    private final BlockPos blockPos;
    private final Vec2 minUv, maxUv;
    private final AABB hitBox;
    private boolean removed;

    public SplatterSection(Level world, Direction direction, @NonNull Vector3f minPos, @NonNull Vector3f maxPos, Vec2 minUv, Vec2 maxUv) {
        this.world = world;
        this.direction = direction;
        this.minPos = minPos;
        this.maxPos = maxPos;
        center = calcCenter(minPos, maxPos);
        blockPos = getAnchor(center, direction);
        this.minUv = minUv;
        this.maxUv = maxUv;
        this.hitBox = new AABB(new Vec3(minPos), new Vec3(maxPos))
                .expandTowards(new Vec3(direction.step()).scale(0.1));
    }

    public static BlockPos getAnchor(Vector3f center, Direction facing) {
        return BlockPos.containing(new Vec3(center).add(new Vec3(facing.step()).scale(0.05)));
    }

    public static Vector3f calcCenter(Vector3f min, Vector3f max) {
        Vector3f center = new Vector3f(min);
        Vector3f delta = new Vector3f(max); // Delta = (max - min) / 2
        delta.sub(min);
        delta = delta.div(2);
        center.add(delta); // (max - min) / 2 + min = center

        return center;
    }

    /**
     * Returns a version of this section wrapped around a vertical face.
     * Uses the same UVs as this section, but with different (vertical) coordinates.
     *
     * @param direction The direction this section faces
     * @param min       The minimum coordinates
     * @param max       The maximum coordinates
     * @return A wrapped version of this section
     */
    public SplatterSection wrapped(Direction direction, Vector3f min, Vector3f max) {
        return wrapped(direction, min, max, UvModification.NONE);
    }

    /**
     * Returns a version of this section wrapped around a vertical face.
     * Uses the same UVs as this section, but with different (vertical) coordinates.
     *
     * @param direction      The direction this section faces
     * @param min            The minimum coordinates
     * @param max            The maximum coordinates
     * @param uvModification What to do with the UVs.
     * @return A wrapped version of this section
     */
    @SuppressWarnings("SuspiciousNameCombination") // Yes, that's the idea.
    public SplatterSection wrapped(Direction direction, Vector3f min, Vector3f max, UvModification uvModification) {
        Vec2 minUv = this.minUv;
        Vec2 maxUv = this.maxUv;

        if (uvModification.isFlip()) {
            minUv = new Vec2(minUv.y, minUv.x);
            maxUv = new Vec2(maxUv.y, maxUv.x);
        }

        if (uvModification.isSwap()) {
            Vec2 intermediary = minUv;
            minUv = maxUv;
            maxUv = intermediary;
        }

        if (uvModification.isUFlip()) {
            float intermediary = minUv.x;
            minUv = new Vec2(maxUv.x, minUv.y);
            maxUv = new Vec2(intermediary, maxUv.y);
        }

        if (uvModification.isVFlip()) {
            float intermediary = minUv.y;
            minUv = new Vec2(minUv.x, maxUv.y);
            maxUv = new Vec2(maxUv.x, intermediary);
        }

        return new SplatterSection(world, direction, new Vector3f(min), new Vector3f(max), minUv, maxUv);
    }

    public boolean hasValidAnchor() {
        BlockPos pos = blockPos.relative(direction.getOpposite());
        return SplatterSplitter.isValidAnchor(world, pos);
    }

    public void tick() {
        if (removed) {
            return;
        }
        if (!hasValidAnchor() || SplatterSplitter.isValidAnchor(world, blockPos)) {
            removed = true;
        }
    }

    // Mostly for debugging
    @Override
    public String toString() {
        return "Section{" +
                "direction=" + direction +
                ", minPos=" + minPos +
                ", maxPos=" + maxPos +
                ", center=" + center +
                ", minUv=" + String.format("[%f, %f]", minUv.x, minUv.y) +
                ", maxUv=" + String.format("[%f, %f]", maxUv.x, maxUv.y) +
                '}';
    }

    @Getter
    public enum UvModification {
        // SWAP was never used, so to get rid of confusing and long names,
        // I renamed SWAP_FLIP to SWAP and removed the old SWAP (which had only swap set to true).
        NONE(false, false, false, false),
        FLIP(true, false, false, false),
        FLIP_U_FLIP(true, false, true, false),
        FLIP_V_FLIP(true, false, false, true),
        SWAP(true, true, false, false),
        SWAP_U_FLIP(true, true, true, false),
        SWAP_V_FLIP(true, true, false, true),
        U_FLIP(false, false, true, false),
        V_FLIP(false, false, false, true);

        private final boolean flip, swap, uFlip, vFlip;

        UvModification(boolean flip, boolean swap, boolean uFlip, boolean vFlip) {
            this.flip = flip;
            this.swap = swap;
            this.uFlip = uFlip;
            this.vFlip = vFlip;
        }
    }
}
