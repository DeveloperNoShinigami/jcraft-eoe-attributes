package net.arna.jcraft.forge;

import com.mojang.serialization.Codec;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.platform.forge.EventBuses;
import lombok.Getter;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.argumenttype.AttackArgumentType;
import net.arna.jcraft.common.argumenttype.SpecArgumentType;
import net.arna.jcraft.common.argumenttype.StandArgumentType;
import net.arna.jcraft.common.attack.core.data.MoveActionType;
import net.arna.jcraft.common.attack.core.data.MoveConditionType;
import net.arna.jcraft.common.attack.core.data.MoveSetLoader;
import net.arna.jcraft.common.attack.core.data.MoveType;
import net.arna.jcraft.common.events.EntityTickEvent;
import net.arna.jcraft.forge.capability.impl.entity.GrabCapability;
import net.arna.jcraft.forge.capability.impl.entity.GravityCapability;
import net.arna.jcraft.forge.capability.impl.living.*;
import net.arna.jcraft.forge.capability.impl.world.ShockwaveHandlerCapability;
import net.arna.jcraft.forge.events.ClientSetupEvents;
import net.arna.jcraft.forge.loot.JForgeLootModifiers;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryBuilder;

import static net.arna.jcraft.JCraft.MOD_ID;

@Mod(MOD_ID)
public final class JCraftForge {
    private static final DeferredRegister<MoveType<?>> MOVE_TYPE_REGISTER = DeferredRegister.create(JCraft.id("move_type"), MOD_ID);
    private static final DeferredRegister<MoveConditionType<?>> MOVE_CONDITION_TYPE_REGISTER = DeferredRegister.create(JCraft.id("move_condition_type"), MOD_ID);
    private static final DeferredRegister<MoveActionType<?>> MOVE_ACTION_TYPE_REGISTER = DeferredRegister.create(JCraft.id("move_action_type"), MOD_ID);
    private static final DeferredRegister<MoveActionType<?>> STAND_REGISTER = DeferredRegister.create(JCraft.id("stand"), MOD_ID);
    @Getter
    private static Codec<MoveType<?>> moveTypeCodec;
    @Getter
    private static Codec<MoveConditionType<?>> moveConditionTypeCodec;
    @Getter
    private static Codec<MoveActionType<?>> moveActionTypeCodec;

    public JCraftForge() {
        IEventBus modBus = Mod.EventBusSubscriber.Bus.MOD.bus().get();
        IEventBus forgeBus = Mod.EventBusSubscriber.Bus.FORGE.bus().get();

        // Submit our event bus to let Architectury API register our content on the right time.
        EventBuses.registerModEventBus(MOD_ID, modBus);

        JCraft.init();

        modBus.addListener(this::onInitializeCommon);
        modBus.addListener(ClientSetupEvents::onInitializeClient);

        JForgeLootModifiers.register(modBus);

        //DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> JCraftClient::init);
        JNetworkingForge.initServer();

        registerMoveTypes(modBus);
        registerMoveConditionTypes(modBus);
        registerMoveActionTypes(modBus);

        EntityTickEvent.ENTITY_PRE.register(JCraftForge::tickEntityCaps);
        TickEvent.ServerLevelTick.SERVER_LEVEL_POST.register(JCraftForge::tickWorldCaps);
        ArgumentTypeInfos.registerByClass(StandArgumentType.class,  SingletonArgumentInfo.contextFree(StandArgumentType::stand));
        ArgumentTypeInfos.registerByClass(SpecArgumentType.class,  SingletonArgumentInfo.contextFree(SpecArgumentType::spec));
        ArgumentTypeInfos.registerByClass(AttackArgumentType.class,  SingletonArgumentInfo.contextFree(AttackArgumentType::attack));
    }

    @SubscribeEvent
    public void onInitializeCommon(final FMLCommonSetupEvent event) {
        JCraft.postInit();
    }

    public static void tickWorldCaps(Level world) {
        ShockwaveHandlerCapability.getCapability(world).tick();
    }

    public static void tickEntityCaps(Entity entity) {
        GrabCapability.getCapability(entity).tick();
        GravityCapability.getCapability(entity).tick();

        if (entity instanceof LivingEntity living) {
            GravityShiftCapability.getCapability(living).tick();
            BombTrackerCapability.getCapability(living).tick();
            CooldownsCapability.getCapability(living).tick();
            HitPropertyCapability.getCapability(living).tick();
            MiscCapability.getCapability(living).tick();

            VampireCapability.getCapability(living).tick();
        }
    }

    private void registerMoveTypes(IEventBus modBus) {
        MoveSetLoader.registerMoves(MOVE_TYPE_REGISTER::register);

        MOVE_TYPE_REGISTER.makeRegistry(() -> RegistryBuilder.<MoveType<?>>of()
                .onCreate((r, m) -> moveTypeCodec = r.getCodec()));
        MOVE_TYPE_REGISTER.register(modBus);
    }

    private void registerMoveConditionTypes(IEventBus modBus) {
        MoveSetLoader.registerConditions(MOVE_CONDITION_TYPE_REGISTER::register);

        MOVE_CONDITION_TYPE_REGISTER.makeRegistry(() -> RegistryBuilder.<MoveConditionType<?>>of()
                .onCreate((r, m) -> moveConditionTypeCodec = r.getCodec()));
        MOVE_CONDITION_TYPE_REGISTER.register(modBus);
    }

    private void registerMoveActionTypes(IEventBus modBus) {
        MoveSetLoader.registerActions(MOVE_ACTION_TYPE_REGISTER::register);

        MOVE_ACTION_TYPE_REGISTER.makeRegistry(() -> RegistryBuilder.<MoveActionType<?>>of()
                .onCreate((r, m) -> moveActionTypeCodec = r.getCodec()));
        MOVE_ACTION_TYPE_REGISTER.register(modBus);
    }

    private void registerStands(IEventBus modBus) {

    }
}
