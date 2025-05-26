package com.github.zly2006.carpetslsaddition.mixin.carpet;

import carpet.patches.EntityPlayerMPFake;
import carpet.utils.Translations;
import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.util.access.SLSBotAccessor;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(EntityPlayerMPFake.class)
public abstract class MixinEntityPlayerMPFake extends ServerPlayerEntity implements SLSBotAccessor {
    @Shadow public abstract void kill(Text reason);

    @Unique
    private boolean bot = false;
    @Unique
    private long spawnTime = -1;

    public MixinEntityPlayerMPFake(MinecraftServer server, ServerWorld world, GameProfile profile, SyncedClientOptions clientOptions) {
        super(server, world, profile, clientOptions);
    }

    @Override
    public boolean carpet_SLS_Addition$isBot() {
        return bot;
    }

    @Override
    public void carpet_SLS_Addition$setBot(boolean isBot) {
        this.bot = isBot;
    }

    @Override
    public long carpet_SLS_Addition$getSpawnTime() {
        if (spawnTime == -1) spawnTime = System.currentTimeMillis();
        return spawnTime;
    }

    @Override
    public void carpet_SLS_Addition$setSpawnTime(long spawnTime) {
        this.spawnTime = spawnTime;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void onTick(CallbackInfo ci) {
        if (!this.bot || SLSCarpetSettings.botMaxOnlineTime < 0) {
            return;
        }

        long currentTime = System.currentTimeMillis();
        long liveTime = currentTime - spawnTime;

        if (liveTime > SLSCarpetSettings.botMaxOnlineTime * 1000) {
            var messageOnKick = Text.literal(
                    Translations.tr("carpet.slsa.bot.bot_timeout")
                            .formatted(
                                    this.getNameForScoreboard(),
                                    getFormattedTime(SLSCarpetSettings.botMaxOnlineTime)
                            )
            ).setStyle(Style.EMPTY.withColor(Formatting.RED));

            ServerMain.server.getPlayerManager().broadcast(messageOnKick, false);

            this.kill(messageOnKick);
        }
    }

    @Redirect(method = "createFake", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/UserCache;findByName(Ljava/lang/String;)Ljava/util/Optional;"))
    private static Optional<GameProfile> onCreate(UserCache instance, String name) {
        if (SLSCarpetSettings.offlineFakePlayers) {
            UUID uuid = Uuids.getOfflinePlayerUuid(name);
            return Optional.of(new GameProfile(uuid, name));
        }

        return instance.findByName(name);
    }

    @Unique
    private static String getFormattedTime(long time) {
        if (time > 120 * 60) {
            return String.format("%02dh%02dm%02ds", time / 3600, (time % 3600) / 60, time % 60);
        }
        else if (time > 120) {
            return String.format("%02dm%02ds", time / 60, time % 60);
        }
        else {
            return String.format("%02ds", time);
        }
    }
}
