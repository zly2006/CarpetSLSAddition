package com.github.zly2006.carpetslsaddition.command;

import carpet.CarpetSettings;
import carpet.patches.EntityPlayerMPFake;
import carpet.patches.FakeClientConnection;
import carpet.utils.Messenger;
import com.github.zly2006.carpetslsaddition.SLSCarpetSettings;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.github.zly2006.carpetslsaddition.util.access.PlayerAccessor;
import com.github.zly2006.carpetslsaddition.util.access.SLSBotAccessor;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.SharedConstants;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.NetworkSide;
import net.minecraft.network.packet.c2s.common.SyncedClientOptions;
import net.minecraft.network.packet.s2c.play.EntityPositionSyncS2CPacket;
import net.minecraft.network.packet.s2c.play.EntitySetHeadYawS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ConnectedClientData;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BotCommand {
    private static final List<String> bannedCommand = List.of("shadow", "spawn");

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        var playerArgumentNode = argument("player", StringArgumentType.word())
                .suggests((c, b) -> suggestMatching(getPlayerSuggestions(c.getSource()), b))
                .then(literal("spawn").executes(BotCommand::spawn))
                .build();

        // 手动重定向工作
        for (var childNode : dispatcher.getRoot().getChild("player").getChild("player").getChildren()) {
            if (bannedCommand.contains(childNode.getName())) continue;
            playerArgumentNode.addChild(childNode);
        }

        var commandNode = literal("bot")
                .requires(source -> source.hasPermissionLevel(4) || Permissions.check(source, "slsaddition.command.bot"))
                .build();

        commandNode.addChild(playerArgumentNode);

        dispatcher.getRoot().addChild(commandNode);
    }

    private static Collection<String> getPlayerSuggestions(ServerCommandSource source) {
        Set<String> players = new LinkedHashSet<>(List.of("Steve", "Alex"));
        players.addAll(source.getPlayerNames());
        return players;
    }

    private static boolean cantSpawn(CommandContext<ServerCommandSource> context) {
        String playerName = getBotPrefix() + StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getServer();
        PlayerManager manager = server.getPlayerManager();

        if (manager.getPlayer(playerName) != null) {
            Messenger.m(context.getSource(), "r Player ", "rb " + playerName, "r  is already logged on");
            return true;
        }
        GameProfile profile = server.getUserCache().findByName(playerName).orElse(null);
        if (profile == null) {
            if (!CarpetSettings.allowSpawningOfflinePlayers) {
                Messenger.m(context.getSource(), "r Player "+playerName+" is either banned by Mojang, or auth servers are down. " +
                        "Banned players can only be summoned in Singleplayer and in servers in off-line mode.");
                return true;
            }
            else {
                profile = new GameProfile(Uuids.getOfflinePlayerUuid(playerName), playerName);
            }
        }
        if (manager.getUserBanList().contains(profile)) {
            Messenger.m(context.getSource(), "r Player ", "rb " + playerName, "r  is banned on this server");
            return true;
        }
        if (manager.isWhitelistEnabled() && manager.isWhitelisted(profile) && !context.getSource().hasPermissionLevel(2)) {
            Messenger.m(context.getSource(), "r Whitelisted players can only be spawned by operators");
            return true;
        }
        return false;
    }

    private static String getBotPrefix() {
        return SLSCarpetSettings.botPrefix.equals("#none") ? "" : SLSCarpetSettings.botPrefix;
    }

    @FunctionalInterface
    interface SupplierWithCSE<T> {
        T get() throws CommandSyntaxException;
    }

    private static <T> T getArgOrDefault(BotCommand.SupplierWithCSE<T> getter, T defaultValue) throws CommandSyntaxException {
        try {
            return getter.get();
        }
        catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }

    private static int spawn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
        if (cantSpawn(context)) return 0;

        ServerCommandSource source = context.getSource();
        Vec3d pos = getArgOrDefault(
                () -> Vec3ArgumentType.getVec3(context, "position"),
                source.getPosition()
        );
        Vec2f facing = getArgOrDefault(
                () -> RotationArgumentType.getRotation(context, "direction").getRotation(source),
                source.getRotation()
        );
        RegistryKey<World> dimType = getArgOrDefault(
                () -> DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey(),
                source.getWorld().getRegistryKey()
        );

        String playerName = getBotPrefix() + StringArgumentType.getString(context, "player");
        if (playerName.length() > maxNameLength(source.getServer())) {
            Messenger.m(source, "rb Player name: " + playerName + " is too long");
            return 0;
        }

        if (!World.isValid(BlockPos.ofFloored(pos))) {
            Messenger.m(source, "rb Player " + playerName + " cannot be placed outside of the world");
            return 0;
        }

        var prefix = Text.empty().append(Text.literal("[%s] ".formatted(source.getName())).setStyle(Style.EMPTY.withColor(Formatting.AQUA))).append(Text.literal(playerName).setStyle(Style.EMPTY));
        EntityPlayerMPFake bot = createBot(playerName, source.getServer(), pos, facing.y, facing.x, dimType, prefix);

        if (bot == null) {
            Messenger.m(source, "rb Player " + playerName + " doesn't exist and cannot spawn in online mode. " +
                    "Turn the server offline to spawn non-existing players");
            return 0;
        }

        // 向所有玩家发送更新后的玩家列表信息
        bot.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME, bot));
        bot.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(PlayerListS2CPacket.Action.UPDATE_LISTED, bot));

        ServerMain.server.getPlayerManager().broadcast(Text.empty()
                .append(Text.literal("假人").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
                .append(Text.literal(playerName).setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)))
                .append(Text.literal("由玩家").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
                .append(source.getDisplayName())
                .append(Text.literal("召唤！").setStyle(Style.EMPTY.withColor(Formatting.GREEN))), false);

        ServerMain.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(
                PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME,
                bot
        ));
        return 1;
    }

    private static EntityPlayerMPFake createBot(String username, MinecraftServer server, Vec3d pos, double yaw, double pitch, RegistryKey<World> dimensionId, MutableText prefix) {
        ServerWorld worldIn = server.getWorld(dimensionId);
        UserCache.setUseRemote(false);
        GameProfile gameprofile;

        if (SLSCarpetSettings.offlineFakePlayers) {
            gameprofile = new GameProfile(Uuids.getOfflinePlayerUuid(username), username);
        }
        else {
            try {
                gameprofile = server.getUserCache().findByName(username).orElse(null);
            }
            finally {
                UserCache.setUseRemote(server.isDedicated() && server.isOnlineMode());
            }
            if (gameprofile == null) {
                if (!CarpetSettings.allowSpawningOfflinePlayers) {
                    return null;
                }
                else {
                    gameprofile = new GameProfile(Uuids.getOfflinePlayerUuid(username), username);
                }
            }
        }

        // 孩子不懂，用反射写着玩的，报错了记得随Carpet一起升级一下
        try {
            Class<EntityPlayerMPFake> fakePlayerClass = EntityPlayerMPFake.class;
            Constructor<EntityPlayerMPFake> constructor = fakePlayerClass.getDeclaredConstructor(
                    MinecraftServer.class,
                    ServerWorld.class,
                    GameProfile.class,
                    SyncedClientOptions.class,
                    boolean.class
            );
            constructor.setAccessible(true);
            EntityPlayerMPFake bot = constructor.newInstance(server, worldIn, gameprofile, SyncedClientOptions.createDefault(), false);

            ((PlayerAccessor) bot).carpet_SLS_Addition$setDisplayName(prefix);

            bot.fixStartingPosition = () -> bot.refreshPositionAndAngles(pos.x, pos.y, pos.z, (float) yaw, (float) pitch);
            server.getPlayerManager().onPlayerConnect(new FakeClientConnection(NetworkSide.SERVERBOUND), bot, new ConnectedClientData(gameprofile, 0, bot.getClientOptions(), false));

            bot.teleport(worldIn, pos.x, pos.y, pos.z, Set.of(), (float) yaw, (float) pitch, true);
            bot.setHealth(20.0F);
            bot.unsetRemoved();
            bot.getAttributeInstance(EntityAttributes.STEP_HEIGHT).setBaseValue(0.6F);
            bot.interactionManager.changeGameMode(GameMode.SURVIVAL);

            server.getPlayerManager().sendToDimension(new EntitySetHeadYawS2CPacket(bot, (byte) (bot.headYaw * 256 / 360)), dimensionId);//bot.dimension);
            server.getPlayerManager().sendToDimension(EntityPositionSyncS2CPacket.create(bot), dimensionId);

            bot.getDataTracker().set(PlayerEntity.PLAYER_MODEL_PARTS, (byte) 0x7f); // show all model layers (incl. capes)
            bot.getAbilities().flying = false;

            ((SLSBotAccessor)bot).carpet_SLS_Addition$setBot(true);
            ((SLSBotAccessor)bot).carpet_SLS_Addition$setSpawnTime(System.currentTimeMillis());

            return bot;
        }
        catch (NoSuchMethodException  | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("An error has occurred when trying spawn a new bot with reflection", e);
        }
    }

    private static int maxNameLength(MinecraftServer server) {
        return server.getServerPort() >= 0 ? SharedConstants.field_49170 : 40;
    }

}
