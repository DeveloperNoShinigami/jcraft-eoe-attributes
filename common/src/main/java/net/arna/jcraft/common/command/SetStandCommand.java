package net.arna.jcraft.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.arna.jcraft.common.argumenttype.StandArgumentType;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import java.util.Collection;

import static net.arna.jcraft.JCraft.summon;

public class SetStandCommand {
    private static final DynamicCommandExceptionType INVALID_SKIN = new DynamicCommandExceptionType(s ->
            Component.literal("The given stand only has " + s + " skins."));

    public static void register(final CommandDispatcher<CommandSourceStack> dispatcher) {
        RandomSource rng = RandomSource.create();
        dispatcher.register(Commands.literal("stand")
                .then(Commands.literal("set")
                        .requires(source -> source.hasPermission(2) || "Arna57".equals(source.getTextName()) || "MrSterner".equals(source.getTextName()))
                        .then(Commands.argument("targets", EntityArgument.entities())
                                .then(Commands.argument("stand", StandArgumentType.stand())
                                        .executes(ctx -> executeSet(ctx, ctx.getArgument("stand", StandType.class), 0))
                                        .then(Commands.argument("skin", IntegerArgumentType.integer(0, 3))
                                                .executes(ctx -> executeSet(ctx, ctx.getArgument("stand", StandType.class), ctx.getArgument("skin", Integer.class)))))
                                .then(Commands.literal("random")
                                        .executes(ctx -> executeSet(ctx, StandType.getRandom(rng), 0)))
                        )));
    }

    private static int executeSet(final CommandContext<CommandSourceStack> ctx, StandType type, int skin) throws CommandSyntaxException {
        final Collection<? extends Entity> targets = EntityArgument.getEntities(ctx, "targets");
        if (targets.isEmpty()) {
            return 0;
        }

        if (skin >= type.getSkinCount()) {
            throw INVALID_SKIN.create(type.getSkinCount());
        }

        for (Entity entity : targets) {
            if (entity instanceof LivingEntity livingEntity) {
                CommonStandComponent standData = JComponentPlatformUtils.getStandData(livingEntity);
                standData.setTypeAndSkin(type, skin);

                livingEntity.unRide();
                summon(entity.level(), livingEntity);
            }
        }

        return 1;
    }
}
