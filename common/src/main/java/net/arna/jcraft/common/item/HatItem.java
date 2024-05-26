package net.arna.jcraft.common.item;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import net.arna.jcraft.client.renderer.armor.KarsArmorRenderer;
import net.arna.jcraft.client.renderer.armor.PuccisHatRenderer;
import net.arna.jcraft.client.renderer.armor.RedHatRenderer;
import net.arna.jcraft.registry.JItemRegistry;
import net.arna.jcraft.registry.JTagRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.core.animation.AnimationController;
import mod.azure.azurelib.core.animation.AnimationState;
import mod.azure.azurelib.core.object.PlayState;
import mod.azure.azurelib.util.AzureLibUtil;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HatItem extends ArmorItem implements GeoItem {
    private final AnimatableInstanceCache cache = AzureLibUtil.createInstanceCache(this);
    private final Supplier<Object> renderProvider = GeoItem.makeRenderer(this);

    public HatItem(ArmorMaterial materialIn, Properties builder) {
        super(materialIn, Type.HELMET, builder);
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack ingredient) {
        if (ingredient.is(Items.LEATHER)) {
            return true;
        }
        return super.isValidRepairItem(stack, ingredient);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        if (getDefaultInstance().is(JTagRegistry.PROTECTS_FROM_SUN)) {
            tooltip.add(Component.translatable("jcraft.sunprotection.desc"));
        }
        super.appendHoverText(stack, world, tooltip, context);
    }


    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController(this, "controller", 20, this::predicate));
    }

    private PlayState predicate(AnimationState animationState) {
        return PlayState.STOP;
    }


    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }

    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private GeoArmorRenderer<?> renderer;

            @Override
            public @NotNull HumanoidModel<LivingEntity> getHumanoidArmorModel(LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
                if (this.renderer == null) {
                    if (itemStack.is(JItemRegistry.KARS_HEADWRAP.get())) {
                        this.renderer = new KarsArmorRenderer();
                    }
                    if (itemStack.is(JItemRegistry.RED_HAT.get())) {
                        this.renderer = new RedHatRenderer();
                    }
                    if (itemStack.is(JItemRegistry.PUCCIS_HAT.get())) {
                        this.renderer = new PuccisHatRenderer();
                    }
                }

                this.renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);

                return this.renderer;
            }
        });
    }

    @Override
    public Supplier<Object> getRenderProvider() {
        return renderProvider;
    }
}
