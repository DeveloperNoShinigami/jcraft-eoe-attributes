package net.arna.jcraft.common.item;

import lombok.NonNull;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class DiaryPageItem extends Item {
    public DiaryPageItem(Properties settings) {
        super(settings);
    }

    @Override
    public void appendHoverText(@NonNull ItemStack stack, @Nullable Level world, List<Component> tooltip, @NonNull TooltipFlag context) {
        tooltip.add(Component.translatable("jcraft.diosdiary.desc"));
        tooltip.add(Component.translatable("jcraft.diarypage.evodesc"));
        super.appendHoverText(stack, world, tooltip, context);
    }
}
