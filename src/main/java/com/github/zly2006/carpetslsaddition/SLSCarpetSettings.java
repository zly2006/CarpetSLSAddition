package com.github.zly2006.carpetslsaddition;

import carpet.CarpetSettings;
import carpet.api.settings.CarpetRule;
import carpet.api.settings.Rule;
import carpet.api.settings.RuleCategory;

import static com.github.zly2006.carpetslsaddition.ServerMain.*;

public class SLSCarpetSettings {
    @Rule(categories = {CARPET_ID})
    public static boolean obtainableReinforcedDeepSlate = false;
    @Rule(categories = {CARPET_ID})
    public static int skipTicksForJoblessVillager = 0;
    @Rule(categories = {CARPET_ID}, options = {"-1", "24", "50"})
    public static int maxVillagersInABlock = -1;
    @Rule(categories = {CARPET_ID, RuleCategory.CREATIVE})
    public static boolean creativeNoInfinitePickup = false;
    @Rule(categories = {CARPET_ID})
    public static boolean autoUrl = false;
    @Rule(categories = {CARPET_ID})
    public static boolean noBatSpawning = false;
}
