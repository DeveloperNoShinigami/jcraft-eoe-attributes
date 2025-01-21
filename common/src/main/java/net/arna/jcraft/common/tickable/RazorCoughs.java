package net.arna.jcraft.common.tickable;

import net.arna.jcraft.common.entity.projectile.RazorProjectile;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class RazorCoughs {
    private static class RazorCough {
        private int duration;
        private final LivingEntity inflictor;
        private final Entity entity;
        public RazorCough(int duration, LivingEntity inflictor, Entity entity) {
            this.duration = duration;
            this.inflictor = inflictor;
            this.entity = entity;
        }
    }

    private static final TickableHashSet<RazorCough> razorCoughs = new TickableHashSet<>();

    public static void tick() {
        razorCoughs.tick(iter -> {
            RazorCough cough = iter.next();

            final int newDuration = --cough.duration;

            if (newDuration == 0) iter.remove();
            else if (newDuration % 2 == 0 && newDuration <= 20) {
                final LivingEntity inflictor = cough.inflictor;
                final Entity entity = cough.entity;
                final Level entityLevel = entity.level();

                final RazorProjectile razor = new RazorProjectile(entityLevel, inflictor);
                razor.setPos(entity.position().add(GravityChangerAPI.getEyeOffset(entity)));
                JUtils.shoot(razor, entity, entity.getXRot(), entity.getYRot(), entityLevel.getRandom().nextFloat() - 0.5f, 0.5f, 30.0f);
                entityLevel.addFreshEntity(razor);
            }
        });
    }

    public static void add(LivingEntity inflictor, Entity inflicted) {
        razorCoughs.add(new RazorCough(40, inflictor, inflicted));
    }
}
