package com.rustysnail.terrafirmathings.data;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import com.rustysnail.terrafirmathings.common.TFCThingsArmorMaterials;
import com.rustysnail.terrafirmathings.common.TFCThingsItems;
import com.rustysnail.terrafirmathings.common.item.SlingAmmoItem;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.neoforged.neoforge.registries.DeferredItem;

final class TFCThingsSanityChecks implements DataProvider
{

    private static final Set<DeferredItem<SlingAmmoItem>> RECOVERABLE_AMMO = Set.of(
        TFCThingsItems.SLING_AMMO_HEAVY,
        TFCThingsItems.SLING_AMMO_LIGHT
    );

    private static final Set<DeferredItem<SlingAmmoItem>> NONRECOVERABLE_AMMO = Set.of(
        TFCThingsItems.SLING_AMMO_SPREAD,
        TFCThingsItems.SLING_AMMO_FIRE
    );

    @Override
    public CompletableFuture<?> run(CachedOutput cache)
    {
        verifyCrowns();
        verifySlingAmmoPartition();
        verifyJavelinFamilies();
        return CompletableFuture.completedFuture(null);
    }

    @Override
    public String getName()
    {
        return "TFCThings Sanity Checks";
    }

    private static void verifyCrowns()
    {
        final var gemCount = getGemCount();

        final var expectedMaterialCount = getExpectedMaterialCount(gemCount);

        int platSize = TFCThingsArmorMaterials.PLATINUM_CROWNS.size();
        if (platSize != expectedMaterialCount)
        {
            throw new IllegalStateException(String.format(
                "[TFCThings] PLATINUM_CROWNS armor-material map has %d entries, expected %d " +
                    "(CrownGem.ALL has %d gems + 1 empty). " +
                    "Did you add a gem to CrownGem.ALL without updating TFCThingsArmorMaterials.GEMS?",
                platSize, expectedMaterialCount, gemCount));
        }
    }

    private static int getExpectedMaterialCount(int gemCount)
    {
        int expectedMaterialCount = gemCount + 1;
        int goldSize = TFCThingsArmorMaterials.GOLD_CROWNS.size();
        if (goldSize != expectedMaterialCount)
        {
            throw new IllegalStateException(String.format(
                "[TFCThings] GOLD_CROWNS armor-material map has %d entries, expected %d " +
                    "(CrownGem.ALL has %d gems + 1 empty). " +
                    "Did you add a gem to CrownGem.ALL without updating TFCThingsArmorMaterials.GEMS?",
                goldSize, expectedMaterialCount, gemCount));
        }
        return expectedMaterialCount;
    }

    private static int getGemCount()
    {
        int gemCount = CrownGem.ALL.length;
        int expectedCrownItems = 2 * (gemCount + 1);
        int actualCrownItems = TFCThingsItems.ALL_CROWNS.size();
        if (actualCrownItems != expectedCrownItems)
        {
            throw new IllegalStateException(String.format(
                "[TFCThings] Crown catalog mismatch: ALL_CROWNS has %d entries, expected %d " +
                    "(2 metals × (%d gems + 1 empty)). " +
                    "Did you add a crown DeferredItem without adding it to TFCThingsItems.ALL_CROWNS, " +
                    "or update CrownGem.ALL (%d gems) without updating TFCThingsItems?",
                actualCrownItems, expectedCrownItems, gemCount, gemCount));
        }
        return gemCount;
    }

