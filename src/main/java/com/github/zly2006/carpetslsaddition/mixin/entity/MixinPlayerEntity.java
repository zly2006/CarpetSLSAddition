package com.github.zly2006.carpetslsaddition.mixin.entity;

import com.github.zly2006.carpetslsaddition.util.access.PlayerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stat;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.github.zly2006.carpetslsaddition.ServerMain.*;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity implements PlayerAccess {
    @Shadow public abstract ItemStack getEquippedStack(EquipmentSlot slot);

    @Shadow public abstract void resetStat(Stat<?> stat);

    @Shadow public abstract Text getName();

    private Text displayName = null;


    @Override
    public boolean holdingObsidianPickaxe() {
        ItemStack itemStack = getEquippedStack(EquipmentSlot.MAINHAND);
        NbtCompound compound = itemStack.getSubNbt(MOD_ID);
        return compound != null && compound.contains(ITEM_NAME) && compound.getString(ITEM_NAME).equals(OBSIDIAN_PICKAXE);
    }

    @Override
    public void setDisplayName(Text name) {
        this.displayName = name;
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void injectGetDisplayName(CallbackInfoReturnable<Text> cir) {
        if (this.displayName != null) {
            cir.setReturnValue(displayName);
        }
    }


    @Inject(method = "getBlockBreakingSpeed", at = @At("HEAD"), cancellable = true)
    private void onGetBlockBreakingSpeed(BlockState block, CallbackInfoReturnable<Float> cir) {
        if (holdingObsidianPickaxe() && block.isOf(Blocks.OBSIDIAN)) {
            cir.setReturnValue(750f);// f = speed / 1500
        }
    }


    @Inject(method = "canHarvest", at = @At("HEAD"), cancellable = true)
    private void onGetCanMine(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (holdingObsidianPickaxe() && state.isOf(Blocks.OBSIDIAN)) {
            cir.setReturnValue(true);
        }
    }
}
