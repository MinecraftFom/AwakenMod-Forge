package com.fomdev.awaken.attribute;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.*;

public class AttributeManager
{
    public static final Map<EquipmentSlot, List<SetAttribute>> attributes = new HashMap<>();

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

    public static List<SetAttribute> getAttributes(
            EquipmentSlot slot
    )
    {
        return attributes.get(slot);
    }
}