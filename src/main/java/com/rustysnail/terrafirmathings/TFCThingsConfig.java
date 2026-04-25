package com.rustysnail.terrafirmathings;

import net.neoforged.neoforge.common.ModConfigSpec;

// TODO: Tweak default values, clean up text entries

public class TFCThingsConfig
{

    public static final ModConfigSpec SPEC;
    public static final Items ITEMS;

    static
    {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        ITEMS = new Items(builder);
        SPEC = builder.build();
    }

    public static class Items
    {
        public final SnowShoes SNOW_SHOES;
        public final HikingBoots HIKING_BOOTS;
        public final Crampons CRAMPONS;
        public final BearTrap BEAR_TRAP;
        public final Snare SNARE;
        public final RopeBridge ROPE_BRIDGE;
        public final RopeLadder ROPE_LADDER;
        public final RopeJavelin ROPE_JAVELIN;
        public final HookJavelin HOOK_JAVELIN;
        public final Sling SLING;
        public final Whetstone WHETSTONE;
        public final GrainPile GRAIN_PILE;
        public final MasterList MASTER_LIST;

        Items(ModConfigSpec.Builder builder)
        {
            builder.comment("Item Configuration").push("items");

            SNOW_SHOES = new SnowShoes(builder);
            HIKING_BOOTS = new HikingBoots(builder);
            CRAMPONS = new Crampons(builder);
            BEAR_TRAP = new BearTrap(builder);
            SNARE = new Snare(builder);
            ROPE_BRIDGE = new RopeBridge(builder);
            ROPE_LADDER = new RopeLadder(builder);
            ROPE_JAVELIN = new RopeJavelin(builder);
            HOOK_JAVELIN = new HookJavelin(builder);
            SLING = new Sling(builder);
            WHETSTONE = new Whetstone(builder);
            GRAIN_PILE = new GrainPile(builder);
            MASTER_LIST = new MasterList(builder);

            builder.pop();
        }

        public static class SnowShoes
        {
            public final ModConfigSpec.IntValue damageDistance;
            public final ModConfigSpec.DoubleValue shoePower;

            SnowShoes(ModConfigSpec.Builder builder)
            {
                builder.comment("Snow Shoes Settings").push("snowShoes");

                damageDistance = builder
                    .comment("The distance in centimeters walked through snow required to apply one damage to the shoes.",
                        "100 cm = 1 block. Setting to 0 will disable the snow shoes from taking damaged.")
                    .defineInRange("damageDistance", 500, 0, Integer.MAX_VALUE);

                shoePower = builder
                    .comment("The percentage of the TFC slowdown effect that the snow shoes will negate.",
                        "1 = remove all the slowdown effects of snow.",
                        "0 = snow shoes have no effect on movement speed.")
                    .defineInRange("shoePower", 1.0D, 0.0D, 1.0D);

                builder.pop();
            }
        }

        public static class HikingBoots
        {
            public final ModConfigSpec.IntValue damageDistance;
            public final ModConfigSpec.DoubleValue bootPower;

            HikingBoots(ModConfigSpec.Builder builder)
            {
                builder.comment("Hiking Boots Settings").push("hikingBoots");

                damageDistance = builder
                    .comment("The distance in centimeters walked through plants required to apply one damage to the boots.",
                        "100 cm = 1 block. Setting to 0 will disable the hiking boots from taking damaged.")
                    .defineInRange("damageDistance", 500, 0, Integer.MAX_VALUE);

                bootPower = builder
                    .comment("The percentage of the TFC plant slowdown effect that the hiking boots will negate.",
                        "1 = remove all the slowdown effects of snow.",
                        "0 = snow shoes have no effect on movement speed.")
                    .defineInRange("bootPower", 1.0D, 0.0D, 1.0D);

                builder.pop();
            }
        }

        public static class Crampons
        {
            public final ModConfigSpec.IntValue damageDistance;

            Crampons(ModConfigSpec.Builder builder)
            {
                builder.comment("Crampon Settings").push("crampons");

                damageDistance = builder
                    .comment("The distance in centimeters walked on ice required to apply one damage to the crampons.",
                        "100 cm = 1 block. Setting to 0 will disable the crampons from taking damaged.")
                    .defineInRange("damageDistance", 500, 0, Integer.MAX_VALUE);

                builder.pop();
            }
        }

