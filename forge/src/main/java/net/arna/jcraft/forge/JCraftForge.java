package net.arna.jcraft.forge;

import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.common.events.ServerEntityTickEvent;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import net.arna.jcraft.JCraft;
import net.minecraftforge.network.PacketDistributor;
import org.stringtemplate.v4.misc.Misc;

import static net.arna.jcraft.JCraft.MOD_ID;

@Mod(MOD_ID)
public final class JCraftForge {

    public JCraftForge() {
        var modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        var forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(MOD_ID, modBus);

        JCraft.init();

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> JCraftClient::init);

        JNetworkingForge.init();

        ServerEntityTickEvent.ENTITY_POST.register(this::tickEntityCaps);
        TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(this::tickWorldCaps);
    }

    private void tickWorldCaps(ServerWorld serverWorld) {
        ShockwaveHandlerCapability.getCapability(serverWorld).tick();
    }

    private void tickEntityCaps(Entity entity) {
        GrabCapability.getCapability(entity).tick();

        if (entity instanceof LivingEntity living) {

            BombTrackerCapability.getCapability(living).tick();
            CooldownsCapability.getCapability(living).tick();
            HitPropertyCapability.getCapability(living).tick();
            MiscCapability.getCapability(living).tick();

            VampireCapability.getCapability(living).tick();
        }
    }
}
