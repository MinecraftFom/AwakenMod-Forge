package com.fomdev.awaken.attribute;

import com.fomdev.awaken.gen.shuffle.WeightedEntry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public interface SetAttribute extends WeightedEntry<SetAttribute>
{
    EquipmentSlot[] weapons = new EquipmentSlot[]{
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    };

    EquipmentSlot[] armors = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    Attribute attribute();
    double amount();
    EquipmentSlot[] slots();
    AttributeModifier.Operation operation();

    boolean positive();
    double chance();

    static SetAttribute of(
            Attribute attribute,
            AttributeModifier.Operation operation,
            double amount,
            boolean positive,
            double chance,
            EquipmentSlot... slots
    )
    {
        return new SetAttribute()
        {
            @Override
            public Attribute attribute()
            {
                return attribute;
            }

            @Override
            public double amount()
            {
                return amount;
            }

            @Override
            public EquipmentSlot[] slots()
            {
                return slots.length == 0? weapons: slots;
            }

            @Override
            public AttributeModifier.Operation operation()
            {
                return operation;
            }

            @Override
            public boolean positive()
            {
                return positive;
            }

            @Override
            public double chance()
            {
                return chance;
            }
        };
    }
}