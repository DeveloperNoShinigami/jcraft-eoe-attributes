package net.arna.jcraft.common.attack.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.entity.stand.MetallicaEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
@RequiredArgsConstructor(staticName = "addIron")
public class MetallicaAddIronAction extends MoveAction<MetallicaAddIronAction, MetallicaEntity> {
    private final float iron;

    @Override
    public void perform(MetallicaEntity attacker, LivingEntity user, MoveContext ctx, Set<LivingEntity> targets) {
        attacker.addIron(iron);
    }

    @Override
    public @NonNull MoveActionType<MetallicaAddIronAction> getType() {
        return Type.INSTANCE;
    }

    public static class Type extends MoveActionType<MetallicaAddIronAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<MetallicaAddIronAction> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    runMoment(),
                    Codec.FLOAT.fieldOf("iron").forGetter(MetallicaAddIronAction::getIron)
            ).apply(instance, apply(MetallicaAddIronAction::addIron)));
        }
    }
}
