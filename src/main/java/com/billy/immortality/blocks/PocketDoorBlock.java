package com.billy.immortality.blocks;

import com.billy.immortality.ImmortalityMod;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

public final class PocketDoorBlock extends BlockWithEntity {
    public static final RegistryKey<World> POCKET_DIMENSION = RegistryKey.of(
            RegistryKeys.WORLD, Identifier.of(ImmortalityMod.MOD_ID, "pocket"));
    public PocketDoorBlock(Settings settings) { super(settings); }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) { }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new PocketDoorBlockEntity(pos, state);
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        super.onPlaced(world, pos, state, placer, stack);
        if (world.getBlockEntity(pos) instanceof PocketDoorBlockEntity door && placer instanceof PlayerEntity player) {
            door.setOwner(player.getUuid());
            door.setOverworldReturn(pos);
        }
    }

    @Override
    protected ActionResult onUse(BlockState state, World world, BlockPos pos,
                                 PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        if (!(player instanceof ServerPlayerEntity serverPlayer)
                || !(world.getBlockEntity(pos) instanceof PocketDoorBlockEntity door)) return ActionResult.PASS;
        UUID owner = door.getOwner();
        if (owner == null || !owner.equals(player.getUuid())) {
            player.sendMessage(Text.translatable("message.immortality.door_owner"), false);
            return ActionResult.FAIL;
        }
        if (world.getServer() == null) return ActionResult.FAIL;

        if (world.getRegistryKey().equals(POCKET_DIMENSION)) {
            teleport(serverPlayer, world.getServer().getOverworld(), door.getOverworldReturn());
        } else {
            if (!world.getRegistryKey().equals(World.OVERWORLD)) {
                player.sendMessage(Text.literal("Pocket Doors must be placed in the Overworld."), false);
                return ActionResult.FAIL;
            }
            ServerWorld pocket = world.getServer().getWorld(POCKET_DIMENSION);
            if (pocket == null) {
                player.sendMessage(Text.translatable("message.immortality.dimension_unavailable"), false);
                return ActionResult.FAIL;
            }
            int region = Math.floorMod(owner.hashCode(), 10000);
            int x = region * 96 + 8;
            int z = 8;
            int y = pocket.getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, x, z);
            BlockPos exit = new BlockPos(x, y, z);
            if (!pocket.getBlockState(exit).isOf(this)) {
                pocket.setBlockState(exit, getDefaultState(), Block.NOTIFY_ALL);
            }
            if (pocket.getBlockEntity(exit) instanceof PocketDoorBlockEntity exitDoor) {
                exitDoor.setOwner(owner);
                exitDoor.setOverworldReturn(pos);
            }
            teleport(serverPlayer, pocket, exit);
        }
        return ActionResult.SUCCESS;
    }

    private static void teleport(ServerPlayerEntity player, ServerWorld destination, BlockPos doorPos) {
        Vec3d targetPos = new Vec3d(doorPos.getX() + 1.5, doorPos.getY(), doorPos.getZ() + 0.5);
        player.teleportTo(new TeleportTarget(destination, targetPos, Vec3d.ZERO,
                player.getYaw(), player.getPitch(), TeleportTarget.NO_OP));
    }
}
