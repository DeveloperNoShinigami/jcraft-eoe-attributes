package net.arna.jcraft.registry;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.item.StandDiscItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;

public interface JCreativeMenuTabRegistry {

    static void init() {
        JCraft.CREATIVE_TAB_REGISTRY.register("general", JCreativeMenuTabRegistry::createItemGroup);
    }

    static ItemGroup createItemGroup() {
        return ItemGroup.create(ItemGroup.Row.TOP, 0)
                .displayName(Text.translatable("itemGroup.jcraft.main"))
                .icon(() -> JItemRegistry.STAND_ARROW.get().getDefaultStack())
                // order of the creative tab
                .entries((displayContext, entries) -> {
                    // everything up to arrows
                    entries.add(JItemRegistry.METEORITE_BLOCK.get());
                    entries.add(JItemRegistry.METEORITE_IRON_ORE_BLOCK.get());
                    entries.add(JItemRegistry.STELLAR_IRON_INGOT.get());
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
