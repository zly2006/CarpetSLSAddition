package com.github.zly2006.carpetslsaddition.mixin.protocol.pcaSyncProtocol.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.network.pca.PcaSyncProtocol;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ComparatorBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ComparatorBlockEntity.class)
public abstract class MixinComparatorBlockEntity extends BlockEntity {
    public MixinComparatorBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            ServerMain.LOGGER.debug("update ComparatorBlockEntity: {}", this.pos);
        }
    }
}
