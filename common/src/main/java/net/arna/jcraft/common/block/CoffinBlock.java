package net.arna.jcraft.common.block;

import com.mojang.datafixers.util.Either;
import net.arna.jcraft.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.registry.JBlockRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

//todo: fix being able to place blocks on the tail of the coffin

// Actual sleep logic is handled in JServerEvents
public class CoffinBlock extends BedBlock {
    public CoffinBlock(Properties settings) {
        super(DyeColor.RED, settings);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return switch (state.getValue(FACING)) {
            case NORTH -> level.getBlockState(pos.south()).isAir();
            case EAST -> level.getBlockState(pos.west()).isAir();
            case SOUTH -> level.getBlockState(pos.north()).isAir();
            case WEST -> level.getBlockState(pos.east()).isAir();
            default -> false;
        };
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level world, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (world.isClientSide) {
            return InteractionResult.CONSUME;
        } else {
            if (!state.is(this)) {
                return InteractionResult.CONSUME;
            }

            Direction facing = state.getValue(FACING);

            if (!canSetSpawn(world)) {
                world.removeBlock(pos, false);
                BlockPos blockPos = pos.relative(facing.getOpposite());
                if (world.getBlockState(blockPos).is(this)) {
                    world.removeBlock(blockPos, false);
                }

                Vec3 vec3d = pos.getCenter();
                world.explode(null, world.damageSources().badRespawnPointExplosion(vec3d), null,
                        (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, 5.0F, true, Level.ExplosionInteraction.BLOCK);
            } else if (state.getValue(OCCUPIED)) {
                if (!kickVillagerOutOfBed(world, pos)) {
                    player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
                }
            } else {
                Either<Player.BedSleepingProblem, Unit> sleep = player.startSleepInBed(pos);
                sleep.ifRight(unit -> {
                    Vec3 bedPos = player.position().add(0, -0.2, 0).add(
                            Vec3.atLowerCornerOf(facing.getNormal()).scale(1.1)
                    );
                    player.teleportTo(bedPos.x, bedPos.y, bedPos.z);
                });

                sleep.ifLeft(problem -> {
                    if (problem.getMessage() != null) {
                        player.displayClientMessage(problem.getMessage(), true);
                    }
                });
            }

            return InteractionResult.SUCCESS;
        }
    }

    private boolean kickVillagerOutOfBed(Level world, BlockPos pos) {
        List<Villager> list = world.getEntitiesOfClass(Villager.class, new AABB(pos), LivingEntity::isSleeping);
        if (list.isEmpty()) {
            return false;
        }
        list.get(0).stopSleeping();
        return true;
    }

    @Override
    public BlockState getStateForPlacement(@NotNull BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        if (state != null) state.setValue(OCCUPIED, false);
        return state;
    }

    /*
     * Creates the block entity that we have playing our animations and rendering
     * the block
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return JBlockEntityTypeRegistry.COFFIN_TILE.get().create(pos, state);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> Block.box(0, 0, -16, 16, 1, 16);
            case SOUTH -> Block.box(0, 0, 0, 16, 1, 32);
            case WEST -> Block.box(-16, 0, 0, 16, 1, 16);
            default -> Block.box(0, 0, 0, 32, 1, 16);
        };
    }

    // Simplified from Block#onBreak
    @Override
    public void playerWillDestroy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        spawnDestroyParticles(world, player, pos, state);
        world.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        if (!player.getAbilities().instabuild) {
            popResource(world, pos, new ItemStack(JBlockRegistry.COFFIN_BLOCK.get()));
        }
    }

    @Override
    public void setPlacedBy(@NotNull Level world, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack itemStack) {
    }

    // Block#getStateForNeighborUpdate
    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction, @NotNull BlockState neighborState,
                                           @NotNull LevelAccessor world, @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        return state;
    }
}
