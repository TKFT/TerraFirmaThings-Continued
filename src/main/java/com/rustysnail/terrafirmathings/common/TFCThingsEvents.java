package com.rustysnail.terrafirmathings.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.item.CramponsItem;
import com.rustysnail.terrafirmathings.common.item.HikingBootsItem;
import com.rustysnail.terrafirmathings.common.item.SnowShoesItem;
import com.rustysnail.terrafirmathings.common.item.WhetstoneItem;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

@EventBusSubscriber(modid = TerraFirmaThings.MOD_ID)
public final class TFCThingsEvents
{
    private static final Map<UUID, Vec3> lastPlayerPositions = new HashMap<>();

    @SubscribeEvent
    public static void onPlayerTickPre(PlayerTickEvent.Pre event)
    {
        Player player = event.getEntity();
        Level level = player.level();

        if (player.isPassenger() || player.getAbilities().instabuild)
        {
            return;
        }

        ItemStack feetItem = player.getItemBySlot(EquipmentSlot.FEET);
        if (!(feetItem.getItem() instanceof CramponsItem))
        {
            return;
        }

        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableCrampons.get())
        {
            return;
        }

        if (player.onGround() && level.getBlockState(player.getBlockPosBelowThatAffectsMyMovement()).is(TFCThingsTags.Blocks.CRAMPONS_NEGATE_SLIP))
        {
            Vec3 motion = player.getDeltaMovement();
            player.setDeltaMovement(0.0, motion.y, 0.0);
        }
    }

    @SubscribeEvent
    public static void onPlayerTickPost(PlayerTickEvent.Post event)
    {
        Player player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide() || player.isPassenger() || player.getAbilities().instabuild)
        {
            return;
        }

        UUID playerId = player.getUUID();
        ItemStack feetItem = player.getItemBySlot(EquipmentSlot.FEET);

        boolean wearingSnowShoes = feetItem.getItem() instanceof SnowShoesItem;
        boolean wearingHikingBoots = feetItem.getItem() instanceof HikingBootsItem;
        boolean wearingCrampons = feetItem.getItem() instanceof CramponsItem;

        if (!wearingSnowShoes && !wearingHikingBoots && !wearingCrampons)
        {
            lastPlayerPositions.remove(playerId);
            return;
        }

        if (!player.onGround())
        {
            lastPlayerPositions.put(playerId, player.position());
            return;
        }

        Vec3 lastPos = lastPlayerPositions.get(playerId);
        lastPlayerPositions.put(playerId, player.position());

        if (lastPos == null)
        {
            return;
        }

        double dx = player.getX() - lastPos.x;
        double dz = player.getZ() - lastPos.z;
        double distanceBlocks = Math.sqrt(dx * dx + dz * dz);

        if (distanceBlocks <= 0.01)
        {
            return;
        }

        int distanceCm = (int) (distanceBlocks * 100);

        if (wearingSnowShoes && TFCThingsConfig.ITEMS.MASTER_LIST.enableSnowShoes.get())
        {
            if (isPlayerOnTag(player, level, TFCThingsTags.Blocks.SNOW_SHOES_NEGATE_SLOW))
            {
                SnowShoesItem.addDistance(feetItem, distanceCm, () ->
                    feetItem.hurtAndBreak(1, player, EquipmentSlot.FEET));
            }
        }

        if (wearingHikingBoots && TFCThingsConfig.ITEMS.MASTER_LIST.enableHikingBoots.get())
        {
            if (isPlayerOnTag(player, level, TFCThingsTags.Blocks.HIKING_BOOTS_NEGATE_SLOW))
            {
                HikingBootsItem.addDistance(feetItem, distanceCm, () ->
                    feetItem.hurtAndBreak(1, player, EquipmentSlot.FEET));
            }
        }

        if (wearingCrampons && TFCThingsConfig.ITEMS.MASTER_LIST.enableCrampons.get())
        {
            if (level.getBlockState(player.getBlockPosBelowThatAffectsMyMovement()).is(TFCThingsTags.Blocks.CRAMPONS_NEGATE_SLIP))
            {
                CramponsItem.addDistance(feetItem, distanceCm, () ->
                    feetItem.hurtAndBreak(1, player, EquipmentSlot.FEET));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        lastPlayerPositions.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return;
        }

        DamageSource source = event.getSource();
        if (!(source.getEntity() instanceof Player player))
        {
            return;
        }
        if (player.level().isClientSide())
        {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        int charges = getSharpnessCharges(weapon);
        if (charges <= 0 || !isSharpnessWeapon(weapon))
        {
            return;
        }

        float bonus = TFCThingsConfig.ITEMS.WHETSTONE.weaponSharpnessBonus.get().floatValue() * sharpnessTierMultiplier(charges);
        event.setAmount(event.getAmount() + bonus);
        consumeSharpnessCharge(weapon);
    }

    @SubscribeEvent
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return;
        }

        Player player = event.getEntity();
        ItemStack tool = player.getMainHandItem();
        int charges = getSharpnessCharges(tool);
        if (charges <= 0 || lacksSharpnessMiningToolTag(tool) || tool.getDestroySpeed(event.getState()) <= 1.0F)
        {
            return;
        }

        event.setNewSpeed(event.getNewSpeed() + getTierMiningBonus(charges));
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return;
        }

        Player player = event.getPlayer();
        if (player.level().isClientSide() || player.getAbilities().instabuild)
        {
            return;
        }

        ItemStack tool = player.getMainHandItem();
        if (getSharpnessCharges(tool) <= 0 || lacksSharpnessMiningToolTag(tool) || tool.getDestroySpeed(event.getState()) <= 1.0F)
        {
            return;
        }

        consumeSharpnessCharge(tool);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (!WhetstoneItem.isSharpenable(stack))
        {
            return;
        }

        int charges = getSharpnessCharges(stack);
        if (charges <= 0)
        {
            return;
        }

        int t2 = TFCThingsConfig.ITEMS.WHETSTONE.maxChargesHoningSteel.get();
        int t1 = TFCThingsConfig.ITEMS.WHETSTONE.maxChargesWhetstone.get();
        ChatFormatting color = charges > t2
            ? ChatFormatting.DARK_PURPLE
            : charges > t1 ? ChatFormatting.BLUE : ChatFormatting.DARK_GREEN;
        event.getToolTip().add(net.minecraft.network.chat.Component.translatable("tfcthings.tooltip.sharpness", charges).withStyle(color));
    }

    private static int getSharpnessCharges(ItemStack stack)
    {
        if (!WhetstoneItem.isSharpenable(stack))
        {
            return 0;
        }
        return stack.getOrDefault(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), 0);
    }

    private static void consumeSharpnessCharge(ItemStack stack)
    {
        int charges = getSharpnessCharges(stack);
        if (charges > 0)
        {
            stack.set(TFCThingsDataComponents.SHARPNESS_CHARGES.get(), charges - 1);
        }
    }

    private static int sharpnessTierMultiplier(int charges)
    {
        if (charges > TFCThingsConfig.ITEMS.WHETSTONE.maxChargesHoningSteel.get()) return 3;
        if (charges > TFCThingsConfig.ITEMS.WHETSTONE.maxChargesWhetstone.get()) return 2;
        return 1;
    }

    private static float getTierMiningBonus(int charges)
    {
        if (charges > TFCThingsConfig.ITEMS.WHETSTONE.maxChargesHoningSteel.get()) return TFCThingsConfig.ITEMS.WHETSTONE.tier3MiningBonus.get().floatValue();
        if (charges > TFCThingsConfig.ITEMS.WHETSTONE.maxChargesWhetstone.get()) return TFCThingsConfig.ITEMS.WHETSTONE.tier2MiningBonus.get().floatValue();
        return TFCThingsConfig.ITEMS.WHETSTONE.tier1MiningBonus.get().floatValue();
    }

    private static boolean isSharpnessWeapon(ItemStack stack)
    {
        if (stack.isEmpty())
        {
            return false;
        }
        return stack.is(TFCThingsTags.Items.SHARPNESS_WEAPONS)
            || (WhetstoneItem.isSharpenable(stack) && lacksSharpnessMiningToolTag(stack));
    }

    private static boolean lacksSharpnessMiningToolTag(ItemStack stack)
    {
        return stack.isEmpty() || !stack.is(TFCThingsTags.Items.SHARPNESS_MINING_TOOLS);
    }

    private static boolean isPlayerOnTag(Player player, Level level,
                                         net.minecraft.tags.TagKey<net.minecraft.world.level.block.Block> tag)
    {
        AABB box = player.getBoundingBox();
        BlockPos minPos = BlockPos.containing(box.minX + 0.001D, box.minY + 0.001D, box.minZ + 0.001D);
        BlockPos maxPos = BlockPos.containing(box.maxX - 0.001D, box.maxY - 0.001D, box.maxZ - 0.001D);

        for (BlockPos pos : BlockPos.betweenClosed(minPos, maxPos))
        {
            if (!level.isLoaded(pos))
            {
                return false;
            }
            BlockState state = level.getBlockState(pos);
            if (state.is(tag))
            {
                return true;
            }
        }
        return false;
    }
}
