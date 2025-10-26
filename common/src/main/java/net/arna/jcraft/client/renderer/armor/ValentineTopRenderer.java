package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.common.item.FlutteringArmorItem;

public class ValentineTopRenderer extends ArmorRenderer {

    public static final String ID = "valentinetop";

    public ValentineTopRenderer() {
        super(() -> new ArmorAnimator(ID), ID);
    }

    /*@Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllVisible(false);

        if (currentSlot == EquipmentSlot.CHEST) {
            setBoneVisible(this.body, true);
            setBoneVisible(this.leftLeg, true);
            setBoneVisible(this.rightLeg, true);
            setBoneVisible(this.leftArm, true);
            setBoneVisible(this.rightArm, true);
        }
        else if (currentSlot == EquipmentSlot.HEAD) {
            setBoneVisible(this.head, true);
        }
    }*/
}
