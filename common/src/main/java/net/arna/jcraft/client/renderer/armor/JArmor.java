package net.arna.jcraft.client.renderer.armor;

import mod.azure.azurelib.animatable.GeoItem;
import mod.azure.azurelib.cache.object.GeoBone;
import mod.azure.azurelib.model.GeoModel;
import mod.azure.azurelib.renderer.GeoArmorRenderer;
import net.arna.jcraft.mixin.client.PlayerModelAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class JArmor<T extends Item & GeoItem> extends GeoArmorRenderer<T> {

    public JArmor(final GeoModel<T> model) {
        super(model);
    }

    @Override
    public GeoBone getHeadBone() {
        return this.model.getBone("helmet").orElse(super.getHeadBone());
    }

    @Nullable
    @Override
    public GeoBone getBodyBone() {
        return this.model.getBone("chestplate").orElse(super.getBodyBone());
    }

    @Nullable
    @Override
    public GeoBone getRightArmBone() {
        return this.model.getBone("rightArm").orElse(super.getRightArmBone());
    }

    @Nullable
    @Override
    public GeoBone getLeftArmBone() {
        return this.model.getBone("leftArm").orElse(super.getLeftArmBone());
    }

    @Nullable
    @Override
    public GeoBone getRightLegBone() {
        return this.model.getBone("rightLeg").orElse(super.getRightLegBone());
    }

    @Nullable
    @Override
    public GeoBone getLeftLegBone() {
        return this.model.getBone("leftLeg").orElse(super.getLeftLegBone());
    }

    @Nullable
    @Override
    public GeoBone getRightBootBone() {
        return this.model.getBone("rightBoot").orElse(super.getRightBootBone());
    }

    @Nullable
    @Override
    public GeoBone getLeftBootBone() {
        return this.model.getBone("leftBoot").orElse(super.getLeftBootBone());
    }

    @Override
    public void prepForRender(@Nullable Entity entity, ItemStack stack, @Nullable EquipmentSlot slot, @Nullable HumanoidModel<?> baseModel) {
        super.prepForRender(entity, stack, slot, baseModel);

        // Scale the arms to 3/4 of their original size for slim models.
        // Slim models have 3 pixel wide arms rather than 4 pixel wide arms

        // We use this convoluted method to check if the player's model is slim
        // because I don't trust comparing player.getModelName() to "slim"
        // as it may not always be accurate in combination with other mods or future updates.

        if (!(entity instanceof AbstractClientPlayer player)) return;
        EntityRenderer<? super AbstractClientPlayer> renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(player);

        if (!(renderer instanceof PlayerRenderer playerRenderer)) return;
        PlayerModel<AbstractClientPlayer> playerModel = playerRenderer.getModel();

        if (!((PlayerModelAccessor) playerModel).isSlim())
            return;

        GeoBone leftArm = getLeftArmBone();
        GeoBone rightArm = getRightArmBone();

        if (leftArm != null && rightArm != null) {
            leftArm.setScaleX(0.75f);
            rightArm.setScaleX(0.75f);
        }
    }
}
