package com.rustysnail.terrafirmathings.data;

import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredItem;

import net.dries007.tfc.common.blocks.rock.Ore;
import net.dries007.tfc.common.items.TFCItems;

final class CrownGem
{
    static final CrownGem[] ALL =
        {
            new CrownGem("agate", cGem("agate"), TFCThingsItems.GOLD_CROWN_AGATE, TFCThingsItems.PLATINUM_CROWN_AGATE),
            new CrownGem("amethyst", cGem("amethyst"), TFCThingsItems.GOLD_CROWN_AMETHYST, TFCThingsItems.PLATINUM_CROWN_AMETHYST),
            new CrownGem("beryl", cGem("beryl"), TFCThingsItems.GOLD_CROWN_BERYL, TFCThingsItems.PLATINUM_CROWN_BERYL),
            new CrownGem("diamond", cGem("diamond"), TFCThingsItems.GOLD_CROWN_DIAMOND, TFCThingsItems.PLATINUM_CROWN_DIAMOND),
            new CrownGem("emerald", cGem("emerald"), TFCThingsItems.GOLD_CROWN_EMERALD, TFCThingsItems.PLATINUM_CROWN_EMERALD),
            new CrownGem("garnet", cGem("garnet"), TFCThingsItems.GOLD_CROWN_GARNET, TFCThingsItems.PLATINUM_CROWN_GARNET),
            new CrownGem("jade", cGem("jade"), TFCThingsItems.GOLD_CROWN_JADE, TFCThingsItems.PLATINUM_CROWN_JADE),
            new CrownGem("jasper", cGem("jasper"), TFCThingsItems.GOLD_CROWN_JASPER, TFCThingsItems.PLATINUM_CROWN_JASPER),
            new CrownGem("opal", Ore.OPAL, TFCThingsItems.GOLD_CROWN_OPAL, TFCThingsItems.PLATINUM_CROWN_OPAL),
            new CrownGem("ruby", Ore.RUBY, TFCThingsItems.GOLD_CROWN_RUBY, TFCThingsItems.PLATINUM_CROWN_RUBY),
            new CrownGem("sapphire", Ore.SAPPHIRE, TFCThingsItems.GOLD_CROWN_SAPPHIRE, TFCThingsItems.PLATINUM_CROWN_SAPPHIRE),
            new CrownGem("topaz", Ore.TOPAZ, TFCThingsItems.GOLD_CROWN_TOPAZ, TFCThingsItems.PLATINUM_CROWN_TOPAZ),
            new CrownGem("tourmaline", cGem("tourmaline"), TFCThingsItems.GOLD_CROWN_TOURMALINE, TFCThingsItems.PLATINUM_CROWN_TOURMALINE),
        };

    private static TagKey<Item> cGem(String name)
    {
        return ItemTags.create(ResourceLocation.fromNamespaceAndPath("c", "gems/" + name));
    }

    final String name;
    final Ingredient gemIngredient;
    final DeferredItem<? extends Item> goldResult;
    final DeferredItem<? extends Item> platinumResult;

    private CrownGem(String name, Ore ore,
                     DeferredItem<? extends Item> goldResult,
                     DeferredItem<? extends Item> platinumResult)
    {
        this.name = name;
        this.gemIngredient = Ingredient.of(TFCItems.GEMS.get(ore));
        this.goldResult = goldResult;
        this.platinumResult = platinumResult;
    }

    private CrownGem(String name, TagKey<Item> gemTag,
                     DeferredItem<? extends Item> goldResult,
                     DeferredItem<? extends Item> platinumResult)
    {
        this.name = name;
        this.gemIngredient = Ingredient.of(gemTag);
        this.goldResult = goldResult;
        this.platinumResult = platinumResult;
    }
}
