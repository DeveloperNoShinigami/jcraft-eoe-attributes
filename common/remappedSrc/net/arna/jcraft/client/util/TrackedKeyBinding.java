package net.arna.jcraft.client.util;

import com.mojang.blaze3d.platform.InputConstants;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.registry.client.keymappings.KeyMappingRegistry;
import lombok.Getter;
import net.minecraft.client.KeyMapping;
import java.util.HashMap;
import java.util.Map;

/**
 * A KeyBinding that can tell you whether it was pressed this tick.
 */
@Getter
public class TrackedKeyBinding {
    private static final Map<KeyMapping, TrackedKeyBinding> bindings = new HashMap<>();
    private static boolean resetForScreen = false;
    private final KeyMapping parent;
    private boolean changedThisTick, pressedThisTick, releasedThisTick;

    static {
        // Reset values
        ClientTickEvent.CLIENT_POST.register(client -> {
            bindings.values().forEach(TrackedKeyBinding::reset);
            if (client.screen != null) {
                bindings.values().stream()
                        .filter(TrackedKeyBinding::isDown)
                        .forEach(TrackedKeyBinding::markReleased);
                resetForScreen = true;
            } else {
                resetForScreen = false;
            }
        });
    }

    private TrackedKeyBinding(KeyMapping parent) {
        this.parent = parent;
    }

    public static TrackedKeyBinding createAndRegister(String translationKey, InputConstants.Type type, int code, String category) {
        KeyMapping keyBinding = new KeyMapping(translationKey, type, code, category);
        KeyMappingRegistry.register(keyBinding);

        return wrap(keyBinding);
    }

    public static TrackedKeyBinding wrap(KeyMapping binding) {
        TrackedKeyBinding trackingBinding = new TrackedKeyBinding(binding);
        bindings.put(binding, trackingBinding);
        return trackingBinding;
    }

    public static void onKeyPressSet(KeyMapping binding, boolean pressed) {
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
        return parent.isDown();
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
