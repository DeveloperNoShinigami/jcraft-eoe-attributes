package net.arna.jcraft.client.registry;

import dev.architectury.registry.client.level.entity.EntityRendererRegistry;
import dev.architectury.registry.registries.RegistrySupplier;
import lombok.NonNull;
import net.arna.jcraft.client.model.entity.BisectModel;
import net.arna.jcraft.client.renderer.entity.npc.AyaTsujiRenderer;
import net.arna.jcraft.client.renderer.entity.npc.DarbyOlderRenderer;
import net.arna.jcraft.client.renderer.entity.npc.DarbyYoungerRenderer;
import net.arna.jcraft.client.renderer.entity.GEButterflyRenderer;
import net.arna.jcraft.client.renderer.entity.GEFrogRenderer;
import net.arna.jcraft.client.renderer.entity.GERScorpionRenderer;
import net.arna.jcraft.client.renderer.entity.GESnakeRenderer;
import net.arna.jcraft.client.renderer.entity.GETreeRenderer;
import net.arna.jcraft.client.renderer.entity.HGNetRenderer;
import net.arna.jcraft.client.renderer.entity.MetallicaForksRenderer;
import net.arna.jcraft.client.renderer.entity.npc.PetshopRenderer;
import net.arna.jcraft.client.renderer.entity.SandTornadoRenderer;
import net.arna.jcraft.client.renderer.entity.SheerHeartAttackRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.AnkhRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.BlockProjectileRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.BloodProjectileRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.BubbleRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.BulletRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.EmeraldRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.GeoProjectileRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.IceBranchRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.IcicleRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.KnifeRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.LargeIcicleRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.LaserProjectileRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.LifeDetectorRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.MeteorRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.PHCapsuleRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.RapierRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.RedBindRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.ScalpelRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.StandArrowRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.SunBeamRenderer;
import net.arna.jcraft.client.renderer.entity.projectiles.WSAcidRenderer;
import net.arna.jcraft.client.renderer.entity.stands.AtumRenderer;
import net.arna.jcraft.client.renderer.entity.stands.CMoonRenderer;
import net.arna.jcraft.client.renderer.entity.stands.ChariotRequiemRenderer;
import net.arna.jcraft.client.renderer.entity.stands.CinderellaRenderer;
import net.arna.jcraft.client.renderer.entity.stands.CreamRenderer;
import net.arna.jcraft.client.renderer.entity.stands.D4CRenderer;
import net.arna.jcraft.client.renderer.entity.stands.DiverDownRenderer;
import net.arna.jcraft.client.renderer.entity.stands.DragonsDreamRenderer;
import net.arna.jcraft.client.renderer.entity.stands.FooFightersRenderer;
import net.arna.jcraft.client.renderer.entity.stands.GERRenderer;
import net.arna.jcraft.client.renderer.entity.stands.GoldExperienceRenderer;
import net.arna.jcraft.client.renderer.entity.stands.GooGooDollsRenderer;
import net.arna.jcraft.client.renderer.entity.stands.HGRenderer;
import net.arna.jcraft.client.renderer.entity.stands.HorusRenderer;
import net.arna.jcraft.client.renderer.entity.stands.KQBTDRenderer;
import net.arna.jcraft.client.renderer.entity.stands.KillerQueenRenderer;
import net.arna.jcraft.client.renderer.entity.stands.KingCrimsonRenderer;
import net.arna.jcraft.client.renderer.entity.stands.MadeInHeavenRenderer;
import net.arna.jcraft.client.renderer.entity.stands.MagiciansRedRenderer;
import net.arna.jcraft.client.renderer.entity.stands.MetallicaRenderer;
import net.arna.jcraft.client.renderer.entity.stands.OsirisRenderer;
import net.arna.jcraft.client.renderer.entity.stands.PurpleHazeDistortionRenderer;
import net.arna.jcraft.client.renderer.entity.stands.PurpleHazeRenderer;
import net.arna.jcraft.client.renderer.entity.stands.SPTWRenderer;
import net.arna.jcraft.client.renderer.entity.stands.ShadowTheWorldRenderer;
import net.arna.jcraft.client.renderer.entity.stands.SilverChariotRenderer;
import net.arna.jcraft.client.renderer.entity.stands.StarPlatinumRenderer;
import net.arna.jcraft.client.renderer.entity.stands.SunRenderer;
import net.arna.jcraft.client.renderer.entity.stands.TheFoolRenderer;
import net.arna.jcraft.client.renderer.entity.stands.TheHandRenderer;
import net.arna.jcraft.client.renderer.entity.stands.TheWorldOverHeavenRenderer;
import net.arna.jcraft.client.renderer.entity.stands.TheWorldRenderer;
import net.arna.jcraft.client.renderer.entity.stands.WhiteSnakeRenderer;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.function.Consumer;

