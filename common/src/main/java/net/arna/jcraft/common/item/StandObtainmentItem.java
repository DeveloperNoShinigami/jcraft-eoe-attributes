package net.arna.jcraft.common.item;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandEntity;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import java.util.HashMap;
import java.util.Map;

public abstract class StandObtainmentItem extends Item {
    public StandObtainmentItem(Properties settings) {
        super(settings);
    }

    /**
     * List of input/output stand IDs required by the StandObtainmentItem instance
     * Key - input
     * Value - output
     */
    public final Map<StandType, StandType> standIOMap = new HashMap<>();

    protected boolean canEvolve(Level world, Player user) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        if (world.isClientSide) {
            return InteractionResultHolder.consume(itemStack);
        }

        CommonStandComponent standData = JComponentPlatformUtils.getStandData(user);
        StandType type = standData.getType();

        // Does the user have the appropriate stand and does he meet the evolution requirements?
        if (standIOMap.containsKey(type) && canEvolve(world, user)) {
            if (!user.isCreative()) {
                itemStack.shrink(1);
            }

            standData.setTypeAndSkin(standIOMap.get(type), standData.getSkin());

            // Re-summon users stand
            StandEntity<?, ?> stand = standData.getStand();
            if (stand != null) {
                stand.desummon();
            }
            JCraft.summon(world, user);
        }

        return InteractionResultHolder.consume(itemStack);
    }
}
