package com.billy.immortality.blocks;
import java.util.UUID;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.ReadView;
import net.minecraft.nbt.WriteView;
import net.minecraft.util.math.BlockPos;

public final class PocketDoorBlockEntity extends BlockEntity {
    private UUID owner;
    private BlockPos overworldReturn = BlockPos.ORIGIN;

    public PocketDoorBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.POCKET_DOOR, pos, state);
    }
    public UUID getOwner() { return owner; }
    public void setOwner(UUID owner) { this.owner = owner; markDirty(); }
    public BlockPos getOverworldReturn() { return overworldReturn; }
    public void setOverworldReturn(BlockPos pos) { overworldReturn = pos.toImmutable(); markDirty(); }

    @Override
    protected void writeData(WriteView view) {
        super.writeData(view);
        if (owner != null) view.putString("Owner", owner.toString());
        view.putInt("ReturnX", overworldReturn.getX());
        view.putInt("ReturnY", overworldReturn.getY());
        view.putInt("ReturnZ", overworldReturn.getZ());
    }

    @Override
    protected void readData(ReadView view) {
        super.readData(view);
        String ownerId = view.getString("Owner", "");
        try { owner = ownerId.isEmpty() ? null : UUID.fromString(ownerId); }
        catch (IllegalArgumentException ignored) { owner = null; }
        overworldReturn = new BlockPos(view.getInt("ReturnX", 0), view.getInt("ReturnY", 0), view.getInt("ReturnZ", 0));
    }
}
