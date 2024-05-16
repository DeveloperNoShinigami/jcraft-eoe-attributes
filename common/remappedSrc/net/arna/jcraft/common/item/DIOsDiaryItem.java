package net.arna.jcraft.common.item;

import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DIOsDiaryItem extends StandObtainmentItem {
    public DIOsDiaryItem(Properties settings) {
        super(settings);

        standIOMap.put(StandType.C_MOON, StandType.MADE_IN_HEAVEN);
        standIOMap.put(StandType.THE_WORLD, StandType.THE_WORLD_OVER_HEAVEN);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.diosdiary.desc"));
        tooltip.add(Component.translatable("jcraft.diosdiary.evodesc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
