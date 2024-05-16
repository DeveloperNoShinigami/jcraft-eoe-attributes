package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.SunProtectionItem;


public class RedHatRenderer extends JArmor<SunProtectionItem> {
    public RedHatRenderer() {
        super(new JArmorModel<>("red_hat"));
    }
}