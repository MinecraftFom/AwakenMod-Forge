package com.fomdev.awaken.nbt;

import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.quality.QualityUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public class NBTUtil
{
    private static final String nbtNamespace = "awakened";

    public static final String nbtAwakenLevelStorage   = "awakenedLevel";
    public static final String nbtEnchantValueStorage  = "awakenedEnchant";
    public static final String nbtExpValueStorage      = "awakenedExp";
    public static final String nbtQualityValueStorage  = "awakenedQuality";

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
        if (!expTag.contains("max")) expTag.putInt("max", 0);

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

        if (!expTag.contains("current")) expTag.putInt("current", 0);
        if (!expTag.contains("level")) expTag.putInt("level", 0);
        if (!expTag.contains("max")) expTag.putInt("max", 0);

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

    public static Float deserializeAwakenLevel(
            LivingEntity entity
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
        if (tag.get("id") == null) return null;

        return QualityUtil.getQuality(ResourceLocation.parse(tag.getString("id")));
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
            LivingEntity entity
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

    public static void serializeAwakenLevel(
            LivingEntity entity,
            float level
    )
    {
        CompoundTag tag = getModTag(entity);

        tag.putFloat(nbtAwakenLevelStorage, level);
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

        tag.putInt("max", exp);
    }

    public static void updateExp(
            ItemStack stack,
            int nextMax
    )
    {
        int current = getCurrentExp(stack);
        int max     = getMaxExp(stack);

        if (current < max)
            return;

        int newValue = current - max;

        addExpValue(stack, 1, 0);
        setMaxExp(stack, nextMax);

        CompoundTag tag = getModTag(stack).getCompound(nbtExpValueStorage);
        tag.putInt("current", newValue);
    }
}