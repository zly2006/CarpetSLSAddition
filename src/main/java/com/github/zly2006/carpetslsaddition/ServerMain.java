package com.github.zly2006.carpetslsaddition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import carpet.api.settings.*;
import com.google.common.collect.MapMaker;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.injection.struct.InjectorGroupInfo;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static net.minecraft.server.command.CommandManager.*;

public class ServerMain implements ModInitializer, CarpetExtension {
    public static final String MOD_ID = "carpet-sls-addition";
    public static final String MOD_NAME = "Carpet SLS Addition";
    public static final String CARPET_ID = "slsaddition";
    static final Gson GSON = new Gson();
    public static final Version MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion();
    public static ServerMain INSTANCE;
    MinecraftServer server;

    @Override
    public void onInitialize() {
        INSTANCE = this;
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        this.server = server;
        CarpetServer.settingsManager.parseSettingsClass(SLSCarpetSettings.class);
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        Map<String, String> translation = Maps.newHashMap();
        String jsonFile;
        try {
            try (InputStream stream = ServerMain.class.getResourceAsStream("/assets/slsaddition/lang/%s.json".formatted(lang))) {
                assert stream != null;
                jsonFile = new String(stream.readAllBytes());
            }
        } catch (IOException | NullPointerException ignored) {
            try {
                try (InputStream stream = ServerMain.class.getResourceAsStream("/assets/slsaddition/lang/%s.json".formatted("en_us"))) {
                    assert stream != null;
                    jsonFile = new String(stream.readAllBytes());
                }
            } catch (IOException | NullPointerException e) {
                return translation;
            }
        }
        GSON.fromJson(jsonFile, JsonObject.class).entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("carpet.rule."))
                .filter(entry -> entry.getValue().isJsonPrimitive())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getAsString()))
                .forEach(entry -> translation.put(entry.getKey(), entry.getValue()));
        return translation;
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
    }
}
