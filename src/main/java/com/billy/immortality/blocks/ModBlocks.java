package com.billy.immortality.blocks;

import com.billy.immortality.ImmortalityMod;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;

public final class ModBlocks {
    public static final Block IMMORTALITY_NULLIFIER = registerBlock();

    public static final Item IMMORTALITY_NULLIFIER_ITEM =
            Registries.ITEM.get(Identifier.of(ImmortalityMod.MOD_ID, "immortality_nullifier"));

    private ModBlocks() {
    }

    private static Block registerBlock() {
        Identifier id = Identifier.of(ImmortalityMod.MOD_ID, "immortality_nullifier");
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        Block block = Registry.register(Registries.BLOCK, blockKey,
                new ImmortalityNullifierBlock(Block.Settings.create()
                        .registryKey(blockKey)
                        .strength(3.5F)
                        .sounds(BlockSoundGroup.AMETHYST_BLOCK)
                        .requiresTool()));
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, itemKey, new BlockItem(block, new Item.Settings().registryKey(itemKey)));
        return block;
    }

    public static void register() {
        // Referencing the registered fields initializes this class.
    }
}
