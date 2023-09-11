package com.github.zly2006.carpetslsaddition.mixin.server;

import com.github.zly2006.carpetslsaddition.util.access.ServerAccess;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.dedicated.MinecraftDedicatedServer;
import net.minecraft.server.dedicated.ServerPropertiesLoader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Environment(EnvType.SERVER)
@Mixin(MinecraftDedicatedServer.class)
public class MixinMinecraftDedicatedServer implements ServerAccess {

    @Shadow
    @Final
    private ServerPropertiesLoader propertiesLoader;

    @Override
    public ServerPropertiesLoader getPropertiesLoader() {
        return propertiesLoader;
    }
}
