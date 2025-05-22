package com.github.zly2006.carpetslsaddition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;

public class SLSCarpetSettings {
    public static final String NEED_CLIENT = "needClient";  // 需要客户端安装SLS-Addition或实现相关支持
    public static final String FROM_AMS = "FROM-AMS";
    public static final String SLSA = "SLS";

    @Rule(categories = {SLSA, RuleCategory.SURVIVAL})
    public static boolean obtainableReinforcedDeepSlate = false;

    @Rule(categories = {SLSA, RuleCategory.CREATIVE})
    public static boolean creativeNoInfinitePickup = false;

    @Rule(categories = {SLSA, RuleCategory.OPTIMIZATION})
    public static boolean noBatSpawning = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean canUseHatCommand = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean canUseSitCommand = false;

    @Rule(categories = {SLSA}, strict = false, options = {"#none", "bot_"})
    public static String botPrefix = "#none";

    @Rule(categories = {SLSA})
    public static long botMaxOnlineTime = -1;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean creativeObeyEnchantmentRule = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean fakePlayersNotOccupiedSleepQuota = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE, NEED_CLIENT})
    public static boolean emptyShulkerBoxStack = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean playerSit = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean endermanCanPickUpMushroom = true;

    @Rule(categories = {SLSA, RuleCategory.CREATIVE})
    public static boolean oldRedstoneConnectionLogic = false;

    // Ported from Carpet AMS Addition
    @Rule(categories = {SLSA, FROM_AMS, RuleCategory.OPTIMIZATION})
    public static boolean optimizedOnDragonRespawn = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean elytraCraftable = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean spectatorCannotUseLeash = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean armadilloImmediateDespawns = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean offlineFakePlayers = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static int netherPortalSize = 21;

    @Rule(categories = {SLSA, RuleCategory.CREATIVE})
    public static boolean rebornOOMSuppressor = false;

    @Rule(categories = {SLSA, RuleCategory.FEATURE})
    public static boolean offlineFakePlayers = false;
}
