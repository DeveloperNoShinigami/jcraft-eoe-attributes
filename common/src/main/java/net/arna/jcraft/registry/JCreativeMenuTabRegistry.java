package net.arna.jcraft.registry;

import dev.architectury.registry.CreativeTabRegistry;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.item.StandDiscItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemGroups;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public interface JCreativeMenuTabRegistry {

    static void init() {
        JCraft.CREATIVE_TAB_REGISTRY.register("general", JCreativeMenuTabRegistry::createJcraftItemGroup);
        // building blocks
        CreativeTabRegistry.modifyBuiltin(Registries.ITEM_GROUP.get(ItemGroups.BUILDING_BLOCKS.getValue()), (flags, output, canUseGameMasterBlocks) -> {
            output.acceptBefore(Items.GOLD_BLOCK, JItemRegistry.STELLAR_IRON_BLOCK.get());
        });
        // natural blocks
        CreativeTabRegistry.modifyBuiltin(Registries.ITEM_GROUP.get(ItemGroups.NATURAL.getValue()), (flags, output, canUseGameMasterBlocks) -> {
            output.acceptAfter(Items.SANDSTONE, JItemRegistry.FOOLISH_SAND_BLOCK.get());
            output.acceptAfter(JItemRegistry.FOOLISH_SAND_BLOCK.get(), JItemRegistry.HOT_SAND_BLOCK.get());
            output.acceptBefore(Items.NETHER_GOLD_ORE, JItemRegistry.METEORITE_IRON_ORE_BLOCK.get());
            output.acceptBefore(Items.OBSIDIAN, JItemRegistry.METEORITE_BLOCK.get());
            output.acceptAfter(Items.SOUL_SOIL, JItemRegistry.SOUL_BLOCK.get());
        });
        // functional blocks
        CreativeTabRegistry.modifyBuiltin(Registries.ITEM_GROUP.get(ItemGroups.NATURAL.getValue()), (flags, output, canUseGameMasterBlocks) -> {
            output.acceptBefore(Items.CANDLE, JItemRegistry.COFFIN_BLOCK.get());
        });
        // tools
        CreativeTabRegistry.modifyBuiltin(Registries.ITEM_GROUP.get(ItemGroups.TOOLS.getValue()), (flags, output, canUseGameMasterBlocks) -> {
            output.acceptBefore(Items.COMPASS, JItemRegistry.STAND_ARROW.get());
            output.acceptBefore(Items.COMPASS, JItemRegistry.STAND_DISC.get());
        });
        // combat
        CreativeTabRegistry.modifyBuiltin(Registries.ITEM_GROUP.get(ItemGroups.COMBAT.getValue()), (flags, output, canUseGameMasterBlocks) -> {
            output.acceptBefore(Items.WOODEN_AXE, JItemRegistry.ANUBIS_SHEATHED.get());
            output.acceptBefore(Items.BOW, JItemRegistry.KNIFE.get());
            output.acceptBefore(Items.BOW, JItemRegistry.KNIFEBUNDLE.get());
            output.acceptBefore(Items.BOW, JItemRegistry.FV_REVOLVER.get());
            output.acceptBefore(Items.BOW, JItemRegistry.BULLET.get());
            output.acceptAfter(Items.NETHERITE_BOOTS, JItemRegistry.JOTARO_CAP.get());
            output.acceptAfter(JItemRegistry.JOTARO_CAP.get(), JItemRegistry.JOTARO_JACKET.get());
            output.acceptAfter(JItemRegistry.JOTARO_JACKET.get(), JItemRegistry.JOTARO_PANTS.get());
            output.acceptAfter(JItemRegistry.JOTARO_PANTS.get(), JItemRegistry.JOTARO_BOOTS.get());
            output.acceptAfter(JItemRegistry.JOTARO_BOOTS.get(), JItemRegistry.DIO_HEADBAND.get());
            output.acceptAfter(JItemRegistry.DIO_HEADBAND.get(), JItemRegistry.DIO_JACKET.get());
            output.acceptAfter(JItemRegistry.DIO_JACKET.get(), JItemRegistry.DIO_CAPE.get());
            output.acceptAfter(JItemRegistry.DIO_CAPE.get(), JItemRegistry.DIO_PANTS.get());
            output.acceptAfter(JItemRegistry.DIO_PANTS.get(), JItemRegistry.DIO_BOOTS.get());
            output.acceptBefore(Items.SHIELD, JItemRegistry.BOXING_GLOVES.get());
            output.acceptBefore(Items.LEATHER_HORSE_ARMOR, JItemRegistry.STONE_MASK.get());
            output.acceptBefore(Items.LEATHER_HORSE_ARMOR, JItemRegistry.RED_HAT.get());
            output.acceptBefore(Items.LEATHER_HORSE_ARMOR, JItemRegistry.KARS_HEADWRAP.get());
        });
        // ingredients
        CreativeTabRegistry.modifyBuiltin(Registries.ITEM_GROUP.get(ItemGroups.INGREDIENTS.getValue()), (flags, output, canUseGameMasterBlocks) -> {
            output.acceptBefore(Items.COPPER_INGOT, JItemRegistry.STELLAR_IRON_INGOT.get());
            output.acceptBefore(Items.GLASS_BOTTLE, JItemRegistry.LIVING_ARROW.get());
            output.acceptBefore(Items.GLASS_BOTTLE, JItemRegistry.REQUIEM_RUBY.get());
            output.acceptBefore(Items.GLASS_BOTTLE, JItemRegistry.REQUIEM_ARROW.get());
            output.acceptBefore(Items.GLASS_BOTTLE, JItemRegistry.GREEN_BABY.get());
            output.acceptBefore(Items.GLASS_BOTTLE, JItemRegistry.DIOS_DIARY.get());
            output.acceptAfter(Items.GLASS_BOTTLE, JItemRegistry.BLOOD_BOTTLE.get());
            output.acceptBefore(Items.WHITE_DYE, JItemRegistry.SINNERS_SOUL.get());
            output.acceptBefore(Items.WHITE_DYE, JItemRegistry.STAND_ARROWHEAD.get());
            output.acceptBefore(Items.WHITE_DYE, JItemRegistry.CINDERELLA_MASK.get());
            output.acceptBefore(Items.BOWL, JItemRegistry.KQ_COIN.get());
        });
        // foods & drinks
        CreativeTabRegistry.modifyBuiltin(Registries.ITEM_GROUP.get(ItemGroups.FOOD_AND_DRINK.getValue()), (flags, output, canUseGameMasterBlocks) -> {
            final ItemStack bloodBottle = new ItemStack(JItemRegistry.BLOOD_BOTTLE.get());
            bloodBottle.getOrCreateNbt().putFloat("Blood", 16f);
            output.acceptBefore(Items.HONEY_BOTTLE, bloodBottle);
        });
    }

    static ItemGroup createJcraftItemGroup() {
        return ItemGroup.create(ItemGroup.Row.TOP, 0)
                .displayName(Text.translatable("itemGroup.jcraft.main"))
                .icon(() -> JItemRegistry.STAND_ARROW.get().getDefaultStack())
                // order of the creative tab
                .entries((displayContext, entries) -> {
                    // everything up to arrows
                    entries.add(JItemRegistry.METEORITE_BLOCK.get());
                    entries.add(JItemRegistry.METEORITE_IRON_ORE_BLOCK.get());
                    entries.add(JItemRegistry.STELLAR_IRON_INGOT.get());
                    entries.add(JItemRegistry.STELLAR_IRON_BLOCK.get());
                    entries.add(JItemRegistry.STAND_ARROWHEAD.get());
                    entries.add(JItemRegistry.STAND_ARROW.get());
                    entries.add(JItemRegistry.LIVING_ARROW.get());
                    entries.add(JItemRegistry.REQUIEM_RUBY.get());
                    entries.add(JItemRegistry.REQUIEM_ARROW.get());
                    // other evolution items
                    entries.add(JItemRegistry.GREEN_BABY.get());
                    entries.add(JItemRegistry.DIOS_DIARY.get());
                    // stand drops
                    entries.add(JItemRegistry.FV_REVOLVER.get());
                    entries.add(JItemRegistry.BULLET.get());
                    entries.add(JItemRegistry.KQ_COIN.get());
                    entries.add(JItemRegistry.FOOLISH_SAND_BLOCK.get());
                    // misc
                    entries.add(JItemRegistry.HOT_SAND_BLOCK.get());
                    entries.add(JItemRegistry.SINNERS_SOUL.get());
                    entries.add(JItemRegistry.SOUL_BLOCK.get());
                    entries.add(JItemRegistry.KNIFE.get());
                    entries.add(JItemRegistry.KNIFEBUNDLE.get());
                    // spec items + related except blood bottles
                    entries.add(JItemRegistry.ANUBIS_SHEATHED.get());
                    entries.add(JItemRegistry.ANUBIS.get());
                    entries.add(JItemRegistry.BOXING_GLOVES.get());
                    entries.add(JItemRegistry.STONE_MASK.get());
                    entries.add(JItemRegistry.RED_HAT.get());
                    entries.add(JItemRegistry.KARS_HEADWRAP.get());
                    entries.add(JItemRegistry.COFFIN_BLOCK.get());
                    // cosplay
                    entries.add(JItemRegistry.JOTARO_CAP.get());
                    entries.add(JItemRegistry.JOTARO_JACKET.get());
                    entries.add(JItemRegistry.JOTARO_PANTS.get());
                    entries.add(JItemRegistry.JOTARO_BOOTS.get());
                    entries.add(JItemRegistry.DIO_HEADBAND.get());
                    entries.add(JItemRegistry.DIO_JACKET.get());
                    entries.add(JItemRegistry.DIO_CAPE.get());
                    entries.add(JItemRegistry.DIO_PANTS.get());
                    entries.add(JItemRegistry.DIO_BOOTS.get());
                    // blood bottles
                    for (int i = 16; i >= 0; i--) {
                        final ItemStack stack = new ItemStack(JItemRegistry.BLOOD_BOTTLE.get());
                        stack.getOrCreateNbt().putFloat("Blood", i);
                        entries.add(stack);
                    }
                    // cinderella mask + enchantments
                    entries.add(JItemRegistry.CINDERELLA_MASK.get());
                    for (int i = 1; i <= 3; i++) {
                        final ItemStack stack = new ItemStack(Items.ENCHANTED_BOOK);
                        final NbtCompound nbt = stack.getOrCreateNbt();
                        final NbtList enchantments = new NbtList();
                        final NbtCompound enchantment = new NbtCompound();
                        enchantment.putString("id", "jcraft:cinderellas_kiss");
                        enchantment.putShort("lvl", (short)i);
                        enchantments.add(enchantment);
                        nbt.put("StoredEnchantments", enchantments);
                        entries.add(stack);
                    }
                    // stand discs
                    for (final StandType standType : StandType.values()) {
                        for (int skin = 0; skin <= standType.getSkinCount(); skin++) {
                            entries.add(StandDiscItem.createDiscStack(standType, skin));
                        }
                    }
                    // weird items
                    if (JItemRegistry.DEBUG_WAND != null) {
                        entries.add(JItemRegistry.DEBUG_WAND.get());
                    }
                })
                .build();
    }

}
