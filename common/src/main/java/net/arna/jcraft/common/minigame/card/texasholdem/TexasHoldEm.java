package net.arna.jcraft.common.minigame.card.texasholdem;

import lombok.NonNull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityLookup;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class TexasHoldEm {

    private final static EntityLookup<LivingEntity> ENTITY_LOOKUP = new EntityLookup<>();

    private final Set<LivingEntity> players;
    private final Set<String> playersUuid = new HashSet<>();
    private Engine engine;

    /**
     * Constructor with no players and no engine for deserialization.
     */
    public TexasHoldEm() {
        this.players = new HashSet<>();
    }

    /**
     * @throws IllegalArgumentException If there are less than 2 or more than 22 players.
     */
    public TexasHoldEm(@NonNull final Set<LivingEntity> players) {
        this.players = new HashSet<>(players);
        this.engine = new Engine(players.size()); // throws IAE
        // sanity check
        final Level world = this.players.iterator().next().level();
        for (final LivingEntity entity : this.players) {
            if (!entity.level().equals(world)) {
                throw new IllegalStateException(String.format("All players must belong to the same world, but %s doesn't!", entity));
            }
        }
        // save UUIDs for comparison with other games
        for (final LivingEntity player : players) {
            playersUuid.add(player.getStringUUID());
        }
    }

    public Set<String> getPlayersUuid() {
        return playersUuid;
    }

    public boolean reloadPlayers(@NonNull final Level world) {
        if (!players.isEmpty()) {
            return false; // means they are already loaded in
        }
        for (final String playerUuid : playersUuid) {
            final LivingEntity player = ENTITY_LOOKUP.getEntity(UUID.fromString(playerUuid));
            if (player == null) {
                players.clear();
                return false;
            }
            players.add(player);
        }
        return true;
    }

    public void readFromNbt(CompoundTag tag) {
        players.clear();
        playersUuid.clear();
        for (final Tag playerTag : tag.getList("players", Tag.TAG_STRING)) {
            playersUuid.add(((StringTag)playerTag).getAsString());
        }
        final CompoundTag engineTag = tag.getCompound("game");
        engine = new Engine(playersUuid.size());
        engine.readFromNbt(engineTag);
    }

    public void writeToNbt(CompoundTag tag) {
        final ListTag playersTag = new ListTag();
        for (final LivingEntity player : players) {
            playersTag.add(StringTag.valueOf(player.getStringUUID()));
        }
        tag.put("players", playersTag);
        final CompoundTag engineTag = new CompoundTag();
        engine.writeToNbt(engineTag);
        tag.put("game", engineTag);
    }

}
