package com.github.zly2006.carpetslsaddition.mixin.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(NetherPortal.class)
public class MixinNetherPortal {
    @ModifyConstant(
            method = "*",
            constant = @Constant(intValue = 21),
            require = 0
    )
    private int portalSize(int original) {
        return SLSCarpetSettings.netherPortalSize;
    }
}
