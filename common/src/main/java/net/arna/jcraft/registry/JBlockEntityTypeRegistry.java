package net.arna.jcraft.registry;

import dev.architectury.registry.registries.Registrar;
import dev.architectury.registry.registries.RegistrySupplier;
import net.arna.jcraft.JCraft;
import net.arna.jcraft.common.block.tile.CoffinTileEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;

import static net.arna.jcraft.JCraft.MANAGER;

public interface JBlockEntityTypeRegistry {

    Registrar<BlockEntityType<?>> REGISTRY = MANAGER.get().get(Registries.BLOCK_ENTITY_TYPE);

    RegistrySupplier<BlockEntityType<CoffinTileEntity>> COFFIN_TILE = REGISTRY.register(JCraft.id("coffin_block_entity"),
            () -> BlockEntityType.Builder.create(CoffinTileEntity::new, JBlockRegistry.COFFIN_BLOCK.get()).build(null));


    static void init() {

    }
}
