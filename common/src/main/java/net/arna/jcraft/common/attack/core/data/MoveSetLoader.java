package net.arna.jcraft.common.attack.core.data;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import net.arna.jcraft.common.attack.actions.*;
import net.arna.jcraft.common.attack.conditions.HoldingAnubisCondition;
import net.arna.jcraft.common.attack.conditions.MetallicaIronCondition;
import net.arna.jcraft.common.attack.core.MoveAction;
import net.arna.jcraft.common.attack.core.MoveCondition;
import net.arna.jcraft.common.attack.moves.anubis.*;
import net.arna.jcraft.common.attack.moves.base.AbstractMove;
import net.arna.jcraft.common.attack.moves.cmoon.*;
import net.arna.jcraft.common.attack.moves.cream.*;
import net.arna.jcraft.common.attack.moves.dirtydeedsdonedirtcheap.*;
import net.arna.jcraft.common.attack.moves.goldexperience.BerryBushAttack;
import net.arna.jcraft.common.attack.moves.goldexperience.LifeGiverAttack;
import net.arna.jcraft.common.attack.moves.goldexperience.OverclockAttack;
import net.arna.jcraft.common.attack.moves.goldexperience.TreeAttack;
import net.arna.jcraft.common.attack.moves.goldexperience.requiem.*;
import net.arna.jcraft.common.attack.moves.hierophantgreen.EmeraldSplashAttack;
import net.arna.jcraft.common.attack.moves.hierophantgreen.NetSetMove;
import net.arna.jcraft.common.attack.moves.horus.*;
import net.arna.jcraft.common.attack.moves.killerqueen.*;
import net.arna.jcraft.common.attack.moves.killerqueen.bitesthedust.*;
import net.arna.jcraft.common.attack.moves.kingcrimson.*;
import net.arna.jcraft.common.attack.moves.madeinheaven.*;
import net.arna.jcraft.common.attack.moves.magiciansred.*;
import net.arna.jcraft.common.attack.moves.metallica.*;
import net.arna.jcraft.common.attack.moves.purplehaze.*;
import net.arna.jcraft.common.attack.moves.purplehaze.distortion.DistortionMove;
import net.arna.jcraft.common.attack.moves.shadowtheworld.ImpalingThrustAttack;
import net.arna.jcraft.common.attack.moves.shadowtheworld.STWChargeAttack;
import net.arna.jcraft.common.attack.moves.shadowtheworld.STWCounterAttack;
import net.arna.jcraft.common.attack.moves.shared.*;
import net.arna.jcraft.common.attack.moves.silverchariot.*;
import net.arna.jcraft.common.attack.moves.starplatinum.BlockBreakingAttack;
import net.arna.jcraft.common.attack.moves.starplatinum.InhaleAttack;
import net.arna.jcraft.common.attack.moves.starplatinum.theworld.SPTWGroundSlamAttack;
import net.arna.jcraft.common.attack.moves.starplatinum.theworld.TimeStrikeAttack;
import net.arna.jcraft.common.attack.moves.thefool.*;
import net.arna.jcraft.common.attack.moves.thehand.*;
import net.arna.jcraft.common.attack.moves.thesun.FireMeteorAttack;
import net.arna.jcraft.common.attack.moves.thesun.FireSunBeamAttack;
import net.arna.jcraft.common.attack.moves.thesun.MeteorShowerAttack;
import net.arna.jcraft.common.attack.moves.theworld.FeignBarrageCounterAttack;
import net.arna.jcraft.common.attack.moves.theworld.TWChargeAttack;
import net.arna.jcraft.common.attack.moves.theworld.TWDonutAttack;
import net.arna.jcraft.common.attack.moves.theworld.overheaven.*;
import net.arna.jcraft.common.attack.moves.vampire.*;
import net.arna.jcraft.common.attack.moves.whitesnake.ChargedSpewAttack;
import net.arna.jcraft.common.attack.moves.whitesnake.GiveStandAttack;
import net.arna.jcraft.common.attack.moves.whitesnake.MeltYourHeartAttack;
import net.arna.jcraft.common.attack.moves.whitesnake.PoisonSpewAttack;
import net.arna.jcraft.common.entity.stand.*;
import net.arna.jcraft.common.spec.AnubisSpec;
import net.arna.jcraft.common.spec.BrawlerSpec;
import net.arna.jcraft.common.spec.VampireSpec;
import net.arna.jcraft.platform.JPlatformUtils;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public class MoveSetLoader {
    public static final Supplier<Codec<MoveType<?>>> MOVE_TYPE_CODEC = JPlatformUtils::getMoveTypeCodec;
    public static final Supplier<Codec<AbstractMove<?, ?>>> MOVE_CODEC = Suppliers.memoize(() -> MOVE_TYPE_CODEC.get()
            .dispatch("type", AbstractMove::getMoveType, MoveType::getCodec));
    public static final Supplier<Codec<MoveConditionType<?>>> MOVE_CONDITION_TYPE_CODEC = JPlatformUtils::getMoveConditionTypeCodec;
    public static final Supplier<Codec<MoveCondition<?, ?>>> MOVE_CONDITION_CODEC = Suppliers.memoize(() -> MOVE_CONDITION_TYPE_CODEC.get()
            .dispatch("type", MoveCondition::getType, MoveConditionType::getCodec));
    public static final Supplier<Codec<MoveActionType<?>>> MOVE_ACTION_TYPE_CODEC = JPlatformUtils::getMoveActionTypeCodec;
    public static final Supplier<Codec<MoveAction<?, ?>>> MOVE_ACTION_CODEC = Suppliers.memoize(() -> MOVE_ACTION_TYPE_CODEC.get()
            .dispatch("type", MoveAction::getType, MoveActionType::getCodec));

    static final Map<Enum<?>, Map<String, MoveSet<?, ?>>> moveSets = new HashMap<>();

    public static void init() {
        // Stands
        registerMS(AtumEntity.MOVE_SET);
        registerMS(ChariotRequiemEntity.MOVE_SET);
        registerMS(CinderellaEntity.MOVE_SET);
        registerMS(CMoonEntity.MOVE_SET);
        registerMS(CreamEntity.DEFAULT_MOVE_SET);
        registerMS(CreamEntity.HALF_BALL_MOVE_SET);
        registerMS(D4CEntity.MOVE_SET);
        registerMS(DiverDownEntity.MOVE_SET);
        registerMS(DragonsDreamEntity.MOVE_SET);
        registerMS(FooFightersEntity.MOVE_SET);
        registerMS(GEREntity.MOVE_SET);
        registerMS(GoldExperienceEntity.MOVE_SET);
        registerMS(GooGooDollsEntity.MOVE_SET);
        registerMS(HGEntity.MOVE_SET);
        registerMS(HorusEntity.MOVE_SET);
        registerMS(KillerQueenEntity.MOVE_SET);
        registerMS(KingCrimsonEntity.MOVE_SET);
        registerMS(KQBTDEntity.MOVE_SET);
        registerMS(MadeInHeavenEntity.MOVE_SET);
        registerMS(MagiciansRedEntity.MOVE_SET);
        registerMS(MetallicaEntity.MOVE_SET);
        registerMS(OsirisEntity.MOVE_SET);
        registerMS(PurpleHazeDistortionEntity.MOVE_SET);
        registerMS(PurpleHazeEntity.MOVE_SET);
        registerMS(ShadowTheWorldEntity.MOVE_SET);
        registerMS(SilverChariotEntity.DEFAULT_MOVE_SET);
        registerMS(SilverChariotEntity.POSSESSED_MOVE_SET);
        registerMS(SPTWEntity.MOVE_SET);
        registerMS(StarPlatinumEntity.MOVE_SET);
        registerMS(TheFoolEntity.MOVE_SET);
        registerMS(TheHandEntity.MOVE_SET);
        registerMS(TheSunEntity.MOVE_SET);
        registerMS(TheWorldEntity.MOVE_SET);
        registerMS(TheWorldOverHeavenEntity.MOVE_SET);
        registerMS(WhiteSnakeEntity.DEFAULT_MOVE_SET);
        registerMS(WhiteSnakeEntity.REMOTE_MOVE_SET);

        // Specs
        registerMS(AnubisSpec.MOVE_SET);
        registerMS(BrawlerSpec.MOVE_SET);
        registerMS(VampireSpec.MOVE_SET);
    }

    private static void registerMS(MoveSet<?, ?> ms) {
        Map<String, MoveSet<?, ?>> moveSets = MoveSetLoader.moveSets.computeIfAbsent(ms.getType(), k -> new HashMap<>());
        if (moveSets.containsKey(ms.getName())) {
            throw new IllegalArgumentException("Duplicate moveset " + ms.getName() + " for " + ms.getType());
        }
        moveSets.put(ms.getName(), ms);
    }

    // Register move types using a registrar function.
    public static void registerMoves(BiConsumer<String, Supplier<MoveType<?>>> registrar) {
        // Generated with the following Python code:
        /*
        from pathlib import Path
        import re
        p = "C:\\...\\common\\src\\main\\java\\net\\arna\\jcraft\\common\\attack\\moves"
        files = list(Path(p).rglob("*.java"))
        out = ""
        ptrn = r"(?<!^)([A-Z][a-z]|(?<=[a-z])[^a-z]|(?<=[A-Z])[0-9_])"
        for file in files:
            parts = str(file)[(len(p)+1):-5].split("\\")
            c = parts[-1]
            if "Abstract" in c:
                continue

            prefix = "_".join(parts[:-1])
            out += "registrar.accept(\"" + prefix + "_" + re.sub(ptrn, r"_\1", c).lower() + "\", () -> " + c + ".Type.INSTANCE);\n"
         */

        // About 164 unique moves in total
        registrar.accept("anubis_low_kick_attack", () -> LowKickAttack.Type.INSTANCE);
        registrar.accept("anubis_rekka_3_attack", () -> Rekka3Attack.Type.INSTANCE);
        registrar.accept("anubis_simple_anubis_attack", () -> SimpleAnubisAttack.Type.INSTANCE);
        registrar.accept("anubis_simple_anubis_multi_hit_attack", () -> SimpleAnubisMultiHitAttack.Type.INSTANCE);
        registrar.accept("anubis_unsheathing_attack", () -> UnsheathingAttack.Type.INSTANCE);

        registrar.accept("cmoon_gravitational_hop_move", () -> GravitationalHopMove.Type.INSTANCE);
        registrar.accept("cmoon_gravity_shift_move", () -> GravityShiftMove.Type.INSTANCE);
        registrar.accept("cmoon_gravity_shift_pulse_move", () -> GravityShiftPulseMove.Type.INSTANCE);
        registrar.accept("cmoon_grav_punch_attack", () -> GravPunchAttack.Type.INSTANCE);
        registrar.accept("cmoon_ground_slam_attack", () -> CGroundSlamAttack.Type.INSTANCE);
        registrar.accept("cmoon_launch_attack", () -> LaunchAttack.Type.INSTANCE);

        registrar.accept("cream_ball_charge_attack", () -> BallChargeAttack.Type.INSTANCE);
        registrar.accept("cream_ball_mode_enter_move", () -> BallModeEnterMove.Type.INSTANCE);
        registrar.accept("cream_ball_mode_exit_move", () -> BallModeExitMove.Type.INSTANCE);
        registrar.accept("cream_consume_attack", () -> ConsumeAttack.Type.INSTANCE);
        registrar.accept("cream_cream_combo_attack", () -> CreamComboAttack.Type.INSTANCE);
        registrar.accept("cream_destroy_attack", () -> DestroyAttack.Type.INSTANCE);
        registrar.accept("cream_detach_charge_move", () -> DetachChargeMove.Type.INSTANCE);
        registrar.accept("cream_surprise_move", () -> SurpriseMove.Type.INSTANCE);

        registrar.accept("dirtydeedsdonedirtcheap_clone_spawn_move", () -> CloneSpawnMove.Type.INSTANCE);
        registrar.accept("dirtydeedsdonedirtcheap_d_4c_counter_attack", () -> D4CCounterAttack.Type.INSTANCE);
        registrar.accept("dirtydeedsdonedirtcheap_d_4c_grab_attack", () -> D4CGrabAttack.Type.INSTANCE);
        registrar.accept("dirtydeedsdonedirtcheap_dimensional_hop_move", () -> DimensionalHopMove.Type.INSTANCE);
        registrar.accept("dirtydeedsdonedirtcheap_flag_move", () -> FlagMove.Type.INSTANCE);
        registrar.accept("dirtydeedsdonedirtcheap_give_gun_move", () -> GiveGunMove.Type.INSTANCE);
        registrar.accept("dirtydeedsdonedirtcheap_item_place_move", () -> ItemPlaceMove.Type.INSTANCE);

        registrar.accept("goldexperience_berry_bush_attack", () -> BerryBushAttack.Type.INSTANCE);
        registrar.accept("goldexperience_life_giver_attack", () -> LifeGiverAttack.Type.INSTANCE);
        registrar.accept("goldexperience_overclock_attack", () -> OverclockAttack.Type.INSTANCE);
        registrar.accept("goldexperience_tree_attack", () -> TreeAttack.Type.INSTANCE);
        registrar.accept("goldexperience_requiem_flight_move", () -> FlightMove.Type.INSTANCE);
        registrar.accept("goldexperience_requiem_life_beam_attack", () -> LifeBeamAttack.Type.INSTANCE);
        registrar.accept("goldexperience_requiem_nullification_attack", () -> NullificationAttack.Type.INSTANCE);
        registrar.accept("goldexperience_requiem_overhead_kick_attack", () -> OverheadKickAttack.Type.INSTANCE);
        registrar.accept("goldexperience_requiem_return_to_zero_move", () -> ReturnToZeroMove.Type.INSTANCE);

        registrar.accept("hierophantgreen_emerald_splash_attack", () -> EmeraldSplashAttack.Type.INSTANCE);
        registrar.accept("hierophantgreen_net_set_move", () -> NetSetMove.Type.INSTANCE);

        registrar.accept("horus_chasing_freeze_attack", () -> ChasingFreezeAttack.Type.INSTANCE);
        registrar.accept("horus_detonate_attack", () -> HorusDetonateAttack.Type.INSTANCE);
        registrar.accept("horus_horus_barrage_attack", () -> HorusBarrageAttack.Type.INSTANCE);
        registrar.accept("horus_horus_divekick_attack", () -> HorusDivekickAttack.Type.INSTANCE);
        registrar.accept("horus_ice_lance_attack", () -> IceLanceAttack.Type.INSTANCE);
        registrar.accept("horus_icicle_fire_attack", () -> IcicleFireAttack.Type.INSTANCE);
        registrar.accept("horus_perfect_freeze_attack", () -> PerfectFreezeAttack.Type.INSTANCE);
        registrar.accept("horus_scatter_attack", () -> ScatterAttack.Type.INSTANCE);
        registrar.accept("horus_stomp_attack", () -> StompAttack.Type.INSTANCE);

        registrar.accept("killerqueen_bomb_plant_attack", () -> BombPlantAttack.Type.INSTANCE);
        registrar.accept("killerqueen_coin_toss_move", () -> CoinTossMove.Type.INSTANCE);
        registrar.accept("killerqueen_detonate_attack", () -> KQDetonateAttack.Type.INSTANCE);
        registrar.accept("killerqueen_explosive_dash_attack", () -> ExplosiveDashAttack.Type.INSTANCE);
        registrar.accept("killerqueen_kq_grab_attack", () -> KQGrabAttack.Type.INSTANCE);
        registrar.accept("killerqueen_kq_grab_hit_attack", () -> KQGrabHitAttack.Type.INSTANCE);
        registrar.accept("killerqueen_sheer_heart_attack_attack", () -> SheerHeartAttackAttack.Type.INSTANCE);

        registrar.accept("killerqueen_bitesthedust_btd_detonate_attack", () -> BTDDetonateAttack.Type.INSTANCE);
        registrar.accept("killerqueen_bitesthedust_btd_grab_hit_attack", () -> BTDGrabHitAttack.Type.INSTANCE);
        registrar.accept("killerqueen_bitesthedust_btd_plant_attack", () -> BTDPlantAttack.Type.INSTANCE);
        registrar.accept("killerqueen_bitesthedust_bubble_attack", () -> BubbleAttack.Type.INSTANCE);
        registrar.accept("killerqueen_bitesthedust_bubble_counter_attack", () -> BubbleCounterAttack.Type.INSTANCE);
        registrar.accept("killerqueen_bitesthedust_elbow_attack", () -> ElbowAttack.Type.INSTANCE);

        registrar.accept("kingcrimson_blood_throw_attack", () -> BloodThrowAttack.Type.INSTANCE);
        registrar.accept("kingcrimson_epitaph_attack", () -> EpitaphAttack.Type.INSTANCE);
        registrar.accept("kingcrimson_kc_donut_attack", () -> KCDonutAttack.Type.INSTANCE);
        registrar.accept("kingcrimson_prediction_move", () -> PredictionMove.Type.INSTANCE);
        registrar.accept("kingcrimson_time_erase_move", () -> TimeEraseMove.Type.INSTANCE);

        registrar.accept("madeinheaven_circle_attack", () -> CircleAttack.Type.INSTANCE);
        registrar.accept("madeinheaven_fury_chop_attack", () -> FuryChopAttack.Type.INSTANCE);
        registrar.accept("madeinheaven_judgement_attack", () -> JudgementAttack.Type.INSTANCE);
        registrar.accept("madeinheaven_speed_slice_attack", () -> SpeedSliceAttack.Type.INSTANCE);
        registrar.accept("madeinheaven_time_acceleration_move", () -> TimeAccelerationMove.Type.INSTANCE);

        registrar.accept("magiciansred_crossfire_attack", () -> CrossfireAttack.Type.INSTANCE);
        registrar.accept("magiciansred_crossfire_hurricane_attack", () -> CrossfireHurricaneAttack.Type.INSTANCE);
        registrar.accept("magiciansred_crossfire_variation_attack", () -> CrossfireVariationAttack.Type.INSTANCE);
        registrar.accept("magiciansred_flamethrower_attack", () -> FlamethrowerAttack.Type.INSTANCE);
        registrar.accept("magiciansred_life_detector_attack", () -> LifeDetectorAttack.Type.INSTANCE);
        registrar.accept("magiciansred_red_bind_attack", () -> RedBindAttack.Type.INSTANCE);
        registrar.accept("magiciansred_redirect_attack", () -> RedirectAttack.Type.INSTANCE);

        registrar.accept("metallica_give_scalpel_move", () -> GiveScalpelMove.Type.INSTANCE);
        registrar.accept("metallica_bisect_attack", () -> BisectAttack.Type.INSTANCE);
        registrar.accept("metallica_bisect_charge_move", () -> BisectChargeMove.Type.INSTANCE);
        registrar.accept("metallica_fan_toss_attack", () -> FanTossAttack.Type.INSTANCE);
        registrar.accept("metallica_harvest_move", () -> HarvestMove.Type.INSTANCE);
        registrar.accept("metallica_internal_attack", () -> InternalAttack.Type.INSTANCE);
        registrar.accept("metallica_invisibility_move", () -> InvisibilityMove.Type.INSTANCE);
        registrar.accept("metallica_precise_toss_atack", () -> PreciseTossAtack.Type.INSTANCE);
        registrar.accept("metallica_create_magnetic_field_move", () -> CreateMagneticFieldMove.Type.INSTANCE);
        registrar.accept("metallica_explode_magnetic_field_move", () -> ExplodeMagneticFieldMove.Type.INSTANCE);
        registrar.accept("metallica_razor_cough_attack", () -> RazorCoughAttack.Type.INSTANCE);
        registrar.accept("metallica_remote_scalpel_move", () -> RemoteScalpelMove.Type.INSTANCE);

        registrar.accept("purplehaze_backhand_attack", () -> BackhandAttack.Type.INSTANCE);
        registrar.accept("purplehaze_full_release_attack", () -> FullReleaseAttack.Type.INSTANCE);
        registrar.accept("purplehaze_ground_slam_attack", () -> PHGroundSlamAttack.Type.INSTANCE);
        registrar.accept("purplehaze_launch_capsule_attack", () -> LaunchCapsuleAttack.Type.INSTANCE);
        registrar.accept("purplehaze_launch_capsules_attack", () -> LaunchCapsulesAttack.Type.INSTANCE);
        registrar.accept("purplehaze_play_move", () -> PlayMove.Type.INSTANCE);
        registrar.accept("purplehaze_rekka_attack", () -> PHRekkaAttack.Type.INSTANCE);
        registrar.accept("purplehaze_distortion_distortion_move", () -> DistortionMove.Type.INSTANCE);

        registrar.accept("shadowtheworld_charge_attack", () -> STWChargeAttack.Type.INSTANCE);
        registrar.accept("shadowtheworld_counter_attack", () -> STWCounterAttack.Type.INSTANCE);
        registrar.accept("shadowtheworld_impaling_thrust_attack", () -> ImpalingThrustAttack.Type.INSTANCE);

        registrar.accept("shared_barrage_attack", () -> BarrageAttack.Type.INSTANCE);
        registrar.accept("shared_charge_barrage_attack", () -> ChargeBarrageAttack.Type.INSTANCE);
        registrar.accept("shared_counter_miss_move", () -> CounterMissMove.Type.INSTANCE);
        registrar.accept("shared_grab_attack", () -> GrabAttack.Type.INSTANCE);
        registrar.accept("shared_heal_move", () -> HealMove.Type.INSTANCE);
        registrar.accept("shared_jump_move", () -> JumpMove.Type.INSTANCE);
        registrar.accept("shared_knockdown_attack", () -> KnockdownAttack.Type.INSTANCE);
        registrar.accept("shared_knockdown_barrage_attack", () -> KnockdownBarrageAttack.Type.INSTANCE);
        registrar.accept("shared_knockdown_multi_hit_attack", () -> KnockdownMultiHitAttack.Type.INSTANCE);
        registrar.accept("shared_main_barrage_attack", () -> MainBarrageAttack.Type.INSTANCE);
        registrar.accept("shared_no_op_move", () -> NoOpMove.Type.INSTANCE);
        registrar.accept("shared_pilot_mode_move", () -> PilotModeMove.Type.INSTANCE);
//        registrar.accept("shared_rekka_attack", () -> RekkaAttack.Type.INSTANCE); // unused
        registrar.accept("shared_simple_attack", () -> SimpleAttack.Type.INSTANCE);
        registrar.accept("shared_simple_holdable_move", () -> SimpleHoldableMove.Type.INSTANCE);
        registrar.accept("shared_simple_multi_hit_attack", () -> SimpleMultiHitAttack.Type.INSTANCE);
        registrar.accept("shared_simple_uppercut_attack", () -> SimpleUppercutAttack.Type.INSTANCE);
        registrar.accept("shared_time_skip_move", () -> TimeSkipMove.Type.INSTANCE);
        registrar.accept("shared_time_stop_move", () -> TimeStopMove.Type.INSTANCE);
        registrar.accept("shared_standby_on_move", () -> StandbyActivationMove.Type.INSTANCE);
        registrar.accept("shared_standby_off_move", () -> StandbyDeactivationMove.Type.INSTANCE);

        registrar.accept("silverchariot_armor_off_attack", () -> ArmorOffAttack.Type.INSTANCE);
        registrar.accept("silverchariot_circle_slash_attack", () -> CircleSlashAttack.Type.INSTANCE);
        registrar.accept("silverchariot_cleave_attack", () -> CleaveAttack.Type.INSTANCE);
        registrar.accept("silverchariot_god_of_death_attack", () -> GodOfDeathAttack.Type.INSTANCE);
        registrar.accept("silverchariot_god_of_death_hit_attack", () -> GodOfDeathHitAttack.Type.INSTANCE);
        registrar.accept("silverchariot_last_shot_attack", () -> LastShotAttack.Type.INSTANCE);
        registrar.accept("silverchariot_ray_dart_attack", () -> RayDartAttack.Type.INSTANCE);
        registrar.accept("silverchariot_sc_charge_attack", () -> SCChargeAttack.Type.INSTANCE);
        registrar.accept("silverchariot_sc_counter_attack", () -> SCCounterAttack.Type.INSTANCE);
        registrar.accept("silverchariot_spin_barrage_attack", () -> SpinBarrageAttack.Type.INSTANCE);

        registrar.accept("starplatinum_block_breaking_attack", () -> BlockBreakingAttack.Type.INSTANCE);
        registrar.accept("starplatinum_inhale_attack", () -> InhaleAttack.Type.INSTANCE);

        registrar.accept("starplatinum_theworld_ground_slam_attack", () -> SPTWGroundSlamAttack.Type.INSTANCE);
        registrar.accept("starplatinum_theworld_time_strike_attack", () -> TimeStrikeAttack.Type.INSTANCE);

        registrar.accept("thefool_air_barrage_attack", () -> AirBarrageAttack.Type.INSTANCE);
        registrar.accept("thefool_glide_move", () -> GlideMove.Type.INSTANCE);
        registrar.accept("thefool_pound_attack", () -> PoundAttack.Type.INSTANCE);
        registrar.accept("thefool_sand_clone_move", () -> SandCloneMove.Type.INSTANCE);
        registrar.accept("thefool_sandstorm_attack", () -> SandstormAttack.Type.INSTANCE);
        registrar.accept("thefool_sand_tornado_move", () -> SandTornadoMove.Type.INSTANCE);
        registrar.accept("thefool_sand_wave_attack", () -> SandWaveAttack.Type.INSTANCE);
        registrar.accept("thefool_slam_attack", () -> SlamAttack.Type.INSTANCE);
        registrar.accept("thefool_charge_attack", () -> TFChargeAttack.Type.INSTANCE);
        registrar.accept("thefool_combo_attack", () -> TFComboAttack.Type.INSTANCE);
        registrar.accept("thefool_launch_attack", () -> TFLaunchAttack.Type.INSTANCE);

        registrar.accept("thehand_erase_attack", () -> SimpleEraseAttack.Type.INSTANCE);
        registrar.accept("thehand_erase_ground_attack", () -> EraseGroundAttack.Type.INSTANCE);
        registrar.accept("thehand_erase_space_attack", () -> EraseSpaceAttack.Type.INSTANCE);
        registrar.accept("thehand_rage_attack", () -> RageAttack.Type.INSTANCE);
        registrar.accept("thehand_stomp2", () -> Stomp2Attack.Type.INSTANCE);

        registrar.accept("thesun_fire_meteor_attack", () -> FireMeteorAttack.Type.INSTANCE);
        registrar.accept("thesun_fire_sun_beam_attack", () -> FireSunBeamAttack.Type.INSTANCE);
        registrar.accept("thesun_meteor_shower_attack", () -> MeteorShowerAttack.Type.INSTANCE);

        registrar.accept("theworld_feign_barrage_counter_attack", () -> FeignBarrageCounterAttack.Type.INSTANCE);
        registrar.accept("theworld_charge_attack", () -> TWChargeAttack.Type.INSTANCE);
        registrar.accept("theworld_donut_attack", () -> TWDonutAttack.Type.INSTANCE);

        registrar.accept("theworld_overheaven_aerial_divine_finisher_attack", () -> AerialDivineFinisherAttack.Type.INSTANCE);
        registrar.accept("theworld_overheaven_charge_overwrite_move", () -> ChargeOverwriteMove.Type.INSTANCE);
        registrar.accept("theworld_overheaven_divine_finisher_attack", () -> DivineFinisherAttack.Type.INSTANCE);
        registrar.accept("theworld_overheaven_lunge_attack", () -> LungeAttack.Type.INSTANCE);
        registrar.accept("theworld_overheaven_overwrite_attack", () -> OverwriteAttack.Type.INSTANCE);
        registrar.accept("theworld_overheaven_singularity_attack", () -> SingularityAttack.Type.INSTANCE);
        registrar.accept("theworld_overheaven_smite_attack", () -> SmiteAttack.Type.INSTANCE);

        registrar.accept("vampire_blood_suck_attack", () -> BloodSuckAttack.Type.INSTANCE);
        registrar.accept("vampire_blood_suck_hits_attack", () -> BloodSuckHitsAttack.Type.INSTANCE);
        registrar.accept("vampire_night_vision_move", () -> NightVisionMove.Type.INSTANCE);
        registrar.accept("vampire_revive_move", () -> ReviveMove.Type.INSTANCE);
        registrar.accept("vampire_space_ripper_attack", () -> SpaceRipperAttack.Type.INSTANCE);

        registrar.accept("whitesnake_charged_spew_attack", () -> ChargedSpewAttack.Type.INSTANCE);
        registrar.accept("whitesnake_give_stand_attack", () -> GiveStandAttack.Type.INSTANCE);
        registrar.accept("whitesnake_melt_your_heart_attack", () -> MeltYourHeartAttack.Type.INSTANCE);
        registrar.accept("whitesnake_poison_spew_attack", () -> PoisonSpewAttack.Type.INSTANCE);
    }

    public static void registerConditions(BiConsumer<String, Supplier<MoveConditionType<?>>> registrar) {
        registrar.accept("metallica_iron", () -> MetallicaIronCondition.Type.INSTANCE);
        registrar.accept("holding_anubis", () -> HoldingAnubisCondition.Type.INSTANCE);
    }

    public static void registerActions(BiConsumer<String, Supplier<MoveActionType<?>>> registrar) {
        registrar.accept("play_sound", () -> PlaySoundAction.Type.INSTANCE);
        registrar.accept("cancel_spec_move", () -> CancelSpecMoveAction.Type.INSTANCE);
        registrar.accept("metallica_add_iron", () -> MetallicaAddIronAction.Type.INSTANCE);
        registrar.accept("effect", () -> EffectAction.Type.INSTANCE);
        registrar.accept("lunge", () -> LungeAction.Type.INSTANCE);
        registrar.accept("cmoon_inversion", () -> CMoonInversionAction.Type.INSTANCE);
        registrar.accept("user_animation", () -> UserAnimationAction.Type.INSTANCE);
        registrar.accept("run_command", () -> RunCommandAction.Type.INSTANCE);
        registrar.accept("scoreboard", () -> ScoreboardAction.Type.INSTANCE);
    }

    public static Map<Enum<?>, Map<String, MoveSet<?, ?>>> getMoveSets() {
        return moveSets.entrySet().stream()
                .map(e -> Map.entry(e.getKey(), Collections.unmodifiableMap(e.getValue())))
                .collect(
                        ImmutableMap::<Enum<?>, Map<String, MoveSet<?, ?>>>builder,
                        ImmutableMap.Builder::put,
                        (b1, b2) -> b1.putAll(b2.build()))
                .build();
    }

    /**
     * Called upon datapack (re)load.
     * Loads stand movesets from datapacks.
     * @param preparationBarrier Preparation stuff that must be finished before reading anything
     * @param resourceManager The resource manager used to get data
     * @param preparationsProfiler Profiler for preparations
     * @param reloadProfiler Profiler for reload
     * @param backgroundExecutor Executor for background tasks
     * @param gameExecutor Executor for game tasks
     * @return A completable future that completes when the reload is done
     * @see net.minecraft.server.ReloadableServerResources
     */
    public static CompletableFuture<Void> onReload(PreparableReloadListener.PreparationBarrier preparationBarrier,
                                                   ResourceManager resourceManager, ProfilerFiller preparationsProfiler,
                                                   ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        return CompletableFuture.<Void>supplyAsync(() -> null, backgroundExecutor)
                .thenCompose(preparationBarrier::wait) // Wait for preparations to finish
                .thenAcceptAsync(v -> MoveSet.loadAll(resourceManager, gameExecutor));
    }
}
