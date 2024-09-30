package net.arna.jcraft.common.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiaryPageItem extends StandObtainmentItem {
    public DiaryPageItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, List<Component> tooltip, @NotNull TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.diosdiary.desc"));
        tooltip.add(Component.translatable("jcraft.diarypage.evodesc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
