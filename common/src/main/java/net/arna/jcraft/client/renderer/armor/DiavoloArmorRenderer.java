package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.FlutteringArmorItem;
import net.minecraft.world.entity.EquipmentSlot;

public class DiavoloArmorRenderer extends JArmor<FlutteringArmorItem> {
    /*public DiavoloArmorRenderer() {
        super(new JArmorModel<>("diavoloclothes"));
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
        else if (currentSlot == EquipmentSlot.HEAD) {
            setBoneVisible(this.head, true);
        }
    }*/
}
