package net.arna.jcraft.client.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import dev.architectury.event.events.client.ClientTickEvent;
import net.arna.jcraft.client.rendering.CloneSkinTracker;
import net.arna.jcraft.common.entity.PlayerCloneEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class PlayerCloneClientPlayerEntity extends AbstractClientPlayer {
    private static final Set<PlayerCloneClientPlayerEntity> entities = Collections.newSetFromMap(new WeakHashMap<>());
    private static final GameProfile CLONE_PROFILE = new GameProfile(UUID.nameUUIDFromBytes("jcraft$playerClone".getBytes()), null);
    private final PlayerCloneEntity clone;

    static {

        ClientTickEvent.CLIENT_LEVEL_POST.register(world -> entities.stream()
                .filter(LivingEntity::isAlive)
                .forEach(PlayerCloneClientPlayerEntity::tick));
        ClientTickEvent.CLIENT_POST.register(client -> {
            if (client.level == null) {
                entities.clear();
            }
        });
    }

    public PlayerCloneClientPlayerEntity(final PlayerCloneEntity clone) {
        super(Objects.requireNonNull(Minecraft.getInstance().level), CLONE_PROFILE);
        this.clone = clone;
        entities.add(this);
    }

    @Override
    public boolean isSpectator() {
        return false;
    }

    @Override
    public boolean isCreative() {
        return false;
    }

    @Nullable
    @Override
    protected PlayerInfo getPlayerInfo() {
        return null;
    }

    @Override
    public boolean isSkinLoaded() {
        return CloneSkinTracker.getSkinFor(clone, MinecraftProfileTexture.Type.SKIN) != null;
    }

    @Override
    public ResourceLocation getSkinTextureLocation() {
        return CloneSkinTracker.getSkinFor(clone, MinecraftProfileTexture.Type.SKIN);
    }

    @Override
    public boolean isCapeLoaded() {
        return CloneSkinTracker.getSkinFor(clone, MinecraftProfileTexture.Type.CAPE) != null;
    }

    @Nullable
    @Override
    public ResourceLocation getCloakTextureLocation() {
        return CloneSkinTracker.getSkinFor(clone, MinecraftProfileTexture.Type.CAPE);
    }

    @Override
    public boolean isElytraLoaded() {
        return CloneSkinTracker.getSkinFor(clone, MinecraftProfileTexture.Type.ELYTRA) != null;
    }

    @Nullable
    @Override
    public ResourceLocation getElytraTextureLocation() {
        return CloneSkinTracker.getSkinFor(clone, MinecraftProfileTexture.Type.ELYTRA);
    }

    @Override
    public String getModelName() {
        return CloneSkinTracker.getModelFor(clone);
    }

    @Override
    public boolean shouldShowName() {
        // Unused because PlayerEntityRenderer extends LivingEntityRenderer which ignores this.
        // Actual implementation is found in LivingEntityRendererMixin.
        return false;
    }

    @Override
    public void tick() {
        super.tick();
        // Age is usually incremented by whatever ticked this entity, except we tick it
        // ourselves, so we have to manually increase the age.
        // Age is used for several animation-related values. (Such as the 'breathing' motion of the arms)
        tickCount++;

        setMainArm(clone.isLeftHanded() ? HumanoidArm.LEFT : HumanoidArm.RIGHT);

        entityData.set(Player.DATA_PLAYER_MODE_CUSTOMISATION, clone.getPartMask());

        swingingArm = clone.swingingArm;

        setArrowCount(clone.getArrowCount());
        setStingerCount(clone.getStingerCount());

        for (EquipmentSlot slot : EquipmentSlot.values()) {
            setItemSlot(slot, clone.getItemBySlot(slot));
        }
    }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        return false;
    }

    public void updateData() {
        xo = clone.xo;
        yo = clone.yo;
        zo = clone.zo;

        setPosRaw(clone.getX(), clone.getY(), clone.getZ());

        yBodyRotO = clone.yBodyRotO;
        yBodyRot = clone.yBodyRot;
        yHeadRotO = clone.yHeadRotO;
        yHeadRot = clone.yHeadRot;

        hurtTime = clone.hurtTime;
        hurtDuration = clone.hurtDuration;

        //TODO lastLimbDistance = clone.lastLimbDistance;
        //limbDistance = clone.limbDistance;
        //limbAngle = clone.limbAngle;

        swinging = clone.swinging;
        oAttackAnim = clone.oAttackAnim;
        attackAnim = clone.attackAnim;
        swingTime = clone.swingTime;

        deathTime = clone.deathTime;
        dead = clone.isDeadOrDying();
    }
}
