package net.arna.jcraft.client.gui;

import dev.architectury.networking.NetworkManager;
import io.netty.buffer.Unpooled;
import me.shedaniel.clothconfig2.api.AbstractConfigListEntry;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.impl.builders.*;
import net.arna.jcraft.common.config.*;
import net.arna.jcraft.common.network.c2s.ConfigUpdatePacket;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import java.util.HashSet;
import java.util.Set;

public class ServerConfigUI {
    public static void show(final boolean editable) {
        final ConfigBuilder builder = ConfigBuilder.create();
        final Set<ConfigOption> changedOptions = new HashSet<>();

        for (final ConfigOption option : ConfigOption.getImmutableOptions().values()) {
            final ConfigCategory category = builder.getOrCreateCategory(Component.translatable("jcraft.serverconfig.category." + option.getCategory()));
            final MutableComponent optionText = Component.translatable("jcraft.serverconfig.option." + option.getKey());

            final AbstractConfigListEntry<?> entry = switch (option.getType()) {
                case INTEGER -> {
                    final IntOption intOption = (IntOption) option;
                    if (intOption.getMin() != null && intOption.getMax() != null) {
                        final IntSliderBuilder sliderBuilder = builder.entryBuilder().startIntSlider(optionText, intOption.getValue(),
                                        intOption.getMin(), intOption.getMax())
                                .setDefaultValue(intOption.getDefaultValue())
                                .setSaveConsumer(value -> {
                                    intOption.setValue(value);
                                    changedOptions.add(intOption);
                                });

                        yield sliderBuilder.build();
                    } else {
                        final IntFieldBuilder fieldBuilder = builder.entryBuilder().startIntField(optionText, intOption.getValue())
                                .setDefaultValue(intOption.getDefaultValue())
                                .setSaveConsumer(value -> {
                                    intOption.setValue(value);
                                    changedOptions.add(intOption);
                                });

                        if (intOption.getMin() != null) {
                            fieldBuilder.setMin(intOption.getMin());
                        } else {
                            fieldBuilder.removeMin();
                        }

                        if (intOption.getMax() != null) {
                            fieldBuilder.setMax(intOption.getMax());
                        } else {
                            fieldBuilder.removeMax();
                        }

                        yield fieldBuilder.build();
                    }
                }
                case FLOAT -> {
                    final FloatOption floatOption = (FloatOption) option;
                    final FloatFieldBuilder fieldBuilder = builder.entryBuilder().startFloatField(optionText, floatOption.getValue())
                            .setDefaultValue(floatOption.getDefaultValue())
                            .setSaveConsumer(value -> {
                                floatOption.setValue(value);
                                changedOptions.add(floatOption);
                            });

                    if (floatOption.getMin() != null) {
                        fieldBuilder.setMin(floatOption.getMin());
                    } else {
                        fieldBuilder.removeMin();
                    }

                    if (floatOption.getMax() != null) {
                        fieldBuilder.setMax(floatOption.getMax());
                    } else {
                        fieldBuilder.removeMax();
                    }

                    yield fieldBuilder.build();
                }
                case BOOLEAN -> {
                    final BooleanOption booleanOption = (BooleanOption) option;
                    final BooleanToggleBuilder toggleBuilder = builder.entryBuilder().startBooleanToggle(optionText, booleanOption.getValue())
                            .setDefaultValue(booleanOption.getDefaultValue())
                            .setSaveConsumer(value -> {
                                booleanOption.setValue(value);
                                changedOptions.add(booleanOption);
                            });
                    yield toggleBuilder.build();
                }
                case ENUM -> {
                    final EnumOption<?> enumOption = (EnumOption<?>) option;
                    //noinspection unchecked // this is fine
                    final EnumSelectorBuilder<Enum<?>> selectorBuilder = builder.entryBuilder().startEnumSelector(optionText,
                                    (Class<Enum<?>>) enumOption.getClazz(), enumOption.getValue())
                            .setDefaultValue(enumOption.getDefaultValue())
                            .setSaveConsumer(e -> {
                                enumOption.setValue(e.ordinal());
                                changedOptions.add(enumOption);
                            });

                    yield selectorBuilder.build();
                }
            };

            category.addEntry(entry);
        }

        builder.setEditable(editable);
        builder.setSavingRunnable(() -> NetworkManager.sendToServer(ConfigUpdatePacket.ID, ConfigOption.writeOptions(
                new FriendlyByteBuf(Unpooled.buffer()), changedOptions)));
        Minecraft.getInstance().setScreen(builder.build());
    }
}
