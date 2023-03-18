package com.github.zly2006.carpetslsaddition.mixin.protocol.pcaSyncProtocol.entity;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.network.pca.PcaSyncProtocol;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.JumpingMount;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.InventoryChangedListener;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractHorseEntity.class)
public abstract class MixinHorseBaseEntity extends AnimalEntity implements InventoryChangedListener, JumpingMount, Saddleable {
    protected MixinHorseBaseEntity(EntityType<? extends AnimalEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onInventoryChanged", at = @At(value = "HEAD"))
    private void updateEntity(Inventory sender, CallbackInfo ci) {
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncEntityToClient(this)) {
            ServerMain.LOGGER.debug("update HorseBaseEntity inventory: onInventoryChanged.");
        }
    }
}
