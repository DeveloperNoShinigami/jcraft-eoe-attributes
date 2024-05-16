package net.arna.jcraft.forge;

import com.mojang.brigadier.arguments.ArgumentType;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import net.arna.jcraft.client.JCraftClient;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.registry.JEntityRendererRegister;
import net.arna.jcraft.client.registry.JModelPredicateProviderRegistry;
import net.arna.jcraft.client.renderer.block.CoffinTileRenderer;
import net.arna.jcraft.common.argumenttype.AttackArgumentType;
import net.arna.jcraft.common.argumenttype.SpecArgumentType;
import net.arna.jcraft.common.argumenttype.StandArgumentType;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.events.ServerEntityTickEvent;
import net.arna.jcraft.common.item.*;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.TimeStopCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.arna.jcraft.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import net.arna.jcraft.JCraft;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
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

        modBus.addListener(this::onInitializeCommon);
        modBus.addListener(this::onInitializeClient);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> JCraftClient::init);

        JNetworkingForge.init();

        ServerEntityTickEvent.ENTITY_POST.register(this::tickEntityCaps);
        TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(this::tickWorldCaps);
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

    @SubscribeEvent
    public void onInitializeClient(final FMLClientSetupEvent event) {
        //JModelPredicateProviderRegistry.register();
        JEntityRendererRegister.registerEntityRenderers();
        //TODO BlockEntityRendererFactories.register(JBlockEntityTypeRegistry.COFFIN_TILE.get(), CoffinTileRenderer::new);
        // Run when the MinecraftClient instance is fully initialized.
        Minecraft.getInstance().tell(EpitaphOverlay::preload);
    }

    private void tickWorldCaps(ServerLevel serverWorld) {
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
