package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import lombok.Getter;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.gravity.api.RotationParameters;
import net.arna.jcraft.common.gravity.util.Gravity;
import net.arna.jcraft.common.gravity.util.RotationUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import java.util.Collection;
import java.util.Collections;

import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

public class GravityCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> literalSet = literal("add");
        for (Direction direction : Direction.values()) {
            literalSet.then(
                    literal(direction.getName())
                            .then(argument("priority", IntegerArgumentType.integer())
                                    .then(argument("duration", IntegerArgumentType.integer())
                                            .executes(context -> executeSet(context.getSource(), direction, IntegerArgumentType.getInteger(context, "priority"), IntegerArgumentType.getInteger(context, "duration"), Collections.singleton(context.getSource().getPlayer())))
                                            .then(argument("entities", EntityArgument.entities())
                                                    .executes(context -> executeSet(context.getSource(), direction, IntegerArgumentType.getInteger(context, "priority"), IntegerArgumentType.getInteger(context, "duration"), EntityArgument.getEntities(context, "entities"))))))
            );
        }

        LiteralArgumentBuilder<CommandSourceStack> literalSetDefault = literal("set");
        for (Direction direction : Direction.values()) {
            literalSetDefault.then(literal(direction.getName())
                    .executes(context -> executeSetDefault(context.getSource(), direction, Collections.singleton(context.getSource().getPlayer())))
                    .then(argument("entities", EntityArgument.entities())
                            .executes(context -> executeSetDefault(context.getSource(), direction, EntityArgument.getEntities(context, "entities")))));
        }

        LiteralArgumentBuilder<CommandSourceStack> literalRotate = literal("rotate");
        for (FacingDirection facingDirection : FacingDirection.values()) {
            literalRotate.then(literal(facingDirection.getName())
                    .executes(context -> executeRotate(context.getSource(), facingDirection, Collections.singleton(context.getSource().getPlayer())))
                    .then(argument("entities", EntityArgument.entities())
                            .executes(context -> executeRotate(context.getSource(), facingDirection, EntityArgument.getEntities(context, "entities")))));
        }

        dispatcher.register(literal("jgravity").requires(source -> source.hasPermission(2))
                .then(literal("get")
                        .executes(context -> executeGet(context.getSource(), context.getSource().getPlayer()))
                        .then(argument("entities", EntityArgument.entity())
                                .executes(context -> executeGet(context.getSource(), EntityArgument.getEntity(context, "entities")))))
                .then(literal("cleargravity")
                        .executes(context -> executeClearGravity(context.getSource(), Collections.singleton(context.getSource().getPlayer())))
                        .then(argument("entities", EntityArgument.entity())
                                .executes(context -> executeClearGravity(context.getSource(), EntityArgument.getEntities(context, "entities")))))
                .then(literalSet).then(literalSetDefault).then(literalRotate).then(literal("randomise")
                        .executes(context -> executeRandomise(context.getSource(), Collections.singleton(context.getSource().getPlayer())))
                        .then(argument("entities", EntityArgument.entities())
                                .executes(context -> executeRandomise(context.getSource(), EntityArgument.getEntities(context, "entities"))))));
    }

    private static void getSendFeedback(final CommandSourceStack source, final Entity entity, final Direction gravityDirection) {
        Component text = Component.translatable("direction." + gravityDirection.getName());
        if (source.getEntity() != null && source.getEntity() == entity) {
            source.sendSuccess(() -> Component.translatable("commands.gravity.get.self", text), true);
        } else {
            source.sendSuccess(() -> Component.translatable("commands.gravity.get.other", entity.getDisplayName(), text), true);
        }
    }

    private static int executeGet(final CommandSourceStack source, final Entity entity) {
        Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
        getSendFeedback(source, entity, gravityDirection);
        return gravityDirection.get3DDataValue();
    }

    private static int executeSet(final CommandSourceStack source, final Direction gravityDirection, final int priority, final int duration, final Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            //if (GravityChangerAPI.getGravityDirection(entity) != gravityDirection) {
            GravityChangerAPI.addGravity(entity, new Gravity(gravityDirection, priority, duration, "command"));
            //getSendFeedback(source, entity, gravityDirection);
            i++;
            //}
        }
        return i;
    }

    private static int executeSetDefault(final CommandSourceStack source, final Direction gravityDirection, final Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            if (GravityChangerAPI.getDefaultGravityDirection(entity) != gravityDirection) {
                GravityChangerAPI.setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
                //GravityChangerAPI.updateGravity(entity);
                getSendFeedback(source, entity, gravityDirection);
                i++;
            }
        }
        return i;
    }

    private static int executeRotate(final CommandSourceStack source, final FacingDirection relativeDirection, final Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            Direction gravityDirection = GravityChangerAPI.getGravityDirection(entity);
            Direction combinedRelativeDirection = switch (relativeDirection) {
                case DOWN -> Direction.DOWN;
                case UP -> Direction.UP;
                case FORWARD, BACKWARD, LEFT, RIGHT ->
                        Direction.from2DDataValue(relativeDirection.getHorizontalOffset() + Direction.fromYRot(entity.getYRot()).get2DDataValue());
            };
            Direction newGravityDirection = RotationUtil.dirPlayerToWorld(combinedRelativeDirection, gravityDirection);
            GravityChangerAPI.setDefaultGravityDirection(entity, newGravityDirection, new RotationParameters());
            //GravityChangerAPI.updateGravity(entity);
            getSendFeedback(source, entity, newGravityDirection);
            i++;
        }
        return i;
    }

    private static int executeRandomise(final CommandSourceStack source, final Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            Direction gravityDirection = Direction.getRandom(source.getLevel().random);
            if (GravityChangerAPI.getGravityDirection(entity) != gravityDirection) {
                GravityChangerAPI.setDefaultGravityDirection(entity, gravityDirection, new RotationParameters());
                //GravityChangerAPI.updateGravity(entity);
                getSendFeedback(source, entity, gravityDirection);
                i++;
            }
        }
        return i;
    }

    private static int executeClearGravity(final CommandSourceStack source, final Collection<? extends Entity> entities) {
        int i = 0;
        for (Entity entity : entities) {
            GravityChangerAPI.clearGravity(entity, new RotationParameters());
            i++;
        }
        return i;
    }

    @Getter
    public enum FacingDirection {
        DOWN(-1, "down"),
        UP(-1, "up"),
        FORWARD(0, "forward"),
        BACKWARD(2, "backward"),
        LEFT(3, "left"),
        RIGHT(1, "right");

        private final int horizontalOffset;
        private final String name;

        FacingDirection(final int horizontalOffset, final String name) {
            this.horizontalOffset = horizontalOffset;
            this.name = name;
        }

    }
}
