package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.FlutteringArmorItem;
import net.minecraft.world.entity.EquipmentSlot;

//renders jacket
public class RisottoTopRenderer extends JArmor<FlutteringArmorItem> {
    public RisottoTopRenderer() {
        super(new JArmorModel<>("risottotop"));
    }
}
