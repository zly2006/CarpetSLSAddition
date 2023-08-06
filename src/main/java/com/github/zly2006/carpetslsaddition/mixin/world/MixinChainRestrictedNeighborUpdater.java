package com.github.zly2006.carpetslsaddition.mixin.world;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.block.ChainRestrictedNeighborUpdater;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayDeque;
import java.util.List;

@Mixin(ChainRestrictedNeighborUpdater.class)
public class MixinChainRestrictedNeighborUpdater {

    @Shadow
    @Final
    private ArrayDeque<?> queue;

    @Shadow
    @Final
    private List<?> pending;

    @Shadow
    private int depth;

    @Inject(method = "enqueue", at = @At(value = "INVOKE", target = "Ljava/util/ArrayDeque;push(Ljava/lang/Object;)V"), cancellable = true, locals = LocalCapture.CAPTURE_FAILSOFT)
    public void injectEnqueue(BlockPos pos, ChainRestrictedNeighborUpdater.Entry entry, CallbackInfo ci, boolean bl) {
        int maxSize = SLSCarpetSettings.maxUpdateQueueSize;
        if (maxSize > 0) {
            if (this.depth + 1 > maxSize) {
                this.queue.clear();
                this.pending.clear();
                this.depth = 0;
                bl = true;
                Throwable e = new OutOfMemoryError("Create by Carpet-SLS-Addition: The update queue exceeded the maximum allowed allocation of heap space set by SLS Addition！");
                e.printStackTrace();
                ci.cancel();
            }
        }
    }
}
