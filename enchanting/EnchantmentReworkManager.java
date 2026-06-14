package com.fomdev.awaken.enchanting;

import com.fomdev.awaken.init.Awaken;
import net.minecraftforge.event.enchanting.EnchantmentLevelSetEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class EnchantmentReworkManager
{
    @SubscribeEvent
    public static void onEnchantmentLevelChange(EnchantmentLevelSetEvent event)
    {
        Awaken.LOGGER.debug("ERM> Caught ench evt, lvl: {}", event.getEnchantLevel());
        event.setEnchantLevel(20); // TEST
    }
}