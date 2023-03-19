package com.github.zly2006.carpetslsaddition.mixin.screen;

import com.github.zly2006.carpetslsaddition.util.ShulkerBoxItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Slot.class)
public class MixinSlot {
    @Redirect(method = "getMaxItemCount(Lnet/minecraft/item/ItemStack;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = 0))
    private int getMaxItemCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }
}
