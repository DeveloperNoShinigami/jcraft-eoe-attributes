package net.arna.jcraft.common.attack.actions;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.IAttacker;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.api.attack.MoveActionType;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
@RequiredArgsConstructor(staticName = "run")
public class RunCommandAction extends MoveAction<RunCommandAction, IAttacker<?, ?>> {
    private final String command;

    @Override
    public void perform(final IAttacker<?, ?> attacker, final LivingEntity user, final MoveContext ctx, final Set<LivingEntity> targets) {
        MinecraftServer server = attacker.getEntityWorld().getServer();
        if (server == null) {
            return;
        }

        CommandSourceStack source = server.createCommandSourceStack();
        if (user != null) {
            source = source.withEntity(user).withPosition(user.position());
        }
        try {
            server.getCommands().getDispatcher().execute(command, source);
        } catch (CommandSyntaxException e) {
            if (user != null) {
                user.sendSystemMessage(Component.literal("An unknown error occurred while executing a 'Run Command' action. " +
                        "Check the console for details.").withStyle(ChatFormatting.RED));
            }

            JCraft.LOGGER.error("An error occurred while executing command '{}' for a RunCommand action.", command, e);
        }
    }

    @Override
    public @NonNull MoveActionType<RunCommandAction> getType() {
        return Type.INSTANCE;
    }

    public static class Type extends MoveActionType<RunCommandAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<RunCommandAction> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    runMoment(),
                    Codec.STRING.fieldOf("command").forGetter(RunCommandAction::getCommand)
            ).apply(instance, apply(RunCommandAction::run)));
        }
    }
}
