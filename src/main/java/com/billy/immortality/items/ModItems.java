package com.billy.immortality.items;

import com.billy.immortality.ImmortalityMod;
import com.billy.immortality.blocks.ModBlocks;
import com.billy.immortality.mechanics.ImmortalityManager;
import java.util.function.Consumer;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ConsumableComponents;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ToolMaterial;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

public final class ModItems {

    public static final Item ELIXIR_OF_IMMORTALITY = elixir(
            "elixir_of_immortality", ImmortalityManager::makeImmortal, true);

    public static final Item ELIXIR_OF_MORTALITY = elixir(
            "elixir_of_mortality", ImmortalityManager::makeMortal, false);

    public static final Item ARROW_OF_MORTALITY = registerArrow("arrow_of_mortality");

    public static final Item IMMORTALS_BANE = registerImmortalsBane();

    private ModItems() {
    }

    private static Item elixir(String name, Consumer<ServerPlayerEntity> action, boolean glint) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ImmortalityMod.MOD_ID, name));
        Item.Settings settings = new Item.Settings()
                .registryKey(key)
                .maxCount(1)
                .rarity(Rarity.EPIC)
                .component(DataComponentTypes.CONSUMABLE, ConsumableComponents.drink().build())
                .useRemainder(Items.GLASS_BOTTLE);
        if (glint) {
            settings.component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        }
        return Registry.register(Registries.ITEM, key, new ElixirItem(settings, action));
    }

    private static Item registerArrow(String name) {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ImmortalityMod.MOD_ID, name));
        return Registry.register(Registries.ITEM, key, new ArrowItem(new Item.Settings().registryKey(key)));
    }

    private static Item registerImmortalsBane() {
        RegistryKey<Item> key = RegistryKey.of(RegistryKeys.ITEM,
                Identifier.of(ImmortalityMod.MOD_ID, "immortals_bane"));
        Item.Settings settings = new Item.Settings()
                .registryKey(key)
                .rarity(Rarity.EPIC)
                .sword(ToolMaterial.DIAMOND, 3.0F, -2.4F)
                .component(DataComponentTypes.ENCHANTMENT_GLINT_OVERRIDE, true);
        return Registry.register(Registries.ITEM, key, new Item(settings));
    }

    public static void register() {
        RegistryKey<ItemGroup> groupKey = RegistryKey.of(
                RegistryKeys.ITEM_GROUP, Identifier.of(ImmortalityMod.MOD_ID, "main"));

        Registry.register(Registries.ITEM_GROUP, groupKey, FabricItemGroup.builder()
                .icon(() -> new ItemStack(ELIXIR_OF_IMMORTALITY))
                .displayName(Text.translatable("itemGroup.immortality.main"))
                .entries((context, entries) -> {
                    entries.add(ELIXIR_OF_IMMORTALITY);
                    entries.add(ELIXIR_OF_MORTALITY);
                    entries.add(ARROW_OF_MORTALITY);
                    entries.add(IMMORTALS_BANE);
                    entries.add(ModBlocks.IMMORTALITY_NULLIFIER_ITEM);
                })
                .build());
    }
}
