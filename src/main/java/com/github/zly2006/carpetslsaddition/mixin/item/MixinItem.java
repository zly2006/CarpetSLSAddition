package com.github.zly2006.carpetslsaddition.mixin.item;

import com.github.zly2006.carpetslsaddition.util.access.PlayerAccess;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class MixinItem {
    @Inject(method = "canMine", at = @At("HEAD"), cancellable = true)
    private void onCheckMine(BlockState state, World world, BlockPos pos, PlayerEntity miner, CallbackInfoReturnable<Boolean> cir) {
        if (((PlayerAccess) miner).holdingObsidianPickaxe() && state.getBlock() == Blocks.OBSIDIAN) {
            cir.setReturnValue(true);
        }
    }
}

