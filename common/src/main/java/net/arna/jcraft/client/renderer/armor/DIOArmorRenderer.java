package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.DIOArmorItem;

public class DIOArmorRenderer extends JArmor<DIOArmorItem> {
    public DIOArmorRenderer() {
        super(new JArmorModel<>("diooutfit"));
    }
}