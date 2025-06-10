package net.arna.jcraft.api.spec;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import net.arna.jcraft.api.IAttackerType;
import net.arna.jcraft.common.data.AttackerDataLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import java.util.function.Function;

@ToString
@RequiredArgsConstructor(staticName = "of")
public class SpecType implements IAttackerType {
    @Getter
    private final ResourceLocation id;
    private final Function<LivingEntity, JSpec<?,?>> factory;

    public JSpec<?, ?> createSpec(final @NonNull LivingEntity user) {
        return factory.apply(user);
    }

    /**
     * Gets the SpecData for this SpecType.
     * <p>
     * <b>Important:</b> if you have a JSpec instance, use {@link JSpec#getSpecData()} instead
     * as it may return a different instance based on its state.
     * @return The SpecData for this SpecType.
     */
    public SpecData getData() {
        return AttackerDataLoader.getSpecData(getId());
    }

    @Override
    public final String kind() {
        return "spec";
    }
}
