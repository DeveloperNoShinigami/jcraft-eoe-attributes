package net.arna.jcraft.client.renderer.armor;

import mod.azure.azurelib.render.AzRendererPipelineContext;
import mod.azure.azurelib.render.armor.AzArmorRendererPipelineContext;
import mod.azure.azurelib.render.armor.bone.AzArmorBoneContext;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Function;

public class DiavoloArmorRenderer extends ArmorRenderer {

    public static final String ID = "diavoloclothes";

    public DiavoloArmorRenderer() {
        super(() -> new ArmorAnimator(ID)/*, b -> b.setPrerenderEntry(prerenderEntry())*/, ID);
    }

    private static Function<AzRendererPipelineContext<UUID, ItemStack>, AzRendererPipelineContext<UUID, ItemStack>> prerenderEntry(/*final @NonNull */) {
        return pc -> {
            final AzArmorRendererPipelineContext apc = (AzArmorRendererPipelineContext) pc;
            final AzArmorBoneContext boneContext = apc.boneContext();
    //        apc.con
            boneContext.body.setHidden(true);
            return pc;
        };
    }

    /*
    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllVisible(false);

        if (currentSlot == EquipmentSlot.LEGS) {
            setBoneVisible(this.body, true);
            setBoneVisible(this.leftLeg, true);
            setBoneVisible(this.rightLeg, true);
        }
        else if (currentSlot == EquipmentSlot.FEET) {
            setBoneVisible(this.leftBoot, true);
            setBoneVisible(this.rightBoot, true);
        }
        else if (currentSlot == EquipmentSlot.HEAD) {
            setBoneVisible(this.head, true);
        }
    }*/
}
