package com.bluelotuscoding;

import com.bluelotuscoding.api.registry.JAttributeRegistry;

public class JCraftAttributes {
    public static final String MOD_ID = "jcraft_attributes";

    public static void init() {
        JAttributeRegistry.init();

        dev.architectury.event.events.common.CommandRegistrationEvent.EVENT.register((dispatcher, registrySelection, selection) ->
            com.bluelotuscoding.api.command.JAttributeCommands.register(dispatcher));
    }
}
