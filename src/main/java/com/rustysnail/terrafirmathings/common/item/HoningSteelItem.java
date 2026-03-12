package com.rustysnail.terrafirmathings.common.item;

public class HoningSteelItem extends AbstractSharpenerItem
{
    private final boolean diamond;

    public HoningSteelItem(boolean diamond, Properties properties)
    {
        super(properties);
        this.diamond = diamond;
    }

    @Override
    protected GrindstoneItem.Tier getSharpenerTier()
    {
        return diamond ? GrindstoneItem.Tier.DIAMOND : GrindstoneItem.Tier.STEEL;
    }

    @Override
    protected float getSoundPitch()
    {
        return diamond ? 1.5f : 1.2f;
    }
}
