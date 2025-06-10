package net.arna.jcraft.common.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import net.arna.jcraft.api.stand.StandEntity;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JSoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AnubisItem extends Item {
    private final Multimap<Attribute, AttributeModifier> attributeModifiers;

    public AnubisItem(Properties settings) {
        super(settings);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 6.0, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4, AttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        return slot == EquipmentSlot.MAINHAND ? this.attributeModifiers : super.getDefaultAttributeModifiers(slot);
    }

    public boolean canAttackBlock(BlockState state, Level world, BlockPos pos, Player miner) {
        return !miner.isCreative();
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.anubis.namedesc"));
        tooltip.add(Component.translatable("jcraft.anubis.bloodthirstdesc"));
        tooltip.add(Component.translatable("jcraft.anubis.removaldesc"));
        tooltip.add(Component.translatable("jcraft.anubis.desc"));
        super.appendHoverText(stack, world, tooltip, context);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack item = user.getItemInHand(hand);
        StandEntity<?, ?> stand = JUtils.getStand(user);

        if (user.isShiftKeyDown() || world.isClientSide || (stand != null && stand.blocking)) {
            return InteractionResultHolder.fail(item);
        }

        JUtils.serverPlaySound(JSoundRegistry.ANUBIS_SHEATHE.get(), (ServerLevel) world, user.position());
        user.setItemInHand(hand, new ItemStack(JItemRegistry.ANUBIS_SHEATHED.get()));

        return InteractionResultHolder.success(item);
    }

    public static void handleAnubisEffects(int timeSinceAttack, Player player) {
        // If was in battle within last 5 minutes, apply haste 1
        if (timeSinceAttack > -6000) {
            player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 20, 0, true, false));
        } else {
            // If wasn't in battle for 10 minutes, apply mining fatigue 1
            if (timeSinceAttack < -12000) {
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 20, 0, true, false));
                // If wasn't in battle for 15 minutes, apply weakness 1
                if (timeSinceAttack < -18000) {
                    player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20, 0, true, false));
                    // If wasn't in battle for 20 minutes, apply nausea 1
                    if (timeSinceAttack < -24000) {
                        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 20, 0, true, false));
                        // If wasn't in battle for 25 minutes, apply slowness 1
                        if (timeSinceAttack < -30000) {
                            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20, 0, true, false));
                        }
                    }
                }
            }
        }
    }

    @Override
    public void inventoryTick(ItemStack stack, Level world, Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);
        if (world.isClientSide) {
            return;
        }
        if (entity instanceof Player player) // Bloodlust
        {
            handleAnubisEffects(player.getLastHurtMobTimestamp() - player.tickCount, player);
        }
    }
}
