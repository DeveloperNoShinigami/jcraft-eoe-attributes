package net.arna.jcraft.api.registry;

import net.arna.jcraft.JCraft;
import net.minecraft.resources.ResourceLocation;

public interface JPacketRegistry {

    // Shortened strings for the sake of saving bandwidth.
    // Further optimization would probably just be an int to char conversion + a static registration method.
    ResourceLocation
      S2C_SERVER_CHANNEL_FEEDBACK = JCraft.id("sfc")
    , S2C_PLAYER_ANIMATION = JCraft.id("anim")
    , S2C_SHADER_ACTIVATION = JCraft.id("s_act")
    , S2C_SHADER_DEACTIVATION = JCraft.id("s_dct")
    , S2C_TIME_ACCELERATION_STATE = JCraft.id("t_acl")
    , S2C_EPITAPH_STATE = JCraft.id("epth")
    , S2C_TIME_ERASE_PREDICTION_STATE = JCraft.id("te_prdct")
    , S2C_SERVER_CONFIG = JCraft.id("s_config")
    , S2C_J_EXPLOSION = JCraft.id("expl")
    , S2C_COMBO_COUNTER = JCraft.id("combo")
    , S2C_TIME_STOP = JCraft.id("ts")
    , S2C_SPLATTER = JCraft.id("splt")
    , S2C_STAND_HURT = JCraft.id("stnd_hurt")
    , S2C_PREDICTION_UPDATE = JCraft.id("prdct")
    , S2C_MAGNETIC_FIELD_PARTICLE = JCraft.id("mfp")
    , S2C_ATTACKER_DATA = JCraft.id("atk_data")
    , S2C_MANDOM_DATA = JCraft.id("mndm_data")
    , S2C_STONE_MASK_CLENCH = JCraft.id("msk_clch")
    , S2C_IPS_TRIGGERED = JCraft.id("ips")

    , C2S_STAND_BLOCK = JCraft.id("stnd_blk")
    , C2S_COOLDOWN_CANCEL = JCraft.id("cdc")
    , C2S_PLAYER_INPUT = JCraft.id("plr_input")
    , C2S_PLAYER_INPUT_HOLD = JCraft.id("plr_input_h")
    , C2S_REMOTE_STAND_INTERACT = JCraft.id("rmt_stnd_act")
    , C2S_PREDICTION_TRIGGER = JCraft.id("prdct_trig")
    , C2S_MENU_CALL = JCraft.id("menu")

    , S2C_SYNC = JCraft.id("s2c_sync")
    , C2S_SYNC = JCraft.id("c2s_sync");
}
