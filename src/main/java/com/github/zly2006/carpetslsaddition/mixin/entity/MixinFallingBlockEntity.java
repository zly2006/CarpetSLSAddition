package com.github.zly2006.carpetslsaddition.mixin.entity;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.FallingBlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Predicate;

@Mixin(FallingBlockEntity.class)
public abstract class MixinFallingBlockEntity extends Entity {

    @Shadow private BlockState blockState;
    @Shadow private boolean destroyedOnLanding;

    public MixinFallingBlockEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "handleFallDamage", at = @At(value = "TAIL"))
    private void handleFallDamage(double fallDistance, float damagePerDistance, DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
        if (!SLSCarpetSettings.obtainableReinforcedDeepSlate || !this.blockState.isIn(BlockTags.ANVIL)) {
            return;
        }

        Predicate<Entity> deepSlatePredicated = (entity) -> {
            if (entity instanceof ItemEntity itemEntity) {
                var itemStack = itemEntity.getStack();
                return itemStack.isOf(Items.DEEPSLATE) && itemStack.getCount() >= 64;
            }

            return false;
        };

        var world = this.getWorld();
        var itemEntitiesList = world.getOtherEntities(this, this.getBoundingBox(), deepSlatePredicated);
        if (itemEntitiesList.size() < 9) {
            return;
        }

        var entity = itemEntitiesList.getFirst();

        for (int i = 0; i < 9; i++) {
            itemEntitiesList.get(i).discard();
        }

        this.destroyedOnLanding = true;


        world.playSound(this, this.getBlockPos(), SoundEvents.BLOCK_ANVIL_USE, SoundCategory.BLOCKS, 1.0F, 1.0F);

        var coreEntity = new ItemEntity(world, entity.getX(), entity.getY(), entity.getZ(), new ItemStack(Items.REINFORCED_DEEPSLATE, 1));
        coreEntity.setPickupDelay(40);
        float f = this.random.nextFloat() * 0.5F;
        float g = this.random.nextFloat() * (float) (Math.PI * 2);
        coreEntity.setVelocity(-MathHelper.sin(g) * f, 0.2F, MathHelper.cos(g) * f);
        world.spawnEntity(coreEntity);
    }
}
