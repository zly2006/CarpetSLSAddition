package com.github.zly2006.carpetslsaddition.command;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.util.SitEntity;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.world.World;

public class SitCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("sit")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .requires((commandSource) -> SLSCarpetSettings.canUseSitCommand)
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    assert player != null;

                    if (player.getVehicle() != null || !player.isOnGround()) {  // 防止错误的坐下行为
                        return 1;
                    }

                    World world = player.getWorld();

                    ArmorStandEntity armorStandEntity = new ArmorStandEntity(world, player.getX(), player.getY(), player.getZ());
                    ((SitEntity) armorStandEntity).setSitEntity(true);
                    world.spawnEntity(armorStandEntity);
                    player.setSneaking(false);
                    player.startRiding(armorStandEntity);

                    return 1;
                })
        );
    }
}
