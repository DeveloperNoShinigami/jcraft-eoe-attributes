package net.arna.jcraft.common.attack.moves.whitesnake;

import lombok.NonNull;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.attack.core.ctx.MoveContext;
import net.arna.jcraft.common.attack.moves.base.AbstractSimpleAttack;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.entity.stand.WhiteSnakeEntity;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import java.util.Set;

public class GiveStandAttack extends AbstractSimpleAttack<GiveStandAttack, WhiteSnakeEntity> {
    public GiveStandAttack(int cooldown, int windup, int duration, float moveDistance, int stun, float hitboxSize, float knockback, float offset) {
        super(cooldown, windup, duration, moveDistance, 0, stun, hitboxSize, knockback, offset);
    }

    @Override
    public boolean canBeInitiated(WhiteSnakeEntity attacker) {
        if (!attacker.hasUser()) {
            return false;
        }
        return super.canBeInitiated(attacker) && attacker.getUserOrThrow().getOffhandItem().getItem() == JItemRegistry.STAND_DISC.get();
    }

    @Override
    public void onInitiate(WhiteSnakeEntity attacker) {
        attacker.setItemSlot(EquipmentSlot.OFFHAND, attacker.getUserOrThrow().getOffhandItem());
        attacker.getUserOrThrow().setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        super.onInitiate(attacker);
    }

    @Override
    public @NonNull Set<LivingEntity> perform(WhiteSnakeEntity attacker, LivingEntity user, MoveContext ctx) {
        ItemStack itemStack = attacker.getOffhandItem();

        super.perform(attacker, user, ctx).stream().findFirst().ifPresent(
                (target) -> {
                    //todo: a wider approach to disabling stands or certain entity types from having stands?
                    if (target instanceof StandEntity<?,?>) return;

                    StandType itemStand = null;
                    int itemSkin = 0;

                    CompoundTag data = itemStack.getOrCreateTag();
                    CommonStandComponent standData = JComponentPlatformUtils.getStandData(target);

                    StandType targetStand = standData.getType();
                    if (targetStand != null) {
                        return; // Can't overwrite other's stands
                    }
                    if (data.contains("StandID", Tag.TAG_INT)) {
                        itemStand = StandType.fromIdOrOrdinal(data.getInt("StandID"));
                    }
                    if (itemStand == null) {
                        return;
                    }
                    if (data.contains("Skin", Tag.TAG_INT)) {
                        itemSkin = data.getInt("Skin");
                    }

                    standData.setTypeAndSkin(itemStand, itemSkin);
                    data.putInt("StandID", 0);
                    data.putInt("Skin", 0);

                    StandEntity<?, ?> stand = standData.getStand();
                    if (stand != null) {
                        stand.discard();
                    }
                    JCraft.summon(target.level(), target);
                }
        );

        attacker.getUserOrThrow().setItemSlot(EquipmentSlot.OFFHAND, itemStack);
        attacker.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
        return Set.of();
    }

    @Override
    protected @NonNull GiveStandAttack getThis() {
        return this;
    }

    @Override
    public @NonNull GiveStandAttack copy() {
        return copyExtras(new GiveStandAttack(
                getCooldown(), getWindup(), getDuration(), getMoveDistance(), getStun(), getHitboxSize(), getKnockback(), getOffset()
        ));
    }
}
