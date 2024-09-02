package net.arna.jcraft.forge;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.argumenttype.AttackArgumentType;
import net.arna.jcraft.common.argumenttype.SpecArgumentType;
import net.arna.jcraft.common.argumenttype.StandArgumentType;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.events.ServerEntityTickEvent;
import net.arna.jcraft.common.item.DIOsDiaryItem;
import net.arna.jcraft.common.item.GreenBabyItem;
import net.arna.jcraft.common.item.LivingArrowItem;
import net.arna.jcraft.common.item.RequiemArrowItem;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.GravityCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.arna.jcraft.forge.events.ClientSetupEvents;
import net.arna.jcraft.registry.JItemRegistry;
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

        ServerEntityTickEvent.ENTITY_POST.register(this::tickEntityCaps);
        TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(this::tickWorldCaps);
        ClientTickEvent.ClientLevel.CLIENT_LEVEL_POST.register(this::tickWorldCaps);
        ArgumentTypeInfos.registerByClass(StandArgumentType.class,  SingletonArgumentInfo.contextFree(StandArgumentType::stand));
        ArgumentTypeInfos.registerByClass(SpecArgumentType.class,  SingletonArgumentInfo.contextFree(SpecArgumentType::spec));
        ArgumentTypeInfos.registerByClass(AttackArgumentType.class,  SingletonArgumentInfo.contextFree(AttackArgumentType::attack));
    }

    @SubscribeEvent
    public void onInitializeCommon(final FMLCommonSetupEvent event) {
        ((GreenBabyItem)JItemRegistry.GREEN_BABY.get()).standIOMap.put(StandType.WHITE_SNAKE, StandType.C_MOON);
        ((DIOsDiaryItem)JItemRegistry.DIOS_DIARY.get()).standIOMap.put(StandType.C_MOON, StandType.MADE_IN_HEAVEN);
        ((DIOsDiaryItem)JItemRegistry.DIOS_DIARY.get()).standIOMap.put(StandType.THE_WORLD, StandType.THE_WORLD_OVER_HEAVEN);


        ((LivingArrowItem)JItemRegistry.LIVING_ARROW.get()).standIOMap.put(StandType.KILLER_QUEEN, StandType.KILLER_QUEEN_BITES_THE_DUST);
        ((LivingArrowItem)JItemRegistry.LIVING_ARROW.get()).standIOMap.put(StandType.STAR_PLATINUM, StandType.STAR_PLATINUM_THE_WORLD);


        ((RequiemArrowItem)JItemRegistry.REQUIEM_ARROW.get()).standIOMap.put(StandType.GOLD_EXPERIENCE, StandType.GOLD_EXPERIENCE_REQUIEM);
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
