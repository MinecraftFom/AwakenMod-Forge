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
import com.fomdev.awaken.title.Title;
import com.fomdev.awaken.title.TitleRegister;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class NBTUtil
{
    private static final String nbtNamespace = "awakened";

    public static final String nbtAwakenLevelStorage    = "awakenedLevel";
    public static final String nbtEnchantmentStorage    = "awakenedEnchantments";
    public static final String nbtEnchantValueStorage   = "awakenedEnchantable";
    public static final String nbtExpValueStorage       = "awakenedExp";
    public static final String nbtForgedValueStorage    = "awakenedForged";
    public static final String nbtReinforceValueStorage = "awakenedReinforce";
    public static final String nbtTitleValueStorage     = "awakenedTitle";
    public static final String nbtQualityValueStorage   = "awakenedQuality";

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

    public static Float deserializeAwakenLevel(
            Player entity
    )
    {
        CompoundTag tag = getModTag(entity);

        if (!tag.contains(nbtAwakenLevelStorage))
            serializeAwakenLevel(entity, 0.0F);

        return tag.getFloat(nbtAwakenLevelStorage);
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

        CompoundTag alignmentTag = tag.getCompound("alignment");

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

    public static Aspect.AspectProvider[] getAspects(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            return new Aspect.AspectProvider[]{};

        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);
        if (!tag.contains("aspect"))
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

        return providers.toArray(new Aspect.AspectProvider[]{});
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

    public static int getMaxExp(
            ItemStack stack
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains("max"))
            addExpValue(stack, 0, 0);

        return tag.getCompound(nbtExpValueStorage).getInt("max");
    }

    public static CompoundTag getModTag(
            Player entity
    )
    {
        CompoundTag tag = entity.getPersistentData();

        if (!tag.contains(nbtNamespace))
            tag.put(nbtNamespace, new CompoundTag());

        return tag.getCompound(nbtNamespace);
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

    public static void putEnchantmentAlignment(
            ItemStack stack,
            Alignment.AlignmentProvider provider
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            tag.put(nbtEnchantmentStorage, new CompoundTag());

        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);
        if (!tag.contains("alignment"))
            tag.put("alignment", new CompoundTag());

        CompoundTag alignmentTag = enchantTag.getCompound("alignment");
        ResourceLocation alignment = EnchantmentRegister.getAlignmentId(provider.alignment());
        if (alignment == null)
            return;

        int amount = provider.level();

        if (!alignmentTag.contains(alignment.toString()))
            alignmentTag.putInt(alignment.toString(), 0);

        int count = alignmentTag.getInt(alignment.toString()) + amount;
        alignmentTag.putInt(alignment.toString(), count);
    }

    public static void putEnchantmentAspect(
            ItemStack stack,
            Aspect.AspectProvider provider
    )
    {
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtEnchantmentStorage))
            tag.put(nbtEnchantmentStorage, new CompoundTag());

        CompoundTag enchantTag = tag.getCompound(nbtEnchantmentStorage);
        if (!tag.contains("aspect"))
            tag.put("aspect", new CompoundTag());

        CompoundTag aspectTag = enchantTag.getCompound("aspect");
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
        CompoundTag tag = getModTag(stack);
        if (!tag.contains(nbtForgedValueStorage))
            tag.put(nbtForgedValueStorage, new CompoundTag());

        CompoundTag forgeTag = tag.getCompound(nbtForgedValueStorage);
        if (!forgeTag.contains("tiers"))
            forgeTag.put("tiers", new ListTag());

        forgeTag.getList("tiers", 8).add(8, StringTag.valueOf(tier.id()));
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

        tag.getCompound(nbtQualityValueStorage).putString("id", qualityLocation.toString());
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

    public static void updateExp(
            ItemStack stack,
            float rewardFactor
    )
    {
        int current = getCurrentExp(stack);
        int max     = getMaxExp(stack);
        Quality quality = deserializeQuality(stack);
        float nextMaxFactor = quality == null? EquipmentExperience.defaultMaxExperienceFactor: quality.factor();

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
        }
    }
}