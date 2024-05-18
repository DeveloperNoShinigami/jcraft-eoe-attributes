package net.arna.jcraft.common.minigame;

import net.minecraft.SharedConstants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.Bootstrap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

/**
 * Tests {@link Wager}.
 */
public class TestWager {

    @BeforeAll
    public static void beforeAll() {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
    }

    @Test
    public void testNbt() {
        final Wager expectedWager = new Wager();
        expectedWager.increaseWager(new ItemStack(Items.DIAMOND, 5));
        expectedWager.increaseWager(new ItemStack(Items.EMERALD, 3));
        final CompoundTag nbt = new CompoundTag();
        expectedWager.writeToNbt(nbt);
        final Wager actualWager = new Wager();
        actualWager.readFromNbt(nbt);
        Assertions.assertEquals(expectedWager, actualWager);
    }
}
