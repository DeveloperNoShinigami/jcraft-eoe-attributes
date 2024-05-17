package net.arna.jcraft.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DIOsDiaryItem extends StandObtainmentItem {
    public DIOsDiaryItem(Properties settings) {
        super(settings);

    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.diosdiary.desc"));
        tooltip.add(Component.translatable("jcraft.diosdiary.evodesc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
