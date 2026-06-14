package com.fomdev.awaken.title;

import com.fomdev.flib.util.Sized;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.Arrays;
import java.util.function.Function;

public interface Title
{
    EquipmentSlot[] hand = new EquipmentSlot[]{
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND
    };

    EquipmentSlot[] body = new EquipmentSlot[]{
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    String id();
    CompoundAttribute attrs(float factor);
    int additionalDurability();

    record CompoundAttribute
            (
                    Attribute attr,
                    double amount,
                    AttributeModifier.Operation operation,
                    EquipmentSlot[] slot
            )
    {
        public CompoundAttribute(
                Attribute attr,
                double amount,
                AttributeModifier.Operation operation,
                @Sized(min = 1, max = Integer.MAX_VALUE) EquipmentSlot slots
        )
        {
            this(attr, amount, operation, new EquipmentSlot[]{slots});
        }
    };

    static Title of(
            String id,
            int additionalDurability,
            Function<Float, CompoundAttribute> procedure
    )
    {
        return new Title()
        {
            @Override
            public String id()
            {
                return id;
            }

            @Override
            public int additionalDurability()
            {
                return additionalDurability;
            }

            @Override
            public CompoundAttribute attrs(float factor)
            {
                return procedure.apply(factor);
            }
        };
    }
}