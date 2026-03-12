package com.rustysnail.terrafirmathings.common.item;

public class WhetstoneItem extends AbstractSharpenerItem
{
    public WhetstoneItem(Properties properties)
    {
        super(properties);
    }

    @Override
    protected GrindstoneItem.Tier getSharpenerTier()
    {
        return GrindstoneItem.Tier.QUARTZ;
    }
}
