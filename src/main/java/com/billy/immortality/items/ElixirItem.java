package com.billy.immortality.items;

import java.util.function.Consumer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

/** A drinkable item that runs an action on the player when finished. */
public class ElixirItem extends Item {

    private final Consumer<ServerPlayerEntity> onDrink;

    public ElixirItem(Settings settings, Consumer<ServerPlayerEntity> onDrink) {
        super(settings);
        this.onDrink = onDrink;
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (user instanceof ServerPlayerEntity player) {
            onDrink.accept(player);
        }
        return super.finishUsing(stack, world, user); // consumes, leaves glass bottle
    }
}
