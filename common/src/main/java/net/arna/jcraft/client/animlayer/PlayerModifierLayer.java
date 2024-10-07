package net.arna.jcraft.client.animlayer;

import dev.kosmx.playerAnim.api.TransformType;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonConfiguration;
import dev.kosmx.playerAnim.api.firstPerson.FirstPersonMode;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.core.util.Vec3f;
import lombok.NonNull;

public class PlayerModifierLayer<T extends IAnimation> extends ModifierLayer<T> implements IAnimation {
    @Override
    public @NonNull Vec3f get3DTransform(final @NonNull String modelName, final @NonNull TransformType type, final float tickDelta, final @NonNull Vec3f value0) {
        return super.get3DTransform(modelName, type, tickDelta, value0);
    }

    @Override
    public void setupAnim(final float tickDelta) {
        super.setupAnim(tickDelta);
    }

    @Override
    public @NonNull FirstPersonMode getFirstPersonMode(final float tickDelta) {
        return FirstPersonMode.THIRD_PERSON_MODEL;
    }

    private static final FirstPersonConfiguration config = new FirstPersonConfiguration(true, true, true, true);

    @Override
    public @NonNull FirstPersonConfiguration getFirstPersonConfiguration(final float tickDelta) {
        return config;
    }
}
