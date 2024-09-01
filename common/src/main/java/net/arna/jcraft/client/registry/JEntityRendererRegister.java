package net.arna.jcraft.client.registry;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import net.arna.jcraft.client.renderer.entity.*;
import net.arna.jcraft.client.renderer.entity.projectiles.*;
import net.arna.jcraft.client.renderer.entity.stands.*;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

@Environment(EnvType.CLIENT)
public interface JEntityRendererRegister {
    static void registerEntityRenderers() {
        EntityRendererRegistry.register(JEntityTypeRegistry.STAR_PLATINUM, StarPlatinumRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.SPTW, SPTWRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.KING_CRIMSON, KingCrimsonRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.D4C, D4CRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.CREAM, CreamRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.KILLER_QUEEN, KillerQueenRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.KILLER_QUEEN_BITES_THE_DUST, KQBTDRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.SHEER_HEART_ATTACK, SheerHeartAttackRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.WHITE_SNAKE, WhiteSnakeRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.C_MOON, CMoonRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.MADE_IN_HEAVEN, MadeInHeavenRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.THE_WORLD, TheWorldRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.THE_WORLD_OVER_HEAVEN, TheWorldOverHeavenRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.SILVER_CHARIOT, SilverChariotRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.MAGICIANS_RED, MagiciansRedRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.THE_FOOL, TheFoolRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.GOLD_EXPERIENCE, GoldExperienceRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.GE_TREE, GETreeRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.GE_FROG, GEFrogRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.GE_SNAKE, GESnakeRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.GE_BUTTERFLY, GEButterflyRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.HIEROPHANT_GREEN, HGRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.EMERALD, EmeraldRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.HG_NET, HGNetRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.THE_SUN, SunRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.GER, GERRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.GER_SCORPION, GERScorpionRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.PURPLE_HAZE_DISTORTION, PurpleHazeDistortionRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.PURPLE_HAZE, PurpleHazeRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.HORUS, HorusRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.ICICLE, IcicleRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.LARGE_ICICLE, LargeIcicleRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.ICE_BRANCH, IceBranchRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.CINDERELLA, CinderellaRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.OSIRIS, OsirisRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.ATUM, AtumRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.DIVER_DOWN, DiverDownRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.CHARIOT_REQUIEM, ChariotRequiemRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.DRAGONS_DREAM, DragonsDreamRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.FOO_FIGHTERS, FooFightersRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.GOO_GOO_DOLLS, GooGooDollsRenderer::new);

        EntityRendererRegistry.register(JEntityTypeRegistry.LASER_PROJECTILE, LaserProjectileRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.BLOOD_PROJECTILE, BloodProjectileRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.BLOCK_PROJECTILE, BlockProjectileRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.KNIFE, KnifeRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.ANKH, AnkhRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.BUBBLE, BubbleRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.LIFE_DETECTOR, LifeDetectorRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.RED_BIND, RedBindRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.SAND_TORNADO, SandTornadoRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.WS_ACID_PROJECTILE, WSAcidRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.SUN_BEAM, SunBeamRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.BULLET, BulletRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.RAPIER, RapierRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.METEOR, MeteorRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.PH_CAPSULE, PHCapsuleRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.PURPLE_HAZE_CLOUD, JEntityRendererRegister::createEmpty);

        EntityRendererRegistry.register(JEntityTypeRegistry.PETSHOP, PetshopRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.AYA_TSUJI, AyaTsujiRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.DARBY_OLDER, DarbyOlderRenderer::new);
        EntityRendererRegistry.register(JEntityTypeRegistry.DARBY_YOUNGER, DarbyYoungerRenderer::new);
    }

    static <T extends Entity> EntityRenderer<T> createEmpty(EntityRendererProvider.Context ctx) {
        return new EntityRenderer<>(ctx) {
            @Override
            public ResourceLocation getTextureLocation(T entity) {
                return null;
            }
        };
    }
}
