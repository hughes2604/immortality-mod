package com.billy.immortality;

import com.billy.immortality.blocks.ModBlocks;
import com.billy.immortality.events.DeathEvents;
import com.billy.immortality.events.PlayerEvents;
import com.billy.immortality.items.ModItems;
import com.billy.immortality.mechanics.ImmortalityManager;
import com.billy.immortality.mechanics.ViltrumiteManager;
import com.billy.immortality.network.ImmortalityNetworking;
import net.fabricmc.api.ModInitializer;

public class ImmortalityMod implements ModInitializer {
    public static final String MOD_ID = "immortality";

    @Override
    public void onInitialize() {
        ImmortalityNetworking.register();
        ImmortalityManager.register();
        ModBlocks.register();
        ModItems.register();
        DeathEvents.register();
        PlayerEvents.register();
        ViltrumiteManager.register();
    }
}
