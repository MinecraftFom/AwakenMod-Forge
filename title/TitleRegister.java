package com.fomdev.awaken.title;

import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class TitleRegister
{
    private static final Map<ResourceLocation, Title> registeredTitles = new HashMap<>();

    public static ResourceLocation getId(Title title)
    {
        for (ResourceLocation location: registeredTitles.keySet())
        {
            if (registeredTitles.get(location) == title)
                return location;
        }

        return null;
    }

    public static Title getTitle(String modid, String id)
    {
        return registeredTitles.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public static Title register(String modid, Title title)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, title.id());

        if (registeredTitles.containsKey(location))
            throw new IllegalStateException("Registered title: " + location);

        return registeredTitles.put(location, title);
    }
}