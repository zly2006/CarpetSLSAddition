package com.github.zly2006.carpetslsaddition.mixin.world;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayDeque;

@Mixin(ChainRestrictedNeighborUpdater.class)
public class MixinChainRestrictedNeighborUpdater {

    @Shadow
    @Final
    private ArrayDeque<?> queue;

    @Inject(method = "runQueuedUpdates", at = @At(value = "INVOKE", target = "Ljava/util/List;clear()V", ordinal = 0, shift = At.Shift.BEFORE), cancellable = true)
    public void injectRunQueuedUpdates(CallbackInfo ci) {
        int maxSize = SLSCarpetSettings.maxUpdateQueueSize;
        if (maxSize > 0) {
            if (this.queue.size() > maxSize) {
                ci.cancel();
                // throw new OutOfMemoryError("The update queue exceeded the maximum allowed allocation of heap space set by SLS Addition！");
            }
        }
    }
}
