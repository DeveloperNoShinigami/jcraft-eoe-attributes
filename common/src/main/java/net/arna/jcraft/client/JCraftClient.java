package net.arna.jcraft.client;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.registry.ReloadListenerRegistry;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import dev.architectury.registry.client.particle.ParticleProviderRegistry;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import lombok.Getter;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.client.gravity.util.GravityChannelClient;
import net.arna.jcraft.client.gui.hud.JCraftAbilityHud;
import net.arna.jcraft.client.net.ClientPacketHandler;
import net.arna.jcraft.client.particle.*;
import net.arna.jcraft.client.registry.*;
import net.arna.jcraft.client.renderer.effects.AttackHitboxEffectRenderer;
import net.arna.jcraft.client.renderer.effects.TimeErasePredictionEffectRenderer;
import net.arna.jcraft.client.rendering.RenderHandler;
import net.arna.jcraft.client.rendering.handler.*;
import net.arna.jcraft.client.util.ClientEntityHandlerImpl;
import net.arna.jcraft.client.util.TrackedKeyBinding;
import net.arna.jcraft.common.attack.core.MoveInputType;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.util.MovementInputType;
import net.arna.jcraft.registry.JParticleTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class JCraftClient {
    // Keybinds
    public static TrackedKeyBinding standSummon, heavyKey, barrageKey, ultKey, special1Key, special2Key, special3Key,
            comboBreaker, cooldownCancel, utility, dash;
    @SuppressWarnings({"ConstantValue", "DataFlowIssue"}) // Not the case here cuz of the lazy getter.
    @Getter(lazy = true)
    private static final Map<TrackedKeyBinding, MoveInputType> bindings = ImmutableMap.<TrackedKeyBinding, MoveInputType>builder()
            .put(standSummon, MoveInputType.STAND_SUMMON)
            .put(TrackedKeyBinding.wrap(Minecraft.getInstance().options.keyAttack), MoveInputType.LIGHT)
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
    private static final TrackedKeyBinding trackedUseKey = TrackedKeyBinding.wrap(Minecraft.getInstance().options.keyUse);
    public static Supplier<DecimalFormat> decimalFormat = Suppliers.memoize(JCraftClient::createDecimalFormat);
    public static KeyMapping menuKey;
    public static boolean comboStarted = false;
    public static int framesSinceComboStarted = 0;

    public static void init() {
        JCraft.setClientEntityHandler(ClientEntityHandlerImpl.INSTANCE);

        AutoConfig.register(JClientConfig.class, JanksonConfigSerializer::new);
        JClientConfig.load();

        ReloadListenerRegistry.register(PackType.CLIENT_RESOURCES, new DecimalFormatUpdater());

        GravityChannelClient.init();

        // Rendering
        JRenderLayerRegistry.init();
        RenderHandler.init();
        JClientEventsRegistry.registerClientEvents();
        JCraftAbilityHud.init();

        InversionShaderHandler.INSTANCE.init();
        ZaWarudoShaderHandler.INSTANCE.init();
        CrimsonShaderHandler.INSTANCE.init();
        EpitaphVignetteShaderHandler.INSTANCE.init();

        // Renderer registration
        JArmorRendererRegistry.registerArmorRenderers();

        // Keybinding registration
        standSummon = TrackedKeyBinding.createAndRegister("key.jcraft.standsummon", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, "key.category.jcraft");
        heavyKey = TrackedKeyBinding.createAndRegister("key.jcraft.heavy", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_R, "key.category.jcraft");
        barrageKey = TrackedKeyBinding.createAndRegister("key.jcraft.barrage", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, "key.category.jcraft");
        ultKey = TrackedKeyBinding.createAndRegister("key.jcraft.ultimate", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_H, "key.category.jcraft");
        special1Key = TrackedKeyBinding.createAndRegister("key.jcraft.special1", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, "key.category.jcraft");
        special2Key = TrackedKeyBinding.createAndRegister("key.jcraft.special2", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, "key.category.jcraft");
        special3Key = TrackedKeyBinding.createAndRegister("key.jcraft.special3", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, "key.category.jcraft");
        //comboBreaker = TrackingKeyBinding.createAndRegister("key.jcraft.combobreaker", InputUtil.Type.KEYSYM, GLFW.GLFW_KEY_LEFT_ALT, "key.category.jcraft");
        cooldownCancel = TrackedKeyBinding.createAndRegister("key.jcraft.cooldowncancel", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_RIGHT_ALT, "key.category.jcraft");
        utility = TrackedKeyBinding.createAndRegister("key.jcraft.utility", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_5, "key.category.jcraft");
        dash = TrackedKeyBinding.createAndRegister("key.jcraft.dash", InputConstants.Type.MOUSE, GLFW.GLFW_MOUSE_BUTTON_4, "key.category.jcraft");

        menuKey = new KeyMapping("key.jcraft.menu", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_KP_DIVIDE, "key.category.jcraft");
        KeyMappingRegistry.register(menuKey);

        ClientPacketHandler.init();

        AttackHitboxEffectRenderer.init();
        TimeErasePredictionEffectRenderer.init();
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
        Options options = Minecraft.getInstance().options;
        return ImmutableMap.<TrackedKeyBinding, MovementInputType>builder()
                .put(TrackedKeyBinding.wrap(options.keyUp), MovementInputType.FORWARD)
                .put(TrackedKeyBinding.wrap(options.keyDown), MovementInputType.BACKWARD)
                .put(TrackedKeyBinding.wrap(options.keyLeft), MovementInputType.LEFT)
                .put(TrackedKeyBinding.wrap(options.keyRight), MovementInputType.RIGHT)
                .put(TrackedKeyBinding.wrap(options.keyJump), MovementInputType.JUMP)
                .put(TrackedKeyBinding.wrap(options.keyShift), MovementInputType.CROUCH)
                .put(dash, MovementInputType.DASH)
                .build();
    }

    /**
     * @return a cleaned-up version of TranslatableText name of button
     */
    public static String generateName(KeyMapping keyBinding) {
        String str = keyBinding.saveString();
        String[] components = str.split("\\.");
        String last = components[components.length - 1];
        String secondLast = components[components.length - 2] + " ";
        if (components[components.length - 2].equals("keyboard")) {
            secondLast = "";
        }
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
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return null;
        }

        return player.getPassengers().stream()
                .filter(e -> e instanceof StandEntity)
                .map(e -> (StandEntity<?, ?>) e)
                .findFirst()
                .orElse(null);
    }

    private static DecimalFormat createDecimalFormat() {
        return new DecimalFormat("#.#", DecimalFormatSymbols.getInstance(
                Locale.forLanguageTag(Minecraft.getInstance().options.languageCode)));
    }

    @Getter
    private static class DecimalFormatUpdater implements PreparableReloadListener {
        private final ResourceLocation fabricId = JCraft.id("decimal_format_updater");

        @Override
        public CompletableFuture<Void> reload(PreparationBarrier synchronizer, ResourceManager manager, ProfilerFiller prepareProfiler,
                                              ProfilerFiller applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
            return synchronizer.wait(Unit.INSTANCE).thenRunAsync(() ->
                    decimalFormat = Suppliers.memoize(JCraftClient::createDecimalFormat), applyExecutor);
        }
    }
}
