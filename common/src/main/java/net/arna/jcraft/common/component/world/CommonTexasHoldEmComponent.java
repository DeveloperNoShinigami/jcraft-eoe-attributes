package net.arna.jcraft.common.component.world;

import net.arna.jcraft.common.minigame.card.texasholdem.TexasHoldEm;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public interface CommonTexasHoldEmComponent {

    Collection<TexasHoldEm> getGames();

    boolean addGame(final @NotNull TexasHoldEm game);

}
