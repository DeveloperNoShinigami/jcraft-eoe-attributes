package net.arna.jcraft.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.api.stand.StandTypeUtil;
import net.arna.jcraft.common.item.StandDiscItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class StandDiscItemRenderer {
    public static void render(final ItemStack stack, final ItemDisplayContext displayContext, final PoseStack poseStack,
                              final MultiBufferSource buffer, final int packedLight, final int packedOverlay) {
        // Pop pose to undo the transformations applied for the stand disc model.
        poseStack.popPose();
        poseStack.pushPose();
        final ItemStack renderStack = new ItemStack(Blocks.DIRT);

        final StandType type = StandDiscItem.getStandType(stack);
        int skin = StandDiscItem.getSkin(stack);

        final ResourceLocation modelLoc = StandTypeUtil.isNone(type) ? JCraft.id("stand_disc") :
                type.getId().withPath(p -> "stand_disc_%s_%s".formatted(p, skin));

        final BakedModel model = Minecraft.getInstance().getModelManager().getModel(new ModelResourceLocation(modelLoc, "inventory"));
        Minecraft.getInstance().getItemRenderer().render(renderStack, displayContext, false, poseStack, buffer,
                packedLight, packedOverlay, model);

//        if (StandTypeUtil.isNone(type)) return;
//
//        Tesselator tess = Tesselator.getInstance();
//        BufferBuilder buff = tess.getBuilder();
//        buff.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
//
//        ResourceLocation overlayTexture = type.getId().withPath(p -> "textures/item/stands/%s_%s.png".formatted(p, skin));
//        RenderSystem.setShaderTexture(0, overlayTexture);
//        RenderSystem.setShaderColor(1f, 1f, 1f, 1f);
//
//        model.getTransforms().getTransform(displayContext).apply(false, poseStack);
//        Matrix4f pose = poseStack.last().pose();
//
//        buff.vertex(pose, -0.5f, -0.5f, -1000)
//                .color(1f, 1f, 1f, 1f)
//                .uv(0f, 1f)
//                .uv2(packedLight)
//                .endVertex();
//
//        buff.vertex(pose, 0.5f, -0.5f, -1000)
//                .color(1f, 1f, 1f, 1f)
//                .uv(1f, 1f)
//                .uv2(packedLight)
//                .endVertex();
//
//        buff.vertex(pose, 0.5f, 0.5f, -1000)
//                .color(1f, 1f, 1f, 1f)
//                .uv(1f, 0f)
//                .uv2(packedLight)
//                .endVertex();
//
//        buff.vertex(pose, -0.5f, 0.5f, -1000)
//                .color(1f, 1f, 1f, 1f)
//                .uv(0f, 0f)
//                .uv2(packedLight)
//                .endVertex();
//
//        tess.end();
    }
}
