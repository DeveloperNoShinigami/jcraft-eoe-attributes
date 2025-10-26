package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.DIOJacketItem;
import net.minecraft.world.entity.EquipmentSlot;

//renders leotard and boots
public class DIOtardRenderer extends JArmor<DIOJacketItem> {
    /*public DIOtardRenderer() {
        super(new JArmorModel<>("diotard"));
    }

    @Override
    protected void applyBoneVisibilityBySlot(EquipmentSlot currentSlot) {
        setAllVisible(false);

        if (currentSlot == EquipmentSlot.LEGS) {
            setBoneVisible(this.body, true);
            setBoneVisible(this.leftLeg, true);
            setBoneVisible(this.rightLeg, true);
        }
        else if (currentSlot == EquipmentSlot.FEET) {
            setBoneVisible(this.leftBoot, true);
            setBoneVisible(this.rightBoot, true);
        }
    }*/
}
