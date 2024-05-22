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
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CoffinBlock extends BedBlock {
    public CoffinBlock(Properties settings) {
        super(DyeColor.RED, settings);
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
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
                world.explode(null, world.damageSources().badRespawnPointExplosion(vec3d), null, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, 5.0F, true, Level.ExplosionInteraction.BLOCK);
            } else if (state.getValue(OCCUPIED)) {
                if (!kickVillagerOutOfBed(world, pos)) {
                    player.displayClientMessage(Component.translatable("block.minecraft.bed.occupied"), true);
                }
            } else {
                Either<Player.BedSleepingProblem, Unit> sleep = player.startSleepInBed(pos);
                if (sleep.right().isPresent()) {
                    Vec3 bedPos = player.position().add(0, -0.2, 0).add(
                            Vec3.atLowerCornerOf(facing.getNormal()).scale(1.1)
                    );
                    player.teleportTo(bedPos.x, bedPos.y, bedPos.z);
                }
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
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getNearestLookingVerticalDirection())
                .setValue(OCCUPIED, false);
    }

    /*
     * Creates the block entity that we have playing our animations and rendering
     * the block
     */
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return JBlockEntityTypeRegistry.COFFIN_TILE.get().create(pos, state);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case NORTH -> Block.box(0, 0, -16, 16, 1, 16);
            case SOUTH -> Block.box(0, 0, 0, 16, 1, 32);
            case WEST -> Block.box(-16, 0, 0, 16, 1, 16);
            default -> Block.box(0, 0, 0, 32, 1, 16);
        };
    }

    // Simplified from Block#onBreak
    @Override
    public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        spawnDestroyParticles(world, player, pos, state);
        world.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        if (!player.getAbilities().instabuild) {
            popResource(world, pos, new ItemStack(JBlockRegistry.COFFIN_BLOCK.get()));
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
    }

    // Block#getStateForNeighborUpdate
    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor world, BlockPos pos, BlockPos neighborPos) {
        return state;
    }
}
