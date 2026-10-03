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
import net.minecraft.entity.effect.StatusEffects;

public final class ModBlocks {
    public static final Block IMMORTALITY_NULLIFIER = registerNullifier();
    public static final Item IMMORTALITY_NULLIFIER_ITEM =
            Registries.ITEM.get(Identifier.of(ImmortalityMod.MOD_ID, "immortality_nullifier"));

    public static final Block WHITE_ROSE = registerFlower("white_rose", StatusEffects.REGENERATION, 0);
    public static final Item WHITE_ROSE_ITEM =
            Registries.ITEM.get(Identifier.of(ImmortalityMod.MOD_ID, "white_rose"));

    public static final Block RED_DEVIL = registerFlower("red_devil", StatusEffects.NAUSEA, 0);
    public static final Item RED_DEVIL_ITEM =
            Registries.ITEM.get(Identifier.of(ImmortalityMod.MOD_ID, "red_devil"));

    public static final Block POCKET_DOOR = registerPocketDoor();
    public static final Item POCKET_DOOR_ITEM =
            Registries.ITEM.get(Identifier.of(ImmortalityMod.MOD_ID, "pocket_door"));

    private ModBlocks() {
    }

    private static Block registerNullifier() {
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

    private static Block registerFlower(String name,
            net.minecraft.registry.entry.RegistryEntry<net.minecraft.entity.effect.StatusEffect> effect,
            int amplifier) {
        Identifier id = Identifier.of(ImmortalityMod.MOD_ID, name);
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        Block flower = Registry.register(Registries.BLOCK, blockKey,
                new MeadowFlowerBlock(effect, 0.2F, effect, amplifier,
                        Block.Settings.create().registryKey(blockKey).noCollision()
                                .breakInstantly().sounds(BlockSoundGroup.GRASS)));
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, itemKey, new BlockItem(flower, new Item.Settings().registryKey(itemKey)));
        return flower;
    }

    private static Block registerPocketDoor() {
        Identifier id = Identifier.of(ImmortalityMod.MOD_ID, "pocket_door");
        RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
        Block door = Registry.register(Registries.BLOCK, blockKey,
                new PocketDoorBlock(Block.Settings.create().registryKey(blockKey)
                        .strength(3.0F).sounds(BlockSoundGroup.AMETHYST_BLOCK).dropsNothing()));
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Registry.register(Registries.ITEM, itemKey, new BlockItem(door, new Item.Settings().registryKey(itemKey)));
        return door;
    }

    public static void register() {
        // Referencing the registered fields initializes this class.
    }
}
