package net.arna;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class JCraft {
    // Unchanging mod values
    public static final String MOD_ID = "jcraft";
    public static final int STAND_COUNT = 11;
    public static final int EVOLUTION_COUNT = 5;
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);


    public static final int SPEC_QUEUE_MOVESTUN_LIMIT = 11; // exclusive, 10 -> 0.5s window for queueing moves
    public static final int QUEUE_MOVESTUN_LIMIT = 7; // exclusive, 6 -> 0.3s window for queueing moves

    public static void init() {

    }
}
