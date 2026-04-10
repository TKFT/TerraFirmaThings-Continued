package com.rustysnail.terrafirmathings.compat.ambiental;

import com.lumintorious.tfcambiental.api.AmbientalRegistry;
import com.lumintorious.tfcambiental.data.TemperatureModifier;
import com.rustysnail.terrafirmathings.common.TFCThingsTags;
import com.rustysnail.terrafirmathings.compat.WearableCompat;

public final class TFCThingsAmbientalCompat
{
    public static void register()
    {
        AmbientalRegistry.EQUIPMENT.register((player, stack) ->
        {
            if (stack.is(TFCThingsTags.Items.HIKING_BOOTS))
                return TemperatureModifier.defined("tfcthings_hiking_boots",
                    WearableCompat.HIKING_BOOTS_AMBIENTAL_CHANGE,
                    WearableCompat.HIKING_BOOTS_AMBIENTAL_POTENCY);

            if (stack.is(TFCThingsTags.Items.SNOWSHOES))
                return TemperatureModifier.defined("tfcthings_snowshoes",
                    WearableCompat.SNOWSHOES_AMBIENTAL_CHANGE,
                    WearableCompat.SNOWSHOES_AMBIENTAL_POTENCY);

            if (stack.is(TFCThingsTags.Items.CROWNS))
                return TemperatureModifier.defined("tfcthings_crown",
                    WearableCompat.CROWN_AMBIENTAL_CHANGE,
                    WearableCompat.CROWN_AMBIENTAL_POTENCY);

            return TemperatureModifier.none();
        });
    }
}
