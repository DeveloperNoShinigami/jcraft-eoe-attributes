package net.arna.jcraft.client.renderer.armor;

import net.arna.jcraft.client.model.armor.JArmorModel;
import net.arna.jcraft.common.item.DIOArmorItem;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class DIOArmorRenderer extends JArmor<DIOArmorItem> {
    public DIOArmorRenderer() {
        super(new JArmorModel<>("diooutfit"));
    }
}