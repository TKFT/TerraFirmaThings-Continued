package com.rustysnail.terrafirmathings.common.item;

import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class HorseshoeItem extends Item
{
    public static final String NBT_HORSESHOE_ID     = "TFCThings.HorseshoeID";
    public static final String NBT_HORSESHOE_DAMAGE = "TFCThings.HorseshoeDamage";

    public static final ResourceLocation SPEED_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "horseshoe_speed");
    public static final ResourceLocation TERRAIN_MODIFIER_ID =
        ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "horseshoe_terrain");

    private final double baseSpeedBonus;

    public HorseshoeItem(double baseSpeedBonus, Properties properties)
    {
        super(properties);
        this.baseSpeedBonus = baseSpeedBonus;
    }

    public double getBaseSpeedBonus()
    {
        return baseSpeedBonus;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableHorseshoes.get())
            return InteractionResult.PASS;

        if (!(target instanceof AbstractHorse horse))
            return InteractionResult.PASS;

        if (target.level().isClientSide())
            return InteractionResult.SUCCESS;

        CompoundTag data = horse.getPersistentData();
        if (data.contains(NBT_HORSESHOE_ID))
        {
            player.displayClientMessage(
                Component.translatable("tfcthings.tooltip.horseshoe.already_equipped"), true);
            return InteractionResult.FAIL;
        }

        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        data.putString(NBT_HORSESHOE_ID, id);
        data.putInt(NBT_HORSESHOE_DAMAGE, stack.getDamageValue());

        if (!player.getAbilities().instabuild)
            stack.shrink(1);

        double bonus = baseSpeedBonus * TFCThingsConfig.ITEMS.HORSESHOE.speedBonusMultiplier.get();
        applySpeedModifier(horse, bonus);

        horse.playSound(SoundEvents.HORSE_ARMOR, 1.0F, 1.0F);
        player.displayClientMessage(
            Component.translatable("tfcthings.tooltip.horseshoe.equipped"), true);

        return InteractionResult.CONSUME;
    }

    public static void applySpeedModifier(AbstractHorse horse, double bonus)
    {
        AttributeInstance attr = horse.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null)
        {
            attr.removeModifier(SPEED_MODIFIER_ID);
            attr.addPermanentModifier(
                new AttributeModifier(SPEED_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public static void removeSpeedModifier(AbstractHorse horse)
    {
        AttributeInstance attr = horse.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null)
        {
            attr.removeModifier(SPEED_MODIFIER_ID);
            attr.removeModifier(TERRAIN_MODIFIER_ID);
        }
    }

    public static void setTerrainModifier(AbstractHorse horse, double bonus, boolean active)
    {
        AttributeInstance attr = horse.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr == null) return;
        attr.removeModifier(TERRAIN_MODIFIER_ID);
        if (active && bonus > 0)
        {
            attr.addTransientModifier(
                new AttributeModifier(TERRAIN_MODIFIER_ID, bonus, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public static boolean damageHorseshoe(AbstractHorse horse, int amount)
    {
        CompoundTag data = horse.getPersistentData();
        if (!data.contains(NBT_HORSESHOE_ID))
            return false;

        String id = data.getString(NBT_HORSESHOE_ID);
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
        if (!(item instanceof HorseshoeItem))
        {
            data.remove(NBT_HORSESHOE_ID);
            data.remove(NBT_HORSESHOE_DAMAGE);
            removeSpeedModifier(horse);
            return true;
        }

        int damage = data.getInt(NBT_HORSESHOE_DAMAGE) + amount;
        int maxDamage = new ItemStack(item).getMaxDamage();

        if (damage >= maxDamage)
        {
            data.remove(NBT_HORSESHOE_ID);
            data.remove(NBT_HORSESHOE_DAMAGE);
            removeSpeedModifier(horse);
            horse.playSound(SoundEvents.ITEM_BREAK, 0.8F, 0.8F + horse.level().random.nextFloat() * 0.4F);
            return true;
        }

        data.putInt(NBT_HORSESHOE_DAMAGE, damage);
        return false;
    }

    public static ItemStack removeAndReturnHorseshoe(AbstractHorse horse)
    {
        CompoundTag data = horse.getPersistentData();
        if (!data.contains(NBT_HORSESHOE_ID))
            return ItemStack.EMPTY;

        String id = data.getString(NBT_HORSESHOE_ID);
        int damage = data.getInt(NBT_HORSESHOE_DAMAGE);

        data.remove(NBT_HORSESHOE_ID);
        data.remove(NBT_HORSESHOE_DAMAGE);
        removeSpeedModifier(horse);

        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(id));
        if (!(item instanceof HorseshoeItem))
            return ItemStack.EMPTY;

        ItemStack result = new ItemStack(item, 1);
        result.setDamageValue(damage);
        return result;
    }
}
