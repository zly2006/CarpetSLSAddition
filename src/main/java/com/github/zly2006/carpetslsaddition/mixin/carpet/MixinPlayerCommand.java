package com.github.zly2006.carpetslsaddition.mixin.carpet;

import carpet.commands.PlayerCommand;
import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import java.util.Optional;

@Mixin(PlayerCommand.class)
public abstract class MixinPlayerCommand {

    @Redirect(
            method = "cantSpawn",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/util/UserCache;findByName(Ljava/lang/String;)Ljava/util/Optional;")
    )
    private static Optional<GameProfile> redirectFindByName(UserCache instance, String playerName) {
        if (SLSCarpetSettings.offlineFakePlayers) {
            return Optional.of(Uuids.getOfflinePlayerProfile(playerName));
        } else {
            return instance.findByName(playerName);
        }
    }
}
