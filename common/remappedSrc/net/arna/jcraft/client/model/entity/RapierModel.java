package net.arna.jcraft.client.model.entity;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.projectile.RapierProjectile;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

import java.util.HashMap;
import java.util.Map;

public class RapierModel extends GeoModel<RapierProjectile> {

    public static final Map<Integer, ResourceLocation> skins = new HashMap<>(
            Map.ofEntries(
                    Map.entry(-1, RapierProjectile.ARMOR_OFF_TEXTURE),
                    Map.entry(-2, RapierProjectile.POSSESSED_TEXTURE)
            )
    );

    static {
        for (int i = 0; i < StandType.SILVER_CHARIOT.getSkinCount(); i++) {
            skins.put(i, JCraft.id("textures/entity/stands/silver_chariot/rapier_" + (i == 0 ? "default" : "skin" + i) + ".png"));
        }
    }

    @Override
    public ResourceLocation getModelResource(RapierProjectile object) {
        return JCraft.id("geo/rapier.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(RapierProjectile object) {
        int skin = object.getSkin();
        if (skins.containsKey(skin)) {
            return skins.get(skin);
        }

        return skins.get(-1);
    }

    @Override
    public ResourceLocation getAnimationResource(RapierProjectile animatable) {
        return JCraft.id("animations/knife.animation.json");
    }
}
