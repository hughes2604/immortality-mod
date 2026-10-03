package com.billy.immortality.events;

import com.billy.immortality.blocks.ImmortalityNullifierBlock;
import com.billy.immortality.items.ModItems;
import com.billy.immortality.mechanics.ImmortalityManager;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.server.network.ServerPlayerEntity;

public final class DeathEvents {
    private DeathEvents() {}

    public static void register() {
        ServerLivingEntityEvents.ALLOW_DEATH.register((entity, source, amount) -> {
            if (entity instanceof ServerPlayerEntity player && ImmortalityManager.isImmortal(player)) {
                if (ImmortalityManager.isInNullifierField(player, ImmortalityNullifierBlock.RADIUS)
                        || isMortalityArrow(source)) {
                    ImmortalityManager.clearPenaltyForRealDeath(player);
                    return true;
                }
                if (isImmortalsBane(source)) {
                    ImmortalityManager.becomeMortalFromBane(player);
                    return true;
                }
                ImmortalityManager.handleLethalHit(player, source);
                return false;
            }
            return true;
        });
    }

    private static boolean isMortalityArrow(DamageSource source) {
        if (source.getSource() instanceof PersistentProjectileEntity projectile
                && projectile.getItemStack().isOf(ModItems.ARROW_OF_MORTALITY)) {
            return true;
        }
        return isPlayerHolding(source, ModItems.ARROW_OF_MORTALITY);
    }

    private static boolean isImmortalsBane(DamageSource source) {
        return isPlayerHolding(source, ModItems.IMMORTALS_BANE);
    }

    private static boolean isPlayerHolding(DamageSource source, net.minecraft.item.Item item) {
        if (!source.isOf(DamageTypes.PLAYER_ATTACK)
                || !(source.getAttacker() instanceof ServerPlayerEntity attacker)) {
            return false;
        }
        return attacker.getMainHandStack().isOf(item) || attacker.getOffHandStack().isOf(item);
    }
}
