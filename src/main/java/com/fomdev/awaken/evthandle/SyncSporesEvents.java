package com.fomdev.awaken.evthandle;

import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.NBTUtil;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class SyncSporesEvents
{
    @SubscribeEvent
    public static void onPlayerClone(
            PlayerEvent.Clone clone
    )
    {
        NBTUtil.getPollinate(clone.getOriginal()).forEach(pi -> NBTUtil.putPollinate(clone.getEntity(), pi.pollinate(), pi.lvl()));
        NBTUtil.getSpores(clone.getOriginal()).forEach(si -> NBTUtil.putSpore(clone.getEntity(), si.spore(), si.lvl()));
    }
}