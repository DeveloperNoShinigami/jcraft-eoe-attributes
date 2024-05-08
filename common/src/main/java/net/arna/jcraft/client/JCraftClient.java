package net.arna.jcraft.client;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import dev.architectury.event.events.client.ClientGuiEvent;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.gravity.util.GravityChannelClient;
import net.arna.jcraft.client.gui.hud.EpitaphOverlay;
import net.arna.jcraft.client.net.ClientPacketHandler;
import net.arna.jcraft.client.particle.*;
import net.arna.jcraft.client.registry.*;
import net.arna.jcraft.client.renderer.block.CoffinTileRenderer;
import net.arna.jcraft.client.renderer.effects.*;
import net.arna.jcraft.client.rendering.RenderHandler;
import net.arna.jcraft.client.rendering.skybox.SkyBoxManager;
import net.arna.jcraft.client.util.ClientEntityHandlerImpl;
import net.arna.jcraft.client.util.TrackedKeyBinding;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.component.living.CommonCooldownsComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.network.c2s.PlayerInputPacket;
import net.arna.jcraft.common.network.c2s.StandBlockPacket;
import net.arna.jcraft.common.util.*;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JBlockEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JPacketRegistry;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Unit;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.profiler.Profiler;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

import static net.arna.jcraft.client.gui.hud.JCraftAbilityHud.getHudX;
import static net.arna.jcraft.client.util.JClientUtils.activeTimestops;

@Environment(EnvType.CLIENT)
public class JCraftClient {
    // Keybinds
    public static TrackedKeyBinding standSummon, heavyKey, barrageKey, ultKey, special1Key, special2Key, special3Key,
            comboBreaker, cooldownCancel, utility, dash;
    @SuppressWarnings({"ConstantValue", "DataFlowIssue"}) // Not the case here cuz of the lazy getter.
    @Getter(lazy = true)
    private static final Map<TrackedKeyBinding, MoveInputType> bindings = ImmutableMap.<TrackedKeyBinding, MoveInputType>builder()
            .put(standSummon, MoveInputType.STAND_SUMMON)
            .put(TrackedKeyBinding.wrap(MinecraftClient.getInstance().options.attackKey), MoveInputType.LIGHT)
            .put(heavyKey, MoveInputType.HEAVY)
            .put(barrageKey, MoveInputType.BARRAGE)
            .put(special1Key, MoveInputType.SPECIAL1)
            .put(special2Key, MoveInputType.SPECIAL2)
            .put(special3Key, MoveInputType.SPECIAL3)
            .put(ultKey, MoveInputType.ULTIMATE)
            .put(utility, MoveInputType.UTILITY)
            .build();
    @Getter(lazy = true)
    private static final Map<TrackedKeyBinding, MovementInputType> movementBindings = createMovementBindingsMap();
    @Getter(lazy = true)
    private static final TrackedKeyBinding trackedUseKey = TrackedKeyBinding.wrap(MinecraftClient.getInstance().options.useKey);
    public static Supplier<DecimalFormat> decimalFormat = Suppliers.memoize(JCraftClient::createDecimalFormat);
    public static boolean comboStarted = false;
    public static int framesSinceComboStarted = 0;

