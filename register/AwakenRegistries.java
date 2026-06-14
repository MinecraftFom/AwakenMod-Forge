package com.fomdev.awaken.register;

import com.fomdev.awaken.awaken.AwakenLevel;
import com.fomdev.awaken.enchanting.Alignment;
import com.fomdev.awaken.enchanting.Aspect;
import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.Title;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class AwakenRegistries
{
    public static final String SIG_AWAKEN_ALIGNMENT = "awaken_alignment";
    public static final String SIG_AWAKEN_ASPECT = "awaken_aspect";
    public static final String SIG_AWAKEN_LEVEL = "awaken_level";
    public static final String SIG_AWAKEN_PREFIX = "awaken_prefix";
    public static final String SIG_AWAKEN_QUALITY = "awaken_quality";
    public static final String SIG_AWAKEN_SUFFIX = "awaken_suffix";
    public static final String SIG_AWAKEN_TIER = "awaken_tier";
    public static final String SIG_AWAKEN_TITLE = "awaken_title";

    public static final ResourceKey<Registry<Alignment>> AWAKEN_ALIGNMENT;
    public static final ResourceKey<Registry<Aspect>> AWAKEN_ASPECT;
    public static final ResourceKey<Registry<AwakenLevel>> AWAKEN_LEVEL;
    public static final ResourceKey<Registry<Prefix>> AWAKEN_PREFIX;
    public static final ResourceKey<Registry<Quality>> AWAKEN_QUALITY;
    public static final ResourceKey<Registry<Suffix>> AWAKEN_SUFFIX;
    public static final ResourceKey<Registry<UpgradeTier>> AWAKEN_TIER;
    public static final ResourceKey<Registry<Title>> AWAKEN_TITLE;

    static
    {
        AWAKEN_ALIGNMENT = register(SIG_AWAKEN_ALIGNMENT);
        AWAKEN_ASPECT = register(SIG_AWAKEN_ASPECT);
        AWAKEN_LEVEL = register(SIG_AWAKEN_LEVEL);
        AWAKEN_PREFIX = register(SIG_AWAKEN_PREFIX);
        AWAKEN_QUALITY = register(SIG_AWAKEN_QUALITY);
        AWAKEN_SUFFIX = register(SIG_AWAKEN_SUFFIX);
        AWAKEN_TIER = register(SIG_AWAKEN_TIER);
        AWAKEN_TITLE = register(SIG_AWAKEN_TITLE);
    }

    private static <T> ResourceKey<Registry<T>> register(String id)
    {
        return ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(AwakenContent.MODID, id));
    }
}