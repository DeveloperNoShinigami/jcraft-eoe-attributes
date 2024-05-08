package net.arna.jcraft.fabric.datagen;

import net.arna.jcraft.JCraft;
import net.arna.jcraft.registry.JBlockRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementFrame;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.text.Text;

import java.util.function.Consumer;

public class JAdvancementProvider extends FabricAdvancementProvider {
    public JAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        // obtain meteorite iron ore
        final Advancement obtainMeteoriteIronOre = Advancement.Builder.create()
                .display(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get(),
                        Text.literal("On the Precipice of Greatness"),
                        Text.literal("Obtain Meteorite Iron Ore"),
                        JCraft.id("textures/block/foolish_sand_block.png"),
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false)
                .criterion("has_ore", InventoryChangedCriterion.Conditions.items(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()))
                .build(JCraft.id("obtain_meteorite_iron_ore"));
        consumer.accept(obtainMeteoriteIronOre);
        // obtain stand arrow
        final Advancement obtainStandArrow = Advancement.Builder.create()
                .display(JItemRegistry.STANDARROW.get(),
                        Text.literal("Stand Proud"),
                        Text.literal("Obtain a Stand Arrow"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .criterion("has_arrow", InventoryChangedCriterion.Conditions.items(JItemRegistry.STANDARROW.get()))
                .build(JCraft.id("obtain_stand_arrow"));
        consumer.accept(obtainStandArrow);
        // obtain stand CD
        final Advancement obtainStandDisc = Advancement.Builder.create()
                .display(JItemRegistry.STAND_DISC.get(),
                        Text.literal("Spin Me Right Round"),
                        Text.literal("Obtain a Stand Disc"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false)
                .parent(obtainStandArrow)
                .criterion("has_disc", InventoryChangedCriterion.Conditions.items(JItemRegistry.STAND_DISC.get()))
                .build(JCraft.id("obtain_stand_disc"));
        consumer.accept(obtainStandDisc);
        // obtain living arrow
        final Advancement obtainLivingArrow = Advancement.Builder.create()
                .display(JItemRegistry.LIVINGARROW.get(),
                        Text.literal("It's Alive!"),
                        Text.literal("Obtain a Living Arrow"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        false,
                        false)
                .parent(obtainStandArrow)
                .criterion("has_arrow", InventoryChangedCriterion.Conditions.items(JItemRegistry.LIVINGARROW.get()))
                .build(JCraft.id("obtain_living_arrow"));
        consumer.accept(obtainLivingArrow);
        // obtain requiem arrow
        final Advancement obtainRequiemArrow = Advancement.Builder.create()
                .display(JItemRegistry.REQUIEMARROW.get(),
                        Text.literal("Requiem"),
                        Text.literal("Obtain a Requiem Arrow"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        false,
                        false)
                .parent(obtainStandArrow)
                .criterion("has_arrow", InventoryChangedCriterion.Conditions.items(JItemRegistry.REQUIEMARROW.get()))
                .build(JCraft.id("obtain_requiem_arrow"));
        consumer.accept(obtainRequiemArrow);
        // find stone mask
        final Advancement findStoneMask = Advancement.Builder.create()
                .display(JItemRegistry.STONE_MASK.get(),
                        Text.literal("This is gonna hurt…"),
                        Text.literal("Find a Stone Mask"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .criterion("has_mask", InventoryChangedCriterion.Conditions.items(JItemRegistry.STONE_MASK.get()))
                .build(JCraft.id("find_stone_mask"));
        consumer.accept(findStoneMask);
        // obtain coffin block
        final Advancement obtainCoffin = Advancement.Builder.create()
                .display(JItemRegistry.COFFIN_BLOCK.get(),
                        Text.literal("Sleepy Vampire"),
                        Text.literal("Obtain a Coffin"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false)
                .parent(findStoneMask)
                .criterion("has_coffin", InventoryChangedCriterion.Conditions.items(JItemRegistry.COFFIN_BLOCK.get()))
                .build(JCraft.id("obtain_coffin"));
        consumer.accept(obtainCoffin);
        // obtain sun protections
        final Advancement obtainSunProtection = Advancement.Builder.create()
                .display(JItemRegistry.KARSHEADWRAP.get(),
                        Text.literal("Rise and Shine"),
                        Text.literal("Obtain all sun protection items"),
                        null,
                        AdvancementFrame.GOAL,
                        true,
                        false,
                        false)
                .parent(findStoneMask)
                .criterion("has_kars_headwrap", InventoryChangedCriterion.Conditions.items(JItemRegistry.KARSHEADWRAP.get()))
                .criterion("has_red_hat", InventoryChangedCriterion.Conditions.items(JItemRegistry.RED_HAT.get()))
                .build(JCraft.id("obtain_sun_protection"));
        consumer.accept(obtainSunProtection);
        // obtain blood bottle
        final Advancement obtainBloodBottle = Advancement.Builder.create()
                .display(JItemRegistry.BLOOD_BOTTLE.get(),
                        Text.literal("Not Kool-Aid"),
                        Text.literal("Obtain a blood bottle"),
                        null,
                        AdvancementFrame.TASK,
                        true,
                        false,
                        false)
                .parent(findStoneMask)
                .criterion("has_bottle", InventoryChangedCriterion.Conditions.items(JItemRegistry.BLOOD_BOTTLE.get()))
                .build(JCraft.id("obtain_blood_bottle"));
        consumer.accept(obtainBloodBottle);
        // obtain Jotaro outfit
        final Advancement obtainJotaroOutfit = Advancement.Builder.create()
                .display(JItemRegistry.JOTAROCAP.get(),
                        Text.literal("ORA ORA"),
                        Text.literal("Obtain all of Jotaro's clothes"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .criterion("has_jotaro_cap", InventoryChangedCriterion.Conditions.items(JItemRegistry.JOTAROCAP.get()))
                .criterion("has_jotaro_jacket", InventoryChangedCriterion.Conditions.items(JItemRegistry.JOTAROJACKET.get()))
                .criterion("has_jotaro_pants", InventoryChangedCriterion.Conditions.items(JItemRegistry.JOTAROPANTS.get()))
                .criterion("has_jotaro_boots", InventoryChangedCriterion.Conditions.items(JItemRegistry.JOTAROBOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_jotaro_outfit"));
        consumer.accept(obtainJotaroOutfit);
        // obtain Dio outfit
        final Advancement obtainDioOutfit = Advancement.Builder.create()
                .display(JItemRegistry.DIOHEADBAND.get(),
                        Text.literal("MUDA MUDA"),
                        Text.literal("Obtain all of Dio's clothes"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .criterion("has_dio_headband", InventoryChangedCriterion.Conditions.items(JItemRegistry.DIOHEADBAND.get()))
                .criterion("has_dio_jacket", InventoryChangedCriterion.Conditions.items(JItemRegistry.DIOJACKET.get()))
                .criterion("has_dio_cape", InventoryChangedCriterion.Conditions.items(JItemRegistry.DIOCAPE.get()))
                .criterion("has_dio_pants", InventoryChangedCriterion.Conditions.items(JItemRegistry.DIOPANTS.get()))
                .criterion("has_dio_boots", InventoryChangedCriterion.Conditions.items(JItemRegistry.DIOBOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .rewards(AdvancementRewards.Builder.recipe(JCraft.id("dios_diary")))
                .build(JCraft.id("obtain_dio_outfit"));
        consumer.accept(obtainDioOutfit);
        // obtain Dio's diary
        final Advancement obtainDiosDiary = Advancement.Builder.create()
                .display(JItemRegistry.DIOSDIARY.get(),
                        Text.literal("It was me, DIO!"),
                        Text.literal("Obtain Dio's diary"),
                        null,
                        AdvancementFrame.CHALLENGE,
                        true,
                        false,
                        false)
                .parent(obtainDioOutfit)
                .criterion("has_diary", InventoryChangedCriterion.Conditions.items(JItemRegistry.DIOSDIARY.get()))
                .rewards(AdvancementRewards.Builder.experience(500))
                .build(JCraft.id("obtain_dios_diary"));
        consumer.accept(obtainDiosDiary);
    }
}