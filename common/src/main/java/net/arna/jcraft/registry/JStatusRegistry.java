package net.arna.jcraft.registry;

import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.effects.*;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

import static net.arna.jcraft.JCraft.EFFECTS;

public interface JStatusRegistry {

    static void init(){

    }

    RegistrySupplier<StatusEffect> DAZED = EFFECTS.register("dazed_effect", DazedStatusEffect::new);
    RegistrySupplier<StatusEffect> KNOCKDOWN = EFFECTS.register("knockdown", KnockdownStatusEffect::new);

    RegistrySupplier<StatusEffect> WSPOISON = EFFECTS.register("ws_poison", WSPoisonEffect::new);
    RegistrySupplier<StatusEffect> STANDLESS = EFFECTS.register("standless", StandlessEffect::new);
    RegistrySupplier<StatusEffect> OUTOFBODY = EFFECTS.register("outofbody", OutOfBodyEffect::new);
    RegistrySupplier<StatusEffect> WEIGHTLESS = EFFECTS.register("weightless", WeightlessStatusEffect::new);
    RegistrySupplier<StatusEffect> BLEEDING = EFFECTS.register("bleeding", BleedingEffect::new);
    RegistrySupplier<StatusEffect> PHPOISON = EFFECTS.register("phpoison", PurpleInfectionEffect::new);

}
