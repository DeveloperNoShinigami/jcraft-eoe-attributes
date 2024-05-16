package net.arna.jcraft.common.item;

import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LivingArrowItem extends StandObtainmentItem {
    public LivingArrowItem(Properties settings) {
        super(settings);

        standIOMap.put(StandType.KILLER_QUEEN, StandType.KILLER_QUEEN_BITES_THE_DUST);
        standIOMap.put(StandType.STAR_PLATINUM, StandType.STAR_PLATINUM_THE_WORLD);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.livingarrow.desc"));
        tooltip.add(Component.translatable("jcraft.livingarrow.evodesc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
