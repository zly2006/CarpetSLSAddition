package com.github.zly2006.carpetslsaddition.mixin;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerInventory.class)
public class MixinPlayerInventory {
    @Redirect(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;getAbilities()Lnet/minecraft/entity/player/PlayerAbilities;"))
    private PlayerAbilities insertStack(PlayerEntity instance) {
        if (SLSCarpetSettings.creativeNoInfinitePickup) {
            AccessorPlayerAbilities accessor = (AccessorPlayerAbilities) instance.getAbilities();
            PlayerAbilities abilities = new PlayerAbilities();
            AccessorPlayerAbilities accessor1 = (AccessorPlayerAbilities) abilities;
            accessor1.setCreativeMode(false);
            accessor1.setFlying(accessor.isFlying());
            accessor1.setFlyingSpeed(accessor.getFlyingSpeed());
            accessor1.setInvulnerable(accessor.isInvulnerable());
            accessor1.setAllowFlying(accessor.isAllowFlying());
            accessor1.setAllowModifyWorld(accessor.isAllowModifyWorld());
            accessor1.setWalkingSpeed(accessor.getWalkingSpeed());
            return abilities;
        }
        return instance.getAbilities();
    }
}
