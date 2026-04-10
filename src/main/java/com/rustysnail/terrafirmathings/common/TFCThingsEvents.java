package com.rustysnail.terrafirmathings.common;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.block.GrainPileBlock;
import com.rustysnail.terrafirmathings.common.blockentity.GrainPileBlockEntity;
import com.rustysnail.terrafirmathings.common.util.FootwearHelper;
import com.rustysnail.terrafirmathings.common.util.SharpnessHelper;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.ItemAttributeModifierEvent;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import net.dries007.tfc.common.entities.misc.ThrownJavelin;

@EventBusSubscriber(modid = TerraFirmaThings.MOD_ID)
public final class TFCThingsEvents
{
    private static final Map<UUID, Vec3> lastPlayerPositions = new HashMap<>();
    private static final ResourceLocation SHARPNESS_DAMAGE_ID = ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "sharpness_bonus");

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
        if (!feetItem.is(TFCThingsTags.Items.CRAMPONS))
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

        boolean wearingSnowShoes = feetItem.is(TFCThingsTags.Items.SNOWSHOES);
        boolean wearingHikingBoots = feetItem.is(TFCThingsTags.Items.HIKING_BOOTS);
        boolean wearingCrampons = feetItem.is(TFCThingsTags.Items.CRAMPONS);

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
                FootwearHelper.addDistance(feetItem, distanceCm,
                    TFCThingsConfig.ITEMS.SNOW_SHOES.damageDistance.get(),
                    () -> feetItem.hurtAndBreak(1, player, EquipmentSlot.FEET));
            }
        }

        if (wearingHikingBoots && TFCThingsConfig.ITEMS.MASTER_LIST.enableHikingBoots.get())
        {
            if (isPlayerOnTag(player, level, TFCThingsTags.Blocks.HIKING_BOOTS_NEGATE_SLOW))
            {
                FootwearHelper.addDistance(feetItem, distanceCm,
                    TFCThingsConfig.ITEMS.HIKING_BOOTS.damageDistance.get(),
                    () -> feetItem.hurtAndBreak(1, player, EquipmentSlot.FEET));
            }
        }

        if (wearingCrampons && TFCThingsConfig.ITEMS.MASTER_LIST.enableCrampons.get())
        {
            if (level.getBlockState(player.getBlockPosBelowThatAffectsMyMovement()).is(TFCThingsTags.Blocks.CRAMPONS_NEGATE_SLIP))
            {
                FootwearHelper.addDistance(feetItem, distanceCm,
                    TFCThingsConfig.ITEMS.CRAMPONS.damageDistance.get(),
                    () -> feetItem.hurtAndBreak(1, player, EquipmentSlot.FEET));
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event)
    {
        lastPlayerPositions.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public static void onItemAttributeModifier(ItemAttributeModifierEvent event)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return;
        }

        ItemStack stack = event.getItemStack();
        int charges = SharpnessHelper.getCharges(stack);
        if (charges <= 0 || lacksSharpnessWeapon(stack))
        {
            return;
        }

        float bonus = SharpnessHelper.getDamageBonusForTier(charges);
        event.addModifier(
            Attributes.ATTACK_DAMAGE,
            new AttributeModifier(SHARPNESS_DAMAGE_ID, bonus, AttributeModifier.Operation.ADD_VALUE),
            EquipmentSlotGroup.MAINHAND
        );
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingIncomingDamageEvent event)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableWhetstones.get())
        {
            return;
        }
        if (event.getEntity().level().isClientSide())
        {
            return;
        }

        DamageSource source = event.getSource();

        if (source.getDirectEntity() instanceof ThrownJavelin tfcJavelin)
        {
            ItemStack weapon = tfcJavelin.getItem();
            float bonus = SharpnessHelper.getDamageBonusForThrown(weapon);
            if (bonus > 0.0F)
            {
                event.setAmount(event.getAmount() + bonus);
                SharpnessHelper.consumeCharge(weapon);
                tfcJavelin.setItem(weapon);
            }
            return;
        }

        if (!(source.getEntity() instanceof Player player))
        {
            return;
        }

        ItemStack weapon = player.getMainHandItem();
        int charges = SharpnessHelper.getCharges(weapon);
        if (charges <= 0 || lacksSharpnessWeapon(weapon))
        {
            return;
        }

        SharpnessHelper.consumeCharge(weapon);
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
        int charges = SharpnessHelper.getCharges(tool);
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
        if (SharpnessHelper.getCharges(tool) <= 0 || lacksSharpnessMiningToolTag(tool) || tool.getDestroySpeed(event.getState()) <= 1.0F)
        {
            return;
        }

        SharpnessHelper.consumeCharge(tool);
    }

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event)
    {
        ItemStack stack = event.getItemStack();
        if (!SharpnessHelper.isSharpenable(stack))
        {
            return;
        }

        int charges = SharpnessHelper.getCharges(stack);
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

    private static float getTierMiningBonus(int charges)
    {
        if (charges > TFCThingsConfig.ITEMS.WHETSTONE.maxChargesHoningSteel.get()) return TFCThingsConfig.ITEMS.WHETSTONE.tier3MiningBonus.get().floatValue();
        if (charges > TFCThingsConfig.ITEMS.WHETSTONE.maxChargesWhetstone.get()) return TFCThingsConfig.ITEMS.WHETSTONE.tier2MiningBonus.get().floatValue();
        return TFCThingsConfig.ITEMS.WHETSTONE.tier1MiningBonus.get().floatValue();
    }

    private static boolean lacksSharpnessWeapon(ItemStack stack)
    {
        if (stack.isEmpty()) return true;
        return !stack.is(TFCThingsTags.Items.SHARPNESS_WEAPONS)
            && !(SharpnessHelper.isSharpenable(stack) && lacksSharpnessMiningToolTag(stack));
    }

    private static boolean lacksSharpnessMiningToolTag(ItemStack stack)
    {
        return stack.isEmpty() || !stack.is(TFCThingsTags.Items.SHARPNESS_MINING_TOOLS);
    }

    @SubscribeEvent
    public static void onRightClickBlockForGrainPile(PlayerInteractEvent.RightClickBlock event)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableGrainPiles.get()) return;

        Player player = event.getEntity();
        Level level = player.level();
        if (!player.isShiftKeyDown()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        ItemStack stack = player.getMainHandItem();
        if (stack.isEmpty() || !stack.is(TFCThingsTags.Items.GRAIN_PILE_ITEMS)) return;

        BlockPos clickedPos = event.getPos();
        Direction face = event.getFace();
        BlockState clickedState = level.getBlockState(clickedPos);

        if (clickedState.getBlock() instanceof GrainPileBlock grainPileBlock)
        {
            event.setCanceled(true);
            if (level.isClientSide()) return;

            if (!(level.getBlockEntity(clickedPos) instanceof GrainPileBlockEntity be)) return;

            if (!be.isEmpty() && !ItemStack.isSameItem(be.getGrainType(), stack))
            {
                player.displayClientMessage(net.minecraft.network.chat.Component.translatable("tfcthings.grain_pile.wrong_type"), true);
                return;
            }

            if (be.isFull() && face == Direction.UP)
            {
                BlockPos above = clickedPos.above();
                BlockState aboveState = level.getBlockState(above);

                if (aboveState.isAir())
                {
                    int inserted = GrainPileBlock.formPile(level, above, stack, grainPileBlock);
                    if (inserted > 0)
                    {
                        stack.shrink(inserted);
                        level.playSound(null, above, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
                    }
                }
                else if (aboveState.getBlock() instanceof GrainPileBlock
                    && level.getBlockEntity(above) instanceof GrainPileBlockEntity aboveBe)
                {
                    if (!aboveBe.isEmpty() && !ItemStack.isSameItem(aboveBe.getGrainType(), stack))
                        player.displayClientMessage(net.minecraft.network.chat.Component.translatable("tfcthings.grain_pile.wrong_type"), true);
                    else if (aboveBe.canInsertGrain(stack))
                    {
                        int before = stack.getCount();
                        aboveBe.insertGrain(stack);
                        if (before - stack.getCount() > 0)
                        {
                            aboveBe.syncBlockState();
                            level.playSound(null, above, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
                        }
                    }
                }
                return;
            }

            int before = stack.getCount();
            be.insertGrain(stack);
            if (before - stack.getCount() > 0)
            {
                be.syncBlockState();
                level.playSound(null, clickedPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
            }
            return;
        }

        if (face != Direction.UP) return;
        if (!clickedState.isFaceSturdy(level, clickedPos, Direction.UP)) return;

        BlockPos targetPos = clickedPos.above();
        BlockState targetState = level.getBlockState(targetPos);
        if (targetState.getBlock() instanceof GrainPileBlock) return;
        if (!targetState.isAir()) return;

        event.setCanceled(true);
        if (level.isClientSide()) return;

        int consumed = GrainPileBlock.formPile(level, targetPos, stack, TFCThingsBlocks.GRAIN_PILE.get());
        if (consumed > 0)
        {
            stack.shrink(consumed);
            level.playSound(null, targetPos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 0.6f, 1.0f);
        }
    }

    @SubscribeEvent
    public static void onLeftClickGrainPile(PlayerInteractEvent.LeftClickBlock event)
    {
        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableGrainPiles.get()) return;

        Player player = event.getEntity();
        Level level = player.level();
        if (level.isClientSide()) return;
        if (player.getAbilities().instabuild) return;

        BlockPos pos = event.getPos();
        if (!(level.getBlockState(pos).getBlock() instanceof GrainPileBlock)) return;
        if (!(level.getBlockEntity(pos) instanceof GrainPileBlockEntity be)) return;

        int toExtract = player.isShiftKeyDown() ? 16 : 1;
        ItemStack extracted = be.extractGrain(toExtract);

        if (!extracted.isEmpty())
        {
            be.syncBlockState();
            if (!player.addItem(extracted))
                Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5, extracted);
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS, 1.0f, 1.2f);
        }

        event.setCanceled(true);
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
