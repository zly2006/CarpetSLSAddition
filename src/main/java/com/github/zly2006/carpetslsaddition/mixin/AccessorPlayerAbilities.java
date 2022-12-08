package com.github.zly2006.carpetslsaddition.mixin;

import net.minecraft.entity.player.PlayerAbilities;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerAbilities.class)
public interface AccessorPlayerAbilities {
    @Accessor("flySpeed")
    void setFlyingSpeed(float speed);
    @Accessor("walkSpeed")
    void setWalkingSpeed(float speed);
    @Accessor("flying")
    void setFlying(boolean flying);
    @Accessor("invulnerable")
    void setInvulnerable(boolean invulnerable);
    @Accessor("allowFlying")
    void setAllowFlying(boolean allowFlying);
    @Accessor("creativeMode")
    void setCreativeMode(boolean creativeMode);
    @Accessor("allowModifyWorld")
    void setAllowModifyWorld(boolean allowModifyWorld);

    @Accessor("flySpeed")
    float getFlyingSpeed();
    @Accessor("walkSpeed")
    float getWalkingSpeed();
    @Accessor("flying")
    boolean isFlying();
    @Accessor("invulnerable")
    boolean isInvulnerable();
    @Accessor("allowFlying")
    boolean isAllowFlying();
    @Accessor("creativeMode")
    boolean isCreativeMode();
    @Accessor("allowModifyWorld")
    boolean isAllowModifyWorld();
}
