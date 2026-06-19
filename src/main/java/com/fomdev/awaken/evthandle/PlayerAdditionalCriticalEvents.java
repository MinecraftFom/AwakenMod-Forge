package com.fomdev.awaken.evthandle;

import com.fomdev.awaken.register.attribute.AwakenAttributes;
import com.fomdev.awaken.init.Awaken;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class PlayerAdditionalCriticalEvents
{
    public static final Random random = new Random();

    @SubscribeEvent
    public static void onNormalAttack(LivingAttackEvent event)
    {
        if (!(event.getSource().getEntity() instanceof Player player))
            return;

        AttributeInstance critical = player.getAttribute(AwakenAttributes.ATTRIBUTE_CRITICAL);
        if (critical == null)
            return;

        double amount = critical.getValue();
        if (amount == 0)
            return;

        if (random.nextInt(100) > amount)
            return;

        event.cancel();
        MinecraftForge.EVENT_BUS.fire(new CriticalHitEvent(player, event.getEntity(), 1.2F, true));
    }
}