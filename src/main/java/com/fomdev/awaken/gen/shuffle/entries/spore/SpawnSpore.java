package com.fomdev.awaken.gen.shuffle.entries.spore;

import com.fomdev.awaken.gen.shuffle.WeightedEntry;
import com.fomdev.awaken.gen.shuffle.entries.WeightedStorage;
import com.fomdev.awaken.spore.Spore;
import com.fomdev.awaken.util.Util;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SpawnSpore extends WeightedStorage<Spore> implements WeightedEntry<SpawnSpore>
{
    public static List<WeightedEntry<SpawnSpore>> spawn = new ArrayList<>();

    public SpawnSpore(Spore entry, double chance)
    {
        super(entry, chance);
        spawn.add(this);
    }

    public static Spore shuffle(
            Random random,
            double diff
    )
    {
        return Objects.requireNonNull(Util.weightedShuffle(random, diff, spawn)).getEntry();
    }

    public static Spore shuffle(
            Random random,
            EquipmentSlot slot,
            double diff
    )
    {
        return Objects.requireNonNull(Util.weightedShuffle(random, diff, spawn.stream().filter(v -> Util.contains(v.get().getEntry().suitableOn(), slot)).toList())).getEntry();
    }
}