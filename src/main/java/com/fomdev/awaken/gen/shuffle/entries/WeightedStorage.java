package com.fomdev.awaken.gen.shuffle.entries;

public class WeightedStorage<T>
{
    private final T entry;
    private final double chance;

    public WeightedStorage(
            T entry,
            double chance
    )
    {
        this.entry = entry;
        this.chance = chance;
    }

    public T getEntry()
    {
        return entry;
    }

    public double chance()
    {
        return chance;
    }
}