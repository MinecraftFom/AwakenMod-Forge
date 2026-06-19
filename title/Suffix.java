package com.fomdev.awaken.title;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;

import java.util.Arrays;

public interface Suffix
{
    String id();
    int additionalDurability();
    Attribute triggerAttribute();
    float modifyFactor();
    MobEffectInstance[] effects();

    static Suffix of(
            String id,
            int durability,
            Attribute trigger,
            float factor,
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
            public Attribute triggerAttribute()
            {
                return trigger;
            }

            @Override
            public float modifyFactor()
            {
                return factor;
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
            Attribute trigger,
            float factor,
            MobEffect[] effects
    )
    {
        return of(id, durability, trigger, factor, Arrays.stream(effects).map(effect -> new MobEffectInstance(effect, 0)).toArray(MobEffectInstance[]::new));
    }
}