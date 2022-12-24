package com.github.zly2006.carpetslsaddition.mixin;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.SpawnHelper;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SpawnHelper.class)
public class MixinSpawnHelper {
    @Inject(method = "createMob", at = @At("HEAD"), cancellable = true)
    private static void onCreateMob(ServerWorld world, EntityType<?> type, CallbackInfoReturnable<@Nullable MobEntity> cir) {
        if (SLSCarpetSettings.noBatSpawning && type == EntityType.BAT) {
            cir.setReturnValue(null);
        }
    }
}
