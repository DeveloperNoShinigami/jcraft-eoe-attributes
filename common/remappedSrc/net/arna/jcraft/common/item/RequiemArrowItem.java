package net.arna.jcraft.common.item;

import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RequiemArrowItem extends StandObtainmentItem {
    public RequiemArrowItem(Properties settings) {
        super(settings);
        standIOMap.put(StandType.GOLD_EXPERIENCE, StandType.GOLD_EXPERIENCE_REQUIEM);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.requiemarrow.desc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
