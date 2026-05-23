package com.fomdev.awaken.quality;

import com.fomdev.awaken.exp.EquipmentExperience;
import com.fomdev.awaken.gen.GeneratingMobTypes;

import java.awt.*;

public interface Quality
{
    String id();
    Color color();

    boolean              infectedOnly();

    float                chance();
    float                durability();
    int                  enchant();
    float                factor();
    int                  level();
    GeneratingMobTypes[] mobs();

    default int maxUpgradeLevel() { return EquipmentExperience.defaultInitialExperienceRequirement; }
    default float upgradeFactor() { return EquipmentExperience.defaultMaxExperienceFactor; }

    static Quality of
            (
                    String id,
                    Color color,
                    boolean infected,
                    float chance,
                    float durability,
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
            public Color color()
            {
                return color;
            }

            @Override
            public float chance()
            {
                return chance;
            }

            @Override
            public float durability()
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

    static Quality of
            (
                    String id,
                    Color color,
                    boolean infected,
                    float chance,
                    float durability,
                    int enchant,
                    float factor,
                    int level,
                    GeneratingMobTypes[] mobs,
                    int maxUpgradeLevel,
                    float upgradeFactor
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
            public Color color()
            {
                return color;
            }

            @Override
            public float chance()
            {
                return chance;
            }

            @Override
            public float durability()
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

            @Override
            public int maxUpgradeLevel()
            {
                return maxUpgradeLevel;
            }

            @Override
            public float upgradeFactor()
            {
                return upgradeFactor;
            }
        };
    }
}