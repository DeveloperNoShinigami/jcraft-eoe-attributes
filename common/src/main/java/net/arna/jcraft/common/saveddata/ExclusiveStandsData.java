package net.arna.jcraft.common.saveddata;

import net.arna.jcraft.common.config.JServerConfig;
import net.arna.jcraft.common.entity.stand.StandType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;
import java.util.TreeSet;

public class ExclusiveStandsData extends SavedData {

    public static final Path DEFAULT_FILE_LOCATION = new File("data", "jcraft_usedStands.dat").toPath();

    private static final CompoundTag NO_STANDS = new CompoundTag();

    private static final String KEYWORD = "used";

    private final Set<Integer> usedStands = new TreeSet<>();

    public ExclusiveStandsData(final @NotNull CompoundTag compoundTag) {
        final int[] usedStands = compoundTag.getIntArray(KEYWORD);
        for (final Integer usedStand : usedStands) {
            this.usedStands.add(usedStand);
        }
    }

    public boolean isStandUsed(final StandType standType) {
        return JServerConfig.EXCLUSIVE_STANDS.getValue() && usedStands.contains(standType.ordinal());
    }

    public boolean switchStand(final @Nullable StandType prev, final @Nullable StandType curr) {
        if (!JServerConfig.EXCLUSIVE_STANDS.getValue()) {
            return true;
        }

        // If the current stand is already used, return false.
        if (isStandUsed(curr)) {
            return false;
        }

        // If the previous stand is not null, remove it from the used stands.
        if (prev != null) {
            usedStands.remove(prev.ordinal());
        }
        // If the current stand is not null, add it to the used stands.
        if (curr != null) {
            usedStands.add(curr.ordinal());
        }
        setDirty();

        return true;
    }

    public static File getDefaultFileLocation(final MinecraftServer server) {
        return server.storageSource.levelDirectory.path().resolve(ExclusiveStandsData.DEFAULT_FILE_LOCATION).toFile();
    }

    @NotNull
    @Override
    public CompoundTag save(final CompoundTag compoundTag) {
        compoundTag.putIntArray(KEYWORD, usedStands.stream().toList());
        return compoundTag;
    }

    public void saveToDefaultFile(final MinecraftServer server) {
        save(getDefaultFileLocation(server));
    }

    public static ExclusiveStandsData fromFile(final File file) {
        final CompoundTag compoundTag;
        try {
            compoundTag = (CompoundTag)NbtIo.read(file).get("data");
        }
        catch (final NullPointerException | ClassCastException | IOException ex) {
            return new ExclusiveStandsData(NO_STANDS);
        }
        if (compoundTag == null) {
            return new ExclusiveStandsData(NO_STANDS);
        }
        return new ExclusiveStandsData(compoundTag);
    }

    public static ExclusiveStandsData fromDefaultFile(final MinecraftServer server) {
        return fromFile(getDefaultFileLocation(server));
    }
}
