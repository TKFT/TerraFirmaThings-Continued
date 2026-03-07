package com.rustysnail.terrafirmathings.mixin;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.item.HikingBootsItem;
import com.rustysnail.terrafirmathings.common.item.SnowShoesItem;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntitySpeedMixin
{

    @Unique
    private Entity tfcthings$self()
    {
        return (Entity) (Object) this;
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("RETURN"), cancellable = true)
    private void tfcthings$modifySpeedForFootwear(CallbackInfoReturnable<Float> cir)
    {
        Entity entity = tfcthings$self();

        if (!(entity instanceof Player player))
        {
            return;
        }

        float currentFactor = cir.getReturnValue();
        if (currentFactor >= 1.0F)
        {
            return;
        }

        BlockState blockState = entity.level().getBlockState(entity.blockPosition());
        ItemStack feetItem = player.getItemBySlot(EquipmentSlot.FEET);

        if (blockState.is(TFCThingsTags.Blocks.SNOW_SHOES_NEGATE_SLOW) && feetItem.getItem() instanceof SnowShoesItem)
        {
            double shoePower = TFCThingsConfig.ITEMS.SNOW_SHOES.shoePower.get();
            float newFactor = tfcthings$calculateNewSpeedFactor(currentFactor, shoePower);
            cir.setReturnValue(newFactor);
            return;
        }

        if (blockState.is(TFCThingsTags.Blocks.HIKING_BOOTS_NEGATE_SLOW) && feetItem.getItem() instanceof HikingBootsItem)
        {
            double bootPower = TFCThingsConfig.ITEMS.HIKING_BOOTS.bootPower.get();
            float newFactor = tfcthings$calculateNewSpeedFactor(currentFactor, bootPower);
            cir.setReturnValue(newFactor);
        }
    }

    @Unique
    private float tfcthings$calculateNewSpeedFactor(float currentFactor, double power)
    {
        if (power >= 1.0)
        {
            return 1.0F;
        }
        else if (power > 0)
        {
            float difference = 1.0F - currentFactor;
            float reducedDifference = difference * (1.0F - (float) power);
            return 1.0F - reducedDifference;
        }
        return currentFactor;
    }
}
