package com.billy.immortality.blocks;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public final class PocketDoorEvents {
    private PocketDoorEvents() {}
    public static void register() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (!(world instanceof ServerWorld serverWorld)
                    || !(blockEntity instanceof PocketDoorBlockEntity door) || door.getOwner() == null) return;
            ServerPlayerEntity owner = serverWorld.getServer().getPlayerManager().getPlayer(door.getOwner());
            if (owner != null) {
                ItemStack recoveredDoor = new ItemStack(ModBlocks.POCKET_DOOR_ITEM);
                if (!owner.getInventory().insertStack(recoveredDoor)) owner.dropItem(recoveredDoor, false);
            }
        });
    }
}
