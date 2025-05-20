package net.arna.jcraft.common.util;

import net.minecraft.commands.arguments.selector.EntitySelectorParser;
import net.minecraft.commands.arguments.selector.options.EntitySelectorOptions;
import net.minecraft.network.chat.Component;

import java.util.function.Predicate;

public interface EntitySelectorOptionsRegistrar {
    void register(String id, EntitySelectorOptions.Modifier modifier, Predicate<EntitySelectorParser> predicate, Component tooltip);
}