        public static class BearTrap
        {
            public final ModConfigSpec.DoubleValue breakChance;
            public final ModConfigSpec.IntValue breakoutChance;
            public final ModConfigSpec.IntValue debuffDuration;
            public final ModConfigSpec.DoubleValue healthCut;
            public final ModConfigSpec.DoubleValue fixedDamage;

            BearTrap(ModConfigSpec.Builder builder)
            {
                builder.comment("Bear Trap Settings").push("bearTrap");

                breakChance = builder
                    .comment("Percent chance for a bear trap to break when harvested after being activated.")
                    .defineInRange("breakChance", 0.1D, 0.0D, 1.0D);

                breakoutChance = builder
                    .comment("The percentage chance (0-100) for a predator to break out of a bear trap per in-game day (24000 ticks).",
                        "0 = no breakouts. 100 = guaranteed breakout within one in-game day (~20 minutes real-time).",
                        "Default 25 = approximately 25% chance per day, expected ~4 in-game days to break out.")
                    .defineInRange("breakoutChance", 25, 0, 100);

                debuffDuration = builder
                    .comment("The duration of the debuffs applied by the bear trap in ticks.",
                        "Set to 0 to disable the debuffs.")
                    .defineInRange("debuffDuration", 1000, 0, Integer.MAX_VALUE);

                healthCut = builder
                    .comment("The fraction of an entity's health that is dealt as damage when stepping in a trap.",
                        "E.g. 3 = 1/3 current health dealt as damage.",
                        "Less than 1 will deal more damage than current health",
                        "Set to 0 to do no damage.")
                    .defineInRange("healthCut", 3.0D, 0.0D, 20.0D);

                fixedDamage = builder
                    .comment("The amount of fixed damage points dealt by a bear trap.",
                        "This will override the fractional health cut setting if set to a value greater than 0.")
                    .defineInRange("fixedDamage", 0.0D, 0.0D, 1000000.0D);

                builder.pop();
            }
        }

        public static class Snare
        {
            public final ModConfigSpec.IntValue checkInterval;
            public final ModConfigSpec.DoubleValue baseCatchChance;
            public final ModConfigSpec.BooleanValue holdNoAi;
            public final ModConfigSpec.BooleanValue preventDamage;

            Snare(ModConfigSpec.Builder builder)
            {
                builder.comment("Snare Settings").push("snare");

                checkInterval = builder
                    .comment("The interval in ticks between catch attempts when the snare is baited.",
                        "Lower values check more frequently. 1200 ticks = 1 minute.")
                    .defineInRange("checkInterval", 2400, 20, 72000);

                baseCatchChance = builder
                    .comment("The base chance (0-1) to catch an animal on each check.",
                        "The actual chance increases slightly over time while baited.")
                    .defineInRange("baseCatchChance", 0.05D, 0.0D, 1.0D);

                holdNoAi = builder
                    .comment("If true, disables AI while an animal is captured so it cannot escape.",
                        "If false, the snare will still pin the entity to the block each tick, but its AI remains enabled.")
                    .define("holdNoAi", false);

                preventDamage = builder
                    .comment("If true, captured animals are made invulnerable while trapped to prevent accidental kills.")
                    .define("preventDamage", false);

                builder.pop();
            }
        }

        public static class RopeBridge
        {
            public final ModConfigSpec.IntValue maxLength;
            public final ModConfigSpec.DoubleValue maxPlacementAngle;

            RopeBridge(ModConfigSpec.Builder builder)
            {
                builder.comment("Rope Bridge Settings").push("ropeBridge");

                maxLength = builder
                    .comment("The maximum length of a rope bridge that can be thrown.")
                    .defineInRange("maxLength", 100, 1, 256);

                maxPlacementAngle = builder
                    .comment("Maximum allowed angle (in degrees) between the throw direction and the bridge axis when placing. Lower values require a more precise straight throw. 0 = perfectly straight only, 44 = any angle accepted.")
                    .defineInRange("maxPlacementAngle", 4.0, 0.0, 30.0);

                builder.pop();
            }
        }

