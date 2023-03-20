package com.github.zly2006.carpetslsaddition.mixin.carpet;

import carpet.patches.EntityPlayerMPFake;
import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.mojang.authlib.GameProfile;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.UUID;

@Mixin(value = EntityPlayerMPFake.class)
public class MixinFakePlayer {
    @Redirect(method = "createFake", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/UserCache;findByName(Ljava/lang/String;)Ljava/util/Optional;"))
    private static Optional<GameProfile> onCreate(UserCache instance, String name) {
        if (SLSCarpetSettings.offlineFakePlayers) {
            UUID uuid = Uuids.getOfflinePlayerUuid(name);
            return Optional.of(new GameProfile(uuid, name));
        }
        return instance.findByName(name);
    }
}
