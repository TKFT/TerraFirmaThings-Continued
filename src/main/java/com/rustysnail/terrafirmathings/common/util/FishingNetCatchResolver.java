package com.rustysnail.terrafirmathings.common.util;

import java.util.List;
import java.util.UUID;

import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;

public final class FishingNetCatchResolver
{
    private static final GameProfile NET_PROFILE = new GameProfile(
        UUID.fromString("b3f6bfe8-16c3-4d7b-9c75-10b2a3b820b0"),
        "tfcthings_fishing_net"
    );

    public static List<ItemStack> resolve(ServerLevel level, LivingEntity entity, BlockPos origin)
    {
        final FakePlayer player = FakePlayerFactory.get(level, NET_PROFILE);
        player.setPos(origin.getX() + 0.5, origin.getY() + 0.5, origin.getZ() + 0.5);

        final var lootTableId = entity.getType().getDefaultLootTable();
        final LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(lootTableId);

        final DamageSource src = level.damageSources().playerAttack(player);

        final LootParams params = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, entity.position())
            .withParameter(LootContextParams.THIS_ENTITY, entity)
            .withParameter(LootContextParams.DAMAGE_SOURCE, src)
            .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
            .withOptionalParameter(LootContextParams.ATTACKING_ENTITY, player)
            .withOptionalParameter(LootContextParams.DIRECT_ATTACKING_ENTITY, player)
            .create(LootContextParamSets.ENTITY);

        return lootTable.getRandomItems(params);
    }

    private FishingNetCatchResolver() {}
}
