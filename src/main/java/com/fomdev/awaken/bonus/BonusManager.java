package com.fomdev.awaken.bonus;

import com.fomdev.awaken.event.RegisterEvent;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.flib.interpreter.ForceLoader;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.*;

@Mod.EventBusSubscriber(modid = AwakenContent.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BonusManager
{
    public static final Random random = new Random();

    static final Map<Class<? extends Mob>, List<LootBonusInstance>> registeredAdditionalLoots = new HashMap<>();
    private static boolean frozen = false;

    public static LootBonusInstance register(LootBonusInstance loot, List<Class<? extends Mob>> mobs)
    {
        if (frozen)
            throw new IllegalStateException("Registry state frozen");

        if (mobs.isEmpty())
            throw new IllegalArgumentException("Must include at least 1 mob");

        mobs.forEach(mob -> registeredAdditionalLoots.computeIfAbsent(mob, m -> new ArrayList<>()).add(loot));
        return loot;
    }

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event)
    {
        Awaken.LOGGER.info("BM> Freezing registry on FML-Common-Setup");
        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_LOOT);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_LOOT));
        frozen = true;
        Awaken.LOGGER.info("BM> Bonus registry state frozen");
    }
}