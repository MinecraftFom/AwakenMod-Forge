package com.fomdev.awaken.reinforce;

import net.minecraft.network.chat.Component;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public enum ReinforcementLevels
{
    NORMAL(0, 0.0F, 0, "level.normal.name", Color.WHITE),
    HARDENED(1, 100.0F, 0.1F, "level.hardened.name", Color.DARK_GRAY),
    STRENGTHENED(2, 500.0F, 0.25F, "level.strengthened.name", Color.LIGHT_GRAY),
    QUENCHED(3, 2000.0F, 0.3F, "level.quenched.name", Color.GRAY),
    STIFF(4, 10000.0F, 0.35F, "level.stiff.name", Color.GREEN),
    OBSTINATE(5, 25000.0F, 0.5F, "level.obstinate.name", Color.CYAN),
    EXPERT(6, 50000.0F, 0.75F, "level.expert.name", Color.PINK),
    SEVERITY(7, 200000.0F, 1.25F, "level.severity.name", Color.MAGENTA),
    FIRM(8, 500000.0F, 1.75F, "level.firm.name", Color.YELLOW),
    SOLID(9, 1000000.0F, 2.25F, "level.solid.name", new Color(0xF0, 0x00, 0xFF)),
    UNBREAKABLE(10, 2147483647.0F, 1000000, "level.unbreakable.name", new Color(0xFF, 0x00, 0xAF));

    private static final Map<Integer, ReinforcementLevels> levels = new HashMap<>();

    private final Color color;
    private final float durability;
    private final String unlocalized;
    private final float required;
    private final int level;

    ReinforcementLevels(final int lvl, final float required, final float durabilityFactor, final String name, final Color color)
    {
        this.color = color;
        this.durability = durabilityFactor;
        this.level = lvl;
        this.required = required;
        this.unlocalized = name;
    }

    public Color getColor()
    {
        return this.color;
    }

    public float getDurability()
    {
        return this.durability;
    }

    public int getLevel()
    {
        return this.level;
    }

    public String getName()
    {
        return this.unlocalized;
    }

    public float getRequired()
    {
        return this.required;
    }

    public static ReinforcementLevels getLevel(int level)
    {
        return levels.get(level);
    }

    public static ReinforcementLevels getNextLevel(ReinforcementLevels level)
    {
        return getLevel(level.getLevel());
    }

    static
    {
        levels.put(0, NORMAL);
        levels.put(1, HARDENED);
        levels.put(2, STRENGTHENED);
        levels.put(3, QUENCHED);
        levels.put(4, STRENGTHENED);
        levels.put(5, OBSTINATE);
        levels.put(6, EXPERT);
        levels.put(7, SEVERITY);
        levels.put(8, FIRM);
        levels.put(9, SOLID);
        levels.put(10, UNBREAKABLE);
    }
}