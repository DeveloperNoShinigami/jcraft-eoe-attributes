package net.arna.jcraft.client.util;

import java.util.HashMap;

public abstract class Easing {
    public static final HashMap<String, Easing> EASINGS = new HashMap<>();
    public final String name;

    public Easing(final String name) {
        this.name = name;
        EASINGS.put(name, this);
    }

    public static Easing valueOf(String name) {
        return EASINGS.get(name);
    }

    /**
     * The basic function for easing.
     *
     * @param t the time (either frames or in seconds/milliseconds)
     * @param b the beginning value
     * @param c the value changed
     * @param d the duration time
     * @return the eased value
     */
    public abstract float ease(final float t, final float b, final float c, final float d);

    public static final Easing CIRC_OUT = new Easing("circOut") {
        public float ease(float t, float b, float c, float d) {
            return c * (float) Math.sqrt(1 - (t = t / d - 1) * t) + b;
        }
    };
}
