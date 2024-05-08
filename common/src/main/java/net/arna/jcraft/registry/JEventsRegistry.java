package net.arna.jcraft.registry;

import dev.architectury.event.CompoundEventResult;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.*;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.block.CoffinBlock;
import net.arna.jcraft.common.component.living.CommonVampireComponent;
import net.arna.jcraft.common.config.ConfigOption;
import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.events.JServerEvents;
import net.arna.jcraft.common.gravity.api.GravityChangerAPI;
import net.arna.jcraft.common.network.c2s.ConfigUpdatePacket;
import net.arna.jcraft.common.spec.SpecType;
import net.arna.jcraft.common.tickable.Revivables;
import net.arna.jcraft.common.util.CooldownType;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;

import static net.arna.jcraft.registry.JItemRegistry.COFFIN_BLOCK;

public class JEventsRegistry {
    public static void registerEvents() {
        EntityEvent.LIVING_HURT.register(JServerEvents::hurt);
        EntityEvent.ADD.register(JServerEvents::entityLoad);
        EntityEvent.LIVING_DEATH.register(JServerEvents::death);
        TickEvent.SERVER_POST.register(JServerEvents::serverTick);

        // Disable item/block usage while stunned
        InteractionEvent.RIGHT_CLICK_ITEM.register(JServerEvents::rightClick);

        InteractionEvent.LEFT_CLICK_BLOCK.register(JServerEvents::leftClickBlock);
        InteractionEvent.RIGHT_CLICK_BLOCK.register(JServerEvents::rightClickBlock);

        // Send initial values of server config options to the player.
        PlayerEvent.PLAYER_JOIN.register((player) -> ConfigUpdatePacket.sendOptionsToClient(player, ConfigOption.getImmutableOptions().values()));

        LifecycleEvent.SERVER_STARTING.register(JServerConfig::load);
        LifecycleEvent.SERVER_STARTED.register(JServerEvents::finishLoading);
    }


}
