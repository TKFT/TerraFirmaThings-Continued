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
        HEAVY(5, 1.0f, 0.03f, false, 0, true),
        SCATTER(2, 0.75f, 0.03f, false, 4, false),
        LIGHT(3, 1.2f, 0.02f, false, 0, true),
        FIRE(2, 1.0f, 0.03f, true, 0, false),
        STONE(1, 1.0f, 0.03f, false, 0, true);

        private final int powerBonus;
        private final float velocityMultiplier;
        private final float gravity;
        private final boolean setsFire;
        private final int scatterCount;

        private final boolean recoverable;

        AmmoType(int powerBonus, float velocityMultiplier, float gravity, boolean setsFire, int scatterCount, boolean recoverable)
        {
            this.powerBonus = powerBonus;
            this.velocityMultiplier = velocityMultiplier;
            this.gravity = gravity;
            this.setsFire = setsFire;
            this.scatterCount = scatterCount;
            this.recoverable = recoverable;
        }

        public int getPowerBonus() {return powerBonus;}

        public float getVelocityMultiplier() {return velocityMultiplier;}

        public float getGravity() {return gravity;}

        public boolean setsFire() {return setsFire;}

        public int getScatterCount() {return scatterCount;}

        public boolean isRecoverable() {return recoverable;}
    }
}
