package net.arna.jcraft.client.renderer.armor;

public class DiavoloArmorRenderer extends ArmorRenderer {

    public static final String ID = "diavoloclothes";

    public DiavoloArmorRenderer() {
        super(() -> new ArmorAnimator(ID), ID);
    }
}