        public static class RopeLadder
        {
            public final ModConfigSpec.IntValue maxLength;

            RopeLadder(ModConfigSpec.Builder builder)
            {
                builder.comment("Rope Ladder Settings").push("ropeLadder");

                maxLength = builder
                    .comment("The maximum length of a rope ladder that can be deployed.")
                    .defineInRange("maxLength", 64, 1, 128);

                builder.pop();
            }
        }

        public static class RopeJavelin
        {
            public final ModConfigSpec.IntValue ropeLength;
            public final ModConfigSpec.DoubleValue pullStrength;

            RopeJavelin(ModConfigSpec.Builder builder)
            {
                builder.comment("Rope Javelin Settings").push("ropeJavelin");

                ropeLength = builder
                    .comment("The maximum length of rope attached to the javelin.")
                    .defineInRange("ropeLength", 32, 1, 64);

                pullStrength = builder
                    .comment("The strength of the pull when reeling in with the rope javelin.",
                        "Higher values pull the entity faster.")
                    .defineInRange("pullStrength", 1.0D, 0.1D, 5.0D);

                builder.pop();
            }
        }

        public static class HookJavelin
        {
            public final ModConfigSpec.IntValue maxRopeLength;
            public final ModConfigSpec.DoubleValue retractAmount;

            HookJavelin(ModConfigSpec.Builder builder)
            {
                builder.comment("Hook Javelin Settings").push("hookJavelin");

                maxRopeLength = builder
                    .comment("The maximum length of rope for the hook javelin grapple.")
                    .defineInRange("maxRopeLength", 32, 10, 64);

                retractAmount = builder
                    .comment("The amount of rope retracted per right-click while airborne.",
                        "Higher values let the player climb faster.")
                    .defineInRange("retractAmount", 0.5D, 0.1D, 2.0D);

                builder.pop();
            }
        }

        public static class Sling
        {
            public final ModConfigSpec.DoubleValue predatorMultiplier;
            public final ModConfigSpec.IntValue maxPower;
            public final ModConfigSpec.IntValue chargeSpeed;

            Sling(ModConfigSpec.Builder builder)
            {
                builder.comment("Sling Settings").push("sling");

                predatorMultiplier = builder
                    .comment("Damage multiplier when hitting predators or skeletons with the sling.")
                    .defineInRange("predatorMultiplier", 2.0D, 1.0D, 10.0D);

                maxPower = builder
                    .comment("The maximum power a fully charged sling can reach.")
                    .defineInRange("maxPower", 12, 1, 100);

                chargeSpeed = builder
                    .comment("The number of ticks to reach max power. Lower = faster charge.")
                    .defineInRange("chargeSpeed", 70, 1, 200);

                builder.pop();
            }
        }

        public static class Whetstone
        {
            public final ModConfigSpec.IntValue chargesPerWhetstone;
            public final ModConfigSpec.IntValue chargesPerHoningSteel;
            public final ModConfigSpec.IntValue chargesPerDiamondHoningSteel;
            public final ModConfigSpec.IntValue ticksPerWhetstoneUse;
            public final ModConfigSpec.IntValue ticksPerHoningSteelUse;
            public final ModConfigSpec.IntValue ticksPerDiamondHoningSteelUse;
            public final ModConfigSpec.IntValue maxChargesWhetstone;
            public final ModConfigSpec.IntValue maxChargesHoningSteel;
            public final ModConfigSpec.IntValue maxChargesDiamondHoningSteel;
            public final ModConfigSpec.DoubleValue tier1WeaponBonus;
            public final ModConfigSpec.DoubleValue tier2WeaponBonus;
            public final ModConfigSpec.DoubleValue tier3WeaponBonus;
            public final ModConfigSpec.DoubleValue tier1MiningBonus;
            public final ModConfigSpec.DoubleValue tier2MiningBonus;
            public final ModConfigSpec.DoubleValue tier3MiningBonus;
            public final ModConfigSpec.DoubleValue grindstoneMinSpeed;
            public final ModConfigSpec.DoubleValue grindstoneSpeedMultiplier;

