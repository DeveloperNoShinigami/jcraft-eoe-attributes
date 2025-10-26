package net.arna.jcraft.client.renderer.armor;

public class DIOCapeRenderer extends ArmorRenderer {

    public static final String ID = "diocape";

    public DIOCapeRenderer() {
        super(() -> new ArmorAnimator(ID), ID);
    }

    /*@Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllVisible(false);

        if (currentSlot == EquipmentSlot.CHEST) {
            setBoneVisible(this.body, true);
            setBoneVisible(this.head, true);
            setBoneVisible(this.leftArm, true);
            setBoneVisible(this.rightArm, true);
        }
    }*/
}