package com.fomdev.awaken.title;

import com.fomdev.awaken.event.RegisterEvent;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.flib.interpreter.ForceLoader;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TitleRegister
{
    private static final Map<ResourceLocation, Prefix> registeredPrefixes = new HashMap<>();
    private static final Map<ResourceLocation, Suffix> registeredSuffixes = new HashMap<>();
    private static final Map<ResourceLocation, Title> registeredTitles = new HashMap<>();

    private static final Map<Prefix, Float> leveledPrefixes = new HashMap<>();
    private static final Map<Suffix, Float> leveledSuffixes = new HashMap<>();
    private static final Map<Title, Float> leveledTitles = new HashMap<>();

    public static boolean prefixFrozen = false;
    public static boolean suffixFrozen = false;
    public static boolean titleFrozen = false;

    public static ResourceLocation getPrefixId(Prefix prefix)
    {
        for (Map.Entry<ResourceLocation, Prefix> entry: registeredPrefixes.entrySet())
        {
            if (entry.getValue() == prefix)
                return entry.getKey();
        }

        return null;
    }

    public static ResourceLocation getSuffixId(Suffix suffix)
    {
        for (Map.Entry<ResourceLocation, Suffix> entry: registeredSuffixes.entrySet())
        {
            if (entry.getValue() == suffix)
                return entry.getKey();
        }

        return null;
    }

    public static ResourceLocation getTitleId(Title title)
    {
        for (Map.Entry<ResourceLocation, Title> entry: registeredTitles.entrySet())
        {
            if (entry.getValue() == title)
                return entry.getKey();
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
        if (prefixFrozen)
            throw new IllegalStateException("Prefix register state frozen");

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, prefix.id());
        if (registeredPrefixes.containsKey(location))
            throw new IllegalStateException("Registered prefix: " + location);
        registeredPrefixes.put(location, prefix);
        return prefix;
    }

    public static Suffix register(String modid, Suffix suffix)
    {
        if (suffixFrozen)
            throw new IllegalStateException("Suffix register state frozen");

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, suffix.id());
        if (registeredSuffixes.containsKey(location))
            throw new IllegalStateException("Registered suffix: " + location);
        registeredSuffixes.put(location, suffix);
        return suffix;
    }

    public static Title register(String modid, Title title)
    {
        if (titleFrozen)
            throw new IllegalStateException("Title register state frozen");

        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, title.id());
        if (registeredTitles.containsKey(location))
            throw new IllegalStateException("Registered title: " + location);
        registeredTitles.put(location, title);
        return title;
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

    @Deprecated(since = "0.0.2", forRemoval = true)
    public static void syncStackPrefix(ItemStack stack)
    {
        Prefix prefix = NBTUtil.deserializePrefixes(stack);
        if (prefix == null) return;

        NBTUtil.setMaxDamage(stack, stack.getMaxDamage() + prefix.additionalDurability());
        NBTUtil.resetEnchant(stack);
        Arrays.stream(prefix.aspects()).forEach(aspect -> NBTUtil.putEnchantmentAspect(stack, aspect));
    }

    @Deprecated(since = "0.0.2", forRemoval = true)
    public static void syncStackSuffix(ItemStack stack)
    {
        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        if (suffix == null) return;

        NBTUtil.setMaxDamage(stack, stack.getMaxDamage() + suffix.additionalDurability());
    }

    @Deprecated(since = "0.0.2", forRemoval = true)
    public static void syncStackTitle(ItemStack stack)
    {
        Quality quality = NBTUtil.deserializeQuality(stack);
        Title title = NBTUtil.deserializeTitle(stack);
        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        if (quality == null) return;
        if (title == null) return;

        int currDamage = stack.getMaxDamage();
        NBTUtil.setMaxDamage(stack, currDamage + title.additionalDurability());

        AttributeUtil.delNamespace(stack, "titleutil"); // Makes sure attributes won't add together

        Title.CompoundAttribute attr = title.attrs(quality.factor());
        AttributeUtil.putAttribute(
                stack,
                attr.attr(),
                quality.id() + attr.attr().toString(),
                "titleutil",
                attr.amount() * quality.factor() * (suffix == null || suffix.triggerAttribute() == null || suffix.triggerAttribute() != attr.attr()? 1.0F: suffix.modifyFactor()),
                attr.operation(),
                stack.getEquipmentSlot()
        );
    }

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event)
    {
        String running = "Running '{}' registry";

        Awaken.LOGGER.info("TR> Registering & Freezing on FML-Common-Setup");

        Awaken.LOGGER.info(running, AwakenRegistries.AWAKEN_PREFIX);
        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_PREFIX);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_PREFIX));
        prefixFrozen = true;
        Awaken.LOGGER.info("TR> Prefix register state frozen");

        Awaken.LOGGER.info(running, AwakenRegistries.AWAKEN_SUFFIX);
        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_SUFFIX);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_SUFFIX));
        suffixFrozen = true;
        Awaken.LOGGER.info("TR> Suffix register state frozen");

        Awaken.LOGGER.info(running, AwakenRegistries.AWAKEN_TITLE);
        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_TITLE);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_TITLE));
        titleFrozen = true;
        Awaken.LOGGER.info("TR> Title register state frozen");
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