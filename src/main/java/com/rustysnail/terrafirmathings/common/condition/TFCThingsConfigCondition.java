package com.rustysnail.terrafirmathings.common.condition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import net.neoforged.neoforge.common.conditions.ICondition;
import org.jetbrains.annotations.NotNull;

public record TFCThingsConfigCondition(String flag) implements ICondition
{
    public static final MapCodec<TFCThingsConfigCondition> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
        Codec.STRING.fieldOf("flag").forGetter(TFCThingsConfigCondition::flag)
    ).apply(inst, TFCThingsConfigCondition::new));

    @Override
    public boolean test(ICondition.@NotNull IContext context)
    {
        TFCThingsConfig.Items.MasterList m = TFCThingsConfig.ITEMS.MASTER_LIST;
        return switch (flag)
        {
            case "enableSnowShoes" -> m.enableSnowShoes.get();
            case "enableHikingBoots" -> m.enableHikingBoots.get();
            case "enableBearTrap" -> m.enableBearTrap.get();
            case "enableSnare" -> m.enableSnare.get();
            case "enableFishingNet" -> m.enableFishingNet.get();
            case "enableRopeBridge" -> m.enableRopeBridge.get();
            case "enableRopeLadder" -> m.enableRopeLadder.get();
            case "enableRopeJavelin" -> m.enableRopeJavelin.get();
            case "enableHookJavelins" -> m.enableHookJavelins.get();
            case "enableGemDisplays" -> m.enableGemDisplays.get();
            case "enableProspectorsHammers" -> m.enableProspectorsHammers.get();
            case "enableSlings" -> m.enableSlings.get();
            case "enableWhetstones" -> m.enableWhetstones.get();
            case "enableCrowns" -> m.enableCrowns.get();
            default -> false;
        };
    }

    @Override
    public @NotNull MapCodec<? extends ICondition> codec()
    {
        return CODEC;
    }
}
