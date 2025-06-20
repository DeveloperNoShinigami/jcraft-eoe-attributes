package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.FlutteringArmorItem;

//renders coat
public class KosakuJacketRenderer extends JArmor<FlutteringArmorItem> {
    public KosakuJacketRenderer() {
        super(new JArmorModel<>("kirajacket", "kosakujacket"));
    }
}
