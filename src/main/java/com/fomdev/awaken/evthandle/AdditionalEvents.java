package com.fomdev.awaken.evthandle;

import com.fomdev.awaken.init.Awaken;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class AdditionalEvents
{
    @SubscribeEvent
    public static void onStopStepping(BlockEvent.FarmlandTrampleEvent event)
    {
        event.cancel();
    }
}