package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.StoneMaskItem;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class StoneMaskRenderer extends JArmor<StoneMaskItem> {
    public StoneMaskRenderer() {
        super(new JArmorModel<>("stone_mask"));
    }
}