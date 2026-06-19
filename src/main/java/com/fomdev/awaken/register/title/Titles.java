package com.fomdev.awaken.register.title;

import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.awaken.title.Title;
import com.fomdev.awaken.title.TitleRegister;
import com.fomdev.flib.load.ForceLoad;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

@ForceLoad(AwakenRegistries.SIG_AWAKEN_TITLE)
public class Titles
{
    public static final Title TITLE_ORDINARY;

    static
    {
        TITLE_ORDINARY = TitleRegister.register(
                AwakenContent.MODID,
                Title.of(
                        "ordinary",
                        100,
                        (p) -> new Title.CompoundAttribute(Attributes.LUCK, 0.1D, AttributeModifier.Operation.ADDITION, EquipmentSlot.MAINHAND)
                )
        );
    }

    @ForceLoad
    public static void placeholder() {}
}