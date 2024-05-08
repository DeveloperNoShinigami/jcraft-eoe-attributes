package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.item.*;
import net.arna.jcraft.common.spec.SpecType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.*;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static net.arna.jcraft.JCraft.*;

public interface JItemRegistry {

    Map<RegistrySupplier<Item>, Identifier> ITEMS = new LinkedHashMap<>();

    RegistrySupplier<Item> DEBUG_WAND = FabricLoader.getInstance().isDevelopmentEnvironment() ? register("debug_wand", () -> new DebugWand(settings())) : null;

    RegistrySupplier<Item> STANDARROW = register("stand_arrow", () -> new StandArrowItem(settings().rarity(Rarity.RARE).fireproof()));

    RegistrySupplier<Item> STAND_DISC = register("stand_disc", () -> new StandDiscItem(settings().rarity(Rarity.RARE).fireproof().maxCount(1)));

    RegistrySupplier<Item> FV_REVOLVER = register("fv_revolver", () -> new FVRevolverItem(settings().rarity(Rarity.UNCOMMON).maxDamage(1200)));

    RegistrySupplier<Item> BULLET = register("bullet", () -> new BulletItem(settings()));

    RegistrySupplier<Item> KQ_COIN = register("kq_coin", () -> new KQCoinItem(settings()));

    RegistrySupplier<Item> GREENBABY = register("green_baby", () -> new GreenBabyItem(settings().rarity(Rarity.RARE)));

    RegistrySupplier<Item> DIOSDIARY = register("dios_diary", () -> new DIOsDiaryItem(settings().rarity(Rarity.EPIC).fireproof()));


    RegistrySupplier<Item> SINNERSSOUL = register("sinners_soul", () -> new SinnersSoulItem(settings()));

    RegistrySupplier<Item> KNIFE = register("knife", () -> new KnifeItem(settings()));

    RegistrySupplier<Item> KNIFEBUNDLE = register("knife_bundle", () -> new KnifeBundleItem(settings().maxCount(1)));

    RegistrySupplier<Item> ANUBIS = register("anubis", () -> new AnubisItem(settings().rarity(Rarity.RARE).maxCount(1)));

    // Spec Obtainment Items
    RegistrySupplier<Item> ANUBISSHEATHED = register("anubis_sheathed", () -> new SheathedAnubisItem(settings().rarity(Rarity.RARE).maxCount(1), SpecType.ANUBIS));

    RegistrySupplier<Item> BOXINGGLOVES = register("boxing_gloves", () -> new BoxingGlovesItem(settings().maxCount(1), SpecType.BRAWLER));


    RegistrySupplier<Item> REQUIEMRUBY = register("requiem_ruby", () -> new Item(settings().rarity(Rarity.EPIC).fireproof()));

    RegistrySupplier<Item> REQUIEMARROW = register("requiem_arrow", () -> new RequiemArrowItem(settings().rarity(Rarity.EPIC).fireproof()));

    RegistrySupplier<Item> LIVINGARROW = register("living_arrow", () -> new LivingArrowItem(settings().rarity(Rarity.RARE).fireproof()));

    RegistrySupplier<Item> DIOHEADBAND = register("dio_headband", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, settings()));
    RegistrySupplier<Item> DIOJACKET = register("dio_jacket", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, settings()));
    RegistrySupplier<Item> DIOPANTS = register("dio_pants", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, settings()));
    RegistrySupplier<Item> DIOBOOTS = register("dio_boots", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, settings()));

    RegistrySupplier<Item> DIOCAPE = register("dio_cape", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, settings()));

    RegistrySupplier<Item> KARSHEADWRAP = register("kars_headwrap", () -> new SunProtectionItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, settings()));

    RegistrySupplier<Item> RED_HAT = register("red_hat", () -> new SunProtectionItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, settings()));

    RegistrySupplier<Item> STONE_MASK = register("stone_mask", () -> new StoneMaskItem(ArmorMaterials.CHAIN, ArmorItem.Type.HELMET, settings()));

    RegistrySupplier<Item> JOTAROCAP = register("jotaro_cap", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, settings()));
    RegistrySupplier<Item> JOTAROJACKET = register("jotaro_jacket", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, settings()));
    RegistrySupplier<Item> JOTAROPANTS = register("jotaro_pants", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, settings()));
    RegistrySupplier<Item> JOTAROBOOTS = register("jotaro_boots", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, settings()));

    RegistrySupplier<Item> CINDERELLA_MASK = register("cinderella_mask", () -> new CinderellaMaskItem());

    RegistrySupplier<Item> BLOOD_BOTTLE = register("blood_bottle", () -> new BloodBottleItem(settings().maxCount(1)));

    RegistrySupplier<Item> STELLAR_IRON_INGOT = register("stellar_iron_ingot", () -> new Item(settings()));
    RegistrySupplier<Item> STAND_ARROWHEAD = register("stand_arrowhead", () -> new Item(settings()));

    RegistrySupplier<Item> MOCK_ITEM = register("mock_item", () -> new MockItem());

    //Block
    RegistrySupplier<Item> FOOLISH_SAND_BLOCK = register("foolish_sand_block",
            () -> new BlockItem(JBlockRegistry.FOOLISH_SAND_BLOCK.get(), settings()
            ));
    RegistrySupplier<Item> SOUL_BLOCK = register("soul_block", () -> new BlockItem(JBlockRegistry.SOUL_BLOCK.get(), settings()
    ));
    RegistrySupplier<Item> METEORITE_BLOCK = register("meteorite_block", () -> new BlockItem(JBlockRegistry.METEORITE_BLOCK.get(), settings()
    ));
    RegistrySupplier<Item> METEORITE_IRON_ORE_BLOCK = register("meteorite_iron_ore_block",
            () -> new BlockItem(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get(), settings()

            ));
    RegistrySupplier<Item> COFFIN_BLOCK = register("coffin",
            () -> new BlockItem(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get(), settings()));

    private void register() {

    }

    static RegistrySupplier<Item> register(String id, Supplier<Item> supplier) {
        var item = ITEM_REGISTRY.register(id, supplier);
        ITEMS.put(item, JCraft.id(id));
        return item;
    }

    static Item.Settings settings() {
        return new Item.Settings();
    }

    static void init() {

    }
}