            Whetstone(ModConfigSpec.Builder builder)
            {
                builder.comment("Whetstone / Honing Steel Settings").push("whetstone");

                chargesPerWhetstone = builder
                    .comment("Sharpness charges added per whetstone use.")
                    .defineInRange("chargesPerWhetstone", 1, 1, 256);

                chargesPerHoningSteel = builder
                    .comment("Sharpness charges added per honing steel use.")
                    .defineInRange("chargesPerHoningSteel", 2, 1, 256);

                chargesPerDiamondHoningSteel = builder
                    .comment("Sharpness charges added per diamond honing steel use.")
                    .defineInRange("chargesPerDiamondHoningSteel", 4, 1, 256);

                ticksPerWhetstoneUse = builder
                    .comment("Use time in ticks for whetstone sharpening.")
                    .defineInRange("ticksPerWhetstoneUse", 40, 0, 72000);

                ticksPerHoningSteelUse = builder
                    .comment("Use time in ticks for honing steel sharpening.")
                    .defineInRange("ticksPerHoningSteelUse", 30, 0, 72000);

                ticksPerDiamondHoningSteelUse = builder
                    .comment("Use time in ticks for diamond honing steel sharpening.")
                    .defineInRange("ticksPerDiamondHoningSteelUse", 20, 0, 72000);

                maxChargesWhetstone = builder
                    .comment("Maximum sharpness charges a tool can hold from whetstone use.")
                    .defineInRange("maxChargesWhetstone", 50, 1, 1024);

                maxChargesHoningSteel = builder
                    .comment("Maximum sharpness charges a tool can hold from honing steel use.")
                    .defineInRange("maxChargesHoningSteel", 100, 1, 1024);

                maxChargesDiamondHoningSteel = builder
                    .comment("Maximum sharpness charges a tool can hold from diamond honing steel use.")
                    .defineInRange("maxChargesDiamondHoningSteel", 200, 1, 1024);

                tier1WeaponBonus = builder
                    .comment("Flat attack damage bonus (hearts) per hit at Tier 1 sharpness (1 to maxChargesWhetstone charges).",
                        "Comparable in feel to Minecraft Sharpness I.")
                    .defineInRange("tier1WeaponBonus", 0.5D, 0.0D, 20.0D);

                tier2WeaponBonus = builder
                    .comment("Flat attack damage bonus (hearts) per hit at Tier 2 sharpness (maxChargesWhetstone+1 to maxChargesHoningSteel charges).",
                        "Comparable in feel to Minecraft Sharpness III.")
                    .defineInRange("tier2WeaponBonus", 1D, 0.0D, 20.0D);

                tier3WeaponBonus = builder
                    .comment("Flat attack damage bonus (hearts) per hit at Tier 3 sharpness (maxChargesHoningSteel+1 to maxChargesDiamondHoningSteel charges).",
                        "Comparable in feel to Minecraft Sharpness V.")
                    .defineInRange("tier3WeaponBonus", 1.5D, 0.0D, 20.0D);

                tier1MiningBonus = builder
                    .comment("Mining speed bonus added when the tool has Tier 1 sharpness (1 to maxChargesWhetstone charges).",
                        "Default matches vanilla Efficiency I (+2).")
                    .defineInRange("tier1MiningBonus", 2.0D, 0.0D, 100.0D);

                tier2MiningBonus = builder
                    .comment("Mining speed bonus added when the tool has Tier 2 sharpness (maxChargesWhetstone+1 to maxChargesHoningSteel charges).",
                        "Default matches vanilla Efficiency III (+10).")
                    .defineInRange("tier2MiningBonus", 10.0D, 0.0D, 100.0D);

                tier3MiningBonus = builder
                    .comment("Mining speed bonus added when the tool has Tier 3 sharpness (maxChargesHoningSteel+1 to maxChargesDiamondHoningSteel charges).",
                        "Default matches vanilla Efficiency V (+26).")
                    .defineInRange("tier3MiningBonus", 26.0D, 0.0D, 100.0D);

                grindstoneMinSpeed = builder
                    .comment("Minimum rotation speed (radians/tick) required for a grindstone to operate.",
                        "TFC reference speeds: windmill ~0.016-0.039, water wheel ~0.079, creative motor ~0.157.")
                    .defineInRange("grindstoneMinSpeed", 0.039D, 0.001D, 0.1D);

                grindstoneSpeedMultiplier = builder
                    .comment("Multiplier converting axle speed (rad/tick) to sharpening progress per tick.",
                        "Progress per tick = speed * multiplier. Operation completes when progress >= ticksPerOperation.",
                        "Default of 12.0 calibrates so a water wheel (~0.079 rad/tick) adds ~1 progress/tick.")
                    .defineInRange("grindstoneSpeedMultiplier", 12.0D, 0.1D, 1000.0D);

                builder.pop();
            }
        }

