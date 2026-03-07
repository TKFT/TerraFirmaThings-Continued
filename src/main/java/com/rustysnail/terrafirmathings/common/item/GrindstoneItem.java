package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import net.minecraft.world.item.Item;

public class GrindstoneItem extends Item
{
    private final Tier tier;

    public GrindstoneItem(Tier tier, Properties properties)
    {
        super(properties);
        this.tier = tier;
    }

    public Tier getTier()
    {
        return tier;
    }

    public enum Tier
    {
        QUARTZ,
        STEEL,
        DIAMOND;

        public int getChargesPerOperation()
        {
            return switch (this)
            {
                case QUARTZ -> TFCThingsConfig.ITEMS.WHETSTONE.chargesPerWhetstone.get();
                case STEEL -> TFCThingsConfig.ITEMS.WHETSTONE.chargesPerHoningSteel.get();
                case DIAMOND -> TFCThingsConfig.ITEMS.WHETSTONE.chargesPerDiamondHoningSteel.get();
            };
        }

        public int getTicksPerOperation()
        {
            return switch (this)
            {
                case QUARTZ -> TFCThingsConfig.ITEMS.WHETSTONE.ticksPerWhetstoneUse.get();
                case STEEL -> TFCThingsConfig.ITEMS.WHETSTONE.ticksPerHoningSteelUse.get();
                case DIAMOND -> TFCThingsConfig.ITEMS.WHETSTONE.ticksPerDiamondHoningSteelUse.get();
            };
        }

        public int getMaxToolCharges()
        {
            return switch (this)
            {
                case QUARTZ -> TFCThingsConfig.ITEMS.WHETSTONE.maxChargesWhetstone.get();
                case STEEL -> TFCThingsConfig.ITEMS.WHETSTONE.maxChargesHoningSteel.get();
                case DIAMOND -> TFCThingsConfig.ITEMS.WHETSTONE.maxChargesDiamondHoningSteel.get();
            };
        }
    }
}
