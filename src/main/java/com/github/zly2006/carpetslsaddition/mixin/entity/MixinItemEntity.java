package com.github.zly2006.carpetslsaddition.mixin.entity;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemEntity.class)
public abstract class MixinItemEntity extends Entity {
    public MixinItemEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Shadow
    public abstract void setStack(ItemStack stack);

    @Inject(method = "damage", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/ItemEntity;discard()V"), cancellable = true)
    private void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (source.getTypeRegistryEntry().matchesKey(DamageTypes.FALLING_ANVIL) && SLSCarpetSettings.obtainableReinforcedDeepSlate) {
            if ((getStack().isOf(Items.DEEPSLATE) || getStack().isOf(Items.COBBLED_DEEPSLATE))
                    && getStack().getCount() == 64) {
                setStack(new ItemStack(Items.REINFORCED_DEEPSLATE));
                cir.setReturnValue(true);
            }
        }
    }
}
