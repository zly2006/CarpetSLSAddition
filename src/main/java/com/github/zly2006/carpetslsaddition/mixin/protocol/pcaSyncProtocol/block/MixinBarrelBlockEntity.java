package com.github.zly2006.carpetslsaddition.mixin.protocol.pcaSyncProtocol.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.network.pca.PcaSyncProtocol;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BarrelBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BarrelBlockEntity.class)
public abstract class MixinBarrelBlockEntity extends LootableContainerBlockEntity {

    protected MixinBarrelBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void markDirty() {
        super.markDirty();

        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            ServerMain.LOGGER.debug("update BarrelBlockEntity: {}", this.pos);
        }
    }
}