package com.github.zly2006.carpetslsaddition;

import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;

import static com.github.zly2006.carpetslsaddition.ServerMain.CARPET_ID;

public class SLSCarpetSettings {
    public static final String PCA = "pca";  // 用于描述兼容PCA的规则
    public static final String NEED_CLIENT = "needClient";  // 需要客户端安装SLS-Addition或实现相关支持
    public static final String PROTOCOL = "protocol";

    @Rule(categories = {CARPET_ID, RuleCategory.SURVIVAL})
    public static boolean obtainableReinforcedDeepSlate = false;

    @Rule(categories = {CARPET_ID, RuleCategory.OPTIMIZATION})
    public static int skipTicksForJoblessVillager = 0;

    @Rule(categories = {CARPET_ID, RuleCategory.OPTIMIZATION}, options = {"-1", "24", "50"})
    public static int maxVillagersInABlock = -1;

    @Rule(categories = {CARPET_ID, RuleCategory.CREATIVE})
    public static boolean creativeNoInfinitePickup = false;

    @Rule(categories = {CARPET_ID})
    public static boolean autoUrl = false;

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

    @Rule(categories = {CARPET_ID, PCA, PROTOCOL})
    public static boolean pcaSyncProtocol = false;

    @Rule(categories = {CARPET_ID, PCA, PROTOCOL})
    public static PCA_SYNC_PLAYER_ENTITY_OPTIONS pcaSyncPlayerEntity = PCA_SYNC_PLAYER_ENTITY_OPTIONS.OPS;

    @Rule(categories = {CARPET_ID, PCA, RuleCategory.FEATURE, NEED_CLIENT})
    public static boolean emptyShulkerBoxStack = false;

    @Rule(categories = {CARPET_ID, PCA, RuleCategory.FEATURE})
    public static boolean useDyeOnShulkerBox = false;

    @Rule(categories = {CARPET_ID, PCA, PROTOCOL}, strict = false, options = {"#none"})
    public static String xaeroWorldName = "#none";

    @Rule(categories = {CARPET_ID, PCA, RuleCategory.FEATURE})
    public static boolean playerSit = false;


    public enum PCA_SYNC_PLAYER_ENTITY_OPTIONS {
        NOBODY, BOT, OPS, OPS_AND_SELF, EVERYONE
    }

}
