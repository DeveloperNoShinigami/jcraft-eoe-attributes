package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Collection;

public class StandBlockCommand {
    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stand")
                .then(Commands.literal("block")
                        .requires(source -> source.hasPermission(2) || "Arna57".equals(source.getTextName()) || "MrSterner".equals(source.getTextName()))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("block", BoolArgumentType.bool())
                                        .executes(ctx -> run(ctx, ctx.getArgument("block", Boolean.class)))
                                )
                        )
                )
        );
    }

    public static int run(final CommandContext<CommandSourceStack> ctx, boolean block) throws CommandSyntaxException {
        final Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        if (targets.isEmpty()) {
            return 0;
        }
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                StandEntity<?, ?> stand = JComponentPlatformUtils.getStandData(livingEntity).getStand();

                if (stand == null) {
                    continue;
                }
                if (block) {
                    if (stand.getMoveStun() < 1) {
                        stand.tryBlock();
                    }
                } else {
                    stand.tryUnblock();
                }
                stand.wantToBlock = block;
            }
        }
        return 1;
    }
}
