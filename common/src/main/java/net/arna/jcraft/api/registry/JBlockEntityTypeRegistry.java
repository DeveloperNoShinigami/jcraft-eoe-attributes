package net.arna.jcraft.api.registry;

import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.block.tile.CoffinTileEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;


public interface JBlockEntityTypeRegistry {

    DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPE_REGISTRY = DeferredRegister.create(JCraft.MOD_ID, Registries.BLOCK_ENTITY_TYPE);

    RegistrySupplier<BlockEntityType<CoffinTileEntity>> COFFIN_TILE = BLOCK_ENTITY_TYPE_REGISTRY.register(JCraft.id("coffin_block_entity"),
            () -> BlockEntityType.Builder.of(CoffinTileEntity::new, JBlockRegistry.COFFIN_BLOCK.get()).build(null));


    static void init() {
        // intentionally left empty
    }
}
