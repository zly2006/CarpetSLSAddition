package com.github.zly2006.carpetslsaddition.mixin.entity;

import com.github.zly2006.carpetslsaddition.util.access.PlayerAccessor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class MixinPlayerEntity extends Entity implements PlayerAccessor {
    @Unique
    private MutableText displayName = null;

    public MixinPlayerEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    protected abstract MutableText addTellClickEvent(MutableText component);

    @Shadow public abstract Text getName();

    @Override
    public void carpet_SLS_Addition$setDisplayName(MutableText name) {
        this.displayName = name;
    }

    @Inject(method = "getDisplayName", at = @At("HEAD"), cancellable = true)
    public void injectGetDisplayName(CallbackInfoReturnable<Text> cir) {
        if (this.displayName != null) {
            cir.setReturnValue(addTellClickEvent(displayName));
        }
    }
}
