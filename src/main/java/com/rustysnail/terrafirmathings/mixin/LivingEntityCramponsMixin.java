package com.rustysnail.terrafirmathings.mixin;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.item.CramponsItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import javax.annotation.Nullable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityCramponsMixin
{
    @Unique
    private LivingEntity tfcthings$self()
    {
        return (LivingEntity) (Object) this;
    }

    @Redirect(
        method = "travel(Lnet/minecraft/world/phys/Vec3;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/block/state/BlockState;getFriction(Lnet/minecraft/world/level/LevelReader;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/Entity;)F"
        )
    )
    private float tfcthings$cramponFrictionOverride(BlockState blockState, LevelReader level, BlockPos pos, @Nullable net.minecraft.world.entity.Entity entity)
    {
        float original = blockState.getFriction(level, pos, entity);

        LivingEntity self = tfcthings$self();
        if (!(self instanceof Player player))
        {
            return original;
        }

        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableCrampons.get())
        {
            return original;
        }

        ItemStack feetItem = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(feetItem.getItem() instanceof CramponsItem))
        {
            return original;
        }

        if (blockState.is(TFCThingsTags.Blocks.CRAMPONS_NEGATE_SLIP))
        {
            return 0.6F;
        }

        return original;
    }
}
