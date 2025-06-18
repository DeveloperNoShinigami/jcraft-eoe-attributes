package net.arna.jcraft.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.spec.SpecType;
import net.arna.jcraft.api.spec.SpecTypeUtil;
import net.arna.jcraft.common.item.SpecDiscItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SpecDiscItemRenderer {
    public static void render(final ItemStack stack, final ItemDisplayContext displayContext, final PoseStack poseStack,
                              final MultiBufferSource buffer, final int packedLight, final int packedOverlay) {
        // Pop pose to undo the transformations applied for the spec disc model.
        poseStack.popPose();
        poseStack.pushPose();
        final ItemStack renderStack = new ItemStack(Blocks.DIRT);

        final SpecType type = SpecDiscItem.getSpecType(stack);

        final ResourceLocation modelLoc = SpecTypeUtil.isNone(type) ? JCraft.id("spec_disc") :
                type.getId().withPath("spec_disc_%s"::formatted);

        final BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(modelLoc, "inventory"));
        Minecraft.getInstance().getItemRenderer().render(renderStack, displayContext, false, poseStack, buffer,
                packedLight, packedOverlay, model);
    }
}