@Environment(EnvType.CLIENT)
public interface JEntityRendererRegister {
    record RendererData <T extends Entity> (RegistrySupplier<? extends EntityType<? extends T>> supplier, EntityRendererProvider<T> provider) {
        public void registerFabric() {
            EntityRendererRegistry.register(supplier, provider);
        }
    }

    RendererData<?>[] entries = {
            new RendererData<>(JEntityTypeRegistry.STAR_PLATINUM, StarPlatinumRenderer::new),
            new RendererData<>(JEntityTypeRegistry.SPTW, SPTWRenderer::new),
            new RendererData<>(JEntityTypeRegistry.KING_CRIMSON, KingCrimsonRenderer::new),

            new RendererData<>(JEntityTypeRegistry.D4C, D4CRenderer::new),

            new RendererData<>(JEntityTypeRegistry.CREAM, CreamRenderer::new),
            new RendererData<>(JEntityTypeRegistry.KILLER_QUEEN, KillerQueenRenderer::new),
            new RendererData<>(JEntityTypeRegistry.KILLER_QUEEN_BITES_THE_DUST, KQBTDRenderer::new),
            new RendererData<>(JEntityTypeRegistry.SHEER_HEART_ATTACK, SheerHeartAttackRenderer::new),

            new RendererData<>(JEntityTypeRegistry.WHITE_SNAKE, WhiteSnakeRenderer::new),
            new RendererData<>(JEntityTypeRegistry.C_MOON, CMoonRenderer::new),
            new RendererData<>(JEntityTypeRegistry.MADE_IN_HEAVEN, MadeInHeavenRenderer::new),

            new RendererData<>(JEntityTypeRegistry.SHADOW_THE_WORLD, ShadowTheWorldRenderer::new),
            new RendererData<>(JEntityTypeRegistry.THE_WORLD, TheWorldRenderer::new),
            new RendererData<>(JEntityTypeRegistry.THE_WORLD_OVER_HEAVEN, TheWorldOverHeavenRenderer::new),

            new RendererData<>(JEntityTypeRegistry.SILVER_CHARIOT, SilverChariotRenderer::new),

            new RendererData<>(JEntityTypeRegistry.MAGICIANS_RED, MagiciansRedRenderer::new),

            new RendererData<>(JEntityTypeRegistry.THE_FOOL, TheFoolRenderer::new),

            new RendererData<>(JEntityTypeRegistry.GOLD_EXPERIENCE, GoldExperienceRenderer::new),
            new RendererData<>(JEntityTypeRegistry.GE_TREE, GETreeRenderer::new),
            new RendererData<>(JEntityTypeRegistry.GE_FROG, GEFrogRenderer::new),
            new RendererData<>(JEntityTypeRegistry.GE_SNAKE, GESnakeRenderer::new),
            new RendererData<>(JEntityTypeRegistry.GE_BUTTERFLY, GEButterflyRenderer::new),

            new RendererData<>(JEntityTypeRegistry.HIEROPHANT_GREEN, HGRenderer::new),
            new RendererData<>(JEntityTypeRegistry.EMERALD, EmeraldRenderer::new),
            new RendererData<>(JEntityTypeRegistry.BISECT, context -> new GeoProjectileRenderer<>(context, new BisectModel())),
            new RendererData<>(JEntityTypeRegistry.HG_NET, HGNetRenderer::new),

            new RendererData<>(JEntityTypeRegistry.THE_SUN, SunRenderer::new),

            new RendererData<>(JEntityTypeRegistry.GER, GERRenderer::new),
            new RendererData<>(JEntityTypeRegistry.GER_SCORPION, GERScorpionRenderer::new),

            new RendererData<>(JEntityTypeRegistry.PURPLE_HAZE_DISTORTION, PurpleHazeDistortionRenderer::new),
            new RendererData<>(JEntityTypeRegistry.PURPLE_HAZE, PurpleHazeRenderer::new),

            new RendererData<>(JEntityTypeRegistry.HORUS, HorusRenderer::new),
            new RendererData<>(JEntityTypeRegistry.ICICLE, IcicleRenderer::new),
            new RendererData<>(JEntityTypeRegistry.LARGE_ICICLE, LargeIcicleRenderer::new),
            new RendererData<>(JEntityTypeRegistry.ICE_BRANCH, IceBranchRenderer::new),

            new RendererData<>(JEntityTypeRegistry.CINDERELLA, CinderellaRenderer::new),
            new RendererData<>(JEntityTypeRegistry.OSIRIS, OsirisRenderer::new),
            new RendererData<>(JEntityTypeRegistry.ATUM, AtumRenderer::new),
            new RendererData<>(JEntityTypeRegistry.DIVER_DOWN, DiverDownRenderer::new),
            new RendererData<>(JEntityTypeRegistry.CHARIOT_REQUIEM, ChariotRequiemRenderer::new),
            new RendererData<>(JEntityTypeRegistry.DRAGONS_DREAM, DragonsDreamRenderer::new),
            new RendererData<>(JEntityTypeRegistry.FOO_FIGHTERS, FooFightersRenderer::new),
            new RendererData<>(JEntityTypeRegistry.GOO_GOO_DOLLS, GooGooDollsRenderer::new),

            new RendererData<>(JEntityTypeRegistry.LASER_PROJECTILE, LaserProjectileRenderer::new),
            new RendererData<>(JEntityTypeRegistry.BLOOD_PROJECTILE, BloodProjectileRenderer::new),
            new RendererData<>(JEntityTypeRegistry.BLOCK_PROJECTILE, BlockProjectileRenderer::new),
            new RendererData<>(JEntityTypeRegistry.KNIFE, KnifeRenderer::new),
            new RendererData<>(JEntityTypeRegistry.SCALPEL, ScalpelRenderer::new),
            new RendererData<>(JEntityTypeRegistry.ANKH, AnkhRenderer::new),
            new RendererData<>(JEntityTypeRegistry.BUBBLE, BubbleRenderer::new),
            new RendererData<>(JEntityTypeRegistry.LIFE_DETECTOR, LifeDetectorRenderer::new),
            new RendererData<>(JEntityTypeRegistry.RED_BIND, RedBindRenderer::new),
            new RendererData<>(JEntityTypeRegistry.SAND_TORNADO, SandTornadoRenderer::new),
            new RendererData<>(JEntityTypeRegistry.WS_ACID_PROJECTILE, WSAcidRenderer::new),
            new RendererData<>(JEntityTypeRegistry.SUN_BEAM, SunBeamRenderer::new),
            new RendererData<>(JEntityTypeRegistry.BULLET, BulletRenderer::new),
            new RendererData<>(JEntityTypeRegistry.RAPIER, RapierRenderer::new),
            new RendererData<>(JEntityTypeRegistry.METEOR, MeteorRenderer::new),
            new RendererData<>(JEntityTypeRegistry.PH_CAPSULE, PHCapsuleRenderer::new),
            new RendererData<>(JEntityTypeRegistry.PURPLE_HAZE_CLOUD, JEntityRendererRegister::createEmpty),
            new RendererData<>(JEntityTypeRegistry.METALLICA_FORKS, MetallicaForksRenderer::new),
            new RendererData<>(JEntityTypeRegistry.STAND_ARROW_PROJECTILE, StandArrowRenderer::new),

            new RendererData<>(JEntityTypeRegistry.PETSHOP, PetshopRenderer::new),
            new RendererData<>(JEntityTypeRegistry.AYA_TSUJI, AyaTsujiRenderer::new),
            new RendererData<>(JEntityTypeRegistry.DARBY_OLDER, DarbyOlderRenderer::new),
            new RendererData<>(JEntityTypeRegistry.DARBY_YOUNGER, DarbyYoungerRenderer::new),

            new RendererData<>(JEntityTypeRegistry.METALLICA, MetallicaRenderer::new),
            new RendererData<>(JEntityTypeRegistry.THE_HAND, TheHandRenderer::new),
    };

    static void registerEntityRenderers(Consumer<RendererData<?>> consumer) {
        for (RendererData<?> entry : entries) consumer.accept(entry);
    }

    static <T extends Entity> EntityRenderer<T> createEmpty(EntityRendererProvider.Context ctx) {
        return new EntityRenderer<>(ctx) {
            @Override
            public ResourceLocation getTextureLocation(@NonNull T entity) {
                return null;
            }
        };
    }
}
