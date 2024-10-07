package net.arna.jcraft.common.attack.core;

import lombok.Getter;
import net.arna.jcraft.common.util.CooldownType;
import net.minecraft.network.chat.Component;

import java.util.Locale;
import java.util.Random;

@Getter
public enum MoveType {
    LIGHT(CooldownType.STAND_LIGHT, "key.attack"),
    HEAVY(CooldownType.STAND_HEAVY),
    BARRAGE(CooldownType.STAND_BARRAGE),
    SPECIAL1(CooldownType.STAND_SP1),
    SPECIAL2(CooldownType.STAND_SP2),
    SPECIAL3(CooldownType.STAND_SP3),
    ULTIMATE(CooldownType.STAND_ULTIMATE),
    UTILITY(CooldownType.UTILITY);

    private final Component friendlyName;
    private final Component key;
    private final CooldownType defaultCooldownType;

    MoveType(final CooldownType defaultCooldownType) {
        this(defaultCooldownType, null);
    }

    MoveType(final CooldownType defaultCooldownType, final String key) {
        friendlyName = Component.translatable("jcraft.movetype." + name().toLowerCase(Locale.ROOT));
        this.key = Component.keybind(key == null ? "key.jcraft." + name().toLowerCase(Locale.ROOT) : key);
        this.defaultCooldownType = defaultCooldownType;
    }

    private static final Random random = new Random();
    public static MoveType randomMoveType() {
        return MoveType.values()[random.nextInt(MoveType.values().length)];
    }
}
