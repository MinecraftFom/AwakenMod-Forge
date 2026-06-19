package com.fomdev.awaken.nbt;

import com.fomdev.awaken.init.AwakenRPG;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class HealthUtil
{
    public static final String nbtHealth = "maxHealth";

    public static void setMaxHealthPersistent(Player player, float maxHealth)
    {
        player.getPersistentData().putFloat(nbtHealth, maxHealth);

        var attribute = player.getAttribute(Attributes.MAX_HEALTH);
        if (attribute != null)
            attribute.setBaseValue(maxHealth);
    }

    public static void applyPersistentMaxHealth(Player player)
    {
        if (player.getPersistentData().contains(nbtHealth))
        {
            float maxHealth = player.getPersistentData().getFloat(nbtHealth);
            setMaxHealthPersistent(player, maxHealth);
        }
    }

    @SubscribeEvent
    public static void onSyncData(PlayerEvent.Clone event)
    {
        if (event.getEntity() == null)
            return;

        applyPersistentMaxHealth(event.getEntity());
    }
}