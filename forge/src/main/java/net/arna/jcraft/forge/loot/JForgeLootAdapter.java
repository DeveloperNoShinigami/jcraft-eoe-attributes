package net.arna.jcraft.forge.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.NonNull;
import net.arna.jcraft.common.loot.JLootTableHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;

import java.util.function.Consumer;
import java.util.function.Supplier;

import static net.minecraft.world.level.storage.loot.LootTable.createStackSplitter;

public class JForgeLootAdapter extends LootModifier {
    public static final Supplier<Codec<JForgeLootAdapter>> CODEC = Suppliers.memoize(() ->
            RecordCodecBuilder.create(inst ->
                    LootModifier.codecStart(inst).apply(inst, JForgeLootAdapter::new)
            )
    );

    protected JForgeLootAdapter(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @SuppressWarnings("deprecation") // forge made another bad mixin-avoidant api
    @Override
    protected @NonNull ObjectArrayList<ItemStack> doApply(final ObjectArrayList<ItemStack> generatedLoot, final LootContext context) {
        final LootTable.Builder builder = LootTable.lootTable();
        for (Consumer<LootTable.Builder> consumer : JLootTableHelper.modifications.get(context.getQueriedLootTableId())) {
            consumer.accept(builder);
        }
        builder.build().getRandomItemsRaw(context, createStackSplitter(context.getLevel(), generatedLoot::add));
        return generatedLoot;
    }

    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
}
