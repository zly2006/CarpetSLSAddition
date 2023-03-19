package com.github.zly2006.carpetslsaddition.mixin.player;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.util.ShulkerBoxItemUtil;
import net.minecraft.entity.player.PlayerAbilities;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Nameable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(PlayerInventory.class)
public abstract class MixinPlayerInventory implements Inventory, Nameable {
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

    // 修改潜影盒拾取逻辑，使从地上捡起潜影盒时仍可堆叠
    @Redirect(method = "canStackAddMore",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isStackable()Z", ordinal = 0))
    private boolean canStackAddMoreIsStackable(ItemStack itemStack) {
        return ShulkerBoxItemUtil.isStackable(itemStack);
    }

    @Redirect(method = "canStackAddMore", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = 0))
    private int canStackAddMoreGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }

    @Redirect(method = "addStack(ILnet/minecraft/item/ItemStack;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = -1))
    private int addStackGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }

    // 避免死循环
    @Redirect(method = "offer", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = 0))
    private int offerGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }
}
