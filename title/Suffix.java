package com.fomdev.awaken.title;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

import java.util.Arrays;

public interface Suffix
{
    String id();
    int additionalDurability();
    MobEffectInstance[] effects();

    static Suffix of(
            String id,
            int durability,
            MobEffectInstance[] effects
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
            public MobEffectInstance[] effects()
            {
                return effects;
            }
        };
    }

    static Suffix of(
            String id,
            int durability,
            MobEffect[] effects
    )
    {
        return of(id, durability, Arrays.stream(effects).map(effect -> new MobEffectInstance(effect, 0)).toArray(MobEffectInstance[]::new));
    }
}