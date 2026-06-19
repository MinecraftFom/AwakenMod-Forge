package com.fomdev.awaken.quality;

import com.fomdev.awaken.exp.EquipmentExperience;

import java.awt.*;
import java.util.List;

public interface Quality
{
    String id();
    List<Color> color();

    int                  enchant();
    float                factor();
    float                level();

    default int maxUpgradeLevel() { return EquipmentExperience.defaultInitialExperienceRequirement; }
    default float upgradeFactor() { return EquipmentExperience.defaultMaxExperienceFactor; }
    default ColorPattern colorPattern() { return ColorPattern.SINGLE; }

    static Quality of
            (
                    String id,
                    int enchant,
                    float factor,
                    float level,
                    ColorPattern pattern,
                    Color... color
            )
    {
        return new Quality()
        {
            @Override
            public String id()
            {
                return id;
            }

            @Override
            public List<Color> color()
            {
                return List.of(color);
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
            public float level()
            {
                return level;
            }

            @Override
            public ColorPattern colorPattern()
            {
                return pattern;
            }
        };
    }

    static Quality of
            (
                    String id,
                    int enchant,
                    float factor,
                    float level,
                    int maxUpgradeLevel,
                    float upgradeFactor,
                    ColorPattern pattern,
                    Color... color
            )
    {
        return new Quality()
        {
            @Override
            public String id()
            {
                return id;
            }

            @Override
            public List<Color> color()
            {
                return List.of(color);
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
            public float level()
            {
                return level;
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

            @Override
            public ColorPattern colorPattern()
            {
                return pattern;
            }
        };
    }

    enum ColorPattern
    {
        SINGLE,
        MULTIPLE,
        CONTINUE
    }
}