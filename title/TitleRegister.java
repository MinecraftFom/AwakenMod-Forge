package com.fomdev.awaken.title;

import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class TitleRegister
{
    private static final Map<ResourceLocation, Prefix> registeredPrefixes = new HashMap<>();
    private static final Map<ResourceLocation, Suffix> registeredSuffixes = new HashMap<>();
    private static final Map<ResourceLocation, Title> registeredTitles = new HashMap<>();

    private static final Map<Prefix, Float> leveledPrefixes = new HashMap<>();
    private static final Map<Suffix, Float> leveledSuffixes = new HashMap<>();
    private static final Map<Title, Float> leveledTitles = new HashMap<>();

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

    public static void setPrefixGenerateChance(
            Prefix prefix,
            float chance
    )
    {
        leveledPrefixes.put(prefix, chance);
    }

    public static void setSuffixGenerateChance(
            Suffix suffix,
            float chance
    )
    {
        leveledSuffixes.put(suffix, chance);
    }

    public static void setTitleGenerateChance(
            Title title,
            float chance
    )
    {
        leveledTitles.put(title, chance);
    }

    public static void syncStackPrefix(ItemStack stack)
    {
        Prefix prefix = NBTUtil.deserializePrefixes(stack);
        if (prefix == null) return;

        NBTUtil.setMaxDamage(stack, stack.getMaxDamage() + prefix.additionalDurability());
        Arrays.stream(prefix.aspects()).forEach(aspect -> NBTUtil.putEnchantmentAspect(stack, aspect));
    }

    public static void syncStackSuffix(ItemStack stack)
    {
        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        if (suffix == null) return;

        NBTUtil.setMaxDamage(stack, stack.getMaxDamage() + suffix.additionalDurability());
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
            AttributeUtil.putAttribute(
                    stack,
                    attr.attr(),
                    quality.id() + attr.attr().toString(),
                    "titleutil",
                    attr.amount() * quality.factor(),
                    attr.operation(),
                    stack.getEquipmentSlot()
            );
        }
    }

    @SubscribeEvent
    public static void handleSuffixEvent(TickEvent.PlayerTickEvent event)
    {
        Player player = event.player;
        if (player == null)
            return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack main = player.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack off = player.getItemBySlot(EquipmentSlot.OFFHAND);

        handleItemEffects(helmet, player);
        handleItemEffects(chest, player);
        handleItemEffects(legs, player);
        handleItemEffects(feet, player);
        handleItemEffects(main, player);
        handleItemEffects(off, player);
    }

    private static void handleItemEffects(ItemStack stack, Player player)
    {
        if (stack.getItem() == Items.AIR)
            return;

        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        if (suffix == null)
            return;

        Arrays.stream(suffix.effects()).forEach(player::addEffect);
    }

    public static Prefix shufflePrefix(
            Random random,
            int min,
            int max,
            float diffFactor
    )
    {
        if (leveledPrefixes.isEmpty())
            return null;

        int lvl = random.nextInt(Math.max(1, Math.abs(max - min))) + min;
        float res = lvl * diffFactor;
        List<Prefix> matched = new ArrayList<>();
        for (Prefix prefix: leveledPrefixes.keySet())
        {
            if (leveledPrefixes.get(prefix) >= res)
                matched.add(prefix);
        }

        if (matched.isEmpty())
            return null;

        int index = random.nextInt(matched.size());
        return matched.get(index);
    }

    public static Suffix shuffleSuffix(
            Random random,
            int min,
            int max,
            float diffFactor
    )
    {
        if (leveledSuffixes.isEmpty())
            return null;

        int lvl = random.nextInt(Math.max(1, Math.abs(max - min))) + min;
        float res = lvl * diffFactor;
        List<Suffix> matched = new ArrayList<>();
        for (Suffix suffix: leveledSuffixes.keySet())
        {
            if (leveledSuffixes.get(suffix) >= res)
                matched.add(suffix);
        }

        if (matched.isEmpty())
            return null;

        int index = random.nextInt(matched.size());
        return matched.get(index);
    }

    public static Title shuffleTitle(
            Random random,
            int min,
            int max,
            float diffFactor
    )
    {
        if (leveledTitles.isEmpty())
            return null;

        int lvl = random.nextInt(Math.max(1, Math.abs(max - min))) + min;
        float res = lvl * diffFactor;

        List<Title> matched = new ArrayList<>();
        for (Title title: leveledTitles.keySet())
        {
            if (leveledTitles.get(title) >= res)
                matched.add(title);
        }

        if (matched.isEmpty())
            return null;

        int index = random.nextInt(matched.size());
        return matched.get(index);
    }
}