package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.common.block.CoffinBlock;
import net.arna.jcraft.common.block.FoolishSandBlock;
import net.arna.jcraft.common.block.SoulBlock;
import net.arna.jcraft.common.item.*;
import net.arna.jcraft.common.spec.SpecType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.LinkedHashMap;
import java.util.Map;

import static net.arna.jcraft.JCraft.*;

public interface JItemRegistry {

    //Registrar<Item> ITEM_REGISTRY = MANAGER.get().get(Registries.ITEM);
    //Registrar<Block> BLOCK_REGISTRY = MANAGER.get().get(Registries.BLOCK);

    Map<Block, Identifier> BLOCKS = new LinkedHashMap<>();
    Map<Item, Identifier> ITEMS = new LinkedHashMap<>();

    RegistrySupplier<Item> DEBUG_WAND = FabricLoader.getInstance().isDevelopmentEnvironment() ? ITEM_REGISTRY.register("debug_wand", () -> new DebugWand(settings())) : null;

    RegistrySupplier<Item> STANDARROW = ITEM_REGISTRY.register("stand_arrow", () -> new StandArrowItem(settings().rarity(Rarity.RARE).fireproof()));

    RegistrySupplier<Item> STAND_DISC = ITEM_REGISTRY.register("stand_disc", () -> new StandDiscItem(settings().rarity(Rarity.RARE).fireproof().maxCount(1)));

    RegistrySupplier<Item> FV_REVOLVER = ITEM_REGISTRY.register("fv_revolver", () -> new FVRevolverItem(settings().rarity(Rarity.UNCOMMON).maxDamage(1200)));

    RegistrySupplier<Item> BULLET = ITEM_REGISTRY.register("bullet", () -> new BulletItem(settings()));

    RegistrySupplier<Item> KQ_COIN = ITEM_REGISTRY.register("kq_coin", () -> new KQCoinItem(settings()));

    //RegistrySupplier<Item> GREENBABY = register("green_baby", new GreenBabyItem(settings().rarity(Rarity.RARE)));
    RegistrySupplier<Item> GREENBABY = ITEM_REGISTRY.register("green_baby", () -> new GreenBabyItem(settings().rarity(Rarity.RARE)));

    //RegistrySupplier<Item> DIOSDIARY = register("dios_diary", new DIOsDiaryItem(settings().rarity(Rarity.EPIC).fireproof()));
    RegistrySupplier<Item> DIOSDIARY = ITEM_REGISTRY.register("dios_diary", () -> new DIOsDiaryItem(settings().rarity(Rarity.EPIC).fireproof()));


    RegistrySupplier<Item> SINNERSSOUL = ITEM_REGISTRY.register("sinners_soul", () -> new SinnersSoulItem(settings()));

    RegistrySupplier<Item> KNIFE = ITEM_REGISTRY.register("knife", () -> new KnifeItem(settings()));

    RegistrySupplier<Item> KNIFEBUNDLE = ITEM_REGISTRY.register("knife_bundle", () -> new KnifeBundleItem(settings().maxCount(1)));

    RegistrySupplier<Item> ANUBIS = ITEM_REGISTRY.register("anubis", () -> new AnubisItem(settings().rarity(Rarity.RARE).maxCount(1)));

    // Spec Obtainment Items
    RegistrySupplier<Item> ANUBISSHEATHED = ITEM_REGISTRY.register("anubis_sheathed", () -> new SheathedAnubisItem(settings().rarity(Rarity.RARE).maxCount(1), SpecType.ANUBIS));

    RegistrySupplier<Item> BOXINGGLOVES = ITEM_REGISTRY.register("boxing_gloves", () -> new BoxingGlovesItem(settings().maxCount(1), SpecType.BRAWLER));


    RegistrySupplier<Item> REQUIEMRUBY = ITEM_REGISTRY.register("requiem_ruby", () -> new Item(settings().rarity(Rarity.EPIC).fireproof()));

    RegistrySupplier<Item> REQUIEMARROW = ITEM_REGISTRY.register("requiem_arrow", () -> new RequiemArrowItem(settings().rarity(Rarity.EPIC).fireproof()));

