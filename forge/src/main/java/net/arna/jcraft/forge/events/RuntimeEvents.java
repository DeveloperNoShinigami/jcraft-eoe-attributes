package net.arna.jcraft.forge.events;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.forge.JCraftForge;
import net.arna.jcraft.forge.capability.api.JCapabilityProvider;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.player.PhCapability;
import net.arna.jcraft.forge.capability.impl.player.SpecCapability;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RuntimeEvents {

    @SubscribeEvent
    public static void attachEntityCapability(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof PlayerEntity player) {
            event.addCapability(JCraft.id("ph_capability"), new JCapabilityProvider<>(PhCapability.CAPABILITY, () -> new PhCapability(player)));
            event.addCapability(JCraft.id("spec_capability"), new JCapabilityProvider<>(SpecCapability.CAPABILITY, () -> new SpecCapability(player)));
        }
        if (event.getObject() instanceof LivingEntity living) {
            event.addCapability(JCraft.id("bomb_capability"), new JCapabilityProvider<>(BombTrackerCapability.CAPABILITY, () -> new BombTrackerCapability(living)));
            event.addCapability(JCraft.id("cd_capability"), new JCapabilityProvider<>(CooldownsCapability.CAPABILITY, () -> new CooldownsCapability(living)));
            event.addCapability(JCraft.id("hit_capability"), new JCapabilityProvider<>(HitPropertyCapability.CAPABILITY, () -> new HitPropertyCapability(living)));
            event.addCapability(JCraft.id("misc_capability"), new JCapabilityProvider<>(MiscCapability.CAPABILITY, () -> new MiscCapability(living)));
            event.addCapability(JCraft.id("stand_capability"), new JCapabilityProvider<>(StandCapability.CAPABILITY, () -> new StandCapability(living)));
            event.addCapability(JCraft.id("vampire_capability"), new JCapabilityProvider<>(VampireCapability.CAPABILITY, () -> new VampireCapability(living)));
        }

        event.addCapability(JCraft.id("grab_capability"), new JCapabilityProvider<>(GrabCapability.CAPABILITY, () -> new GrabCapability(event.getObject())));
        event.addCapability(JCraft.id("time_capability"), new JCapabilityProvider<>(TimeStopCapability.CAPABILITY, () -> new TimeStopCapability(event.getObject())));
    }

    @SubscribeEvent
    public static void attachWorldCapability(AttachCapabilitiesEvent<World> event) {
        event.addCapability(JCraft.id("shock_capability"), new JCapabilityProvider<>(ShockwaveHandlerCapability.CAPABILITY, () -> new ShockwaveHandlerCapability(event.getObject())));
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayerEntity serverPlayer) {
            JCraftForge.syncSelf(PhCapability.getCapabilityOptional(serverPlayer), serverPlayer);
            JCraftForge.syncSelf(SpecCapability.getCapabilityOptional(serverPlayer), serverPlayer);
        }
    }

    @SubscribeEvent
    public static void syncPlayerCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget() instanceof PlayerEntity player && player.getWorld() instanceof ServerWorld) {
            JCraftForge.syncTracking(PhCapability.getCapabilityOptional(player), player);
            JCraftForge.syncTracking(SpecCapability.getCapabilityOptional(player), player);
        }
    }

    @SubscribeEvent
    public static void playerTick(TickEvent.PlayerTickEvent event) {
        PhCapability.getCapabilityOptional(event.player).ifPresent(c -> {});
        SpecCapability.getCapabilityOptional(event.player).ifPresent(c -> {});
    }

    @SubscribeEvent
    public static void playerClone(PlayerEvent.Clone event) {
        PhCapability.getCapabilityOptional(event.getOriginal()).ifPresent(o -> PhCapability.getCapabilityOptional(event.getEntity()).ifPresent(c -> c.deserializeNBT(o.serializeNBT())));
        SpecCapability.getCapabilityOptional(event.getOriginal()).ifPresent(o -> SpecCapability.getCapabilityOptional(event.getEntity()).ifPresent(c -> c.deserializeNBT(o.serializeNBT())));
    }
}
