package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import net.arna.jcraft.common.argumenttype.AttackArgumentType;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.attack.core.MoveType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import java.util.Collection;

public class InduceAttackCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("attack")
                .requires(source -> source.hasPermission(2) || "Arna57".equals(source.getTextName()) || "MrSterner".equals(source.getTextName()))
                .then(Commands.argument("ents", EntityArgument.entities())
                        .then(Commands.literal("stand")
                                .then(Commands.argument("attack", AttackArgumentType.attack()).executes(
                                        context -> runAttack(
                                                context.getSource(),
                                                EntityArgumentType.getEntities(context, "ents"),
                                                true,
                                                context.getArgument("attack", MoveType.class)
                                        )
                                ))
                        )
                        .then(Commands.literal("spec")
                                .then(Commands.argument("attack", AttackArgumentType.attack()).executes(
                                        context -> runAttack(
                                                context.getSource(),
                                                EntityArgumentType.getEntities(context, "ents"),
                                                false,
                                                context.getArgument("attack", MoveType.class)
                                        )
                                ))
                        )
                )
        );
    }

    public static int runAttack(CommandSourceStack source, Collection<? extends Entity> targets, boolean stand, MoveType type) {
        int flag = 0;
        String typeName = type.toString();

        if (stand) {
            for (Entity entity : targets) {
                if (entity instanceof LivingEntity living) {
                    JComponentPlatformUtils.getCooldowns(living).clear();
                    StandEntity<?, ?> standEntity = JComponentPlatformUtils.getStandData(living).getStand();

                    if (standEntity != null) {
                        if (standEntity.initMove(type)) {
                            source.sendSuccess(() -> Component.literal("Initiating stand attack " + typeName + " for " + living.getName().getString()), true);
                        } else {
                            source.sendSuccess(() -> Component.literal("Queueing stand attack " + typeName + " for " + living.getName().getString()), true);
                            standEntity.queueMove(MoveInputType.fromMoveType(type));
                        }

                        flag = 1;
                    }
                }
            }
        } else {
            for (Entity entity : targets) {
                if (!(entity instanceof Player player)) {
                    continue;
                }
                JComponentPlatformUtils.getCooldowns(player).clear();
                JSpec<?, ?> spec = JUtils.getSpec(player);

                if (spec != null) {
                    if (spec.initMove(type)) {
                        source.sendSuccess(() -> Component.literal("Initiating spec attack " + typeName + " for " + entity.getName().getString()), true);
                    } else {
                        source.sendSuccess(() -> Component.literal("Queueing spec attack " + typeName + " for " + entity.getName().getString()), true);
                        spec.queuedMove = MoveInputType.fromMoveType(type);
                    }

                    flag = 1;
                }
            }
        }

        return flag;
    }
}
