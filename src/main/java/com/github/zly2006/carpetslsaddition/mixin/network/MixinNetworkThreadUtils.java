package com.github.zly2006.carpetslsaddition.mixin.network;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.network.NetworkThreadUtils;
import net.minecraft.util.crash.CrashException;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(NetworkThreadUtils.class)
public class MixinNetworkThreadUtils {
    @Redirect(
            method = "method_11072",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/util/crash/CrashException;getCause()Ljava/lang/Throwable;"
            )
    )
    private static Throwable onCheckCauseType(CrashException instance) {
        if (SLSCarpetSettings.rebornOOMSuppressor && instance.getCause() instanceof OutOfMemoryError) {
            return new RuntimeException();
        }
        
        return instance.getCause();
    }
}