    private static void verifySlingAmmoPartition()
    {
        for (DeferredItem<SlingAmmoItem> ammo : TFCThingsItems.ALL_SLING_AMMO)
        {
            boolean inRecoverable = RECOVERABLE_AMMO.contains(ammo);
            boolean inNonRecoverable = NONRECOVERABLE_AMMO.contains(ammo);
            if (inRecoverable == inNonRecoverable)
            {
                throw new IllegalStateException(String.format(
                    "[TFCThings] Sling ammo '%s' is in %s of the recoverable/non-recoverable " +
                        "partition in TFCThingsSanityChecks. " +
                        "Classify it in exactly one of RECOVERABLE_AMMO or NONRECOVERABLE_AMMO here, " +
                        "and in exactly one of RECOVERABLE_SLING_AMMO or NONRECOVERABLE_SLING_AMMO in TFCThingsItemTags.",
                    ammo.getId(),
                    inRecoverable ? "both buckets" : "neither bucket"));
            }
        }

        for (DeferredItem<SlingAmmoItem> ammo : RECOVERABLE_AMMO)
        {
            if (!TFCThingsItems.ALL_SLING_AMMO.contains(ammo))
            {
                throw new IllegalStateException(String.format(
                    "[TFCThings] RECOVERABLE_AMMO in TFCThingsSanityChecks contains '%s' " +
                        "which is not in TFCThingsItems.ALL_SLING_AMMO. " +
                        "Remove it from RECOVERABLE_AMMO or add it to ALL_SLING_AMMO.",
                    ammo.getId()));
            }
        }
        for (DeferredItem<SlingAmmoItem> ammo : NONRECOVERABLE_AMMO)
        {
            if (!TFCThingsItems.ALL_SLING_AMMO.contains(ammo))
            {
                throw new IllegalStateException(String.format(
                    "[TFCThings] NONRECOVERABLE_AMMO in TFCThingsSanityChecks contains '%s' " +
                        "which is not in TFCThingsItems.ALL_SLING_AMMO. " +
                        "Remove it from NONRECOVERABLE_AMMO or add it to ALL_SLING_AMMO.",
                    ammo.getId()));
            }
        }
    }

    private static void verifyJavelinFamilies()
    {
        int registeredRope = TFCThingsItems.ALL_ROPE_JAVELINS.size();
        int familyRope = RopeJavelinFamily.ALL.length;
        if (registeredRope != familyRope)
        {
            throw new IllegalStateException(String.format(
                "[TFCThings] Rope javelin mismatch: ALL_ROPE_JAVELINS has %d items but " +
                    "RopeJavelinFamily.ALL has %d entries. " +
                    "Add the new javelin to RopeJavelinFamily.ALL and ALL_ROPE_JAVELINS (or remove it from both).",
                registeredRope, familyRope));
        }
        for (RopeJavelinFamily f : RopeJavelinFamily.ALL)
        {
            if (!TFCThingsItems.ALL_ROPE_JAVELINS.contains(f.javelinItem))
            {
                throw new IllegalStateException(String.format(
                    "[TFCThings] RopeJavelinFamily entry '%s' references a javelinItem that is not " +
                        "in TFCThingsItems.ALL_ROPE_JAVELINS. Add it to ALL_ROPE_JAVELINS.",
                    f.name));
            }
        }

        int registeredHook = TFCThingsItems.ALL_HOOK_JAVELINS.size();
        int familyHook = HookJavelinFamily.ALL.length;
        if (registeredHook != familyHook)
        {
            throw new IllegalStateException(String.format(
                "[TFCThings] Hook javelin mismatch: ALL_HOOK_JAVELINS has %d items but " +
                    "HookJavelinFamily.ALL has %d entries. " +
                    "Add the new javelin to HookJavelinFamily.ALL and ALL_HOOK_JAVELINS (or remove it from both).",
                registeredHook, familyHook));
        }
        for (HookJavelinFamily f : HookJavelinFamily.ALL)
        {
            if (!TFCThingsItems.ALL_HOOK_JAVELINS.contains(f.javelinItem))
            {
                throw new IllegalStateException(String.format(
                    "[TFCThings] HookJavelinFamily entry '%s' references a javelinItem that is not " +
                        "in TFCThingsItems.ALL_HOOK_JAVELINS. Add it to ALL_HOOK_JAVELINS.",
                    f.name));
            }
        }
    }
}
