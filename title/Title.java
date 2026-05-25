package com.fomdev.awaken.title;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.function.Function;

public interface Title
{
    String id();
    CompoundAttribute[] attrs(float factor);
    int additionalDurability();

    record CompoundAttribute
            (
                    Attribute attr,
                    double amount,
                    AttributeModifier.Operation operation
            )
    {};

    static Title of(
            String id,
            int additionalDurability,
            Function<Float, CompoundAttribute[]> procedure
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
            public CompoundAttribute[] attrs(float factor)
            {
                return procedure.apply(factor);
            }
        };
    }
}