        public static class GrainPile
        {
            public final ModConfigSpec.IntValue maxCapacity;
            public final ModConfigSpec.DoubleValue decayModifier;
            public final ModConfigSpec.DoubleValue shelterDecayModifier;

            GrainPile(ModConfigSpec.Builder builder)
            {
                builder.comment("Grain Pile Settings").push("grainPile");

                maxCapacity = builder
                    .comment("Maximum number of grain items a single pile block can hold.")
                    .defineInRange("maxCapacity", 128, 1, 1024);

                decayModifier = builder
                    .comment("Decay rate modifier for grain in an unsheltered pile.",
                        "Values > 1 accelerate decay; values < 1 slow it down.",
                        "TFC default for no trait = 1.0. A value of 0.5 doubles shelf life.")
                    .defineInRange("decayModifier", 0.8, 0.01, 10.0);

                shelterDecayModifier = builder
                    .comment("Decay rate modifier for grain in a sheltered pile (sky blocked).",
                        "Should generally be lower than decayModifier.")
                    .defineInRange("shelterDecayModifier", 0.5, 0.01, 10.0);

                builder.pop();
            }
        }

        public static class MasterList
        {
            public final ModConfigSpec.BooleanValue enableSnowShoes;
            public final ModConfigSpec.BooleanValue enableHikingBoots;
            public final ModConfigSpec.BooleanValue enableCrampons;
            public final ModConfigSpec.BooleanValue enableBearTrap;
            public final ModConfigSpec.BooleanValue enableSnare;
            public final ModConfigSpec.BooleanValue enableFishingNet;
            public final ModConfigSpec.BooleanValue enableRopeBridge;
            public final ModConfigSpec.BooleanValue enableRopeLadder;
            public final ModConfigSpec.BooleanValue enableRopeJavelin;
            public final ModConfigSpec.BooleanValue enableHookJavelins;
            public final ModConfigSpec.BooleanValue enableGemDisplays;
            public final ModConfigSpec.BooleanValue enableProspectorsHammers;
            public final ModConfigSpec.BooleanValue enableSlings;
            public final ModConfigSpec.BooleanValue enableWhetstones;
            public final ModConfigSpec.BooleanValue enableCrowns;
            public final ModConfigSpec.BooleanValue enableGrainPiles;
            public final ModConfigSpec.BooleanValue enableTagFixes;

            MasterList(ModConfigSpec.Builder builder)
            {
                builder.comment("Enable/Disable Items").push("masterList");

                enableSnowShoes = builder.define("enableSnowShoes", true);
                enableHikingBoots = builder.define("enableHikingBoots", true);
                enableCrampons = builder.define("enableCrampons", true);
                enableBearTrap = builder.define("enableBearTrap", true);
                enableSnare = builder.define("enableSnare", true);
                enableFishingNet = builder.define("enableFishingNet", true);
                enableRopeBridge = builder.define("enableRopeBridge", true);
                enableRopeLadder = builder.define("enableRopeLadder", true);
                enableRopeJavelin = builder.define("enableRopeJavelin", true);
                enableHookJavelins = builder.define("enableHookJavelins", true);
                enableGemDisplays = builder.define("enableGemDisplays", true);
                enableProspectorsHammers = builder.define("enableProspectorsHammers", true);
                enableSlings = builder.define("enableSlings", true);
                enableWhetstones = builder.define("enableWhetstones", true);
                enableCrowns = builder.define("enableCrowns", true);
                enableGrainPiles = builder.define("enableGrainPiles", true);
                enableTagFixes = builder.comment("Adds missing common tags to TFC items and blocks").define("enableTagFixes", true);

                builder.pop();
            }
        }
    }
}
