package com.billy.immortality.blocks;

import com.billy.immortality.ImmortalityMod;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public final class ModBlockEntities {
    public static final BlockEntityType<PocketDoorBlockEntity> POCKET_DOOR =
            Registry.register(Registries.BLOCK_ENTITY_TYPE,
                    Identifier.of(ImmortalityMod.MOD_ID, "pocket_door"),
                    FabricBlockEntityTypeBuilder.create(PocketDoorBlockEntity::new, ModBlocks.POCKET_DOOR).build());

    private ModBlockEntities() {
    }

    public static void register() {
        // Loads and registers the block entity type.
    }
}
