package com.github.zly2006.carpetslsaddition.command;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class HatCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager.literal("hat")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .requires((commandSource) -> SLSCarpetSettings.canUseHatCommand)
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    assert player != null;
                    ItemStack stack = player.getMainHandStack();
                    ItemStack head = player.getEquippedStack(EquipmentSlot.HEAD);
                    player.equipStack(EquipmentSlot.HEAD, stack);
                    player.getInventory().setStack(player.getInventory().selectedSlot, head);
                    player.currentScreenHandler.syncState();

                    return 1;
                }
                )
        );
    }
}
