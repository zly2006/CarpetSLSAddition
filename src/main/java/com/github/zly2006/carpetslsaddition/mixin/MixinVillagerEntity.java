package com.github.zly2006.carpetslsaddition.mixin;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.VillagerData;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VillagerEntity.class)
public abstract class MixinVillagerEntity extends MerchantEntity {
    @Shadow public abstract VillagerData getVillagerData();

    @Shadow protected abstract void decayGossip();

    int tickCount = 0;
    public MixinVillagerEntity(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "tick", cancellable = true)
    private void onTick(CallbackInfo ci) {
        if (SLSCarpetSettings.skipTicksForJoblessVillager != 0) {
            VillagerProfession profession = this.getVillagerData().getProfession();
            if (profession != VillagerProfession.NONE && profession != VillagerProfession.NITWIT) {
                tickCount = 0;
                return;
            }

            if (tickCount > 0) {
                tickCount--;
            }
            else {
                tickCount = SLSCarpetSettings.skipTicksForJoblessVillager + 1;
                ci.cancel();

                if (this.getHeadRollingTimeLeft() > 0) {
                    this.setHeadRollingTimeLeft(this.getHeadRollingTimeLeft() - 1);
                }
                this.decayGossip();
            }
        }
    }
}
