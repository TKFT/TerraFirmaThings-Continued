package com.rustysnail.terrafirmathings.common.item;

import net.minecraft.world.item.Item;

public class SlingAmmoItem extends Item
{

    private final AmmoType ammoType;

    public SlingAmmoItem(AmmoType ammoType, Properties properties)
    {
        super(properties);
        this.ammoType = ammoType;
    }

    public AmmoType getAmmoType()
    {
        return ammoType;
    }

    public enum AmmoType
    {
        HEAVY(5, 1.0f, 0.03f, false, 0),
        SCATTER(2, 0.75f, 0.03f, false, 4),
        LIGHT(3, 1.2f, 0.02f, false, 0),
        FIRE(2, 1.0f, 0.03f, true, 0),
        STONE(1, 1.0f, 0.03f, false, 0);

        private final int powerBonus;
        private final float velocityMultiplier;
        private final float gravity;
        private final boolean setsFire;
        private final int scatterCount;

        AmmoType(int powerBonus, float velocityMultiplier, float gravity, boolean setsFire, int scatterCount)
        {
            this.powerBonus = powerBonus;
            this.velocityMultiplier = velocityMultiplier;
            this.gravity = gravity;
            this.setsFire = setsFire;
            this.scatterCount = scatterCount;
        }

        public int getPowerBonus() {return powerBonus;}

        public float getVelocityMultiplier() {return velocityMultiplier;}

        public float getGravity() {return gravity;}

        public boolean setsFire() {return setsFire;}

        public int getScatterCount() {return scatterCount;}
    }
}
