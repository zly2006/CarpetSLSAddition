package com.github.zly2006.carpetslsaddition.mixin.carpet;

import carpet.api.settings.CarpetRule;
import carpet.api.settings.SettingsManager;
import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.util.NeighborUpdaterChanger;
import com.github.zly2006.carpetslsaddition.util.access.SettingsManagerAccessor;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SettingsManager.class, remap = false)
public abstract class MixinSettingsManager implements SettingsManagerAccessor {
    @Shadow protected abstract void loadConfigurationFromConf();

    @Override
    public void loadSettings() {
        this.loadConfigurationFromConf();
    }

    @Inject(method = "setRule", at = @At(value = "INVOKE", target = "Lcarpet/utils/Messenger;m(Lnet/minecraft/server/command/ServerCommandSource;[Ljava/lang/Object;)V", shift = At.Shift.BEFORE))
    private void injectSetRule(ServerCommandSource source, CarpetRule<?> rule, String newValue, CallbackInfoReturnable<Integer> cir) {
        if ("maxUpdateQueueSize".equals(rule.name())) {
            MinecraftServer server = ServerMain.server;

            if (server != null)
            {
                server.execute(() -> {
                    for (ServerWorld world : server.getWorlds())
                    {
                        ((NeighborUpdaterChanger)world).useRewriteChainNeighborUpdater(SLSCarpetSettings.maxUpdateQueueSize > -1);
                    }
                });
            }
        }
    }
}
