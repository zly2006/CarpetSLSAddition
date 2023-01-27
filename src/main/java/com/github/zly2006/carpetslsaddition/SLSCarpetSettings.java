package com.github.zly2006.carpetslsaddition;

import carpet.settings.Rule;
import carpet.settings.RuleCategory;

import static com.github.zly2006.carpetslsaddition.ServerMain.CARPET_ID;

public class SLSCarpetSettings {
    @Rule(category = {CARPET_ID}, desc = "dont open")
    public static boolean obtainableReinforcedDeepSlate = false;
    @Rule(category = {CARPET_ID}, desc = "useless")
    public static int skipTicksForJoblessVillager = 0;
    @Rule(category = {CARPET_ID}, options = {"-1", "24", "50"}, desc = "not implemented")
    public static int maxVillagersInABlock = -1;
    @Rule(category = {CARPET_ID, RuleCategory.CREATIVE}, desc = "as its name")
    public static boolean creativeNoInfinitePickup = false;
    @Rule(category = {CARPET_ID}, desc = "not implemented")
    public static boolean autoUrl = false;
    @Rule(category = {CARPET_ID}, desc = "as its name")
    public static boolean noBatSpawning = false;
}
