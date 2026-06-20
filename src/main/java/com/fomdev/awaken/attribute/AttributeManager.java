package com.fomdev.awaken.attribute;

import com.fomdev.awaken.gen.shuffle.WeightedEntry;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.*;

public class AttributeManager
{
    public static final Map<EquipmentSlot, List<WeightedEntry<SetAttribute>>> attributes = new HashMap<>();

    public static void addAttribute(
            SetAttribute attribute
    )
    {
        for (EquipmentSlot slot: attribute.slots())
        {
            attributes.computeIfAbsent(slot, s -> new ArrayList<>())
                    .add(attribute);
        }
    }

    public static List<WeightedEntry<SetAttribute>> getAttributes(
            EquipmentSlot slot
    )
    {
        return attributes.get(slot);
    }
}