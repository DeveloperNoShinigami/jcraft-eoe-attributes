package net.arna.jcraft.common.entity;

import net.arna.jcraft.common.component.living.CommonStandComponent;
import net.arna.jcraft.common.entity.stand.StandType;
import net.arna.jcraft.common.tickable.JEnemies;
import net.arna.jcraft.platform.JComponentPlatformUtils;
import net.arna.jcraft.registry.JEntityTypeRegistry;
import net.arna.jcraft.registry.JItemRegistry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import mod.azure.azurelib.animatable.GeoEntity;
import mod.azure.azurelib.core.animatable.instance.AnimatableInstanceCache;
import mod.azure.azurelib.core.animation.AnimatableManager;
import mod.azure.azurelib.util.AzureLibUtil;
import org.jetbrains.annotations.Nullable;

public class AyaTsujiEntity extends PathfinderMob implements GeoEntity, Merchant {

    private Player tradingPlayer;
    private MerchantOffers merchantOffers = new MerchantOffers();;

    private final AnimatableInstanceCache geoCache = AzureLibUtil.createInstanceCache(this);

    public AyaTsujiEntity(Level world) {
        super(JEntityTypeRegistry.AYA_TSUJI.get(), world);
        final CommonStandComponent standData = JComponentPlatformUtils.getStandData(this);
        standData.setType(StandType.CINDERELLA);
        standData.setSkin(0);

        if (world.isClientSide()) return;
        JEnemies.add(this);

        final ItemStack[] masks = new ItemStack[4];
        for (int i = 1; i <= 4; i++) {
            masks[i-1] = new ItemStack(JItemRegistry.CINDERELLA_MASK.get());
            final CompoundTag nbt = masks[i-1].getOrCreateTag();
            final ListTag enchantments = new ListTag();
            final CompoundTag enchantment = new CompoundTag();
            enchantment.putString("id", "jcraft:cinderellas_kiss");
            enchantment.putShort("lvl", (short) i);
            enchantments.add(enchantment);
            nbt.put("Enchantments", enchantments);
        }
        merchantOffers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 30), masks[0], 4, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 45), masks[1], 3, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(Items.EMERALD, 60), masks[2], 2, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(Items.ENDER_PEARL, 4), masks[0], 4, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(Items.ENDER_PEARL, 6), masks[1], 3, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(Items.ENDER_PEARL, 8), masks[2], 2, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(Items.ENDER_PEARL, 10), masks[3], 1, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(JItemRegistry.STAND_ARROW.get()), masks[1], 3, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(JItemRegistry.STAND_ARROW.get(), 2), masks[2], 2, 0, 1f));
        merchantOffers.add(new MerchantOffer(new ItemStack(JItemRegistry.STAND_ARROW.get(), 3), masks[3], 1, 0, 1f));
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        // TODO Arna
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return geoCache;
    }

    public static AttributeSupplier.Builder createAyaTsujiAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.5);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (this.getOffers().isEmpty()) {
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        } else {
            if (!this.level().isClientSide) {
                this.setTradingPlayer(player);
                this.openTradingScreen(player, this.getDisplayName(), 1);
            }

            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
    }

    @Override
    public void setTradingPlayer(@Nullable Player tradingPlayer) {
        this.tradingPlayer = tradingPlayer;
    }

    @Nullable
    @Override
    public Player getTradingPlayer() {
        return tradingPlayer;
    }

    @Override
    public MerchantOffers getOffers() {
        return merchantOffers;
    }

    @Override
    public void overrideOffers(MerchantOffers offers) {
        // intentionally left empty
    }

    @Override
    public void notifyTrade(MerchantOffer offer) {
        // intentionally left empty
    }

    @Override
    public void notifyTradeUpdated(ItemStack stack) {
        // intentionally left empty
    }

    @Override
    public int getVillagerXp() {
        return 0;
    }

    @Override
    public void overrideXp(int xp) {
        // intentionally left empty
    }

    @Override
    public boolean showProgressBar() {
        return false;
    }

    @Override
    public SoundEvent getNotifyTradeSound() {
        return null;
    }

    @Override
    public boolean isClientSide() {
        return level().isClientSide();
    }
}
