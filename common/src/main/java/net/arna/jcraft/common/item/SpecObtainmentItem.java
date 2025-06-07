package net.arna.jcraft.common.item;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.api.spec.SpecType2;
import net.arna.jcraft.common.spec.JSpec;
import net.arna.jcraft.common.util.JUtils;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JStatRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;

public abstract class SpecObtainmentItem extends Item {
    protected boolean warned = false;
    protected final RegistrySupplier<SpecType2> switchTo;

    public SpecObtainmentItem(Properties settings, RegistrySupplier<SpecType2> switchTo) {
        super(settings);
        this.switchTo = switchTo;
    }

    private boolean setSpec(Player player) {
        if (player == null) {
            return false;
        }

        JComponentPlatformUtils.getSpecData(player).setType(switchTo.get());
        if (!player.level().isClientSide()) {
            player.awardStat(JStatRegistry.SPECS_CHANGED.get());
        }
        warned = false;
        return true;
    }

    protected boolean tryGetSpec(Player player) {
        JSpec<?, ?> spec = JUtils.getSpec(player);
        if (spec != null) { // If the player already has a spec
            if (spec.getType() != switchTo.get()) { // And it isn't the one that will be switched to
                if (!warned) {
                    player.sendSystemMessage(Component.translatable("warning.jcraft.spec.change"));
                    warned = true;
                    return false;
                }
                return setSpec(player);
            }
            else {
                return false;
            }
        }
        return setSpec(player);
    }
}
