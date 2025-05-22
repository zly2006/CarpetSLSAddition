package com.github.zly2006.carpetslsaddition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.github.zly2006.carpetslsaddition.command.BotCommand;
import com.github.zly2006.carpetslsaddition.command.HatCommand;
import com.github.zly2006.carpetslsaddition.command.SitCommand;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.Strictness;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class ServerMain implements ModInitializer, CarpetExtension {
    public static final String MOD_ID = "carpet-sls-addition";
    public static final String MOD_NAME = "Carpet SLS Addition";
    public static final Version MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion();

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    static final Gson GSON = new GsonBuilder().setStrictness(Strictness.LENIENT).create();  // 使用宽容模式，避免部分开发者在书写JSON时不遵守RFC 4627规范

    public static ServerMain INSTANCE;
    public static MinecraftServer server;

    public static final boolean tisCarpetLoaded = FabricLoader.getInstance().isModLoaded("carpet-tis-addition");

    @Override
    public void onInitialize() {
        INSTANCE = this;
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onGameStarted() {
        CarpetServer.settingsManager.parseSettingsClass(SLSCarpetSettings.class);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        ServerMain.server = server;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<String, String> canHasTranslations(String lang) {
        Map<String, String> translation = Maps.newHashMap();

        try {
            try (InputStream stream = ServerMain.class.getResourceAsStream("/assets/slsaddition/lang/%s.json".formatted(lang))) {
                assert stream != null;
                return GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Map.class);
            }
        } catch (IOException | NullPointerException ignored) {
            try {
                try (InputStream stream = ServerMain.class.getResourceAsStream("/assets/slsaddition/lang/en_us.json")) {
                    assert stream != null;
                    return GSON.fromJson(new InputStreamReader(stream, StandardCharsets.UTF_8), Map.class);
                }
            } catch (IOException | NullPointerException e) {
                return translation;
            }
        }
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
        HatCommand.register(dispatcher);
        SitCommand.register(dispatcher);
        BotCommand.register(dispatcher);
    }
}
