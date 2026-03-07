package com.rustysnail.terrafirmathings.common;

import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.rustysnail.terrafirmathings.TerraFirmaThings;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class TFCThingsArmorMaterials
{

    public static final DeferredRegister<ArmorMaterial> ARMOR_MATERIALS =
        DeferredRegister.create(Registries.ARMOR_MATERIAL, TerraFirmaThings.MOD_ID);

    public static final Holder<ArmorMaterial> SNOW_SHOES = ARMOR_MATERIALS.register("snow_shoes",
        () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.CHESTPLATE, 0);
                map.put(ArmorItem.Type.HELMET, 0);
                map.put(ArmorItem.Type.BODY, 0);
            }),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "snow_shoes"))),
            0f,
            0f
        ));

    public static final Holder<ArmorMaterial> DURABLE_SNOW_SHOES = ARMOR_MATERIALS.register("durable_snow_shoes",
        () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.CHESTPLATE, 0);
                map.put(ArmorItem.Type.HELMET, 0);
                map.put(ArmorItem.Type.BODY, 0);
            }),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "durable_snow_shoes"))),
            0f,
            0f
        ));
    public static final Map<String, Holder<ArmorMaterial>> GOLD_CROWNS = new LinkedHashMap<>();
    public static final Map<String, Holder<ArmorMaterial>> PLATINUM_CROWNS = new LinkedHashMap<>();
    public static final Holder<ArmorMaterial> HIKING_BOOTS = ARMOR_MATERIALS.register("hiking_boots",
        () -> new ArmorMaterial(
            Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                map.put(ArmorItem.Type.BOOTS, 1);
                map.put(ArmorItem.Type.LEGGINGS, 0);
                map.put(ArmorItem.Type.CHESTPLATE, 0);
                map.put(ArmorItem.Type.HELMET, 0);
                map.put(ArmorItem.Type.BODY, 0);
            }),
            0,
            SoundEvents.ARMOR_EQUIP_LEATHER,
            () -> Ingredient.EMPTY,
            List.of(new ArmorMaterial.Layer(ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "hiking_boots"))),
            0f,
            0f
        ));
    private static final List<String> GEMS = List.of(
        "empty", "agate", "amethyst", "beryl", "diamond", "emerald",
        "garnet", "jade", "jasper", "opal", "ruby", "sapphire", "topaz", "tourmaline"
    );

    static
    {
        for (String gem : GEMS)
        {
            GOLD_CROWNS.put(gem, registerCrown("gold_" + gem));
            PLATINUM_CROWNS.put(gem, registerCrown("platinum_" + gem));
        }
    }

    private static Holder<ArmorMaterial> registerCrown(String variant)
    {
        return ARMOR_MATERIALS.register(variant + "_crown",
            () -> new ArmorMaterial(
                Util.make(new EnumMap<>(ArmorItem.Type.class), map -> {
                    map.put(ArmorItem.Type.BOOTS, 0);
                    map.put(ArmorItem.Type.LEGGINGS, 0);
                    map.put(ArmorItem.Type.CHESTPLATE, 0);
                    map.put(ArmorItem.Type.HELMET, 0);
                    map.put(ArmorItem.Type.BODY, 0);
                }),
                0,
                SoundEvents.ARMOR_EQUIP_GOLD,
                () -> Ingredient.EMPTY,
                List.of(new ArmorMaterial.Layer(
                    ResourceLocation.fromNamespaceAndPath(TerraFirmaThings.MOD_ID, "crown/" + variant))),
                0f, 0f
            ));
    }
}
