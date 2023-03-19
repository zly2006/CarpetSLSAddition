package com.github.zly2006.carpetslsaddition.mixin.protocol.pcaSyncProtocol.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.network.pca.PcaSyncProtocol;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.LootableContainerBlockEntity;
import net.minecraft.block.entity.ShulkerBoxBlockEntity;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ShulkerBoxBlockEntity.class)
public abstract class MixinShulkerBoxBlockEntity extends LootableContainerBlockEntity implements SidedInventory {

    protected MixinShulkerBoxBlockEntity(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            ServerMain.LOGGER.debug("update ShulkerBoxBlockEntity: {}", this.pos);
        }
    }
}