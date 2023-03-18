package com.github.zly2006.carpetslsaddition.mixin.server;

import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.zip.CRC32;

@Mixin(PlayerManager.class)
public class MixinPlayerManager {
    @Inject(method = "sendWorldInfo", at = @At(value = "RETURN"))
    public void preOnSendWorldInfo(ServerPlayerEntity player, ServerWorld world, CallbackInfo ci) {
        if (SLSCarpetSettings.xaeroWorldName.equals("#none")) {
            return;
        }
        Identifier xaeroworldmap = new Identifier("xaeroworldmap", "main");
        Identifier xaerominimap = new Identifier("xaerominimap", "main");

        CRC32 crc = new CRC32();
        crc.update(SLSCarpetSettings.xaeroWorldName.getBytes());
        ByteBuf buf = Unpooled.buffer();
        buf.writeByte(0);
        buf.writeInt((int) crc.getValue());

        ServerPlayNetworking.send(player, xaeroworldmap, new PacketByteBuf(buf.duplicate()));
        ServerPlayNetworking.send(player, xaerominimap, new PacketByteBuf(buf.duplicate()));
    }
}
