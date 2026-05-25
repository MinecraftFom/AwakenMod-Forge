package com.fomdev.awaken.title;

import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TitleRegister
{
    private static final Map<ResourceLocation, Prefix> registeredPrefixes = new HashMap<>();
    private static final Map<ResourceLocation, Suffix> registeredSuffixes = new HashMap<>();
    private static final Map<ResourceLocation, Title> registeredTitles = new HashMap<>();

    public static ResourceLocation getPrefixId(Prefix prefix)
    {
        for (ResourceLocation location: registeredPrefixes.keySet())
        {
            if (registeredPrefixes.get(location) == prefix)
                return location;
        }

        return null;
    }

    public static ResourceLocation getSuffixId(Suffix suffix)
    {
        for (ResourceLocation location: registeredSuffixes.keySet())
        {
            if (registeredSuffixes.get(location) == suffix)
                return location;
        }

        return null;
    }

    public static ResourceLocation getTitleId(Title title)
    {
        for (ResourceLocation location: registeredTitles.keySet())
        {
            if (registeredTitles.get(location) == title)
                return location;
        }

        return null;
    }

    public static Prefix getPrefix(ResourceLocation location)
    {
        return registeredPrefixes.get(location);
    }

    public static Prefix getPrefix(String modid, String id)
    {
        return registeredPrefixes.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public static Suffix getSuffix(ResourceLocation location)
    {
        return registeredSuffixes.get(location);
    }

    public static Suffix getSuffix(String modid, String id)
    {
        return registeredSuffixes.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public static Title getTitle(ResourceLocation location)
    {
        return registeredTitles.get(location);
    }

    public static Title getTitle(String modid, String id)
    {
        return registeredTitles.get(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public static String localizePrefix(String id)
    {
        return "prefix."+id+".name";
    }

    public static String localizeSuffix(String id)
    {
        return "suffix."+id+".name";
    }

    public static String localizeTitle(String id)
    {
        return "title."+id+".name";
    }

    public static Prefix register(String modid, Prefix prefix)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, prefix.id());

        if (registeredPrefixes.containsKey(location))
            throw new IllegalStateException("Registered prefix: " + location);

        return registeredPrefixes.put(location, prefix);
    }

    public static Suffix register(String modid, Suffix suffix)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, suffix.id());

        if (registeredSuffixes.containsKey(location))
            throw new IllegalStateException("Registered suffix: " + location);

        return registeredSuffixes.put(location, suffix);
    }

    public static Title register(String modid, Title title)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, title.id());

        if (registeredTitles.containsKey(location))
            throw new IllegalStateException("Registered title: " + location);

        return registeredTitles.put(location, title);
    }

    public static void syncStackPrefix(ItemStack stack)
    {

    }

    public static void syncStackTitle(ItemStack stack)
    {
        Quality quality = NBTUtil.deserializeQuality(stack);
        Title title = NBTUtil.deserializeTitle(stack);
        if (quality == null) return;
        if (title == null) return;

        int currDamage = stack.getMaxDamage();
        NBTUtil.setMaxDamage(stack, currDamage + title.additionalDurability());

        for (Title.CompoundAttribute attr: title.attrs(quality.factor()))
        {
            stack.addAttributeModifier(
                    attr.attr(),
                    new AttributeModifier(
                            UUID.randomUUID(),
                            "_fast_attribute_compound_from_title",
                            attr.amount(),
                            attr.operation()
                    ),
                    stack.getEquipmentSlot()
            );
        }
    }
}