package com.github.zly2006.carpetslsaddition.mixin.protocol.pcaSyncProtocol.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.network.pca.PcaSyncProtocol;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.BrewingStandBlockEntity;
import net.minecraft.block.entity.LockableContainerBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BrewingStandBlockEntity.class)
public abstract class MixinBrewingStandBlockEntity extends LockableContainerBlockEntity implements SidedInventory {

    protected MixinBrewingStandBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            ServerMain.LOGGER.debug("update BrewingStandBlockEntity: {}", this.pos);
        }
    }
}