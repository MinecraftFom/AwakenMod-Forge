package com.fomdev.awaken.nbt;

import com.fomdev.awaken.enchanting.Alignment;
import com.fomdev.awaken.enchanting.Aspect;
import com.fomdev.awaken.enchanting.EnchantmentRegister;
import com.fomdev.awaken.exp.EquipmentExperience;
import com.fomdev.awaken.forging.ForgeUtils;
import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.quality.QualityUtil;
import com.fomdev.awaken.reinforce.ReinforcementLevels;
import com.fomdev.awaken.spore.Pollinate;
import com.fomdev.awaken.spore.Spore;
import com.fomdev.awaken.spore.SporeManager;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.Title;
import com.fomdev.awaken.title.TitleRegister;
import com.fomdev.flib.util.Suggested;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class NBTUtil
{
    private static final String nbtNamespace = "awakened";

    public static final String nbtAwakenLevelStorage    = "awakenedLevel";
    public static final String nbtEnchantmentStorage    = "awakenedEnchantments";
    public static final String nbtEnchantValueStorage   = "awakenedEnchantable";
    public static final String nbtExpValueStorage       = "awakenedExp";
    public static final String nbtForgedValueStorage    = "awakenedForged";
    public static final String nbtPollinateStorage      = "awakenedPollinate";
    public static final String nbtReinforceValueStorage = "awakenedReinforce";
    public static final String nbtSporeValueStorage     = "awakenedSpores";
    public static final String nbtTitleValueStorage     = "awakenedTitle";
    public static final String nbtQualityValueStorage   = "awakenedQuality";

    public static final String nbtEfficiencyCapability = "awakenedEfficiencyCapability";

    public static void addEnchantValue(
            ItemStack stack,
            int value,
            int operation
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtEnchantValueStorage))
            tag.putInt(nbtEnchantValueStorage, 0);

        int original = tag.getInt(nbtEnchantValueStorage);
        int result = switch (operation)
        {
            case 0 -> original + value;
            case 1 -> original - value;
            case 2 -> original * value;
            case 3 -> original / value;
            default -> throw new IllegalArgumentException("Illegal argument: should be 0 ~ 3, got " + operation);
        };

        tag.putInt(nbtEnchantValueStorage, result);
    }

    public static void addExpLevel(
            ItemStack stack,
            int value,
            int operation
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtExpValueStorage))
            tag.put(nbtExpValueStorage, new CompoundTag());

        CompoundTag expTag = tag.getCompound(nbtExpValueStorage);

        if (!expTag.contains("current")) expTag.putInt("current", 0);
        if (!expTag.contains("level")) expTag.putInt("level", 0);
        if (!expTag.contains("max")) expTag.putInt("max", EquipmentExperience.defaultInitialExperienceRequirement);

        int original = expTag.getInt("level");
        int result = switch (operation)
        {
            case 0 -> original + value;
            case 1 -> original - value;
            case 2 -> original * value;
            case 3 -> original / value;
            default -> throw new IllegalArgumentException("Illegal argument: should be 0 ~ 3, got " + operation);
        };

        expTag.putInt("level", result);
    }

    public static void addExpValue(
            ItemStack stack,
            int value,
            int operation
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtExpValueStorage))
            tag.put(nbtExpValueStorage, new CompoundTag());

        CompoundTag expTag = tag.getCompound(nbtExpValueStorage);

        Quality quality = NBTUtil.deserializeQuality(stack);
        if (!expTag.contains("current")) expTag.putInt("current", 0);
        if (!expTag.contains("level")) expTag.putInt("level", 0);
        if (!expTag.contains("max")) expTag.putInt("max", quality == null? EquipmentExperience.defaultInitialExperienceRequirement: quality.maxUpgradeLevel());

        int original = expTag.getInt("current");
        int result = switch (operation)
        {
            case 0 -> original + value;
            case 1 -> original - value;
            case 2 -> original * value;
            case 3 -> original / value;
            default -> throw new IllegalArgumentException("Illegal argument: should be 0 ~ 3, got " + operation);
        };

        expTag.putInt("current", result);
    }

    public static boolean addForged(
            ItemStack stack,
            int count
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtForgedValueStorage))
            tag.put(nbtForgedValueStorage, new CompoundTag());

        CompoundTag forgeTag = tag.getCompound(nbtForgedValueStorage);

        if (!forgeTag.contains("level")) forgeTag.putInt("level", 0);
        if (!forgeTag.contains("max")) serializeMaxForgeLevel(stack, ForgeUtils.defaultMaxForgingCounts);

        int result = forgeTag.getInt("level") + count;
        if (result > forgeTag.getInt("max")) return false; // Makes sure it won't cause any error

        forgeTag.putInt("level", result);
        return true;
    }

    public static void addMaxForgeLevel(
            ItemStack stack,
            int count
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtForgedValueStorage))
            tag.put(nbtForgedValueStorage, new CompoundTag());

        CompoundTag forgeTag = tag.getCompound(nbtForgedValueStorage);
        int result = forgeTag.getInt("max") + count;

        tag.putInt("max", result);
    }

    public static void addReinforceValue(
            ItemStack stack,
            float value,
            int operation
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtReinforceValueStorage))
            serializeReinforce(stack, ReinforcementLevels.NORMAL);

        CompoundTag reinforceTag = tag.getCompound(nbtReinforceValueStorage);
        if (!tag.contains("current")) reinforceTag.putInt("current", 0);

        float original = reinforceTag.getFloat("current");
        float result = switch (operation)
        {
            case 0 -> original + value;
            case 1 -> original - value;
            case 2 -> original * value;
            case 3 -> original / value;
            default -> throw new IllegalArgumentException("Illegal argument: should be 0 ~ 3, got " + operation);
        };

        reinforceTag.putFloat("current", result);
    }

    public static void consumeAspect(
            ItemStack stack,
            Aspect aspect,
            int amount
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            tag.put(nbtEnchantmentStorage, new CompoundTag());

        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);

        ResourceLocation location = EnchantmentRegister.getAspectId(aspect);
        if (location == null)
            return;

        if (!enchantTag.contains("consumed"))
            enchantTag.put("consumed", new CompoundTag());

        CompoundTag consumedTag = enchantTag.getCompound("consumed");

        if (!consumedTag.contains(location.toString()))
            consumedTag.putInt(location.toString(), 0);

        consumedTag.putInt(location.toString(), consumedTag.getInt(location.toString()) + amount);
    }

    public static Float deserializeAwakenLevel(
            Player entity
    )
    {
        CompoundTag tag = getModTag(entity);

        if (!tag.contains(nbtAwakenLevelStorage))
            serializeAwakenLevel(entity, 0.0F);

        return tag.getFloat(nbtAwakenLevelStorage);
    }

    public static int deserializeForgeLevel(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtForgedValueStorage))
            return 0;

        CompoundTag forgeTag = tag.getCompound(nbtForgedValueStorage);
        if (!forgeTag.contains("level"))
            return 0;

        return forgeTag.getInt("level");
    }

    public static int deserializeMaxForgeLevel(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtForgedValueStorage))
            return 0;

        CompoundTag forgeTag = tag.getCompound(nbtForgedValueStorage);
        if (!forgeTag.contains("max"))
            return 0;

        return forgeTag.getInt("max");
    }

    @Nullable
    public static Quality deserializeQuality(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtQualityValueStorage)) return null;
        CompoundTag qualityTag = tag.getCompound(nbtQualityValueStorage);

        if (!qualityTag.contains("id")) return null;

        return QualityUtil.getQuality(ResourceLocation.parse(qualityTag.getString("id")));
    }

    public static ReinforcementLevels deserializeReinforcement(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtReinforceValueStorage)) return null;
        CompoundTag reinforceTag = tag.getCompound(nbtReinforceValueStorage);

        if (!reinforceTag.contains("level")) reinforceTag.putInt("level", 0);

        return ReinforcementLevels.getLevel(reinforceTag.getInt("level"));
    }

    public static Prefix deserializePrefixes(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        
        if (!tag.contains(nbtTitleValueStorage))
            return null;

        CompoundTag titleTag = tag.getCompound(nbtTitleValueStorage);
        if (!titleTag.contains("prefix"))
            return null;

        return TitleRegister.getPrefix(ResourceLocation.parse(titleTag.getString("prefix")));
    }
    
    public static Suffix deserializeSuffixes(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtTitleValueStorage))
            return null;

        CompoundTag titleTag = tag.getCompound(nbtTitleValueStorage);
        if (!titleTag.contains("suffix"))
            return null;

        return TitleRegister.getSuffix(ResourceLocation.parse(titleTag.getString("suffix")));
    }
    
    public static Title deserializeTitle(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtTitleValueStorage)) return null;
        CompoundTag titleTag = tag.getCompound(nbtTitleValueStorage);

        if (!titleTag.contains("title")) return null;

        return TitleRegister.getTitle(ResourceLocation.parse(titleTag.getString("title")));
    }

    public static Alignment.AlignmentProvider[] getAlignments(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            return new Alignment.AlignmentProvider[]{};

        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);
        if (!tag.contains("alignment"))
            return new Alignment.AlignmentProvider[]{};

        CompoundTag alignmentTag = enchantTag.getCompound("alignment");

        List<Alignment.AlignmentProvider> providers = new ArrayList<>();
        for (String key: alignmentTag.getAllKeys())
        {
            ResourceLocation location = ResourceLocation.parse(key);
            Alignment alignment = EnchantmentRegister.getAlignment(location);
            if (alignment == null)
                continue;

            int level = alignmentTag.getInt(key);
            providers.add(new Alignment.AlignmentProvider(alignment, level));
        }

        return providers.toArray(new Alignment.AlignmentProvider[]{});
    }

    @Suggested
    public static int getAspect(
            ItemStack stack,
            Aspect aspect
    ) {
        ResourceLocation location = EnchantmentRegister.getAspectId(aspect);
        if (location == null)
            return 0;

        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            return 0;

        int consumed = 0;
        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);
        if (enchantTag.contains("consumed")) {
            CompoundTag consumedTag = enchantTag.getCompound("consumed");
            if (consumedTag.contains(location.toString()))
                consumed = consumedTag.getInt(location.toString());
        }

        if (!enchantTag.contains("aspect"))
            return 0;

        CompoundTag aspectTag = enchantTag.getCompound("aspect");
        if (!aspectTag.contains(location.toString()))
            return 0;

        int amount = aspectTag.getInt(location.toString());

        for (Aspect.AspectProvider provider : combineAspects(AwakenAttributeUtil.getAspects(stack)))
        {
            if (provider.aspect() == aspect)
            {
                amount += provider.amount();
                break;
            }
        }

        return amount - consumed;
    }

    public static Aspect.AspectProvider[] getAspects(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            return new Aspect.AspectProvider[]{};

        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);
        if (!enchantTag.contains("aspect"))
            return new Aspect.AspectProvider[]{};

        CompoundTag aspectTag = tag.getCompound("aspect");

        List<Aspect.AspectProvider> providers = new ArrayList<>();
        for (String key: aspectTag.getAllKeys())
        {
            ResourceLocation location = ResourceLocation.parse(key);
            Aspect aspect = EnchantmentRegister.getAspect(location);
            if (aspect == null)
                continue;

            int level = aspectTag.getInt(key);
            providers.add(Aspect.of(level, aspect));
        }

        providers.addAll(AwakenAttributeUtil.getAspects(stack));

        return providers.toArray(new Aspect.AspectProvider[]{});
    }

    public static Aspect[] getAvailableAspects(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            return new Aspect[]{};

        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);
        if (!enchantTag.contains("aspect"))
            return new Aspect[]{};

        CompoundTag aspectTag = enchantTag.getCompound("aspect");
        return aspectTag.getAllKeys().stream().map(k -> EnchantmentRegister.getAspect(ResourceLocation.parse(k))).filter(Objects::nonNull).toArray(Aspect[]::new);
    }

    public static int getCurrentExp(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtExpValueStorage))
            addExpValue(stack, 0, 0);

        return tag.getCompound(nbtExpValueStorage).getInt("current");
    }

    public static int getCurrentExpLevel(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtExpValueStorage))
            addExpValue(stack, 0, 0);

        return tag.getCompound(nbtExpValueStorage).getInt("level");
    }

    public static float getCurrentReinforce(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtReinforceValueStorage))
            addReinforceValue(stack, 0.0F, 0);

        return tag.getCompound(nbtReinforceValueStorage).getFloat("current");
    }

    public static float getEfficiency(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(nbtEfficiencyCapability))
            return -1;

        return tag.getFloat(nbtEfficiencyCapability);
    }

    public static List<UpgradeTier> getForgeTiers(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtForgedValueStorage))
            return List.of();

        ListTag forgedTiers = tag.getCompound(nbtForgedValueStorage).getList("tiers", 8);
        List<UpgradeTier> result = new ArrayList<>();

        for (Tag ftag: forgedTiers)
        {
            if (!(ftag instanceof StringTag stag))
                continue;

            ResourceLocation location = ResourceLocation.parse(stag.getAsString());
            UpgradeTier tier = ForgeUtils.getTier(location);
            if (tier == null)
                continue;

            result.add(tier);
        }

        return result;
    }

    public static int getMaxExp(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains("max"))
            addExpValue(stack, 0, 0);

        return tag.getCompound(nbtExpValueStorage).getInt("max");
    }

    public static CompoundTag getModSubTag(
            CompoundTag tag,
            String sub
    )
    {
        if (!tag.contains(sub))
            tag.put(sub, new CompoundTag());

        return tag.getCompound(sub);
    }

    public static CompoundTag getModTag(
            Entity entity
    )
    {
        CompoundTag tag = entity.getPersistentData();

        return getModSubTag(tag, nbtNamespace);
    }

    public static CompoundTag getModTag(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(nbtNamespace))
            tag.put(nbtNamespace, new CompoundTag());

        return tag.getCompound(nbtNamespace);
    }

    public static CompoundTag getModTag(
            Entity entity,
            String namespace
    )
    {
        CompoundTag tag = getModTag(entity);

        if (!tag.contains(namespace))
            tag.put(namespace, new CompoundTag());

        return tag.getCompound(namespace);
    }

    public static CompoundTag getModTag(
            ItemStack stack,
            String namespace
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(namespace))
            tag.put(namespace, new CompoundTag());

        return tag.getCompound(namespace);
    }

    public static List<Pollinate.PollinateInstance> getPollinate(
            Entity entity
    )
    {
        CompoundTag tag = getModTag(entity);
        if (!tag.contains(nbtPollinateStorage))
            return List.of();

        List<Pollinate.PollinateInstance> instances = new ArrayList<>();
        CompoundTag pollinateTag = tag.getCompound(nbtPollinateStorage);
        for (String key: pollinateTag.getAllKeys())
        {
            com.fomdev.awaken.spore.Pollinate pollinate = SporeManager.getPollinate(ResourceLocation.parse(key));
            if (pollinate == null)
                continue;

            int lvl = pollinateTag.getInt(key);
            if (lvl <= 0)
                continue;

            instances.add(new Pollinate.PollinateInstance(pollinate, lvl));
        }

        return instances;
    }

    public static List<Pollinate.PollinateInstance> getPollinate(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtPollinateStorage))
            return List.of();

        List<Pollinate.PollinateInstance> instances = new ArrayList<>();
        CompoundTag pollinateTag = tag.getCompound(nbtPollinateStorage);
        for (String key: pollinateTag.getAllKeys())
        {
            com.fomdev.awaken.spore.Pollinate pollinate = SporeManager.getPollinate(ResourceLocation.parse(key));
            if (pollinate == null)
                continue;

            int lvl = pollinateTag.getInt(key);
            if (lvl <= 0)
                continue;

            instances.add(new Pollinate.PollinateInstance(pollinate, lvl));
        }

        return instances;
    }

    public static List<Spore.SporeInstance> getSpores(
            Entity entity
    )
    {
        CompoundTag tag = getModTag(entity);
        if (!tag.contains(nbtSporeValueStorage))
            return List.of();

        List<Spore.SporeInstance> instances = new ArrayList<>();
        CompoundTag sporeTag = tag.getCompound(nbtSporeValueStorage);
        for (String key: sporeTag.getAllKeys())
        {
            Spore spore = SporeManager.getSpore(ResourceLocation.parse(key));
            if (spore == null)
                continue;

            int lvl = sporeTag.getInt(key);
            if (lvl <= 0)
                continue;

            instances.add(new Spore.SporeInstance(spore, lvl));
        }

        return instances;
    }

    public static List<Spore.SporeInstance> getSpores(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtSporeValueStorage))
            return List.of();

        List<Spore.SporeInstance> instances = new ArrayList<>();
        CompoundTag sporeTag = tag.getCompound(nbtSporeValueStorage);
        for (String key: sporeTag.getAllKeys())
        {
            Spore spore = SporeManager.getSpore(ResourceLocation.parse(key));
            if (spore == null)
                continue;

            int lvl = sporeTag.getInt(key);
            if (lvl <= 0)
                continue;

            instances.add(new Spore.SporeInstance(spore, lvl));
        }

        return instances;
    }

    public static void putEnchantmentAlignment(
            ItemStack stack,
            Alignment.AlignmentProvider provider
    )
    {
        CompoundTag enchantTag = getModTag(stack, nbtEnchantmentStorage);
        CompoundTag alignmentTag = getModSubTag(enchantTag, "alignment");
        ResourceLocation alignment = EnchantmentRegister.getAlignmentId(provider.alignment());
        if (alignment == null)
            return;

        int amount = provider.level();

        if (!alignmentTag.contains(alignment.toString()))
            alignmentTag.putInt(alignment.toString(), 0);

        int count = alignmentTag.getInt(alignment.toString()) + amount;
        alignmentTag.putInt(alignment.toString(), count);
    }

    @Suggested
    public static void putEnchantmentAlignmentStrict(
            ItemStack stack,
            Alignment.AlignmentProvider provider
    )
    {
        Alignment alignment = provider.alignment();

        for (Aspect.AspectProvider prov: alignment.aspects())
        {
            Aspect aspect = prov.aspect();
            int amount = getAspect(stack, aspect);
            if (amount < prov.amount())
                return;
        }

        for (Aspect.AspectProvider prov: alignment.aspects())
            consumeAspect(stack, prov.aspect(), prov.amount());

        putEnchantmentAlignment(stack, provider);
    }

    public static void putEnchantmentAspect(
            ItemStack stack,
            Aspect.AspectProvider provider
    )
    {
        CompoundTag enchantTag = getModTag(stack, nbtEnchantmentStorage);
        CompoundTag aspectTag = getModSubTag(enchantTag, "aspect");
        ResourceLocation aspect = EnchantmentRegister.getAspectId(provider.aspect());
        if (aspect == null)
            return;

        int amount = provider.amount();

        if (!aspectTag.contains(aspect.toString()))
            aspectTag.putInt(aspect.toString(), 0);

        int count = aspectTag.getInt(aspect.toString()) + amount;
        aspectTag.putInt(aspect.toString(), count);
    }

    public static void putForgeTier(
            ItemStack stack,
            UpgradeTier tier
    )
    {
        CompoundTag forgeTag = getModTag(stack, nbtForgedValueStorage);
        if (!forgeTag.contains("tiers"))
            forgeTag.put("tiers", new ListTag());

        ResourceLocation location = ForgeUtils.getID(tier);
        if (location == null)
            return;

        forgeTag.getList("tiers", 8).add(StringTag.valueOf(location.toString()));
    }
    
    public static void putPrefix(
            ItemStack stack,
            Prefix prefix
    )
    {
        CompoundTag titleTag = getModTag(stack, nbtTitleValueStorage);

        ResourceLocation location = TitleRegister.getPrefixId(prefix);
        if (location == null)
            return;

        titleTag.putString("prefix", location.toString());
    }

    public static void putPollinate(
            Entity entity,
            Pollinate pollinate,
            int lvl
    )
    {
        CompoundTag pollinateTag = getModTag(entity, nbtPollinateStorage);
        ResourceLocation location = SporeManager.getPollinateId(pollinate);
        if (location == null)
            return;

        if (!pollinateTag.contains(location.toString()))
            pollinateTag.putInt(location.toString(), 0);

        pollinateTag.putInt(location.toString(), pollinateTag.getInt(location.toString()) + lvl);
    }

    public static void putPollinate(
            ItemStack stack,
            Pollinate pollinate,
            int lvl
    )
    {
        CompoundTag pollinateTag = getModTag(stack, nbtPollinateStorage);
        ResourceLocation location = SporeManager.getPollinateId(pollinate);
        if (location == null)
            return;

        if (!pollinateTag.contains(location.toString()))
            pollinateTag.putInt(location.toString(), 0);

        pollinateTag.putInt(location.toString(), pollinateTag.getInt(location.toString()) + lvl);
    }

    public static void putSpore(
            Entity entity,
            Spore spore,
            int lvl
    )
    {
        CompoundTag sporeTag = getModTag(entity, nbtSporeValueStorage);
        ResourceLocation location = SporeManager.getSporeId(spore);
        if (location == null)
            return;

        if (!sporeTag.contains(location.toString()))
            sporeTag.putInt(location.toString(), 0);

        sporeTag.putInt(location.toString(), sporeTag.getInt(location.toString()) + lvl);
    }

    public static void putSpore(
            ItemStack stack,
            Spore spore,
            int lvl
    )
    {
        CompoundTag sporeTag = getModTag(stack, nbtSporeValueStorage);
        ResourceLocation location = SporeManager.getSporeId(spore);
        if (location == null)
            return;

        if (!sporeTag.contains(location.toString()))
            sporeTag.putInt(location.toString(), 0);

        sporeTag.putInt(location.toString(), sporeTag.getInt(location.toString()) + lvl);
    }

    public static void putSuffix(
            ItemStack stack,
            Suffix suffix
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtTitleValueStorage))
            tag.put(nbtTitleValueStorage, new CompoundTag());
        CompoundTag titleTag = tag.getCompound(nbtTitleValueStorage);

        ResourceLocation location = TitleRegister.getSuffixId(suffix);
        if (location == null)
            return;

        titleTag.putString("suffix", location.toString());
    }

    public static void refreshDamage(
            ItemStack stack,
            float factor
    )
    {
        int damage = stack.getDamageValue();
        int newDamage = (int) (damage * (1 + factor));

        stack.setDamageValue(newDamage);
    }

    public static void resetEnchant(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtEnchantmentStorage))
            return;

        tag.remove(nbtEnchantmentStorage);
        tag.put(nbtEnchantmentStorage, new CompoundTag());
    }

    public static void resetTitle(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtTitleValueStorage))
            return;

        tag.remove(nbtTitleValueStorage);
        tag.put(nbtTitleValueStorage, new CompoundTag());
    }

    public static void serializeAwakenLevel(
            Player entity,
            float level
    )
    {
        CompoundTag tag = getModTag(entity);

        tag.putFloat(nbtAwakenLevelStorage, level);
    }

    public static void serializeMaxForgeLevel(
            ItemStack stack,
            int level
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtForgedValueStorage))
            tag.put(nbtForgedValueStorage, new CompoundTag());

        CompoundTag forgeTag = tag.getCompound(nbtForgedValueStorage);
        forgeTag.putInt("max", level);
    }

    public static void serializeReinforce(
            ItemStack stack,
            ReinforcementLevels level
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtReinforceValueStorage))
            tag.put(nbtReinforceValueStorage, new CompoundTag());

        CompoundTag reinforceTag = tag.getCompound(nbtReinforceValueStorage);
        int value = level.getLevel();

        reinforceTag.putInt("level", value);
    }

    public static void serializeTitle(
            ItemStack stack,
            Title title
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtTitleValueStorage))
            tag.put(nbtTitleValueStorage, new CompoundTag());

        CompoundTag titleTag = tag.getCompound(nbtTitleValueStorage);
        ResourceLocation location = TitleRegister.getTitleId(title);
        if (location == null) throw new IllegalStateException("Illegal title: not registered");

        titleTag.putString("title", location.toString());
    }

    public static void serializeQuality(
            ItemStack stack,
            Quality quality
    )
    {
        CompoundTag tag = getModTag(stack);

        if (!tag.contains(nbtQualityValueStorage))
            tag.put(nbtQualityValueStorage, new CompoundTag());

        ResourceLocation qualityLocation = QualityUtil.getQualityId(quality);
        if (qualityLocation == null) throw new IllegalStateException("Illegal quality: not registered");

        addEnchantValue(stack, quality.enchant(), 0);
        tag.getCompound(nbtQualityValueStorage).putString("id", qualityLocation.toString());
    }

    public static void setEfficiency(
            ItemStack stack,
            float efficiency
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putFloat(nbtEfficiencyCapability, efficiency);
    }

    public static void setLores(
            ItemStack stack,
            String lore
    )
    {
        CompoundTag tag = stack.getOrCreateTagElement("display");
        ListTag lores = tag.getList("Lore", 8);
        lores.add(8, StringTag.valueOf(lore));
    }

    public static void setMaxDamage(
            ItemStack stack,
            int damage
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        int actual = damage;

        while (stack.getMaxDamage() >= actual) // Prevents if the actual damage is larger than the actual one
            actual++;

        tag.putInt("MaxDamage", actual);
    }

    public static void setMaxExp(
            ItemStack stack,
            int exp
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtExpValueStorage))
            addExpValue(stack, 0, 0);

        tag.getCompound(nbtExpValueStorage).putInt("max", exp);
    }

    public static void setTrim(
            ItemStack stack,
            ResourceLocation material,
            ResourceLocation pattern
    )
    {
        CompoundTag tag = stack.getOrCreateTagElement("Trim");
        tag.putString("material", material.toString());
        tag.putString("pattern", pattern.toString());
    }

    public static void updateExp(
            Player player,
            ItemStack stack,
            float rewardFactor
    )
    {
        int current = getCurrentExp(stack);
        int max     = getMaxExp(stack);
        Quality quality = deserializeQuality(stack);
        float nextMaxFactor = quality == null? EquipmentExperience.defaultMaxExperienceFactor: quality.upgradeFactor();

        while (current >= max)
        {
            int newValue = current - max;

            addExpValue(stack, max, 1);
            addExpLevel(stack, 1, 0);
            setMaxExp(stack, (int) (max * nextMaxFactor));

            CompoundTag tag = getModTag(stack).getCompound(nbtExpValueStorage);
            tag.putInt("current", newValue);
            refreshDamage(stack, rewardFactor);

            current = getCurrentExp(stack);
            max     = getMaxExp(stack);

            player.sendSystemMessage(
                    Component.translatable(
                            "chat.congrates_player_tool_upgrade.msg"
                    ).append(
                            Component.literal(
                                    ": " + getCurrentExpLevel(stack)
                            )
                    ).withStyle(ChatFormatting.GOLD)
            );
        }
    }

    private static List<Aspect.AspectProvider> combineAspects(
            List<Aspect.AspectProvider> providers
    )
    {
        List<Aspect.AspectProvider> result = new ArrayList<>();
        Map<Aspect, Integer> cache = new HashMap<>();

        for (Aspect.AspectProvider prov: providers)
        {
            if (!cache.containsKey(prov.aspect()))
                cache.put(prov.aspect(), 0);

            cache.put(prov.aspect(), cache.get(prov.aspect()) + prov.amount());
        }

        for (Map.Entry<Aspect, Integer> entry: cache.entrySet())
        {
            result.add(new Aspect.AspectProvider()
            {
                @Override
                public int amount()
                {
                    return entry.getValue();
                }

                @Override
                public @NotNull Aspect aspect()
                {
                    return entry.getKey();
                }
            });
        }

        return result;
    }
}