package com.github.zly2006.carpetslsaddition.mixin.entity;

import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.damage.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Predicate;

@Mixin(FallingBlockEntity.class)
public class MixinFallingBlockEntity {
    @Inject(method = "handleFallDamage", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getOtherEntities(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/Box;Ljava/util/function/Predicate;)Ljava/util/List;"), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir, int i, Predicate<?> predicate, DamageSource damageSource2, float f) {

    }
}
