package com.github.zly2006.carpetslsaddition.block;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.mojang.logging.LogUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.block.NeighborUpdater;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

public class RewriteChainRestrictedNeighborUpdater implements NeighborUpdater {
    private static final Logger LOGGER = LogUtils.getLogger();
    private final World world;
    private final int maxChainDepth;
    private final ArrayDeque<Entry> queue = new ArrayDeque<>();
    private final List<Entry> pending = new ArrayList<>();
    private int depth = 0;

    // rewrite part
    private boolean needUpdate = true;


    public RewriteChainRestrictedNeighborUpdater(World world, int maxChainDepth) {
        this.world = world;
        this.maxChainDepth = maxChainDepth;
    }

    public void replaceWithStateForNeighborUpdate(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int flags, int maxUpdateDepth) {
        this.enqueue(pos, new StateReplacementEntry(direction, neighborState, pos.toImmutable(), neighborPos.toImmutable(), flags, maxUpdateDepth));
    }

    public void updateNeighbor(BlockPos pos, Block sourceBlock, BlockPos sourcePos) {
        this.enqueue(pos, new SimpleEntry(pos, sourceBlock, sourcePos.toImmutable()));
    }

    public void updateNeighbor(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean notify) {
        this.enqueue(pos, new StatefulEntry(state, pos.toImmutable(), sourceBlock, sourcePos.toImmutable(), notify));
    }

    public void updateNeighbors(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
        this.enqueue(pos, new SixWayEntry(pos.toImmutable(), sourceBlock, except));
    }

    private void enqueue(BlockPos pos, Entry entry) {
        int maxSize = SLSCarpetSettings.maxUpdateQueueSize;
        if (maxSize > 0) {
            needUpdate = this.pending.size() + 1 <= maxSize;
        }
        else {
            needUpdate = true;
        }

        boolean notEmpty = this.depth > 0;
        boolean isFull = this.maxChainDepth >= 0 && this.depth >= this.maxChainDepth;
        ++this.depth;
        if (!isFull) {
            if (notEmpty) {
                this.pending.add(entry);
            } else {
                this.queue.push(entry);
            }
        }
        else if (this.depth - 1 == this.maxChainDepth) {
            LOGGER.error("Too many chained neighbor updates. Skipping the rest. First skipped position: " + pos.toShortString());
        }

        if (!notEmpty) {
            this.runQueuedUpdates();
        }

    }

    private void runQueuedUpdates() {
        try {
            while(!this.queue.isEmpty() || !this.pending.isEmpty()) {
                if (SLSCarpetSettings.maxUpdateQueueSize == 0) {
                    break;
                }

                for(int i = this.pending.size() - 1; i >= 0; --i) {
                    this.queue.push(this.pending.get(i));
                }

                this.pending.clear();
                Entry entry = this.queue.peek();
                assert entry != null;

                if (needUpdate) {
                    while(this.pending.isEmpty()) {
                        if (!entry.update(this.world)) {
                            this.queue.pop();
                            break;
                        }
                    }
                }
                else {
                    entry.update(this.world);
                    throw new OutOfMemoryError("Create by Carpet-SLS-Addition");
                }
            }
        }
        catch (OutOfMemoryError e) {
            if (e.getMessage().endsWith("Carpet-SLS-Addition")) {
                LOGGER.error("The update queue exceeded the maximum allowed allocation of heap space set by SLS Addition！", e);
            }
            else {
                throw e;
            }
        }
        finally {
            this.queue.clear();
            this.pending.clear();
            this.depth = 0;
        }
    }


    private interface Entry {
        boolean update(World world);
    }

    private record StateReplacementEntry(Direction direction, BlockState neighborState, BlockPos pos, BlockPos neighborPos, int updateFlags, int updateLimit) implements Entry {

        public boolean update(World world) {
            NeighborUpdater.replaceWithStateForNeighborUpdate(world, this.direction, this.neighborState, this.pos, this.neighborPos, this.updateFlags, this.updateLimit);
            return false;
        }
    }

    record SimpleEntry(BlockPos pos, Block sourceBlock, BlockPos sourcePos) implements Entry {
        public boolean update(World world) {
            BlockState blockState = world.getBlockState(this.pos);
            NeighborUpdater.tryNeighborUpdate(world, blockState, this.pos, this.sourceBlock, this.sourcePos, false);
            return false;
        }
    }

    record StatefulEntry(BlockState state, BlockPos pos, Block sourceBlock, BlockPos sourcePos, boolean movedByPiston) implements Entry {
        public boolean update(World world) {
            NeighborUpdater.tryNeighborUpdate(world, this.state, this.pos, this.sourceBlock, this.sourcePos, this.movedByPiston);
            return false;
        }
    }

    static final class SixWayEntry implements Entry {
        private final BlockPos pos;
        private final Block sourceBlock;
        @Nullable
        private final Direction except;
        private int currentDirectionIndex = 0;

        SixWayEntry(BlockPos pos, Block sourceBlock, @Nullable Direction except) {
            this.pos = pos;
            this.sourceBlock = sourceBlock;
            this.except = except;
            if (NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == except) {
                ++this.currentDirectionIndex;
            }

        }

        public boolean update(World world) {
            BlockPos blockPos = this.pos.offset(NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex++]);
            BlockState blockState = world.getBlockState(blockPos);
            blockState.neighborUpdate(world, blockPos, this.sourceBlock, this.pos, false);
            if (this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length && NeighborUpdater.UPDATE_ORDER[this.currentDirectionIndex] == this.except) {
                ++this.currentDirectionIndex;
            }

            return this.currentDirectionIndex < NeighborUpdater.UPDATE_ORDER.length;
        }
    }
}
