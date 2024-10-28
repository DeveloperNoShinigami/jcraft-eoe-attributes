package net.arna.jcraft.client.registry;

import net.arna.jcraft.client.model.entity.StandMeteorModel;
import net.arna.jcraft.client.renderer.entity.StandMeteorRenderer;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.builders.LayerDefinition;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class JEntityModelLayerRegistry {
    private static final HashMap<ModelLayerLocation, Supplier<LayerDefinition>> modelLayers = new HashMap<>();
    static {
        modelLayers.put(
                StandMeteorRenderer.STAND_METEOR_LAYER, StandMeteorModel::createBodyLayer
        );
    }
    public static void init(Consumer<Map.Entry<ModelLayerLocation, Supplier<LayerDefinition>>> consumer) {
        modelLayers.entrySet().forEach(consumer);
    }
}
