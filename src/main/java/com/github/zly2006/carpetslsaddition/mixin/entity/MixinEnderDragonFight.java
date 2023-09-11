package com.github.zly2006.carpetslsaddition.mixin.entity;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.mixin.block.BlockPatternTestTransformInvoker;
import com.google.common.cache.LoadingCache;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.EndGatewayBlockEntity;
import net.minecraft.block.entity.EndPortalBlockEntity;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.pattern.CachedBlockPosition;
import net.minecraft.entity.boss.dragon.EnderDragonFight;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.chunk.WorldChunk;
import net.minecraft.world.gen.feature.EndPortalFeature;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(EnderDragonFight.class)
public class MixinEnderDragonFight {
    @Shadow
    @Final
    private ServerWorld world;

    @Shadow
    @Final
    private BlockPattern endPortalPattern;

    @Shadow
    private BlockPos exitPortalLocation;
    @Shadow
    private boolean doLegacyCheck;

    @Unique
    private int defaultChunkX = -8;
    @Unique
    private int defaultChunkZ = -8;
    @Unique
    private int defaultOriginY = -1;

    /**
     * @author Disy920
     * @reason Optimize the search process of the end portal
     */
    @Overwrite
    private @Nullable BlockPattern.Result findEndPortal() {
        int i,j;
        if(!SLSCarpetSettings.optimizedOnDragonRespawn) {
            defaultChunkX = -8;
            defaultChunkZ = -8;
        }
        for(i = defaultChunkX; i <= 8; ++i) {
            for(j = defaultChunkZ; j <= 8; ++j) {
                WorldChunk worldChunk = this.world.getChunk(i, j);
                for(BlockEntity blockEntity : worldChunk.getBlockEntities().values()) {
                    if(SLSCarpetSettings.optimizedOnDragonRespawn && blockEntity instanceof EndGatewayBlockEntity) continue;
                    if (blockEntity instanceof EndPortalBlockEntity) {
                        BlockPattern.Result result = this.endPortalPattern.searchAround(this.world, blockEntity.getPos());
                        if (result != null) {
                            BlockPos blockPos = result.translate(3, 3, 3).getBlockPos();
                            if (this.exitPortalLocation == null) {
                                this.exitPortalLocation = blockPos;
                            }
                            defaultChunkX = i;
                            defaultChunkZ = j;
                            return result;
                        }
                    }
                }
            }
        }
        if(this.doLegacyCheck || this.exitPortalLocation == null){
            if(SLSCarpetSettings.optimizedOnDragonRespawn && defaultOriginY != -1) {
                i = defaultOriginY;
            }
            else {
                i = this.world.getTopPosition(Heightmap.Type.MOTION_BLOCKING, EndPortalFeature.offsetOrigin(BlockPos.ORIGIN)).getY();
            }
            boolean notFirstSearch = false;
            for(j = i; j >= 0; --j) {
                BlockPattern.Result result2;
                if(SLSCarpetSettings.optimizedOnDragonRespawn && notFirstSearch) {
                    result2 = partialSearchAround(this.endPortalPattern, this.world, new BlockPos(EndPortalFeature.offsetOrigin(BlockPos.ORIGIN).getX(), j, EndPortalFeature.offsetOrigin(BlockPos.ORIGIN).getZ()));
                }
                else{
                    result2 = this.endPortalPattern.searchAround(this.world, new BlockPos(EndPortalFeature.offsetOrigin(BlockPos.ORIGIN).getX(), j, EndPortalFeature.offsetOrigin(BlockPos.ORIGIN).getZ()));
                }
                if (result2 != null) {
                    if (this.exitPortalLocation == null) {
                        this.exitPortalLocation = result2.translate(3, 3, 3).getBlockPos();
                    }
                    defaultOriginY = j;
                    return result2;
                }
                notFirstSearch = true;
            }
        }

        return null;
    }

    @Inject(method = "respawnDragon(Ljava/util/List;)V", at = @At("HEAD"))
    private void resetCache(List<EndCrystalEntity> crystals, CallbackInfo ci) {
        this.defaultChunkX = -8;
        this.defaultChunkZ = -8;
        this.defaultOriginY = -1;
    }

    @Unique
    private BlockPattern.Result partialSearchAround(BlockPattern pattern, WorldView world, BlockPos pos) {
        LoadingCache<BlockPos, CachedBlockPosition> loadingCache = BlockPattern.makeCache(world, false);
        int i = Math.max(Math.max(pattern.getWidth(), pattern.getHeight()), pattern.getDepth());
        for (BlockPos blockPos : BlockPos.iterate(pos, pos.add(i - 1, 0, i - 1))) {
            for (Direction direction : Direction.values()) {
                for (Direction direction2 : Direction.values()) {
                    BlockPattern.Result result;
                    if (direction2 == direction || direction2 == direction.getOpposite() || (result = ((BlockPatternTestTransformInvoker)pattern).invokeTestTransform(blockPos, direction, direction2, loadingCache)) == null) continue;
                    return result;
                }
            }
        }
        return null;
    }
}
