package net.arna.jcraft.client.renderer.entity.stands;

import net.arna.jcraft.client.model.entity.stand.PurpleHazeModel;
import net.arna.jcraft.common.entity.stand.AbstractPurpleHazeEntity;
import net.minecraft.client.renderer.entity.EntityRendererProvider;

public class PurpleHazeDistortionRenderer extends StandEntityRenderer<AbstractPurpleHazeEntity<?, ?>> {
    public PurpleHazeDistortionRenderer(EntityRendererProvider.Context context) {
        super(context, new PurpleHazeModel(true));
    }
}
