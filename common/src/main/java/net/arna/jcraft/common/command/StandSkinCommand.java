package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Collection;

import static net.arna.jcraft.JCraft.summon;

public class StandSkinCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("stand")
                .then(Commands.literal("skin")
                        .requires(source -> source.hasPermission(2) || "Arna57".equals(source.getTextName()) || "MrSterner".equals(source.getTextName()))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("skin", IntegerArgumentType.integer(0, 3))
                                        .executes(ctx -> run(ctx, ctx.getArgument("skin", Integer.class)))
                                )
                        )
                )
        );
    }

    public static int run(CommandContext<CommandSourceStack> ctx, int skin) throws CommandSyntaxException {
        Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        if (targets.isEmpty()) {
            return 0;
        }
        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                CommonStandComponent standData = JComponentPlatformUtils.getStandData(livingEntity);
                StandEntity<?, ?> stand = standData.getStand();

                if (stand == null) {
                    continue;
                }

                StandType type = stand.getStandType();
                if (skin <= type.getSkinCount()) {
                    standData.setSkin(skin);
                }

                livingEntity.unRide();
                summon(entity.level(), livingEntity);
            }
        }
        return 1;
    }
}
