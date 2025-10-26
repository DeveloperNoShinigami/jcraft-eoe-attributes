package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.common.item.FlutteringArmorItem;

public class FinalKiraArmorRenderer extends ArmorRenderer {

    public static final String ID = "finalkiraoutfit";

    public FinalKiraArmorRenderer() {
        // TODO: super(new JArmorModel<>("kiraoutfit", "finalkiraoutfit"));
        super(() -> new ArmorAnimator(ID), ID);
    }

    /*@Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllVisible(false);

        if (currentSlot == EquipmentSlot.LEGS) {
            setBoneVisible(this.body, true);
            setBoneVisible(this.leftLeg, true);
            setBoneVisible(this.rightLeg, true);
            setBoneVisible(this.leftArm, true);
            setBoneVisible(this.rightArm, true);
        }
        else if (currentSlot == EquipmentSlot.FEET) {
            setBoneVisible(this.leftBoot, true);
            setBoneVisible(this.rightBoot, true);
        }
        else if (currentSlot == EquipmentSlot.HEAD) {
            setBoneVisible(this.head, true);
        }
    }*/
}
