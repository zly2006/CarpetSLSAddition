package com.github.zly2006.carpetslsaddition.mixin.entity;

import com.github.zly2006.carpetslsaddition.util.SitEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ArmorStandEntity.class)
public abstract class MixinArmorStandEntity extends LivingEntity implements SitEntity {
    @Unique
    private boolean sitEntity = false;  // 用于标识该盔甲架是否用于玩家乘坐

    protected MixinArmorStandEntity(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    protected abstract void setMarker(boolean marker);

    @Override
    public boolean carpet_SLS_Addition$isSitEntity() {
        return sitEntity;
    }

    @Override
    public void carpet_SLS_Addition$setSitEntity(boolean isSitEntity) {
        this.sitEntity = isSitEntity;
        this.setMarker(isSitEntity);
        this.setInvisible(isSitEntity);
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (this.carpet_SLS_Addition$isSitEntity()) {
            this.setPosition(this.getX(), this.getY() + 0.16, this.getZ());
            this.remove(Entity.RemovalReason.KILLED);
        }
        super.removePassenger(passenger);
    }

    @Inject(method = "writeCustomDataToNbt", at = @At(value = "RETURN"))
    private void postWriteCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        if (this.sitEntity) {
            nbt.putBoolean("SitEntity", true);
        }
    }

    @Inject(method = "readCustomDataFromNbt", at = @At(value = "RETURN"))
    private void postReadCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (nbt.contains("SitEntity")) {
            this.sitEntity = nbt.getBoolean("SitEntity").orElse(false);
        }
    }
}
