package net.arna.jcraft.fabric.datagen;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.api.stand.StandType;
import net.arna.jcraft.common.advancements.ObtainedStandTrigger;
import net.arna.jcraft.api.registry.JBlockRegistry;
import net.arna.jcraft.api.registry.JItemRegistry;
import net.arna.jcraft.api.registry.JStandTypeRegistry;
import net.arna.jcraft.api.registry.JTagRegistry;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricAdvancementProvider;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.FrameType;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.network.chat.Component;

import java.util.List;
import java.util.function.Consumer;

public class JAdvancementProvider extends FabricAdvancementProvider {
    public JAdvancementProvider(FabricDataOutput output) {
        super(output);
    }

    @Override
    public void generateAdvancement(Consumer<Advancement> consumer) {
        // obtain meteorite iron ore
        final Advancement obtainMeteoriteIronOre = Advancement.Builder.advancement()
                .display(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get(),
                        Component.literal("On the Precipice of Greatness"),
                        Component.literal("Obtain Meteorite Iron Ore"),
                        JCraft.id("textures/block/foolish_sand_block.png"),
                        FrameType.TASK,
                        true,
                        false,
                        false)
                .addCriterion("has_ore", InventoryChangeTrigger.TriggerInstance.hasItems(JBlockRegistry.METEORITE_IRON_ORE_BLOCK.get()))
                .build(JCraft.id("obtain_meteorite_iron_ore"));
        consumer.accept(obtainMeteoriteIronOre);
        // obtain stand
        final Advancement obtainStand = Advancement.Builder.advancement()
                .display(JItemRegistry.STAND_ARROW.get(),
                        Component.literal("Stand Proud"),
                        Component.literal("Obtain a Stand"),
                        null,
                        FrameType.TASK,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .addCriterion("has_stand", ObtainedStandTrigger.TriggerInstance.obtainedStand())
                .build(JCraft.id("obtain_stand"));
        consumer.accept(obtainStand);
        // obtain stand CD
        final Advancement obtainStandDisc = Advancement.Builder.advancement()
                .display(JItemRegistry.STAND_DISC.get(),
                        Component.literal("Spin Me Right Round"),
                        Component.literal("Obtain a Stand Disc"),
                        null,
                        FrameType.TASK,
                        true,
                        false,
                        false)
                .parent(obtainStand)
                .addCriterion("has_disc", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STAND_DISC.get()))
                .build(JCraft.id("obtain_stand_disc"));
        consumer.accept(obtainStandDisc);
        // obtain "all" stands
        // stand data is not available during datagen so we have to list all stands needed for the achievement
        final var obtainables = List.of(
                JStandTypeRegistry.STAR_PLATINUM,
                JStandTypeRegistry.STAR_PLATINUM_THE_WORLD,
                JStandTypeRegistry.MAGICIANS_RED,
                JStandTypeRegistry.THE_WORLD,
                JStandTypeRegistry.KING_CRIMSON,
                JStandTypeRegistry.D4C,
                JStandTypeRegistry.CREAM,
                JStandTypeRegistry.KILLER_QUEEN,
                JStandTypeRegistry.WHITE_SNAKE,
                JStandTypeRegistry.SILVER_CHARIOT,
                JStandTypeRegistry.THE_FOOL,
                JStandTypeRegistry.GOLD_EXPERIENCE,
                JStandTypeRegistry.HIEROPHANT_GREEN,
                JStandTypeRegistry.THE_SUN,
                JStandTypeRegistry.PURPLE_HAZE,
                JStandTypeRegistry.C_MOON,
                JStandTypeRegistry.MADE_IN_HEAVEN,
                JStandTypeRegistry.THE_WORLD_OVER_HEAVEN,
                JStandTypeRegistry.KILLER_QUEEN_BITES_THE_DUST,
                JStandTypeRegistry.GOLD_EXPERIENCE_REQUIEM,
                JStandTypeRegistry.PURPLE_HAZE_DISTORTION,
                JStandTypeRegistry.HORUS,
                JStandTypeRegistry.SHADOW_THE_WORLD,
                JStandTypeRegistry.METALLICA,
                JStandTypeRegistry.THE_HAND,
                JStandTypeRegistry.MANDOM
        );
        final Advancement.Builder obtainAllStandsBuilder = Advancement.Builder.advancement()
                .display(JItemRegistry.STAND_DISC.get(),
                        Component.literal("Roundabout"),
                        Component.literal("Obtain all Stands"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainStandDisc)
                .rewards(AdvancementRewards.Builder.experience(1395)); // that's from level 0 to 30
        for (final RegistrySupplier<StandType> type : obtainables) {
            obtainAllStandsBuilder.addCriterion("has_" + type.getId().getPath(), ObtainedStandTrigger.TriggerInstance.obtainedStand(type.get()));
        }
        consumer.accept(obtainAllStandsBuilder.build(JCraft.id("obtain_all_stands")));
        // obtain living arrow
        final Advancement obtainLivingArrow = Advancement.Builder.advancement()
                .display(JItemRegistry.LIVING_ARROW.get(),
                        Component.literal("It's Alive!"),
                        Component.literal("Obtain a Living Arrow"),
                        null,
                        FrameType.GOAL,
                        true,
                        false,
                        false)
                .parent(obtainStand)
                .addCriterion("has_arrow", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.LIVING_ARROW.get()))
                .build(JCraft.id("obtain_living_arrow"));
        consumer.accept(obtainLivingArrow);
        // obtain requiem arrow
        final Advancement obtainRequiemArrow = Advancement.Builder.advancement()
                .display(JItemRegistry.REQUIEM_ARROW.get(),
                        Component.literal("Requiem"),
                        Component.literal("Obtain a Requiem Arrow"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        false,
                        false)
                .parent(obtainStand)
                .addCriterion("has_arrow", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.REQUIEM_ARROW.get()))
                .build(JCraft.id("obtain_requiem_arrow"));
        consumer.accept(obtainRequiemArrow);
        // find stone mask
        final Advancement findStoneMask = Advancement.Builder.advancement()
                .display(JItemRegistry.STONE_MASK.get(),
                        Component.literal("This is gonna hurt…"),
                        Component.literal("Find a Stone Mask"),
                        null,
                        FrameType.TASK,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .addCriterion("has_mask", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.STONE_MASK.get()))
                .build(JCraft.id("find_stone_mask"));
        consumer.accept(findStoneMask);
        // obtain coffin block
        final Advancement obtainCoffin = Advancement.Builder.advancement()
                .display(JItemRegistry.COFFIN_BLOCK.get(),
                        Component.literal("Sleepy Vampire"),
                        Component.literal("Obtain a Coffin"),
                        null,
                        FrameType.TASK,
                        true,
                        false,
                        false)
                .parent(findStoneMask)
                .addCriterion("has_coffin", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.COFFIN_BLOCK.get()))
                .build(JCraft.id("obtain_coffin"));
        consumer.accept(obtainCoffin);
        // obtain sun protections
        final Advancement obtainSunProtection = Advancement.Builder.advancement()
                .display(JItemRegistry.KARS_HEADWRAP.get(),
                        Component.literal("Rise and Shine"),
                        Component.literal("Obtain all sun protection items"),
                        null,
                        FrameType.GOAL,
                        true,
                        false,
                        false)
                .parent(findStoneMask)
                .addCriterion("has_kars_headwrap", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.KARS_HEADWRAP.get()))
                .addCriterion("has_red_hat", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.RED_HAT.get()))
                .addCriterion("has_puccis_hat", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.PUCCIS_HAT.get()))
                .addCriterion("has_risotto_cap", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.RISOTTO_CAP.get()))
                .build(JCraft.id("obtain_sun_protection"));
        consumer.accept(obtainSunProtection);
        // obtain blood bottle
        final Advancement obtainBloodBottle = Advancement.Builder.advancement()
                .display(JItemRegistry.BLOOD_BOTTLE.get(),
                        Component.literal("Not Kool-Aid"),
                        Component.literal("Obtain a Blood Bottle"),
                        null,
                        FrameType.TASK,
                        true,
                        false,
                        false)
                .parent(findStoneMask)
                .addCriterion("has_bottle", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.BLOOD_BOTTLE.get()))
                .build(JCraft.id("obtain_blood_bottle"));
        consumer.accept(obtainBloodBottle);
        // obtain any cosplay
        final Advancement obtainCosplay = Advancement.Builder.advancement()
                .display(JItemRegistry.DIO_CAPE.get(),
                        Component.literal("Bizarre Outfit"),
                        Component.literal("Obtain any cosplay piece"),
                        null,
                        FrameType.GOAL,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .addCriterion("has_cosplay", InventoryChangeTrigger.TriggerInstance.hasItems(ItemPredicate.Builder.item().of(JTagRegistry.COSPLAY).build()))
                .build(JCraft.id("obtain_cosplay"));
        consumer.accept(obtainCosplay);
        // obtain Jotaro outfit
        final Advancement obtainJotaroOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.JOTARO_CAP.get(),
                        Component.literal("ORA ORA"),
                        Component.literal("Obtain all of Jotaro's clothes from Part 3"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_jotaro_cap", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_CAP.get()))
                .addCriterion("has_jotaro_jacket", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_JACKET.get()))
                .addCriterion("has_jotaro_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_PANTS.get()))
                .addCriterion("has_jotaro_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_jotaro_outfit"));
        consumer.accept(obtainJotaroOutfit);
        // obtain Jotaro P4 outfit
        final Advancement obtainJotaroP4Outfit = Advancement.Builder.advancement()
                .display(JItemRegistry.JOTARO_P4_CAP.get(),
                        Component.literal("Ugly watch"),
                        Component.literal("Obtain all of Jotaro's clothes from Part 4"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainJotaroOutfit)
                .addCriterion("has_jotaro_p4_cap", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_P4_CAP.get()))
                .addCriterion("has_jotaro_p4_jacket", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_P4_JACKET.get()))
                .addCriterion("has_jotaro_p4_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_P4_PANTS.get()))
                .addCriterion("has_jotaro_p4_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOTARO_P4_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_jotaro_p4_outfit"));
        consumer.accept(obtainJotaroP4Outfit);
        // obtain Kakyoin outfit
        final Advancement obtainKakyoinOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.KAKYOIN_WIG.get(),
                        Component.literal("I see"),
                        Component.literal("Obtain all of Kakyoin's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_kakyoin_wig", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.KAKYOIN_WIG.get()))
                .addCriterion("has_kakyoin_coat", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.KAKYOIN_COAT.get()))
                .addCriterion("has_kakyoin_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.KAKYOIN_PANTS.get()))
                .addCriterion("has_kakyoin_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.KAKYOIN_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_kakyoin_outfit"));
        consumer.accept(obtainKakyoinOutfit);
        // obtain Risotto outfit
        final Advancement obtainRisottoOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.RISOTTO_CAP.get(),
                        Component.literal("I know how I'll kill you"),
                        Component.literal("Obtain all of Risotto's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_risotto_cap", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.RISOTTO_CAP.get()))
                .addCriterion("has_risotto_jacket", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.RISOTTO_JACKET.get()))
                .addCriterion("has_risotto_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.RISOTTO_PANTS.get()))
                .addCriterion("has_risotto_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.RISOTTO_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_risotto_outfit"));
        consumer.accept(obtainRisottoOutfit);
        // obtain Risotto outfit
        final Advancement obtainDoppioOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.DOPPIO_WIG.get(),
                        Component.literal("Ring! Ring!"),
                        Component.literal("Obtain all of Doppio's clothes"),
                        null,
                        FrameType.GOAL,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_doppio_wig", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DOPPIO_WIG.get()))
                .addCriterion("has_doppio_jacket", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DOPPIO_SHIRT.get()))
                .build(JCraft.id("obtain_doppio_outfit"));
        consumer.accept(obtainDoppioOutfit);
        // obtain Diavolo outfit
        final Advancement obtainDiavoloOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.DIAVOLO_WIG.get(),
                        Component.literal("This is a test"),
                        Component.literal("Obtain all of Diavolo's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainDoppioOutfit)
                .addCriterion("has_diavolo_wig", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIAVOLO_WIG.get()))
                .addCriterion("has_diavolo_shirt", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIAVOLO_SHIRT.get()))
                .addCriterion("has_diavolo_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIAVOLO_PANTS.get()))
                .addCriterion("has_diavolo_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIAVOLO_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_diavolo_outfit"));
        consumer.accept(obtainDiavoloOutfit);
        // obtain Johnny outfit
        final Advancement obtainJohnnyOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.JOHNNY_CAP.get(),
                        Component.literal("No Wavering"),
                        Component.literal("Obtain all of Johnny's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_johnny_cap", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOHNNY_CAP.get()))
                .addCriterion("has_johnny_jacket", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOHNNY_JACKET.get()))
                .addCriterion("has_johnny_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOHNNY_PANTS.get()))
                .addCriterion("has_johnny_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.JOHNNY_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_johnny_outfit"));
        consumer.accept(obtainJohnnyOutfit);
        // obtain Gyro outfit
        final Advancement obtainGyroOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.GYRO_HAT.get(),
                        Component.literal("Pizza Mozzarella"),
                        Component.literal("Obtain all of Gyro's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_gyro_hat", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.GYRO_HAT.get()))
                .addCriterion("has_gyro_shirt", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.GYRO_SHIRT.get()))
                .addCriterion("has_gyro_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.GYRO_PANTS.get()))
                .addCriterion("has_gyro_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.GYRO_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_gyro_outfit"));
        consumer.accept(obtainGyroOutfit);
        // obtain Pucci outfit
        final Advancement obtainPucciOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.PUCCIS_HAT.get(),
                        Component.literal("Count prime numbers"),
                        Component.literal("Obtain all of Pucci's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_pucci_hat", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.PUCCIS_HAT.get()))
                .addCriterion("has_pucci_robe", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.PUCCI_ROBE.get()))
                .addCriterion("has_pucci_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.PUCCI_PANTS.get()))
                .addCriterion("has_pucci_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.PUCCI_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_pucci_outfit"));
        consumer.accept(obtainPucciOutfit);
        // obtain Dio outfit
        final Advancement obtainDioOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.DIO_HEADBAND.get(),
                        Component.literal("MUDA MUDA"),
                        Component.literal("Obtain all of Dio's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainCosplay)
                .addCriterion("has_dio_headband", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIO_HEADBAND.get()))
                .addCriterion("has_dio_jacket", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIO_JACKET.get()))
                .addCriterion("has_dio_cape", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIO_CAPE.get()))
                .addCriterion("has_dio_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIO_PANTS.get()))
                .addCriterion("has_dio_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIO_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .rewards(AdvancementRewards.Builder.recipe(JCraft.id("dios_diary")))
                .build(JCraft.id("obtain_dio_outfit"));
        consumer.accept(obtainDioOutfit);
        // obtain Heaven Attained outfit
        final Advancement obtainHeavenAttainedOutfit = Advancement.Builder.advancement()
                .display(JItemRegistry.HEAVEN_ATTAINED_WIG.get(),
                        Component.literal("Heaven attained"),
                        Component.literal("Obtain all of Heaven attained Dio's clothes"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        true,
                        false)
                .parent(obtainDioOutfit)
                .addCriterion("has_heaven_attained_headband", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.HEAVEN_ATTAINED_WIG.get()))
                .addCriterion("has_heaven_attained_shirt", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.HEAVEN_ATTAINED_SHIRT.get()))
                .addCriterion("has_heaven_attained_pants", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.HEAVEN_ATTAINED_PANTS.get()))
                .addCriterion("has_heaven_attained_boots", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.HEAVEN_ATTAINED_BOOTS.get()))
                .rewards(AdvancementRewards.Builder.experience(200))
                .build(JCraft.id("obtain_heaven_attained_outfit"));
        consumer.accept(obtainHeavenAttainedOutfit);
        // obtain Diary Page
        final Advancement obtainDiaryPage = Advancement.Builder.advancement()
                .display(JItemRegistry.DIARY_PAGE.get(),
                        Component.literal("It was me, §kDIO"),
                        Component.literal("Obtain a Diary Page"),
                        null,
                        FrameType.GOAL,
                        true,
                        false,
                        false)
                .parent(obtainMeteoriteIronOre)
                .addCriterion("has_diary_page", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIARY_PAGE.get()))
                .rewards(AdvancementRewards.Builder.experience(100))
                .build(JCraft.id("obtain_diary_page"));
        consumer.accept(obtainDiaryPage);
        // obtain Dio's diary
        final Advancement obtainDiosDiary = Advancement.Builder.advancement()
                .display(JItemRegistry.DIOS_DIARY.get(),
                        Component.literal("It was me, DIO!"),
                        Component.literal("Obtain DIO's diary"),
                        null,
                        FrameType.CHALLENGE,
                        true,
                        false,
                        false)
                .parent(obtainDiaryPage)
                .addCriterion("has_diary", InventoryChangeTrigger.TriggerInstance.hasItems(JItemRegistry.DIOS_DIARY.get()))
                .rewards(AdvancementRewards.Builder.experience(800))
                .build(JCraft.id("obtain_dios_diary"));
        consumer.accept(obtainDiosDiary);
    }
}
