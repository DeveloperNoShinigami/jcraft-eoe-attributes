package net.arna.jcraft.common.component.living;

import net.arna.jcraft.common.component.JComponent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import java.util.UUID;

public interface CommonMiscComponent extends JComponent {
    // General
    Vec3 getDesiredVelocity();

    void updateRemoteInputs(int forward, int sideways, boolean jumping);

    void startDamageTimer();

    boolean isOnDamageTimer();

    // TheWorldOverHeavenEntity
    UUID getSlavedTo();

    void setSlavedTo(UUID uuid);

    LivingEntity getMaster();

    // StuckKnivesFeatureRenderer
    int getStuckKnifeCount();

    void stab();

    // WeightlessStatusEffect
    int getHoverTime();

    void setHoverTime(int hoverTime);

    boolean getPrevNoGrav();

    void setPrevNoGrav(boolean noGrav);

    // Armored Hits
    int getArmoredHitTicks();

    void displayArmoredHit();

    // AnubisSpec
    float getAttackSpeedMult();

    void setAttackSpeedMult(float speedMult);

    // MetallicaEntity
    float getMetallicaIron();
    void setMetallicaIron(float iron);
}
