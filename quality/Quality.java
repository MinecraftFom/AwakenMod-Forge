package com.fomdev.awaken.quality;

import com.fomdev.awaken.gen.GeneratingMobTypes;

public interface Quality
{
    String id();

    boolean              infectedOnly();

    float                chance();
    int                  durability();
    int                  enchant();
    float                factor();
    int                  level();
    GeneratingMobTypes[] mobs();

    static Quality of
            (
                    String id,
                    boolean infected,
                    float chance,
                    int durability,
                    int enchant,
                    float factor,
                    int level,
                    GeneratingMobTypes[] mobs
            )
    {
        return new Quality()
        {
            @Override
            public boolean infectedOnly()
            {
                return infected;
            }

            @Override
            public String id()
            {
                return id;
            }

            @Override
            public float chance()
            {
                return chance;
            }

            @Override
            public int durability()
            {
                return durability;
            }

            @Override
            public int enchant()
            {
                return enchant;
            }

            @Override
            public float factor()
            {
                return factor;
            }

            @Override
            public int level()
            {
                return level;
            }

            @Override
            public GeneratingMobTypes[] mobs()
            {
                return mobs;
            }
        };
    }
}