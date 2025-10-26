package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.common.item.FlutteringArmorItem;

//renders coat
public class KakyoinCoatRenderer extends ArmorRenderer {

    public static final String ID = "kakyoinshirt";

    public KakyoinCoatRenderer() {
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
            setBoneVisible(this.head, true);
        }
    }*/
}
