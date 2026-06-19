package com.fomdev.awaken.util;

import com.fomdev.awaken.gen.shuffle.WeightedEntry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class Util
{
    public record AttributeHolder(
            Attribute attr,
            Double amount,
            AttributeModifier.Operation operation,
            @Nullable String id,
            @Nullable EquipmentSlot slot,
            @Nullable UUID uuid
    ){
        public AttributeHolder(
                Attribute attr,
                String id,
                EquipmentSlot slot,
                Double amount,
                AttributeModifier.Operation operation
        )
        {
            this(attr, amount, operation, id, slot, null);
        }

        public AttributeHolder(
                Attribute attr,
                Double amount,
                AttributeModifier.Operation operation
        )
        {
            this(attr, amount, operation, null, null, null);
        }
    }

    public static <T> boolean contains(
            T[] list,
            T entry
    )
    {
        return List.of(list).contains(entry);
    }

    public static EquipmentSlot getSlot(
            ItemStack stack
    )
    {
        if (stack.getItem() instanceof ArmorItem armor)
            return armor.getEquipmentSlot();

        if (stack.getItem() instanceof ShieldItem)
            return EquipmentSlot.OFFHAND;

        return EquipmentSlot.MAINHAND;
    }

    public static <T> T ifNull(
            T org,
            T rep
    )
    {
        return org == null? rep: org;
    }

    public static boolean noneNull(
            Object... objects
    )
    {
        return Arrays.stream(objects).filter(Objects::nonNull).toList().isEmpty();
    }

    @Nullable
    public static <T extends WeightedEntry<T>> T weightedShuffle(
            Random random,
            double diff,
            List<WeightedEntry<T>> entries
    )
    {
        AtomicReference<Double> weight = new AtomicReference<>(0.0D);
        entries.forEach(e -> weight.updateAndGet(v -> v + e.chance()));
        double weightValue = weight.get();

        if (weightValue <= 0)
            return null;

        double rollFactor = random.nextDouble(weightValue);
        double cumulative = 0.0D;

        for (WeightedEntry<T> entry: entries)
        {
            if (entry.diff() > diff)
                continue;

            double w = entry.chance() * diff;
            if (w <= 0)
                continue;

            cumulative += w;
            if (rollFactor <= cumulative)
                return entry.get();
        }

        return null;
    }
}