package com.rustysnail.terrafirmathings.compat.jade;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import com.rustysnail.terrafirmathings.common.blockentity.FishingNetAnchorBlockEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.ItemStackHandler;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

public enum FishingNetInventoryProvider implements IServerExtensionProvider<ItemStack>, IClientExtensionProvider<ItemStack, ItemView>
{
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "fishing_net_inventory");

    @Override
    public ResourceLocation getUid()
    {
        return UID;
    }

    @Override
    @Nullable
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor)
    {
        if (!(accessor.getTarget() instanceof FishingNetAnchorBlockEntity be)) return null;
        if (!be.isLinked()) return null;

        FishingNetAnchorBlockEntity master = be.getMaster(accessor.getLevel());
        if (master == null) master = be;

        ItemStackHandler inv = master.getInventory();
        List<ItemStack> items = new ArrayList<>();
        for (int i = 0; i < inv.getSlots(); i++)
        {
            ItemStack stack = inv.getStackInSlot(i);
            if (!stack.isEmpty()) items.add(stack.copy());
        }

        return List.of(new ViewGroup<>(items));
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(Accessor<?> accessor, List<ViewGroup<ItemStack>> groups)
    {
        return ClientViewGroup.map(groups, ItemView::new, null);
    }

    @Override
    public boolean shouldRequestData(Accessor<?> accessor)
    {
        return accessor.getTarget() instanceof FishingNetAnchorBlockEntity be && be.isLinked();
    }
}
