package net.arna.jcraft.common.item;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public class CinderellaMaskItem extends Item {
    public CinderellaMaskItem() {
        super(new Properties()
                .rarity(Rarity.RARE));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.cinderella_mask.enchdesc"));
        tooltip.add(Component.translatable("jcraft.cinderella_mask.usedesc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
