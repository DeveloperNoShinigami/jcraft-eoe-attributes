package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.FlutteringArmorItem;
import net.minecraft.world.entity.EquipmentSlot;

//renders coat
public class KakyoinCoatRenderer extends JArmor<FlutteringArmorItem> {
    public KakyoinCoatRenderer() {
        super(new JArmorModel<>("kakyoinshirt"));
    }
    @Override
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
    }
}
