package com.fomdev.awaken.register.level;

import com.fomdev.awaken.awaken.AwakenLevel;
import com.fomdev.awaken.awaken.AwakenLevelRegister;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.flib.load.ForceLoad;

import java.awt.*;

@ForceLoad
public class AwakenLevels
{
    public static final AwakenLevel levelNaive;
    public static final AwakenLevel levelNovice;

    static
    {
        levelNaive = AwakenLevelRegister.register(
                AwakenLevel.of("naive", Color.LIGHT_GRAY, 0.0F),
                AwakenRPG.MODID
        );

        levelNovice = AwakenLevelRegister.register(
                AwakenLevel.of("novice", Color.DARK_GRAY, 100.0F),
                AwakenRPG.MODID
        );
    }

    @ForceLoad
    public static void placeholder() {}
}