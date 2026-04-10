package com.rustysnail.terrafirmathings.data;

import java.util.Arrays;
import com.rustysnail.terrafirmathings.common.condition.TFCThingsConfigCondition;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.conditions.OrCondition;

public final class RecipeConditions
{
    public static final String SNOW_SHOES = "enableSnowShoes";
    public static final String HIKING_BOOTS = "enableHikingBoots";
    public static final String BEAR_TRAP = "enableBearTrap";
    public static final String SNARE = "enableSnare";
    public static final String FISHING_NET = "enableFishingNet";
    public static final String ROPE_BRIDGE = "enableRopeBridge";
    public static final String ROPE_LADDER = "enableRopeLadder";
    public static final String ROPE_JAVELIN = "enableRopeJavelin";
    public static final String HOOK_JAVELINS = "enableHookJavelins";
    public static final String GEM_DISPLAYS = "enableGemDisplays";
    public static final String PROSPECTORS_HAMMERS = "enableProspectorsHammers";
    public static final String SLINGS = "enableSlings";
    public static final String WHETSTONES = "enableWhetstones";
    public static final String CROWNS = "enableCrowns";

    public static TFCThingsConfigCondition flag(String flag)
    {
        return new TFCThingsConfigCondition(flag);
    }

    public static ICondition anyFlag(String... flags)
    {
        return new OrCondition(Arrays.stream(flags).<ICondition>map(RecipeConditions::flag).toList());
    }

    private RecipeConditions() {}
}
