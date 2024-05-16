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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
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
        if (event.getObject() instanceof Player player) {
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
    public static void attachWorldCapability(AttachCapabilitiesEvent<Level> event) {
        event.addCapability(JCraft.id("shock_capability"), new JCapabilityProvider<>(ShockwaveHandlerCapability.CAPABILITY, () -> new ShockwaveHandlerCapability(event.getObject())));
    }

    @SubscribeEvent
    public static void entityJoin(EntityJoinLevelEvent event) {
        if (event.getEntity() instanceof ServerPlayer serverPlayer) {
            PhCapability.syncEntityCapability(serverPlayer);
            SpecCapability.syncEntityCapability(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void syncEntityCapability(PlayerEvent.StartTracking event) {
        if (event.getTarget().level() instanceof ServerLevel) {
            Entity entity = event.getTarget();

            GrabCapability.syncEntityCapability(event);
            TimeStopCapability.syncEntityCapability(event);

            if (entity instanceof LivingEntity) {
                BombTrackerCapability.syncEntityCapability(event);
                CooldownsCapability.syncEntityCapability(event);
                HitPropertyCapability.syncEntityCapability(event);
                MiscCapability.syncEntityCapability(event);
                StandCapability.syncEntityCapability(event);
                VampireCapability.syncEntityCapability(event);
            }
            if (entity instanceof Player) {
                PhCapability.syncEntityCapability(event);
                SpecCapability.syncEntityCapability(event);
            }
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
