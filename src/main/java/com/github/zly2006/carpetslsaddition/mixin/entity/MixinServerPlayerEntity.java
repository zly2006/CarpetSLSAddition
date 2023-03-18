package com.github.zly2006.carpetslsaddition.mixin.entity;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.util.SitEntity;
import com.mojang.authlib.GameProfile;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.encryption.PlayerPublicKey;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayerEntity.class)
public abstract class MixinServerPlayerEntity extends PlayerEntity {
    @Shadow
    public ServerPlayNetworkHandler networkHandler;

    private int sneakTimes = 0;
    private long lastSneakTime = 0;

    public MixinServerPlayerEntity(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }


    @Override
    public void setSneaking(boolean sneaking){
        if (!SLSCarpetSettings.playerSit || (sneaking && this.isSneaking())) {
            super.setSneaking(sneaking);
            return;
        }

        if (sneaking) {
            long nowTime = System.currentTimeMillis();
            if (nowTime - lastSneakTime < 400 && sneakTimes == 0) {
                return;
            }
            super.setSneaking(true);
            if (this.isOnGround() && nowTime - lastSneakTime < 400) {
                sneakTimes += 1;
                if (sneakTimes == 3) {
                    ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, this.getX(), this.getY() - 0.16, this.getZ());
                    ((SitEntity) armorStandEntity).setSitEntity(true);
                    world.spawnEntity(armorStandEntity);
                    this.setSneaking(false);
                    this.startRiding(armorStandEntity);
                    sneakTimes = 0;
                }
            } else {
                sneakTimes = 1;
            }
            lastSneakTime = nowTime;
        } else {
            super.setSneaking(false);
            // 同步潜行状态到客户端
            // 如果不同步的话客户端会认为仍在潜行，从而碰撞箱的高度会计算错误
            if (sneakTimes == 0 && this.networkHandler != null) {
                // TODO: 构造函数改变，需要进行适配
                this.networkHandler.sendPacket(new EntityTrackerUpdateS2CPacket(this.getId(), this.getDataTracker(), true));
            }
        }
    }
}
