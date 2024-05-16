package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.FlutteringArmorItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DIOCapeRenderer extends JArmor<FlutteringArmorItem> {
    public DIOCapeRenderer() {
        super(new JArmorModel<>("diocape"));
    }
}