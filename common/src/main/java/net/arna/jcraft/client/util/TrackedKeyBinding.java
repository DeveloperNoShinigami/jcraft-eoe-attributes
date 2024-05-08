package net.arna.jcraft.client.util;

import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import lombok.Getter;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * A KeyBinding that can tell you whether it was pressed this tick.
 */
@Getter
public class TrackedKeyBinding {
    private static final Map<KeyBinding, TrackedKeyBinding> bindings = new HashMap<>();
    private static boolean resetForScreen = false;
    private final KeyBinding parent;
    private boolean changedThisTick, pressedThisTick, releasedThisTick;

    static {
        // Reset values
        ClientTickEvent.CLIENT_POST.register(client -> {
            bindings.values().forEach(TrackedKeyBinding::reset);
            if (client.currentScreen != null) {
                bindings.values().stream()
                        .filter(TrackedKeyBinding::isDown)
                        .forEach(TrackedKeyBinding::markReleased);
                resetForScreen = true;
            } else {
                resetForScreen = false;
            }
        });
    }

    private TrackedKeyBinding(KeyBinding parent) {
        this.parent = parent;
    }

    public static TrackedKeyBinding createAndRegister(String translationKey, InputUtil.Type type, int code, String category) {
        KeyBinding keyBinding = new KeyBinding(translationKey, type, code, category);
        KeyMappingRegistry.register(keyBinding);

        return wrap(keyBinding);
    }

    public static TrackedKeyBinding wrap(KeyBinding binding) {
        TrackedKeyBinding trackingBinding = new TrackedKeyBinding(binding);
        bindings.put(binding, trackingBinding);
        return trackingBinding;
    }

    public static void onKeyPressSet(KeyBinding binding, boolean pressed) {
        TrackedKeyBinding trackedBinding = bindings.get(binding);
        if (trackedBinding == null) {
            return;
        }

        if (pressed) {
            trackedBinding.markPressed();
        } else {
            trackedBinding.markReleased();
        }
    }

    public boolean isDown() {
        return parent.isPressed();
    }

    private void markPressed() {
        changedThisTick = true;
        if (!releasedThisTick) {
            pressedThisTick = true;
        }
    }

    private void markReleased() {
        // Released takes precedence.
        changedThisTick = true;
        pressedThisTick = false;
        releasedThisTick = true;
    }

    private void reset() {
        changedThisTick = pressedThisTick = releasedThisTick = false;
    }
}
