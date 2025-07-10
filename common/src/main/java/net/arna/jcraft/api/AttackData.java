package net.arna.jcraft.api;

import lombok.Data;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import static net.arna.jcraft.api.component.living.CommonHitPropertyComponent.HitAnimation;
import static net.arna.jcraft.api.component.living.CommonHitPropertyComponent.HitAnimation.MID;

@Data
public class AttackData {
    Vec3 kbVec = Vec3.ZERO;
    int stunTicks = 0;
    int stunLevel = 1;
    boolean overrideStun = false;
    float damage = 0.0f;
    boolean lift = false;
    int blockstun = 0;
    DamageSource source = null;
    Entity attacker = null;
    HitAnimation hitAnimation = MID;
    MoveUsage moveUsage = null;
    boolean canBackstab = false;
    boolean unblockable = false;



    boolean cancelMoves = true;

    public AttackData() {

    }
    public AttackData(Vec3 kbVec, int stunTicks, int stunLevel, boolean overrideStun, float damage, boolean lift, int blockstun, DamageSource source, Entity attacker, HitAnimation hitAnimation, MoveUsage moveUsage, boolean canBackstab, boolean unblockable) {
        this.kbVec = kbVec;
        this.stunTicks = stunTicks;
        this.stunLevel = stunLevel;
        this.overrideStun = overrideStun;
        this.damage = damage;
        this.lift = lift;
        this.blockstun = blockstun;
        this.source = source;
        this.attacker = attacker;
        this.hitAnimation = hitAnimation;
        this.moveUsage = moveUsage;
        this.canBackstab = canBackstab;
        this.unblockable = unblockable;
    }
}
