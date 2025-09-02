package com.github.zly2006.carpetslsaddition.mixin.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.block.spawner.TrialSpawnerConfig;
import net.minecraft.block.spawner.TrialSpawnerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(TrialSpawnerData.class)
public class MixinTrialSpawnerData {
    @WrapOperation(
            method = "resetAndClearMobs",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/block/spawner/TrialSpawnerConfig;getCooldownLength()J"
            )
    )
    private long hackCooldown(TrialSpawnerConfig instance, Operation<Integer> original) {
        if (SLSCarpetSettings.trialSpawnerCD < 0) {
            return original.call(instance);
        } else {
            return SLSCarpetSettings.trialSpawnerCD;
        }
    }
}
