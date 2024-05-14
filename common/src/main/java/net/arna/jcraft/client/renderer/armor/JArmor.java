package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.common.item.DIOArmorItem;
import net.minecraft.item.Item;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JArmor<T extends Item & GeoItem> extends GeoArmorRenderer<T> {

    public JArmor(GeoModel<T> model) {
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
}
