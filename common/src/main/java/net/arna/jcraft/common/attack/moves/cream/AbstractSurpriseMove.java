package net.arna.jcraft.common.attack.moves.cream;

import lombok.Getter;
import lombok.NonNull;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.entity.stand.CreamEntity;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;

import java.util.Set;

public abstract class AbstractSurpriseMove<T extends AbstractSurpriseMove<T>> extends AbstractMove<T, CreamEntity> {
    @Getter
    protected Vector3f outPos = new Vector3f(), outDir = new Vector3f();

    public AbstractSurpriseMove(final int cooldown, final int windup, final int duration, final float moveDistance) {
        super(cooldown, windup, duration, moveDistance);
    }

    @Override
    public void onInitiate(final CreamEntity attacker) {
        super.onInitiate(attacker);

        attacker.setFree(true);
        attacker.setFreePos(attacker.getUserOrThrow().position().toVector3f());
    }

    @Override
    public @NonNull Set<LivingEntity> perform(final CreamEntity attacker, final LivingEntity user) {
        attacker.setCharging(true);

        // OUT_DIR is set in .withAction() in CreamEntity.java

        outPos.sub(outDir);
        attacker.setPos(new Vec3(outPos.x(), outPos.y(), outPos.z()));
        attacker.setFreePos(outPos);

        attacker.setVoidTime(getWindupPoint());

        attacker.playSound(JSoundRegistry.IMPACT_5.get(), 1, 0.75f);

        return Set.of();
    }
}
