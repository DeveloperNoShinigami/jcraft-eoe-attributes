package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.common.item.FlutteringArmorItem;

//renders cap
public class RisottoCapRenderer extends ArmorRenderer {

    public static final String ID = "risottocap";

    public RisottoCapRenderer() {
        super(() -> new ArmorAnimator(ID), ID);
    }

    /*@Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllVisible(false);

        if (currentSlot == EquipmentSlot.HEAD) {
            setBoneVisible(this.body, true);
            setBoneVisible(this.head, true);
        }
    }*/
}
