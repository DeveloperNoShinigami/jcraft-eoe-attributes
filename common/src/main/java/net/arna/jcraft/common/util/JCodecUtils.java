package net.arna.jcraft.common.util;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonParseException;
import com.mojang.datafixers.Products;
import com.mojang.datafixers.kinds.K1;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class JCodecUtils {
    public static final Codec<MobEffectInstance> MOB_EFFECT_INSTANCE_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            BuiltInRegistries.MOB_EFFECT.byNameCodec().fieldOf("effect").forGetter(MobEffectInstance::getEffect),
            Codec.INT.fieldOf("duration").forGetter(MobEffectInstance::getDuration),
            Codec.INT.fieldOf("amplifier").forGetter(MobEffectInstance::getAmplifier),
            Codec.BOOL.optionalFieldOf("ambient", false).forGetter(MobEffectInstance::isAmbient),
            Codec.BOOL.optionalFieldOf("visible", true).forGetter(MobEffectInstance::isVisible),
            Codec.BOOL.optionalFieldOf("show_icon", true).forGetter(MobEffectInstance::showIcon)
    ).apply(instance, MobEffectInstance::new));
    public static final Codec<Supplier<SoundEvent>> SOUND_EVENT_SUPPLIER_CODEC = ResourceLocation.CODEC.xmap(
            loc -> () -> BuiltInRegistries.SOUND_EVENT.get(loc),
            s -> s instanceof RegistrySupplier<SoundEvent> rs ? rs.getId() :
            BuiltInRegistries.SOUND_EVENT.getKey(s.get()));
    public static final Codec<ItemPredicate> ITEM_PREDICATE_CODEC = ExtraCodecs.JSON.comapFlatMap(
            input -> {
                try {
                    return DataResult.success(ItemPredicate.fromJson(input));
                } catch (JsonParseException e) {
                    return DataResult.error(() -> "Failed to parse item predicate: " + e.getMessage());
                }
            }, ItemPredicate::serializeToJson);

    public static <E extends Enum<?>> Codec<E> createEnumCodec(Class<E> enumClass) {
        Map<String, E> constants = Stream.of(enumClass.getEnumConstants())
                .collect(ImmutableMap.toImmutableMap(Enum::name, e -> e));
        return Codec.STRING.comapFlatMap(DataResult.partialGet(constants::get, () -> "Unknown enum constant: "), E::name);
    }

    public static <O, T> Function<O, Optional<T>> optional(Function<O, T> f, T defaultValue) {
        return o -> {
            T result = f.apply(o);
            return Objects.equals(result, defaultValue) ? Optional.empty() : Optional.ofNullable(result);
        };
    }

    // and-functions generated with the following Python script:
    /*
    out = ""
    for i in range(1, 9):
        for j in range(max(9 - i, 1), min(17 - i, 9)):
            out += "public static <F extends K1, "
            types = ", ".join([f"T{x}" for x in range(1, i + j + 1)])
            out += types
            out += f"> Products.P{i + j}<F, "
            out += types
            out += ">\n"
            i_types = ", ".join([f"T{x}" for x in range(1, i + 1)])
            j_types = ", ".join([f"T{x}" for x in range(i + 1, i + j + 1)])
            out += f"and(Products.P{i}<F, {i_types}> p1, Products.P{j}<F, {j_types}> p2) {{\n"
            ts = ", ".join([f"p1.t{x}()" for x in range(1, i + 1)]) + ", " + ", ".join([f"p2.t{x}()" for x in range(1, j + 1)])
            out += f"    return new Products.P{i + j}<>({ts});\n"
            out += "}\n\n"
     */

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P1<F, T1> p1, Products.P8<F, T2, T3, T4, T5, T6, T7, T8, T9> p2) {
        return new Products.P9<>(p1.t1(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P2<F, T1, T2> p1, Products.P7<F, T3, T4, T5, T6, T7, T8, T9> p2) {
        return new Products.P9<>(p1.t1(), p1.t2(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
    and(Products.P2<F, T1, T2> p1, Products.P8<F, T3, T4, T5, T6, T7, T8, T9, T10> p2) {
        return new Products.P10<>(p1.t1(), p1.t2(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P3<F, T1, T2, T3> p1, Products.P6<F, T4, T5, T6, T7, T8, T9> p2) {
        return new Products.P9<>(p1.t1(), p1.t2(), p1.t3(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
    and(Products.P3<F, T1, T2, T3> p1, Products.P7<F, T4, T5, T6, T7, T8, T9, T10> p2) {
        return new Products.P10<>(p1.t1(), p1.t2(), p1.t3(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
    and(Products.P3<F, T1, T2, T3> p1, Products.P8<F, T4, T5, T6, T7, T8, T9, T10, T11> p2) {
        return new Products.P11<>(p1.t1(), p1.t2(), p1.t3(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P4<F, T1, T2, T3, T4> p1, Products.P5<F, T5, T6, T7, T8, T9> p2) {
        return new Products.P9<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
    and(Products.P4<F, T1, T2, T3, T4> p1, Products.P6<F, T5, T6, T7, T8, T9, T10> p2) {
        return new Products.P10<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
    and(Products.P4<F, T1, T2, T3, T4> p1, Products.P7<F, T5, T6, T7, T8, T9, T10, T11> p2) {
        return new Products.P11<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Products.P12<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>
    and(Products.P4<F, T1, T2, T3, T4> p1, Products.P8<F, T5, T6, T7, T8, T9, T10, T11, T12> p2) {
        return new Products.P12<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P5<F, T1, T2, T3, T4, T5> p1, Products.P4<F, T6, T7, T8, T9> p2) {
        return new Products.P9<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p2.t1(), p2.t2(), p2.t3(), p2.t4());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
    and(Products.P5<F, T1, T2, T3, T4, T5> p1, Products.P5<F, T6, T7, T8, T9, T10> p2) {
        return new Products.P10<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
    and(Products.P5<F, T1, T2, T3, T4, T5> p1, Products.P6<F, T6, T7, T8, T9, T10, T11> p2) {
        return new Products.P11<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Products.P12<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>
    and(Products.P5<F, T1, T2, T3, T4, T5> p1, Products.P7<F, T6, T7, T8, T9, T10, T11, T12> p2) {
        return new Products.P12<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Products.P13<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>
    and(Products.P5<F, T1, T2, T3, T4, T5> p1, Products.P8<F, T6, T7, T8, T9, T10, T11, T12, T13> p2) {
        return new Products.P13<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P6<F, T1, T2, T3, T4, T5, T6> p1, Products.P3<F, T7, T8, T9> p2) {
        return new Products.P9<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p2.t1(), p2.t2(), p2.t3());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
    and(Products.P6<F, T1, T2, T3, T4, T5, T6> p1, Products.P4<F, T7, T8, T9, T10> p2) {
        return new Products.P10<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p2.t1(), p2.t2(), p2.t3(), p2.t4());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
    and(Products.P6<F, T1, T2, T3, T4, T5, T6> p1, Products.P5<F, T7, T8, T9, T10, T11> p2) {
        return new Products.P11<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Products.P12<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>
    and(Products.P6<F, T1, T2, T3, T4, T5, T6> p1, Products.P6<F, T7, T8, T9, T10, T11, T12> p2) {
        return new Products.P12<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Products.P13<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>
    and(Products.P6<F, T1, T2, T3, T4, T5, T6> p1, Products.P7<F, T7, T8, T9, T10, T11, T12, T13> p2) {
        return new Products.P13<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Products.P14<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>
    and(Products.P6<F, T1, T2, T3, T4, T5, T6> p1, Products.P8<F, T7, T8, T9, T10, T11, T12, T13, T14> p2) {
        return new Products.P14<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p1, Products.P2<F, T8, T9> p2) {
        return new Products.P9<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p2.t1(), p2.t2());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
    and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p1, Products.P3<F, T8, T9, T10> p2) {
        return new Products.P10<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p2.t1(), p2.t2(), p2.t3());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
    and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p1, Products.P4<F, T8, T9, T10, T11> p2) {
        return new Products.P11<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p2.t1(), p2.t2(), p2.t3(), p2.t4());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Products.P12<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>
    and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p1, Products.P5<F, T8, T9, T10, T11, T12> p2) {
        return new Products.P12<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Products.P13<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>
    and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p1, Products.P6<F, T8, T9, T10, T11, T12, T13> p2) {
        return new Products.P13<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Products.P14<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>
    and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p1, Products.P7<F, T8, T9, T10, T11, T12, T13, T14> p2) {
        return new Products.P14<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Products.P15<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>
    and(Products.P7<F, T1, T2, T3, T4, T5, T6, T7> p1, Products.P8<F, T8, T9, T10, T11, T12, T13, T14, T15> p2) {
        return new Products.P15<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9> Products.P9<F, T1, T2, T3, T4, T5, T6, T7, T8, T9>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P1<F, T9> p2) {
        return new Products.P9<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10> Products.P10<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P2<F, T9, T10> p2) {
        return new Products.P10<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1(), p2.t2());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11> Products.P11<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P3<F, T9, T10, T11> p2) {
        return new Products.P11<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1(), p2.t2(), p2.t3());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12> Products.P12<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P4<F, T9, T10, T11, T12> p2) {
        return new Products.P12<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1(), p2.t2(), p2.t3(), p2.t4());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13> Products.P13<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P5<F, T9, T10, T11, T12, T13> p2) {
        return new Products.P13<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14> Products.P14<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P6<F, T9, T10, T11, T12, T13, T14> p2) {
        return new Products.P14<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15> Products.P15<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P7<F, T9, T10, T11, T12, T13, T14, T15> p2) {
        return new Products.P15<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7());
    }

    public static <F extends K1, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16> Products.P16<F, T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16>
    and(Products.P8<F, T1, T2, T3, T4, T5, T6, T7, T8> p1, Products.P8<F, T9, T10, T11, T12, T13, T14, T15, T16> p2) {
        return new Products.P16<>(p1.t1(), p1.t2(), p1.t3(), p1.t4(), p1.t5(), p1.t6(), p1.t7(), p1.t8(), p2.t1(), p2.t2(), p2.t3(), p2.t4(), p2.t5(), p2.t6(), p2.t7(), p2.t8());
    }

    public static <A> Codec<A> recursive(final String name, final Function<Codec<A>, Codec<A>> function) {
        return new RecursiveCodec<>(name, function);
    }

    // From newer Minecraft version
    /**
     * Allows a codec to have fields of the same type as the codec itself.
     * @param <T> The type of the codec
     */
    private static class RecursiveCodec<T> implements Codec<T> {
        private final String name;
        private final Supplier<Codec<T>> wrapped;

        private RecursiveCodec(final String name, final Function<Codec<T>, Codec<T>> wrapped) {
            this.name = name;
            this.wrapped = Suppliers.memoize(() -> wrapped.apply(this));
        }

        @Override
        public <S> DataResult<Pair<T, S>> decode(final DynamicOps<S> ops, final S input) {
            return wrapped.get().decode(ops, input);
        }

        @Override
        public <S> DataResult<S> encode(final T input, final DynamicOps<S> ops, final S prefix) {
            return wrapped.get().encode(input, ops, prefix);
        }

        @Override
        public String toString() {
            return "RecursiveCodec[" + name + ']';
        }
    }
}
