package com.fomdev.awaken.register.title;

import com.fomdev.awaken.enchanting.Aspect;
import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.TitleRegister;
import com.fomdev.flib.load.ForceLoad;

@ForceLoad(AwakenRegistries.SIG_AWAKEN_PREFIX)
public class Prefixes
{
    public static final Prefix PREFIX_ORDINARY;

    static
    {
        PREFIX_ORDINARY = TitleRegister.register(
                AwakenContent.MODID,
                Prefix.of(
                        "ordinary",
                        0,
                        new Aspect.AspectProvider[]{}
                )
        );
    }

    @ForceLoad
    public static void placeholder() {}
}