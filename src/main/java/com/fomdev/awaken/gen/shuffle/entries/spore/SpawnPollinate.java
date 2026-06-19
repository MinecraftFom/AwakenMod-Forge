package com.fomdev.awaken.gen.shuffle.entries.spore;

import com.fomdev.awaken.gen.shuffle.WeightedEntry;
import com.fomdev.awaken.gen.shuffle.entries.WeightedStorage;
import com.fomdev.awaken.spore.Pollinate;
import com.fomdev.awaken.util.Util;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.*;

public class SpawnPollinate extends WeightedStorage<Pollinate> implements WeightedEntry<SpawnPollinate>
{
    public static List<WeightedEntry<SpawnPollinate>> spawn = new ArrayList<>();

    public SpawnPollinate(Pollinate entry, double chance)
    {
        super(entry, chance);
        spawn.add(this);
    }

    public static Pollinate shuffle(
            Random random,
            double diff
    )
    {
        return Objects.requireNonNull(Util.weightedShuffle(random, diff, spawn)).getEntry();
    }

    public static Pollinate shuffle(
            Random random,
            EquipmentSlot slot,
            double diff
    )
    {
        return Objects.requireNonNull(Util.weightedShuffle(random, diff, spawn.stream().filter(v -> Util.contains(v.get().getEntry().suitableOn(), slot)).toList())).getEntry();
    }
}