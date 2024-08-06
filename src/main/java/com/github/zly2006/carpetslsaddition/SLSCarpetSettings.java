package com.github.zly2006.carpetslsaddition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;

import static com.github.zly2006.carpetslsaddition.ServerMain.CARPET_ID;

public class SLSCarpetSettings {
    public static final String PCA = "pca";  // 用于描述兼容PCA的规则
    public static final String PROTOCOL = "protocol";

    @Rule(categories = {CARPET_ID, RuleCategory.SURVIVAL})
    public static boolean obtainableReinforcedDeepSlate = false;

    @Rule(categories = {CARPET_ID, RuleCategory.OPTIMIZATION})
    public static int skipTicksForJoblessVillager = 0;

    @Rule(categories = {CARPET_ID, RuleCategory.CREATIVE})
    public static boolean creativeNoInfinitePickup = false;

    @Rule(categories = {CARPET_ID, RuleCategory.OPTIMIZATION})
    public static boolean noBatSpawning = false;

    @Rule(categories = {CARPET_ID, RuleCategory.FEATURE})
    public static boolean offlineFakePlayers = false;

    @Rule(categories = {CARPET_ID, RuleCategory.FEATURE})
    public static boolean canUseHatCommand = false;

    @Rule(categories = {CARPET_ID, RuleCategory.FEATURE})
    public static boolean canUseSitCommand = false;

    @Rule(categories = {CARPET_ID, RuleCategory.CREATIVE})
    public static int maxUpdateQueueSize = -1;

    @Rule(categories = {CARPET_ID, PCA, PROTOCOL}, strict = false, options = {"#none"})
    public static String xaeroWorldName = "#none";

    @Rule(categories = {CARPET_ID, PCA, RuleCategory.FEATURE})
    public static boolean playerSit = false;

    @Rule(categories = {CARPET_ID, RuleCategory.CREATIVE})
    public static boolean oldRedstoneConnectionLogic = false;

    @Rule(categories = {CARPET_ID, RuleCategory.FEATURE})
    public static int netherPortalSize = 21;
}
