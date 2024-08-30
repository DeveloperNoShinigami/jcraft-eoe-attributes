package net.arna.jcraft.forge.events;


import net.arna.jcraft.client.gui.hud.JCraftHudOverlay;
import net.arna.jcraft.client.registry.JEntityRendererRegister;
import net.arna.jcraft.client.renderer.entity.*;
import net.arna.jcraft.client.renderer.entity.projectiles.*;
import net.arna.jcraft.client.renderer.entity.stands.*;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetupEvents {

    @SubscribeEvent
    public static void onInitializeClient(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(JEntityTypeRegistry.STAR_PLATINUM.get(), StarPlatinumRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.SPTW.get(), SPTWRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.KING_CRIMSON.get(), KingCrimsonRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.D4C.get(), D4CRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.CREAM.get(), CreamRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.KILLER_QUEEN.get(), KillerQueenRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.KILLER_QUEEN_BITES_THE_DUST.get(), KQBTDRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.SHEER_HEART_ATTACK.get(), SheerHeartAttackRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.WHITE_SNAKE.get(), WhiteSnakeRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.C_MOON.get(), CMoonRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.MADE_IN_HEAVEN.get(), MadeInHeavenRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.THE_WORLD.get(), TheWorldRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.THE_WORLD_OVER_HEAVEN.get(), TheWorldOverHeavenRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.SILVER_CHARIOT.get(), SilverChariotRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.MAGICIANS_RED.get(), MagiciansRedRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.THE_FOOL.get(), TheFoolRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.GOLD_EXPERIENCE.get(), GoldExperienceRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.GE_TREE.get(), GETreeRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.GE_FROG.get(), GEFrogRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.GE_SNAKE.get(), GESnakeRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.GE_BUTTERFLY.get(), GEButterflyRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.HIEROPHANT_GREEN.get(), HGRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.EMERALD.get(), EmeraldRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.HG_NET.get(), HGNetRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.THE_SUN.get(), SunRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.GER.get(), GERRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.GER_SCORPION.get(), GERScorpionRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.PURPLE_HAZE_DISTORTION.get(), PurpleHazeDistortionRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.PURPLE_HAZE.get(), PurpleHazeRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.HORUS.get(), HorusRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.ICICLE.get(), IcicleRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.LARGE_ICICLE.get(), LargeIcicleRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.CHARIOT_REQUIEM.get(), ChariotRequiemRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.CINDERELLA.get(), CinderellaRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.OSIRIS.get(), OsirisRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.ATUM.get(), AtumRenderer::new);

        event.registerEntityRenderer(JEntityTypeRegistry.LASER_PROJECTILE.get(), LaserProjectileRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.BLOOD_PROJECTILE.get(), BloodProjectileRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.BLOCK_PROJECTILE.get(), BlockProjectileRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.KNIFE.get(), KnifeRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.ANKH.get(), AnkhRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.BUBBLE.get(), BubbleRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.LIFE_DETECTOR.get(), LifeDetectorRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.RED_BIND.get(), RedBindRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.SAND_TORNADO.get(), SandTornadoRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.WS_ACID_PROJECTILE.get(), WSAcidRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.SUN_BEAM.get(), SunBeamRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.BULLET.get(), BulletRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.RAPIER.get(), RapierRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.METEOR.get(), MeteorRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.PH_CAPSULE.get(), PHCapsuleRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.PURPLE_HAZE_CLOUD.get(), JEntityRendererRegister::createEmpty);

        event.registerEntityRenderer(JEntityTypeRegistry.PETSHOP.get(), PetshopRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.AYA_TSUJI.get(), AyaTsujiRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.DARBY_OLDER.get(), DarbyOlderRenderer::new);
        event.registerEntityRenderer(JEntityTypeRegistry.DARBY_YOUNGER.get(), DarbyYoungerRenderer::new);
    }

    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerBelow(VanillaGuiOverlay.CHAT_PANEL.id(), "hud", (gui, guiGraphics, partialTick, screenWidth, screenHeight) ->
                JCraftHudOverlay.render(guiGraphics));
    }
}
