package com.billy.immortality.blocks;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

public final class PocketDoorEvents {
    private PocketDoorEvents() {
    }

    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(player instanceof ServerPlayerEntity breaker)
                    || !(blockEntity instanceof PocketDoorBlockEntity door)
                    || door.getOwner() == null) {
                return;
            }
            ServerPlayerEntity owner = breaker.getServer().getPlayerManager().getPlayer(door.getOwner());
            if (owner != null) {
                ItemStack recoveredDoor = new ItemStack(ModBlocks.POCKET_DOOR_ITEM);
                if (!owner.getInventory().insertStack(recoveredDoor)) {
                    owner.dropItem(recoveredDoor, false);
                }
            }
        });
    }
}