    RegistrySupplier<Item> LIVINGARROW = ITEM_REGISTRY.register("living_arrow", () -> new LivingArrowItem(settings().rarity(Rarity.RARE).fireproof()));

    RegistrySupplier<Item> DIOHEADBAND = ITEM_REGISTRY.register("dio_headband", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, settings()));
    RegistrySupplier<Item> DIOJACKET = ITEM_REGISTRY.register("dio_jacket", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, settings()));
    RegistrySupplier<Item> DIOPANTS = ITEM_REGISTRY.register("dio_pants", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, settings()));
    RegistrySupplier<Item> DIOBOOTS = ITEM_REGISTRY.register("dio_boots", () -> new DIOArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, settings()));

    RegistrySupplier<Item> DIOCAPE = ITEM_REGISTRY.register("dio_cape", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, settings()));

    RegistrySupplier<Item> KARSHEADWRAP = ITEM_REGISTRY.register("kars_headwrap", () -> new SunProtectionItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, settings()));

    RegistrySupplier<Item> RED_HAT = ITEM_REGISTRY.register("red_hat", () -> new SunProtectionItem(ArmorMaterials.IRON, ArmorItem.Type.HELMET, settings()));

    RegistrySupplier<Item> STONE_MASK = ITEM_REGISTRY.register("stone_mask", () -> new StoneMaskItem(ArmorMaterials.CHAIN, ArmorItem.Type.HELMET, settings()));

    RegistrySupplier<Item> JOTAROCAP = ITEM_REGISTRY.register("jotaro_cap", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.HELMET, settings()));
    RegistrySupplier<Item> JOTAROJACKET = ITEM_REGISTRY.register("jotaro_jacket", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.CHESTPLATE, settings()));
    RegistrySupplier<Item> JOTAROPANTS = ITEM_REGISTRY.register("jotaro_pants", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.LEGGINGS, settings()));
    RegistrySupplier<Item> JOTAROBOOTS = ITEM_REGISTRY.register("jotaro_boots", () -> new FlutteringArmorItem(ArmorMaterials.NETHERITE, ArmorItem.Type.BOOTS, settings()));

    RegistrySupplier<Item> CINDERELLA_MASK = ITEM_REGISTRY.register("cinderella_mask", () -> new CinderellaMaskItem());

    RegistrySupplier<Item> BLOOD_BOTTLE = ITEM_REGISTRY.register("blood_bottle", () -> new BloodBottleItem(settings().maxCount(1)));

    RegistrySupplier<Item> STELLAR_IRON_INGOT = ITEM_REGISTRY.register("stellar_iron_ingot", () -> new Item(settings()));
    RegistrySupplier<Item> STAND_ARROWHEAD = ITEM_REGISTRY.register("stand_arrowhead", () -> new Item(settings()));

    RegistrySupplier<Item> MOCK_ITEM = ITEM_REGISTRY.register("mock_item", () -> new MockItem());

    //Block
    RegistrySupplier<Item> FOOLISH_SAND_BLOCK = ITEM_REGISTRY.register("foolish_sand_block",
            () -> new BlockItem(JBlockRegistry.FOOLISH_SAND_BLOCK.get(), settings()
    ));
    RegistrySupplier<Item> SOUL_BLOCK = ITEM_REGISTRY.register("soul_block", () -> new BlockItem(JBlockRegistry.SOUL_BLOCK.get(), settings()
    ));
    RegistrySupplier<Item> METEORITE_BLOCK = ITEM_REGISTRY.register("meteorite_block", () -> new BlockItem(JBlockRegistry.METEORITE_BLOCK.get(), settings()
    ));
    RegistrySupplier<Item> METEORITE_IRON_ORE_BLOCK = ITEM_REGISTRY.register("meteorite_iron_ore_block",
            () -> new BlockItem(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get(), settings()

    ));
    RegistrySupplier<Item> COFFIN_BLOCK = ITEM_REGISTRY.register("coffin",
            () -> new BlockItem(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get() ,settings()));

    static Item.Settings settings() {
        return new Item.Settings();
    }

    static void init() {

    }
}
