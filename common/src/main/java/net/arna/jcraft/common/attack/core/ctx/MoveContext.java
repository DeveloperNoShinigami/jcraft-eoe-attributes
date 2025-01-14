package net.arna.jcraft.common.attack.core.ctx;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

public class MoveContext {
    private final Map<MoveVariable<?>, Entry<?>> entries = new HashMap<>();

    @SuppressWarnings("unchecked")
    public <T> T get(final MoveVariable<T> variable) {
        final Entry<?> entry = getEntry(variable);
        return (T) entry.getValue();
    }

    public int getInt(final IntMoveVariable variable) {
        final IntMoveVariable.IntEntry entry = (IntMoveVariable.IntEntry) getEntry(variable);
        return entry.getIntValue();
    }

    public float getFloat(final FloatMoveVariable variable) {
        final FloatMoveVariable.FloatEntry entry = (FloatMoveVariable.FloatEntry) getEntry(variable);
        return entry.getFloatValue();
    }

    public boolean getBoolean(final BooleanMoveVariable variable) {
        final BooleanMoveVariable.BooleanEntry entry = (BooleanMoveVariable.BooleanEntry) getEntry(variable);
        return entry.getBooleanValue();
    }

    public <T> void set(final MoveVariable<T> variable, final T value) {
        getEntry(variable).setValue(value);
    }

    public void setInt(final IntMoveVariable variable, final int value) {
        final IntMoveVariable.IntEntry entry = (IntMoveVariable.IntEntry) getEntry(variable);
        entry.setValue(value);
    }

    public void setFloat(final FloatMoveVariable variable, final float value) {
        final FloatMoveVariable.FloatEntry entry = (FloatMoveVariable.FloatEntry) getEntry(variable);
        entry.setValue(value);
    }

    public void setBoolean(final BooleanMoveVariable variable, final boolean value) {
        final BooleanMoveVariable.BooleanEntry entry = (BooleanMoveVariable.BooleanEntry) getEntry(variable);
        entry.setValue(value);
    }

    public void incrementInt(final IntMoveVariable variable) {
        incrementInt(variable, 1);
    }

    public void incrementInt(final IntMoveVariable variable, final int increment) {
        final IntMoveVariable.IntEntry entry = (IntMoveVariable.IntEntry) getEntry(variable);
        entry.setValue(entry.getIntValue() + increment);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    private <T> Entry<T> getEntry(final MoveVariable<T> variable) {
        final Entry<?> entry = entries.get(variable);
        if (entry == null) {
            throw new IllegalArgumentException("No entry for the given variable could be found. " +
                    "Has it been registered?");
        }

        return (Entry<T>) entry;
    }

    public void clear() {
        entries.clear();
    }

    public <T> boolean register(final @NonNull MoveVariable<T> variable) {
        if (entries.containsKey(variable)) {
            return false;
        }

        entries.put(variable, variable.createEntry());
        return true;
    }

    public <T> void register(final @NonNull MoveVariable<T> variable, final T initialValue) {
        if (register(variable)) {
            set(variable, initialValue);
        }
    }

    public void register(@NonNull IntMoveVariable variable, int initialValue) {
        if (register(variable)) {
            setInt(variable, initialValue);
        }
    }

    public void register(@NonNull FloatMoveVariable variable, float initialValue) {
        if (register(variable)) {
            setFloat(variable, initialValue);
        }
    }

    public void register(@NonNull BooleanMoveVariable variable, boolean initialValue) {
        if (register(variable)) {
            setBoolean(variable, initialValue);
        }
    }

    @Getter
    @RequiredArgsConstructor
    static class Entry<T> {
        private final Class<T> type;
        @Setter
        private T value;
    }
}