    public static void init() {
        JCraft.setClientEntityHandler(ClientEntityHandlerImpl.INSTANCE);

        AutoConfig.register(JClientConfig.class, JanksonConfigSerializer::new);
        JClientConfig.load();

        ReloadListenerRegistry.register(ResourceType.CLIENT_RESOURCES, new DecimalFormatUpdater());

        GravityChannelClient.init();

        // Rendering
        JRenderLayerRegistry.init();
        RenderHandler.init();
        JClientEventsRegistry.registerClientEvents();
        JModelPredicateProviderRegistry.register();
/*TODO
        InversionShaderHandler.INSTANCE.init();
        ZaWarudoShaderHandler.INSTANCE.init();
        CrimsonShaderHandler.INSTANCE.init();
        EpitaphVignetteShaderHandler.INSTANCE.init();
        UIShaderHandler.INSTANCE.init(); // Should be last

 */

        // Particle registration
        ParticleProviderRegistry.register(JParticleTypeRegistry.COMBO_BREAK, ComboBreakerParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.COOLDOWN_CANCEL, CooldownCancelParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.HITSPARK_1, provider -> new HitsparkParticle.Factory(provider, 0.4f, 5));
        ParticleProviderRegistry.register(JParticleTypeRegistry.HITSPARK_2, provider -> new HitsparkParticle.Factory(provider, 0.66f, 6));
        ParticleProviderRegistry.register(JParticleTypeRegistry.HITSPARK_3, provider -> new HitsparkParticle.Factory(provider, 1f, 8));
        ParticleProviderRegistry.register(JParticleTypeRegistry.KCPARTICLE, KCParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BACKSTAB, BackstabParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.SPEED_PARTICLE, SpeedParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BITES_THE_DUST, BitesTheDustParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BOOM_1, BoomParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.PIXEL, PixelParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.BLOCKSPARK, provider -> new BlocksparkParticle.Factory(provider, 0.15f));
        ParticleProviderRegistry.register(JParticleTypeRegistry.GO, GoParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.AURA_ARC, AuraArcParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.AURA_BLOB, AuraBlobParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.INVERSION, InversionParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.SUN_LOCK_ON, BackstabParticle.Factory::new); // 9 frames, reusing
        ParticleProviderRegistry.register(JParticleTypeRegistry.PURPLE_HAZE_CLOUD, PurpleHazeCloudParticle.Factory::new);
        ParticleProviderRegistry.register(JParticleTypeRegistry.PURPLE_HAZE_PARTICLE, PurpleHazeErraticParticle.Factory::new);

        // Renderer registration
        JEntityRendererRegister.registerEntityRenderers();
        JArmorRendererRegistry.registerArmorRenderers();
        BlockEntityRendererFactories.register(JBlockEntityTypeRegistry.COFFIN_TILE.get(), CoffinTileRenderer::new);

        // Keybinding registration
        standSummon = TrackedKeyBinding.createAndRegister("key.jcraft.standsummon", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.category.jcraft");
        heavyKey = TrackedKeyBinding.createAndRegister("key.jcraft.heavy", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.category.jcraft");
        barrageKey = TrackedKeyBinding.createAndRegister("key.jcraft.barrage", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.category.jcraft");
        ultKey = TrackedKeyBinding.createAndRegister("key.jcraft.ultimate", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.category.jcraft");
        special1Key = TrackedKeyBinding.createAndRegister("key.jcraft.special1", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.jcraft");
        special2Key = TrackedKeyBinding.createAndRegister("key.jcraft.special2", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category.jcraft");
        special3Key = TrackedKeyBinding.createAndRegister("key.jcraft.special3", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.category.jcraft");
        //comboBreaker = TrackingKeyBinding.createAndRegister("key.jcraft.combobreaker", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.category.jcraft");
        cooldownCancel = TrackedKeyBinding.createAndRegister("key.jcraft.cooldowncancel", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "key.category.jcraft");
        utility = TrackedKeyBinding.createAndRegister("key.jcraft.utility", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, "key.category.jcraft");
        dash = TrackedKeyBinding.createAndRegister("key.jcraft.dash", InputUtil.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_4, "key.category.jcraft");

        ClientPacketHandler.init();

        AttackHitboxEffectRenderer.init();
        TimeErasePredictionEffectRenderer.init();

        // Run when the MinecraftClient instance is fully initialized.
        MinecraftClient.getInstance().send(EpitaphOverlay::preload);

        if (!FabricLoader.getInstance().isDevelopmentEnvironment()) return;

        //Identifier itemId = JItemRegistry.ITEMS.get(JItemRegistry.DEBUG_WAND);
        //BigItemRenderer itemRenderer = new BigItemRenderer(itemId);

        /*TODO
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES).registerReloadListener(itemRenderer);
        BuiltinItemRendererRegistry.INSTANCE.register(JObjectRegistry.DEBUG_WAND, itemRenderer);

        ModelLoadingRegistry.INSTANCE.registerModelProvider((manager, out) -> {
            out.accept(new ModelIdentifier(new Identifier(itemId + "_gui"), "inventory"));
            out.accept(new ModelIdentifier(new Identifier(itemId + "_handheld"), "inventory"));
        });

         */
    }

    /// TEXT HUD
    public static final List<String> comboRemarks = List.of("admin rdm!!!", "baby combo", "caught lackin", "kinda ez", "skill issue", "cancelled on twitter", "sent to bulgaria", "down bad");
    public static int comboCounter = 0;
    public static float damageScaling = 1.00f;
    public static int framesSinceCounted = 0;



    public static void markComboStarted() {
        comboStarted = true;
        framesSinceComboStarted = 0;
    }

    private static Map<TrackedKeyBinding, MovementInputType> createMovementBindingsMap() {
        GameOptions options = MinecraftClient.getInstance().options;
        return ImmutableMap.<TrackedKeyBinding, MovementInputType>builder()
                .put(TrackedKeyBinding.wrap(options.forwardKey), MovementInputType.FORWARD)
                .put(TrackedKeyBinding.wrap(options.backKey), MovementInputType.BACKWARD)
                .put(TrackedKeyBinding.wrap(options.leftKey), MovementInputType.LEFT)
                .put(TrackedKeyBinding.wrap(options.rightKey), MovementInputType.RIGHT)
                .put(TrackedKeyBinding.wrap(options.jumpKey), MovementInputType.JUMP)
                .put(TrackedKeyBinding.wrap(options.sneakKey), MovementInputType.CROUCH)
                .put(dash, MovementInputType.DASH)
                .build();
    }

    /**
     * @return a cleaned-up version of TranslatableText name of button
     */
    public static String generateName(KeyBinding keyBinding) {
        String str = keyBinding.getBoundKeyTranslationKey();
        String[] components = str.split("\\.");
        String last = components[components.length - 1];
        String secondLast = components[components.length - 2] + " ";
        if (components[components.length - 2].equals("keyboard")) secondLast = "";
        return StringUtils.capitalize(secondLast) + StringUtils.capitalize(last);
    }

    public static <E extends Enum<E>> Object2BooleanMap<E> getChangedInputs(Map<TrackedKeyBinding, E> bindings) {
        return bindings.entrySet().stream()
                .filter(entry -> entry.getKey().isChangedThisTick())
                .collect(Object2BooleanOpenHashMap::new, (map, entry) -> map.put(entry.getValue(), entry.getKey().isPressedThisTick()),
                        Object2BooleanMap::putAll);
    }

    @Nullable
    public static StandEntity<?, ?> getStandEntity() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return null;

        return player.getPassengerList().stream()
                .filter(e -> e instanceof StandEntity)
                .map(e -> (StandEntity<?, ?>) e)
                .findFirst()
                .orElse(null);
    }

    private static DecimalFormat createDecimalFormat() {
        return new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(
                Locale.forLanguageTag(MinecraftClient.getInstance().options.language)));
    }

    @Getter
    private static class DecimalFormatUpdater implements ResourceReloader {
        private final Identifier fabricId = JCraft.id("decimal_format_updater");

        @Override
        public CompletableFuture<Void> reload(Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler,
                                              Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
            return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() ->
                    decimalFormat = Suppliers.memoize(JCraftClient::createDecimalFormat), applyExecutor);
        }
    }
}
