package net.arna.jcraft.common.component.world;

import lombok.NonNull;
import net.arna.jcraft.common.minigame.card.texasholdem.TexasHoldEm;

import java.util.Collection;

public interface CommonTexasHoldEmComponent {

    Collection<TexasHoldEm> getGames();

    boolean addGame(final @NonNull TexasHoldEm game);

}
