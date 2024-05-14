package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.FlutteringArmorItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class JotaroArmorRenderer extends JArmor<FlutteringArmorItem> {
    public JotaroArmorRenderer() {
        super(new JArmorModel<>("jotarooutfit"));
    }
}