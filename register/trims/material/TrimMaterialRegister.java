package com.fomdev.awaken.register.trims.material;

import com.fomdev.awaken.register.item.MaterialItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TrimMaterialRegister
{
    public static final List<TrimMaterialHolder> MATERIAL_LIST = new ArrayList<>();

    public static final ResourceKey<TrimMaterial> MATERIAL_TEST;

    private static ResourceKey<TrimMaterial> registryKey(String id)
    {
        return ResourceKey.create(Registries.TRIM_MATERIAL, ResourceLocation.parse(id));
    }

    private static ResourceKey<TrimMaterial> registerAndCreate(String id, Item represent, Color color, float factor)
    {
        ResourceKey<TrimMaterial> key = registryKey(id);
        MATERIAL_LIST.add(new TrimMaterialHolder(key, represent, color, factor));
        return key;
    }

    private static ResourceKey<TrimMaterial> registerAndCreate(String id, Item represent, Color color, float factor, Map<ArmorMaterial, String> additional)
    {
        ResourceKey<TrimMaterial> key = registryKey(id);
        MATERIAL_LIST.add(new TrimMaterialHolder(key, represent, color, factor, additional));
        return key;
    }

    static
    {
        MATERIAL_TEST = registerAndCreate("test", MaterialItems.abropht.get(), new Color(0xF0, 0xF0, 0xF0), 1.1F);
    }

    public record TrimMaterialHolder(
            ResourceKey<TrimMaterial> id,
            Item represent,
            Color color,
            float factor,
            Map<ArmorMaterial, String> special
    )
    {
        public TrimMaterialHolder(
                ResourceKey<TrimMaterial> id,
                Item represent,
                Color color,
                float factor
        )
        {
            this(id, represent, color, factor, Map.of());
        }
    }
}