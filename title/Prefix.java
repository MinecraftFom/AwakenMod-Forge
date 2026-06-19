package com.fomdev.awaken.title;

import com.fomdev.awaken.enchanting.Aspect;

public interface Prefix
{
    String id();
    int additionalDurability();
    Aspect.AspectProvider[] aspects();

    static Prefix of(
            String id,
            int durability,
            Aspect.AspectProvider[] aspects
    )
    {
        return new Prefix()
        {
            @Override
            public String id()
            {
                return id;
            }

            @Override
            public int additionalDurability()
            {
                return durability;
            }

            @Override
            public Aspect.AspectProvider[] aspects()
            {
                return aspects;
            }
        };
    }
}