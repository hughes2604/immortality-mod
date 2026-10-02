package com.billy.immortality.blocks;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public final class ImmortalityNullifierBlock extends Block {
    public static final int RADIUS = 10;
    private static final int PARTICLE_INTERVAL = 10;
    private static final DustParticleEffect RED_PARTICLE = new DustParticleEffect(0xE61414, 0.8F);

    public ImmortalityNullifierBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(Properties.POWERED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.POWERED);
    }

    @Override
    protected void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (!world.isClient) {
            updatePowerAndSchedule(world, pos);
        }
    }

    @Override
    protected void neighborUpdate(BlockState state, World world, BlockPos pos, Block sourceBlock,
                                  BlockPos sourcePos, boolean notify) {
        if (!world.isClient) {
            updatePowerAndSchedule(world, pos);
        }
    }

    private void updatePowerAndSchedule(World world, BlockPos pos) {
        boolean powered = world.isReceivingRedstonePower(pos);
        BlockState current = world.getBlockState(pos);
        if (current.isOf(this) && current.get(Properties.POWERED) != powered) {
            world.setBlockState(pos, current.with(Properties.POWERED, powered), Block.NOTIFY_ALL);
        }
        if (powered) {
            world.scheduleBlockTick(pos, this, PARTICLE_INTERVAL);
        }
    }

    @Override
    protected void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!state.get(Properties.POWERED) || !world.isReceivingRedstonePower(pos)) {
            if (state.get(Properties.POWERED)) {
                world.setBlockState(pos, state.with(Properties.POWERED, false), Block.NOTIFY_ALL);
            }
            return;
        }

        // One compact ring of dust per tick; no entities, block entities, or per-block particle ticking.
        double centerX = pos.getX() + 0.5;
        double centerY = pos.getY() + 1.0;
        double centerZ = pos.getZ() + 0.5;
        int points = 64;
        for (int i = 0; i < points; i++) {
            double angle = Math.PI * 2.0 * i / points;
            world.spawnParticles(RED_PARTICLE,
                    centerX + Math.cos(angle) * RADIUS, centerY,
                    centerZ + Math.sin(angle) * RADIUS,
                    1, 0.0, 0.0, 0.0, 0.0);
        }
        world.scheduleBlockTick(pos, this, PARTICLE_INTERVAL);
    }
}
