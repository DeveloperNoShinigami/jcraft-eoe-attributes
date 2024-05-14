package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.SunProtectionItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class KarsArmorRenderer extends JArmor<SunProtectionItem> {
    public KarsArmorRenderer() {
        super(new JArmorModel<>("karsoutfit"));
    }
}