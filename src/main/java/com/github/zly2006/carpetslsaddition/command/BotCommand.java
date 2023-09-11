package com.github.zly2006.carpetslsaddition.command;

import carpet.CarpetSettings;
import carpet.fakes.ServerPlayerInterface;
import carpet.helpers.EntityPlayerActionPack;
import carpet.patches.EntityPlayerMPFake;
import carpet.utils.Messenger;
import com.github.zly2006.carpetslsaddition.util.access.PlayerAccess;
import com.github.zly2006.carpetslsaddition.ServerMain;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.GameModeArgumentType;
import net.minecraft.command.argument.RotationArgumentType;
import net.minecraft.command.argument.Vec3ArgumentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.PlayerListS2CPacket;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Uuids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static net.minecraft.command.CommandSource.suggestMatching;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class BotCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher)
    {
        LiteralArgumentBuilder<ServerCommandSource> command = literal("bot")
                .requires(source -> source.hasPermissionLevel(4) || Permissions.check(source, "slsaddition.command.bot"))
                .then(argument("player", StringArgumentType.word())
                        .suggests((c, b) -> suggestMatching(getPlayerSuggestions(c.getSource()), b))
                        .then(literal("stop").executes(manipulation(EntityPlayerActionPack::stopAll)))
                        .then(makeActionCommand("use", EntityPlayerActionPack.ActionType.USE))
                        .then(makeActionCommand("jump", EntityPlayerActionPack.ActionType.JUMP))
                        .then(makeActionCommand("attack", EntityPlayerActionPack.ActionType.ATTACK))
                        .then(makeActionCommand("drop", EntityPlayerActionPack.ActionType.DROP_ITEM))
                        .then(makeDropCommand("drop", false))
                        .then(makeActionCommand("dropStack", EntityPlayerActionPack.ActionType.DROP_STACK))
                        .then(makeDropCommand("dropStack", true))
                        .then(makeActionCommand("swapHands", EntityPlayerActionPack.ActionType.SWAP_HANDS))
                        .then(literal("hotbar")
                                .then(argument("slot", IntegerArgumentType.integer(1, 9))
                                        .executes(c -> manipulate(c, ap -> ap.setSlot(IntegerArgumentType.getInteger(c, "slot"))))))
                        .then(literal("kill").executes(BotCommand::kill))
                        .then(literal("mount").executes(manipulation(ap -> ap.mount(true)))
                                .then(literal("anything").executes(manipulation(ap -> ap.mount(false)))))
                        .then(literal("dismount").executes(manipulation(EntityPlayerActionPack::dismount)))
                        .then(literal("sneak").executes(manipulation(ap -> ap.setSneaking(true))))
                        .then(literal("unsneak").executes(manipulation(ap -> ap.setSneaking(false))))
                        .then(literal("sprint").executes(manipulation(ap -> ap.setSprinting(true))))
                        .then(literal("unsprint").executes(manipulation(ap -> ap.setSprinting(false))))
                        .then(literal("look")
                                .then(literal("north").executes(manipulation(ap -> ap.look(Direction.NORTH))))
                                .then(literal("south").executes(manipulation(ap -> ap.look(Direction.SOUTH))))
                                .then(literal("east").executes(manipulation(ap -> ap.look(Direction.EAST))))
                                .then(literal("west").executes(manipulation(ap -> ap.look(Direction.WEST))))
                                .then(literal("up").executes(manipulation(ap -> ap.look(Direction.UP))))
                                .then(literal("down").executes(manipulation(ap -> ap.look(Direction.DOWN))))
                                .then(literal("at").then(argument("position", Vec3ArgumentType.vec3())
                                        .executes(c -> manipulate(c, ap -> ap.lookAt(Vec3ArgumentType.getVec3(c, "position"))))))
                                .then(argument("direction", RotationArgumentType.rotation())
                                        .executes(c -> manipulate(c, ap -> ap.look(RotationArgumentType.getRotation(c, "direction").toAbsoluteRotation(c.getSource())))))
                        ).then(literal("turn")
                                .then(literal("left").executes(manipulation(ap -> ap.turn(-90, 0))))
                                .then(literal("right").executes(manipulation(ap -> ap.turn(90, 0))))
                                .then(literal("back").executes(manipulation(ap -> ap.turn(180, 0))))
                                .then(argument("rotation", RotationArgumentType.rotation())
                                        .executes(c -> manipulate(c, ap -> ap.turn(RotationArgumentType.getRotation(c, "rotation").toAbsoluteRotation(c.getSource())))))
                        ).then(literal("move").executes(manipulation(EntityPlayerActionPack::stopMovement))
                                .then(literal("forward").executes(manipulation(ap -> ap.setForward(1))))
                                .then(literal("backward").executes(manipulation(ap -> ap.setForward(-1))))
                                .then(literal("left").executes(manipulation(ap -> ap.setStrafing(1))))
                                .then(literal("right").executes(manipulation(ap -> ap.setStrafing(-1))))
                        ).then(literal("spawn").executes(BotCommand::spawn)
                                .then(literal("in").requires((player) -> player.hasPermissionLevel(2))
                                        .then(argument("gamemode", GameModeArgumentType.gameMode())
                                                .executes(BotCommand::spawn)))
                                .then(literal("at").then(argument("position", Vec3ArgumentType.vec3()).executes(BotCommand::spawn)
                                        .then(literal("facing").then(argument("direction", RotationArgumentType.rotation()).executes(BotCommand::spawn)
                                                .then(literal("in").then(argument("dimension", DimensionArgumentType.dimension()).executes(BotCommand::spawn)
                                                        .then(literal("in").requires((player) -> player.hasPermissionLevel(2))
                                                                .then(argument("gamemode", GameModeArgumentType.gameMode())
                                                                        .executes(BotCommand::spawn)
                                                                )))
                                                )))
                                ))
                        )
                );
        dispatcher.register(command);
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeActionCommand(String actionName, EntityPlayerActionPack.ActionType type)
    {
        return literal(actionName)
                .executes(manipulation(ap -> ap.start(type, EntityPlayerActionPack.Action.once())))
                .then(literal("once").executes(manipulation(ap -> ap.start(type, EntityPlayerActionPack.Action.once()))))
                .then(literal("continuous").executes(manipulation(ap -> ap.start(type, EntityPlayerActionPack.Action.continuous()))))
                .then(literal("interval").then(argument("ticks", IntegerArgumentType.integer(1))
                        .executes(c -> manipulate(c, ap -> ap.start(type, EntityPlayerActionPack.Action.interval(IntegerArgumentType.getInteger(c, "ticks")))))));
    }

    private static LiteralArgumentBuilder<ServerCommandSource> makeDropCommand(String actionName, boolean dropAll)
    {
        return literal(actionName)
                .then(literal("all").executes(manipulation(ap -> ap.drop(-2, dropAll))))
                .then(literal("mainhand").executes(manipulation(ap -> ap.drop(-1, dropAll))))
                .then(literal("offhand").executes(manipulation(ap -> ap.drop(40, dropAll))))
                .then(argument("slot", IntegerArgumentType.integer(0, 40)).
                        executes(c -> manipulate(c, ap -> ap.drop(IntegerArgumentType.getInteger(c, "slot"), dropAll))));
    }

    private static Collection<String> getPlayerSuggestions(ServerCommandSource source)
    {
        Set<String> players = new LinkedHashSet<>(List.of("Steve", "Alex"));
        players.addAll(source.getPlayerNames());
        return players;
    }

    private static ServerPlayerEntity getPlayer(CommandContext<ServerCommandSource> context)
    {
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getServer();
        return server.getPlayerManager().getPlayer(playerName);
    }

    private static boolean cantManipulate(CommandContext<ServerCommandSource> context)
    {
        PlayerEntity player = getPlayer(context);
        ServerCommandSource source = context.getSource();
        if (player == null)
        {
            Messenger.m(source, "r Can only manipulate existing players");
            return true;
        }
        PlayerEntity sender = source.getPlayer();
        if (sender == null)
        {
            return false;
        }

        if (!source.getServer().getPlayerManager().isOperator(sender.getGameProfile()))
        {
            if (sender != player && !(player instanceof EntityPlayerMPFake))
            {
                Messenger.m(source, "r Non OP players can't control other real players");
                return true;
            }
        }
        return false;
    }

    private static boolean cantReMove(CommandContext<ServerCommandSource> context)
    {
        if (cantManipulate(context)) return true;
        PlayerEntity player = getPlayer(context);
        if (player instanceof EntityPlayerMPFake) return false;
        Messenger.m(context.getSource(), "r Only fake players can be moved or killed");
        return true;
    }

    private static boolean cantSpawn(CommandContext<ServerCommandSource> context)
    {
        String playerName = StringArgumentType.getString(context, "player");
        MinecraftServer server = context.getSource().getServer();
        PlayerManager manager = server.getPlayerManager();

        if (manager.getPlayer(playerName) != null)
        {
            Messenger.m(context.getSource(), "r Player ", "rb " + playerName, "r  is already logged on");
            return true;
        }
        GameProfile profile = server.getUserCache().findByName(playerName).orElse(null);
        if (profile == null)
        {
            if (!CarpetSettings.allowSpawningOfflinePlayers)
            {
                Messenger.m(context.getSource(), "r Player "+playerName+" is either banned by Mojang, or auth servers are down. " +
                        "Banned players can only be summoned in Singleplayer and in servers in off-line mode.");
                return true;
            } else {
                profile = new GameProfile(Uuids.getOfflinePlayerUuid(playerName), playerName);
            }
        }
        if (manager.getUserBanList().contains(profile))
        {
            Messenger.m(context.getSource(), "r Player ", "rb " + playerName, "r  is banned on this server");
            return true;
        }
        if (manager.isWhitelistEnabled() && manager.isWhitelisted(profile) && !context.getSource().hasPermissionLevel(2))
        {
            Messenger.m(context.getSource(), "r Whitelisted players can only be spawned by operators");
            return true;
        }
        return false;
    }

    private static int kill(CommandContext<ServerCommandSource> context)
    {
        if (cantReMove(context)) return 0;
        getPlayer(context).kill();
        return 1;
    }

    @FunctionalInterface
    interface SupplierWithCSE<T>
    {
        T get() throws CommandSyntaxException;
    }

    private static <T> T getArgOrDefault(BotCommand.SupplierWithCSE<T> getter, T defaultValue) throws CommandSyntaxException
    {
        try
        {
            return getter.get();
        }
        catch (IllegalArgumentException e)
        {
            return defaultValue;
        }
    }

    private static int spawn(CommandContext<ServerCommandSource> context) throws CommandSyntaxException
    {
        if (cantSpawn(context)) return 0;

        ServerCommandSource source = context.getSource();
        Vec3d pos = getArgOrDefault(
                () -> Vec3ArgumentType.getVec3(context, "position"),
                source.getPosition()
        );
        Vec2f facing = getArgOrDefault(
                () -> RotationArgumentType.getRotation(context, "direction").toAbsoluteRotation(source),
                source.getRotation()
        );
        RegistryKey<World> dimType = getArgOrDefault(
                () -> DimensionArgumentType.getDimensionArgument(context, "dimension").getRegistryKey(),
                source.getWorld().getRegistryKey()
        );

        String playerName = "bot_" + StringArgumentType.getString(context, "player");
        if (playerName.length() > maxNameLength(source.getServer()))
        {
            Messenger.m(source, "rb Player name: " + playerName + " is too long");
            return 0;
        }

        if (!World.isValid(BlockPos.ofFloored(pos)))
        {
            Messenger.m(source, "rb Player " + playerName + " cannot be placed outside of the world");
            return 0;
        }
        ServerPlayerEntity player = EntityPlayerMPFake.createFake(playerName, source.getServer(), pos, facing.y, facing.x, dimType, GameMode.SURVIVAL, false);
        if (player == null)
        {
            Messenger.m(source, "rb Player " + playerName + " doesn't exist and cannot spawn in online mode. " +
                    "Turn the server offline to spawn non-existing players");
            return 0;
        }
        else {
            ((PlayerAccess) player).setDisplayName(Text.empty().append(Text.literal("[%s] ".formatted(source.getName())).setStyle(Style.EMPTY.withColor(Formatting.GRAY))).append(Text.literal(playerName).setStyle(Style.EMPTY)));
            ServerMain.server.getPlayerManager().broadcast(Text.empty()
                    .append(Text.literal("假人").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
                    .append(Text.literal(playerName).setStyle(Style.EMPTY.withColor(Formatting.GOLD).withBold(true)))
                    .append(Text.literal("由玩家").setStyle(Style.EMPTY.withColor(Formatting.GREEN)))
                    .append(source.getDisplayName())
                    .append(Text.literal("召唤！").setStyle(Style.EMPTY.withColor(Formatting.GREEN))), false);

            ServerMain.server.getPlayerManager().sendToAll(new PlayerListS2CPacket(
                    PlayerListS2CPacket.Action.UPDATE_DISPLAY_NAME,
                    player
            ));
        }
        return 1;
    }

    private static int maxNameLength(MinecraftServer server)
    {
        return server.getServerPort() >= 0 ? PlayerEntity.field_30643 : 40;
    }

    private static int manipulate(CommandContext<ServerCommandSource> context, Consumer<EntityPlayerActionPack> action)
    {
        if (cantManipulate(context)) return 0;
        ServerPlayerEntity player = getPlayer(context);
        action.accept(((ServerPlayerInterface) player).getActionPack());
        return 1;
    }

    private static Command<ServerCommandSource> manipulation(Consumer<EntityPlayerActionPack> action)
    {
        return c -> manipulate(c, action);
    }
}
