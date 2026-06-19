package com.fomdev.awaken.init;

import com.fomdev.awaken.attribute.AwakenAttributes;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AwakenContent.MODID)
public class AwakenContent
{
    public static final String MODID = "awaken_content";

    public AwakenContent()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        AwakenAttributes.register(bus);
    }
}