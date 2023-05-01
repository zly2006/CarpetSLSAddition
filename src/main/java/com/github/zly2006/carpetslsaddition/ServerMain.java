package com.github.zly2006.carpetslsaddition;

import carpet.CarpetExtension;
import carpet.CarpetServer;
import com.github.zly2006.carpetslsaddition.network.pca.PcaSyncProtocol;
import com.github.zly2006.carpetslsaddition.util.SitEntity;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.ShapedRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class ServerMain implements ModInitializer, CarpetExtension {
    public static final String MOD_ID = "carpet-sls-addition";
    public static final String MOD_NAME = "Carpet SLS Addition";
    public static final Version MOD_VERSION = FabricLoader.getInstance().getModContainer(MOD_ID).get().getMetadata().getVersion();

    public static final String CARPET_ID = "SLS";

    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);
    static final Gson GSON = new GsonBuilder().setLenient().create();  // 使用宽容模式，避免部分开发者在书写JSON时不遵守RFC 4627规范

    public static ServerMain INSTANCE;
    public static MinecraftServer server;

    public static final boolean tisCarpetLoaded = FabricLoader.getInstance().isModLoaded("carpet-tis-addition");
    public static final String ITEM_NAME = "item_name";
    public static final String OBSIDIAN_PICKAXE = "obsidian_pickaxe";

    @Override
    public void onInitialize() {
        INSTANCE = this;
        CarpetServer.manageExtension(this);
    }

    @Override
    public void onServerLoaded(MinecraftServer server) {
        ServerMain.server = server;
        CarpetServer.settingsManager.parseSettingsClass(SLSCarpetSettings.class);
        ((SettingsManagerAccessor) CarpetServer.settingsManager).loadSettings();
        if (false) {
            ItemStack stack = new ItemStack(Items.STONE_PICKAXE);
            stack.setCustomName(Text.empty().formatted(Formatting.LIGHT_PURPLE).append("黑曜石镐"));
            stack.getOrCreateSubNbt(MOD_ID).putString(ITEM_NAME, OBSIDIAN_PICKAXE);
            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(9, Ingredient.EMPTY);
            ingredients.set(0, Ingredient.ofItems(Items.OBSIDIAN));
            ingredients.set(1, Ingredient.ofItems(Items.OBSIDIAN));
            ingredients.set(2, Ingredient.ofItems(Items.OBSIDIAN));
            ingredients.set(4, Ingredient.ofItems(Items.STICK));
            ingredients.set(7, Ingredient.ofItems(Items.STICK));
            server.getRecipeManager().setRecipes(List.of(new ShapedRecipe(new Identifier(MOD_ID, "recipe.obsidian_pickaxe"), MOD_ID, CraftingRecipeCategory.EQUIPMENT, 3, 3, ingredients, stack)));
        }
    }

    @Override
    public Map<String, String> canHasTranslations(String lang) {
        Map<String, String> translation = Maps.newHashMap();
        PcaSyncProtocol.init();
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
                .filter(entry -> entry.getKey().startsWith("carpet."))
                .filter(entry -> entry.getValue().isJsonPrimitive())
                .map(entry -> Map.entry(entry.getKey(), entry.getValue().getAsString()))
                .forEach(entry -> translation.put(entry.getKey(), entry.getValue()));
        return translation;
    }

    @Override
    public void registerCommands(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess commandBuildContext) {
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
                }));

        dispatcher.register(CommandManager.literal("sit")
                .requires(ServerCommandSource::isExecutedByPlayer)
                .requires((commandSource) -> SLSCarpetSettings.canUseSitCommand)
                .executes(context -> {
                    ServerPlayerEntity player = context.getSource().getPlayer();
                    assert player != null;

                    if (player.getVehicle() != null) {  // 防止重复的坐下行为
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
