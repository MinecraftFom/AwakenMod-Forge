package com.fomdev.awaken.title;

import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.function.Function;

public interface Title
{
    String id();
    CompoundAttribute[] attrs(float factor);

    record CompoundAttribute
            (
                    Attribute attr,
                    double amount,
                    AttributeModifier.Operation operation
            )
    {};

    static Title of(
            String id,
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
            public CompoundAttribute[] attrs(float factor)
            {
                return procedure.apply(factor);
            }
        };
    }
}