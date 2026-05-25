package com.fomdev.awaken.title;

import com.fomdev.awaken.enchanting.Alignment;

public interface Suffix
{
    String id();
    int additionalDurability();
    Alignment[] alignments();

    static Suffix of(
            String id,
            int durability,
            Alignment[] alignments
    )
    {
        return new Suffix()
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
            public Alignment[] alignments()
            {
                return alignments;
            }
        };
    }
}