package net.arna.jcraft.common.item;

import lombok.NonNull;
import mod.azure.azurelib.animatable.client.RenderProvider;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import net.arna.jcraft.client.renderer.armor.JotaroArmorRenderer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

public class JotaroClothesItem extends JotaroCoatItem {
    public JotaroClothesItem(ArmorMaterial materialIn, Type slot, Properties builder) {
        super(materialIn, slot, builder);
    }
    @Override
    public void createRenderer(Consumer<Object> consumer) {
        consumer.accept(new RenderProvider() {
            private static GeoArmorRenderer<?> renderer;
            @SuppressWarnings("unchecked")
            @Override public @NonNull HumanoidModel<LivingEntity> getHumanoidArmorModel(
                    LivingEntity livingEntity, ItemStack itemStack, EquipmentSlot equipmentSlot, HumanoidModel<LivingEntity> original) {
                if (renderer == null) renderer = new JotaroArmorRenderer();
                renderer.prepForRender(livingEntity, itemStack, equipmentSlot, original);
                return renderer;
            }});
    }
}
