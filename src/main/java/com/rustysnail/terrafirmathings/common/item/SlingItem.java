package com.rustysnail.terrafirmathings.common.item;

import java.util.function.Predicate;
import com.rustysnail.terrafirmathings.TFCThingsConfig;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.common.entity.SlingStoneEntity;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;

public class SlingItem extends ProjectileWeaponItem
{

    public SlingItem(Properties properties)
    {
        super(properties);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles()
    {
        return this::isValidAmmo;
    }

    @Override
    public int getDefaultProjectileRange()
    {
        return 8;
    }

    @Override
    protected void shootProjectile(
        LivingEntity shooter, Projectile projectile, int index,
        float velocity, float inaccuracy, float angle, @Nullable LivingEntity target)
    {
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand)
    {
        ItemStack stack = player.getItemInHand(hand);

        if (!TFCThingsConfig.ITEMS.MASTER_LIST.enableSlings.get())
        {
            return InteractionResultHolder.fail(stack);
        }

        if (findAmmo(player) == null && !player.getAbilities().instabuild)
        {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 0.5f, 0.4f);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack)
    {
        return UseAnim.BOW;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity)
    {
        return 72000;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft)
    {
        if (!(entity instanceof Player player)) return;
        if (level.isClientSide()) return;

        int chargeTime = getUseDuration(stack, entity) - timeLeft;
        int maxPower = TFCThingsConfig.ITEMS.SLING.maxPower.get();
        int chargeSpeed = TFCThingsConfig.ITEMS.SLING.chargeSpeed.get();

        float power = Math.min((float) chargeTime / chargeSpeed, maxPower);
        if (power < 0.5f) return;

        AmmoResult ammo = findAmmo(player);
        if (ammo == null && !player.getAbilities().instabuild) return;

        SlingAmmoItem.AmmoType ammoType = SlingAmmoItem.AmmoType.STONE;
        if (ammo != null && ammo.stack().getItem() instanceof SlingAmmoItem slingAmmo)
        {
            ammoType = slingAmmo.getAmmoType();
        }

        float totalPower = power + ammoType.getPowerBonus();
        float velocity = 1.6f * (power / maxPower) * ammoType.getVelocityMultiplier();
        float inaccuracy = 0.5f * (8 - Math.min(power, 8));

        spawnProjectile(level, player, totalPower, velocity, inaccuracy, ammoType);

        for (int i = 0; i < ammoType.getScatterCount(); i++)
        {
            spawnProjectile(level, player, power, velocity * 0.75f, inaccuracy + 2.5f, ammoType);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
            SoundEvents.SNOWBALL_THROW, SoundSource.PLAYERS, 0.5f, 0.4f / (level.getRandom().nextFloat() * 0.4f + 0.8f));

        if (ammo != null && !player.getAbilities().instabuild)
        {
            ammo.stack().shrink(1);
        }

        stack.hurtAndBreak(1, player, LivingEntity.getSlotForHand(player.getUsedItemHand()));
    }

    private void spawnProjectile(Level level, Player player, float power, float velocity, float inaccuracy, SlingAmmoItem.AmmoType ammoType)
    {
        SlingStoneEntity stone = new SlingStoneEntity(level, player, power, ammoType);
        stone.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0f, velocity, inaccuracy);
        level.addFreshEntity(stone);
    }

    @Nullable
    private AmmoResult findAmmo(Player player)
    {
        ItemStack offhand = player.getOffhandItem();
        if (isValidAmmo(offhand)) return new AmmoResult(offhand);

        ItemStack mainHand = player.getMainHandItem();
        if (isValidAmmo(mainHand)) return new AmmoResult(mainHand);

        for (int i = 0; i < player.getInventory().getContainerSize(); i++)
        {
            ItemStack s = player.getInventory().getItem(i);
            if (isValidAmmo(s)) return new AmmoResult(s);
        }

        return null;
    }

    private boolean isValidAmmo(ItemStack stack)
    {
        if (stack.isEmpty()) return false;
        return stack.is(TFCThingsTags.Items.SLING_AMMO);
    }

    private record AmmoResult(ItemStack stack) {}
}
