package net.arna.jcraft.api.pose;

import com.mojang.serialization.Codec;
import net.arna.jcraft.common.util.JCodecUtils;
import net.arna.jcraft.common.util.JUtils;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.LivingEntity;

import java.util.List;
import java.util.function.BiPredicate;

public enum ModifierCondition {
    LEFT_ARM_EMPTY((model, user) -> model instanceof HumanoidModel<?> hModel &&
            hModel.leftArmPose == HumanoidModel.ArmPose.EMPTY),
    RIGHT_ARM_EMPTY((model, user) -> model instanceof HumanoidModel<?> hModel &&
            hModel.rightArmPose == HumanoidModel.ArmPose.EMPTY),
    LEFT_ARM_EMPTY_OR_ITEM((model, user) -> model instanceof HumanoidModel<?> hModel &&
            (hModel.leftArmPose == HumanoidModel.ArmPose.EMPTY || hModel.leftArmPose == HumanoidModel.ArmPose.ITEM)),
    RIGHT_ARM_EMPTY_OR_ITEM((model, user) -> model instanceof HumanoidModel<?> hModel &&
            (hModel.rightArmPose == HumanoidModel.ArmPose.EMPTY || hModel.rightArmPose == HumanoidModel.ArmPose.ITEM)),
    USER_NOT_MOVING((model, user) -> JUtils.deltaPos(user).horizontalDistanceSqr() <= 0),
    USER_NOT_SPRINTING((model, user) -> !user.isSprinting());

    public static final Codec<ModifierCondition> CODEC = JCodecUtils.createEnumCodec(ModifierCondition.class);

    private final BiPredicate<Model, LivingEntity> condition;

    ModifierCondition(final BiPredicate<Model, LivingEntity> condition) {
        this.condition = condition;
    }

    public static boolean anyFails(final List<ModifierCondition> conditions, final Model model, final LivingEntity user) {
        return conditions.stream().anyMatch(condition -> !condition.test(model, user));
    }

    public boolean test(final Model model, final LivingEntity user) {
        return condition.test(model, user);
    }

}
