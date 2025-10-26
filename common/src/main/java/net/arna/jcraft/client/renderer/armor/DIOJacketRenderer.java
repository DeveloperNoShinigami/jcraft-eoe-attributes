package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.common.item.DIOJacketItem;

//renders jacket, headband, and belt
public class DIOJacketRenderer extends ArmorRenderer {

    public static final String ID = "diojacket";

    public DIOJacketRenderer() {
        super(() -> new ArmorAnimator(ID), ID);
    }
}
