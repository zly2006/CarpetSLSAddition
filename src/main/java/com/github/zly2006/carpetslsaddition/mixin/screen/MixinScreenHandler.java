package com.github.zly2006.carpetslsaddition.mixin.screen;

import com.github.zly2006.carpetslsaddition.util.ShulkerBoxItemUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ScreenHandler.class)
public class MixinScreenHandler {
    @Redirect(method = "insertItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;isStackable()Z", ordinal = 0))
    private boolean insertItemIsStackable(ItemStack itemStack) {
        return ShulkerBoxItemUtil.isStackable(itemStack);
    }

    @Redirect(method = "insertItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I"))
    private int insertItemGetMaxCount0(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }

    @Redirect(method = "internalOnSlotClick", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getMaxCount()I", ordinal = -1))
    private int removeStackGetMaxCount(ItemStack itemStack) {
        return ShulkerBoxItemUtil.getMaxCount(itemStack);
    }
}
