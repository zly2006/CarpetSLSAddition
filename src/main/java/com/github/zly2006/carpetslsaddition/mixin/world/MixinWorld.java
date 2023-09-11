package com.github.zly2006.carpetslsaddition.mixin.world;

import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.block.RewriteChainRestrictedNeighborUpdater;
import com.github.zly2006.carpetslsaddition.util.NeighborUpdaterChanger;
import com.github.zly2006.carpetslsaddition.util.access.ServerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import org.spongepowered.asm.mixin.*;

@Mixin(World.class)
public abstract class MixinWorld implements NeighborUpdaterChanger {
    @Mutable
    @Shadow
    @Final
    protected NeighborUpdater neighborUpdater;

    @Unique
    private NeighborUpdater previousUpdater = null;

    @Override
    public void useRewriteChainNeighborUpdater(boolean use) {
        if (use) {
            this.previousUpdater = this.neighborUpdater;
            int maxDepth;

            if (FabricLoader.getInstance().getEnvironmentType() == EnvType.SERVER) {
                maxDepth = ((ServerAccess) ServerMain.server).getPropertiesLoader().getPropertiesHandler().maxChainedNeighborUpdates;
            }
            else {
                maxDepth = 1000000;
            }

            this.neighborUpdater = new RewriteChainRestrictedNeighborUpdater((World)(Object)this, maxDepth);

        }
        else {
            this.neighborUpdater = previousUpdater;
        }
    }
}
