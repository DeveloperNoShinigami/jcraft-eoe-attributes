package net.arna.jcraft.client.model.entity.stand;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.AbstractPurpleHazeEntity;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.minecraft.resources.ResourceLocation;

/**
 * The common {@link StandEntityModel} for {@link net.arna.jcraft.common.entity.stand.PurpleHazeEntity PurpleHazeEntity}
 * and {@link net.arna.jcraft.common.entity.stand.PurpleHazeDistortionEntity PurpleHazeDistortionEntity}.
 * @see net.arna.jcraft.client.renderer.entity.stands.PurpleHazeRenderer PurpleHazeRenderer
 * @see net.arna.jcraft.client.renderer.entity.stands.PurpleHazeDistortionRenderer PurpleHazeDistortionRenderer
 */
public class PurpleHazeModel extends StandEntityModel<AbstractPurpleHazeEntity<?, ?>> {
    private static final ResourceLocation MODEL = JCraft.id("geo/purple_haze.geo.json");

    public PurpleHazeModel(boolean distortion) {
        super(distortion ? JStandTypeRegistry.PURPLE_HAZE_DISTORTION.get() : JStandTypeRegistry.PURPLE_HAZE.get());
    }

    @Override
    public ResourceLocation getModelResource(AbstractPurpleHazeEntity<?, ?> animatable) {
        return MODEL;
    }
}
