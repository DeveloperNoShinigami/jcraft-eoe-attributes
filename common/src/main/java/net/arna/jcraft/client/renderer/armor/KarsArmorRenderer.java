package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.HatItem;

public class KarsArmorRenderer extends JArmor<HatItem> {
    public KarsArmorRenderer() {
        super(new JArmorModel<>("karsoutfit"));
    }
}