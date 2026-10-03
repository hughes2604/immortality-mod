package com.billy.immortality.items;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public final class LiveByTheSwordLoot {
    private static final Identifier ABANDONED_MINESHAFT = Identifier.of("minecraft", "chests/abandoned_mineshaft");
    private static final RegistryKey<Enchantment> LIVE_BY_THE_SWORD =
            RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("immortality", "live_by_the_sword"));
    private LiveByTheSwordLoot() {}
    public static void register() {
        LootTableEvents.MODIFY.register((key, table, source, registries) -> {
            if (!key.getValue().equals(ABANDONED_MINESHAFT)) return;
            RegistryEntry<Enchantment> enchantment = registries.getOrThrow(RegistryKeys.ENCHANTMENT)
                    .getOptional(LIVE_BY_THE_SWORD).orElse(null);
            if (enchantment == null) return;
            table.pool(LootPool.builder().rolls(ConstantLootNumberProvider.create(1.0F))
                    .with(ItemEntry.builder(Items.ENCHANTED_BOOK).weight(1)
                            .apply(EnchantRandomlyLootFunction.builder(registries).option(enchantment)))
                    .with(EmptyEntry.builder().weight(19)));
        });
    }
}
