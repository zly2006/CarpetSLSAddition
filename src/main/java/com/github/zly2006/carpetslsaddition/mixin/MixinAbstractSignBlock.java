package com.github.zly2006.carpetslsaddition.mixin;

import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.entity.InventoryOwner;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractSignBlock.class)
public class MixinAbstractSignBlock {
    @Inject(method = "onUse", at = @At("HEAD"), cancellable = true)
    private void use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (state.getBlock() instanceof WallSignBlock && false) {
            BlockPos pos1 = pos.offset(state.get(WallSignBlock.FACING).getOpposite());
            BlockState state1 = world.getBlockState(pos1);
            if (state1.getBlock() instanceof ChestBlock || state1.getBlock() instanceof BarrelBlock) {
                SignBlockEntity signBlockEntity = (SignBlockEntity) world.getBlockEntity(pos);
                BlockEntity blockEntity = world.getBlockEntity(pos1);
                if (blockEntity instanceof Inventory inventory) {
                    if (signBlockEntity.getTextOnRow(0, true).getString().equals("[[Once]]")) {
                        int i = 0;
                        for (; i < inventory.size(); i++) {
                            if (!inventory.getStack(i).isEmpty()) {
                                break;
                            }
                        }
                        if (i == inventory.size()) {
                            return;
                        }
                        ItemStack stack = inventory.removeStack(i);
                        inventory.markDirty();
                        if (player.getInventory().insertStack(stack)) {
                            cir.setReturnValue(ActionResult.SUCCESS);
                        }
                    }
                }
            }
        }
    }
}
