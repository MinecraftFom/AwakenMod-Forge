package com.fomdev.awaken.register.title;

import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.TitleRegister;
import com.fomdev.flib.load.ForceLoad;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.Attributes;

@ForceLoad(AwakenRegistries.SIG_AWAKEN_SUFFIX)
public class Suffixes
{
    public static final Suffix SUFFIX_ORDINARY;

    static
    {
        SUFFIX_ORDINARY = TitleRegister.register(
                AwakenContent.MODID,
                Suffix.of(
                        "ordinary",
                        0,
                        Attributes.LUCK,
                        1.0F,
                        new MobEffectInstance[]{
                                new MobEffectInstance(MobEffects.LUCK, 4)
                        }
                )
        );
    }

    @ForceLoad
    public static void placeholder() {}
}