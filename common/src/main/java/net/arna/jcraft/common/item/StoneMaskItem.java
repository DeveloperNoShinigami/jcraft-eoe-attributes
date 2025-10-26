package net.arna.jcraft.common.item;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.platform.Platform;
import it.unimi.dsi.fastutil.ints.Int2IntMap;
import it.unimi.dsi.fastutil.ints.Int2IntOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.component.player.CommonSpecComponent;
import net.arna.jcraft.api.registry.JSoundRegistry;
import net.arna.jcraft.api.registry.JSpecTypeRegistry;
import net.arna.jcraft.common.network.s2c.StoneMaskClenchPacket;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.fabricmc.api.EnvType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class StoneMaskItem extends ArmorItem {
    private static final int CLENCH_DURATION = 100; // Duration in ticks for which the clench animation plays
    private static final Int2IntMap CLENCH = new Int2IntOpenHashMap();
    //private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    //private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    static {
        if (Platform.getEnv() == EnvType.CLIENT) {
            ClientTickEvent.CLIENT_POST.register(client -> {
                IntSet toRemove = new IntOpenHashSet();
                for (int i : CLENCH.keySet()) {
                    int newTime = CLENCH.get(i) - 1;
                    if (newTime <= 0) {
                        toRemove.add(i);
                        continue;
                    }
                    CLENCH.put(i, newTime);
                }

                toRemove.forEach(CLENCH::remove);
            });
        }
    }

    public StoneMaskItem(ArmorMaterial materialIn, Type slot, Properties builder) {
        super(materialIn, slot, builder);
    }

    public static void clench(final LivingEntity entity) {
        if (entity == null || !entity.isAlive()) {
            return;
        }

        if (Platform.getEnv() == EnvType.CLIENT) {
            CLENCH.put(entity.getId(), CLENCH_DURATION);

            // entity.level() is a server level on singleplayer.
            ClientLevel level = Minecraft.getInstance().level;
            if (level == null) {
                return;
            }

            level.playLocalSound(entity.blockPosition(), JSoundRegistry.VAMPIRE_SPEC_CHANGE.get(), SoundSource.PLAYERS,
                    1f, 1f, true);
        } else {
            StoneMaskClenchPacket.sendStoneMaskClench((ServerPlayer) entity);
        }
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level world, @NotNull Entity entity, int slot, boolean selected) {
        super.inventoryTick(stack, world, entity, slot, selected);

        if (slot != EquipmentSlot.HEAD.getIndex()) {
            return;
        }

        // The wasRecentlyAttacked check only succeeds on the server side.
        if (entity instanceof ServerPlayer player && JCraft.wasRecentlyAttacked(player.getCombatTracker())) {
            CommonSpecComponent specComponent = JComponentPlatformUtils.getSpecData(player);
            if (specComponent.getType() != JSpecTypeRegistry.VAMPIRE.get()) {
                specComponent.setType(JSpecTypeRegistry.VAMPIRE.get());
                clench(player);
            }
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, List<Component> tooltip, @NotNull TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.stonemask.desc"));
        super.appendHoverText(stack, world, tooltip, context);
    }

    /*
    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 5, this::predicate));
    }

    private PlayState predicate(AnimationState<StoneMaskItem> state) {
        Entity entity = state.getData(DataTickets.ENTITY);
        if (entity instanceof LivingEntity livingEntity) {
            RawAnimation anim = CLENCH.containsKey(livingEntity.getId())
                    ? RawAnimation.begin().thenPlayAndHold("animation.stone_mask.clench")
                    : RawAnimation.begin().thenLoop("animation.stone_mask.dormant");

            state.getController().setAnimation(anim);

        }
        return PlayState.CONTINUE;
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private static GeoArmorRenderer<?> renderer;
            @SuppressWarnings("unchecked")
            @Override public @NonNull HumanoidModel<LivingEntity> getHumanoidArmorModel(
                    LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
                if (renderer == null) renderer = new StoneMaskRenderer();
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return renderer;
            }});
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }*/
}
