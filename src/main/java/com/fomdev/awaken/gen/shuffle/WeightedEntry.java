package com.fomdev.awaken.gen.shuffle;

public interface WeightedEntry<T extends WeightedEntry<T>>
{
    double chance();
    default double diff() { return 0.0D; }
    default T get() { return (T) this; }
}