package com.github.zly2006.carpetslsaddition.mixin.server;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.function.BooleanSupplier;

@Mixin(MinecraftServer.class)
public abstract class MixinMinecraftServer {
    @Redirect(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;tick(Ljava/util/function/BooleanSupplier;)V"))
    private void redirectTicking(MinecraftServer instance, BooleanSupplier shouldKeepTicking) {
        if (SLSCarpetSettings.maxUpdateQueueSize != -1) {
            try {
                instance.tick(shouldKeepTicking);
            }
            catch (Throwable throwable) {
                if (throwable instanceof OutOfMemoryError oom) {
                    if (!oom.getMessage().startsWith("Create by Carpet-SLS-Addition")) {
                        throw throwable;
                    }
                }
                else {
                    throw throwable;
                }
            }
        }
        else {
            instance.tick(shouldKeepTicking);
        }
    }
}
