package com.github.zly2006.carpetslsaddition.mixin.protocol.pcaSyncProtocol.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.network.pca.PcaSyncProtocol;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(BeehiveBlockEntity.class)
public abstract class MixinBeehiveBlockEntity extends BlockEntity {


    public MixinBeehiveBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Inject(method = "tickBees", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;remove()V", shift = At.Shift.AFTER))
    private static void postTickBees(World world, BlockPos pos, BlockState state, List<Object> bees, BlockPos flowerPos, CallbackInfo ci) {
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(Objects.requireNonNull(world.getBlockEntity(pos)))) {
            ServerMain.LOGGER.debug("update BeehiveBlockEntity: {}", pos);
        }
    }

    @Inject(method = "tryReleaseBee", at = @At(value = "RETURN"))
    public void postTryReleaseBee(BlockState state, BeehiveBlockEntity.BeeState beeState, CallbackInfoReturnable<List<Entity>> cir) {
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this) && cir.getReturnValue() != null) {
            ServerMain.LOGGER.debug("update BeehiveBlockEntity: {}", this.pos);
        }
    }

    @Inject(method = "readNbt", at = @At(value = "RETURN"))
    public void postFromTag(NbtCompound nbt, CallbackInfo ci) {
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            ServerMain.LOGGER.debug("update BeehiveBlockEntity: {}", this.pos);
        }
    }

    @Inject(method = "tryEnterHive(Lnet/minecraft/entity/Entity;ZI)V", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/entity/Entity;discard()V", ordinal = 0))
    public void postEnterHive(Entity entity, boolean hasNectar, int ticksInHive, CallbackInfo ci) {
        if (SLSCarpetSettings.pcaSyncProtocol && PcaSyncProtocol.syncBlockEntityToClient(this)) {
            ServerMain.LOGGER.debug("update BeehiveBlockEntity: {}", this.pos);
        }
    }
}