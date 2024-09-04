package net.arna.jcraft.forge;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.argumenttype.AttackArgumentType;
import net.arna.jcraft.common.argumenttype.SpecArgumentType;
import net.arna.jcraft.common.argumenttype.StandArgumentType;
import net.arna.jcraft.common.events.EntityTickEvent;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.GravityCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.arna.jcraft.forge.events.ClientSetupEvents;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import static net.arna.jcraft.JCraft.MOD_ID;

@Mod(MOD_ID)
public final class JCraftForge {

    public JCraftForge() {
        var modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        var forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(MOD_ID, modBus);

        JCraft.init();

        modBus.addListener(this::onInitializeCommon);
        modBus.addListener(ClientSetupEvents::onInitializeClient);

        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> JCraftClient::init);
        JNetworkingForge.init();

        EntityTickEvent.ENTITY_PRE.register(this::tickEntityCaps);
        TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(this::tickWorldCaps);
        ClientTickEvent.ClientLevel.CLIENT_LEVEL_POST.register(this::tickWorldCaps);
        ArgumentTypeInfos.registerByClass(StandArgumentType.class,  SingletonArgumentInfo.contextFree(StandArgumentType::stand));
        ArgumentTypeInfos.registerByClass(SpecArgumentType.class,  SingletonArgumentInfo.contextFree(SpecArgumentType::spec));
        ArgumentTypeInfos.registerByClass(AttackArgumentType.class,  SingletonArgumentInfo.contextFree(AttackArgumentType::attack));
    }

    @SubscribeEvent
    public void onInitializeCommon(final FMLCommonSetupEvent event) {
        JCraft.initStandIOMaps();
        JCraft.initDispenserBehaviors();
    }

    private void tickWorldCaps(Level world) {
        ShockwaveHandlerCapability.getCapability(world).tick();
    }

    private void tickEntityCaps(Entity entity) {
        GrabCapability.getCapability(entity).tick();
        GravityCapability.getCapability(entity).tick();

        if (entity instanceof LivingEntity living) {
            BombTrackerCapability.getCapability(living).tick();
            CooldownsCapability.getCapability(living).tick();
            HitPropertyCapability.getCapability(living).tick();
            MiscCapability.getCapability(living).tick();

            VampireCapability.getCapability(living).tick();
        }
    }
}
