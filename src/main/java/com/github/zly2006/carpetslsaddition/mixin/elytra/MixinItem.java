package com.github.zly2006.carpetslsaddition.mixin.elytra;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class MixinItem implements ItemConvertible {
    @Inject(
            method = "getRecipeRemainder",
            at = @At("HEAD"),
            cancellable = true
    )
    public void getRecipeRemainder(CallbackInfoReturnable<ItemStack> cir) {
        if (SLSCarpetSettings.elytraCraftable && this.equals(Items.ELYTRA)) {
            cir.setReturnValue(new ItemStack(this));
        }
    }
}
