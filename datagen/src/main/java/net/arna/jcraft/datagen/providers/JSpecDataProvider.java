package net.arna.jcraft.datagen.providers;

import lombok.Getter;
import net.arna.jcraft.api.JRegistries;
import net.arna.jcraft.api.spec.SpecData;
import net.arna.jcraft.api.spec.SpecType;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@Getter
public class JSpecDataProvider extends JAttackerDataProvider<SpecType, SpecData> {
    private final String name = "Spec Data";

    public JSpecDataProvider(FabricDataOutput dataOutput) {
        super(dataOutput, PackOutput.Target.DATA_PACK, "specs", SpecData.CODEC,
                JRegistries.SPEC_TYPE_REGISTRY, SpecData.class, "DATA");
    }

    @SuppressWarnings("DataFlowIssue")
    @Override
    protected Class<?> getHolderClass(SpecType type) {
        // Create fake LivingEntity to get a fake spec instance.
        return type.createSpec(new LivingEntity(EntityType.PIG, null) {
            @Override
            public @NotNull Iterable<ItemStack> getArmorSlots() {
                return List.of();
            }

            @Override
            public @NotNull ItemStack getItemBySlot(@NotNull EquipmentSlot slot) {
                return ItemStack.EMPTY;
            }

            @Override
            public void setItemSlot(@NotNull EquipmentSlot slot, @NotNull ItemStack stack) {

            }

            @Override
            public @NotNull HumanoidArm getMainArm() {
                return HumanoidArm.RIGHT;
            }
        }).getClass();
    }
}
