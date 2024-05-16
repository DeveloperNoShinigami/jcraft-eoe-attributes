package net.arna.jcraft.common.item;

import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class GreenBabyItem extends StandObtainmentItem {
    public GreenBabyItem(Properties settings) {
        super(settings);

        standIOMap.put(StandType.WHITE_SNAKE, StandType.C_MOON);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.greenbaby.desc"));
        tooltip.add(Component.translatable("jcraft.greenbaby.evodesc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
