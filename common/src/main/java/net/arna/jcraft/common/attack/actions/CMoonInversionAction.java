package net.arna.jcraft.common.attack.actions;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import net.arna.jcraft.api.attack.core.MoveAction;
import net.arna.jcraft.api.attack.core.MoveActionType;
import net.arna.jcraft.common.entity.stand.CMoonEntity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Set;

@Getter
@RequiredArgsConstructor(staticName = "addInversion")
public class CMoonInversionAction extends MoveAction<CMoonInversionAction, CMoonEntity> {
    private final int time;
    private final float damage;
    private final boolean slow;

    @Override
    public void perform(CMoonEntity attacker, LivingEntity user, Set<LivingEntity> targets) {
        targets.forEach(t -> attacker.addInversion(t, time, damage, slow));
    }

    @Override
    public @NonNull MoveActionType<CMoonInversionAction> getType() {
        return Type.INSTANCE;
    }

    public static class Type extends MoveActionType<CMoonInversionAction> {
        public static final Type INSTANCE = new Type();

        @Override
        public Codec<CMoonInversionAction> getCodec() {
            return RecordCodecBuilder.create(instance -> instance.group(
                    runMoment(),
                    Codec.INT.fieldOf("time").forGetter(CMoonInversionAction::getTime),
                    Codec.FLOAT.fieldOf("damage").forGetter(CMoonInversionAction::getDamage),
                    Codec.BOOL.fieldOf("slow").forGetter(CMoonInversionAction::isSlow)
            ).apply(instance, apply(CMoonInversionAction::addInversion)));
        }
    }
}
