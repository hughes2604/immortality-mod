package com.billy.immortality.blocks;

import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;

public final class PocketDoorBlockEntity extends BlockEntity {
    private UUID owner;
    private BlockPos overworldReturn = BlockPos.ORIGIN;

    public PocketDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POCKET_DOOR, pos, state);
    }

    public UUID getOwner() {
        return owner;
    }

    public void setOwner(UUID owner) {
        this.owner = owner;
        markDirty();
    }

    public BlockPos getOverworldReturn() {
        return overworldReturn;
    }

    public void setOverworldReturn(BlockPos overworldReturn) {
        this.overworldReturn = overworldReturn.toImmutable();
        markDirty();
    }

    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.writeNbt(nbt, registries);
        if (owner != null) {
            nbt.putUuid("Owner", owner);
        }
        nbt.putInt("ReturnX", overworldReturn.getX());
        nbt.putInt("ReturnY", overworldReturn.getY());
        nbt.putInt("ReturnZ", overworldReturn.getZ());
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registries) {
        super.readNbt(nbt, registries);
        owner = nbt.containsUuid("Owner") ? nbt.getUuid("Owner") : null;
        overworldReturn = new BlockPos(nbt.getInt("ReturnX"), nbt.getInt("ReturnY"), nbt.getInt("ReturnZ"));
    }
}
