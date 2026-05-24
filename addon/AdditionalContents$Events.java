package com.fomdev.awaken.addon;

import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AdditionalContents$Events
{
    @SubscribeEvent
    public static void stopTrample(BlockEvent.FarmlandTrampleEvent event)
    {
        event.cancel();
    }
}