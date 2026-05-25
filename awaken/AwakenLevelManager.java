package com.fomdev.awaken.awaken;

import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.flib.util.ColorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class AwakenLevelManager
{
    public static final AwakenLevel levelNaive;
    public static final AwakenLevel levelNovice;

    public static Float awaken(
            Entity player,
            float amount,
            int operation
    )
    {
        if (!(player instanceof Player p))
            return 0.0F;

        float original = NBTUtil.deserializeAwakenLevel(p);
        float result = switch (operation)
        {
            case 0 -> original + amount;
            case 1 -> original - amount;
            case 2 -> original * amount;
            case 3 -> original / amount;
            default -> original;
        };

        storeAwakenLevelAsNBT(p, result); // Update the value for the player
        return result;
    }

    public static String localize(
            String id
    )
    {
        return "level." + id + ".name";
    }

    @SubscribeEvent
    public static void onDeathPlayerPunish(LivingDeathEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
            return;


    }

    @SubscribeEvent
    public static void onSyncPlayerDatToCache(EntityJoinLevelEvent event)
    {
        if (!(event.getEntity() instanceof Player p))
            return;

        Float awakenLevel = NBTUtil.deserializeAwakenLevel(p);
        Awaken.LOGGER.info("Listened player login: Player {}, AwakenLevel {}", p.getName(), awakenLevel);
    }

    @SubscribeEvent
    public static void onSyncPlayerName(TickEvent.PlayerTickEvent event)
    {
        Player player = event.player;
        Component originalName = player.getName();
        AwakenLevel level = getLevelOf(player);
        if (level == null)
            return;

        player.setCustomName(Component.literal(originalName.getString()).setStyle(Style.EMPTY.withColor(ColorUtil.colorToTextColor(level.color()))));
    }

    @Nullable
    private static AwakenLevel getLevelOf(Player player)
    {
        Float awakenLevel = readAwakenLevelFromNBT(player);
        return AwakenLevelRegister.getLevel(awakenLevel);
    }

    private static Float readAwakenLevelFromNBT
            (
                    Entity player
            )
    {
        if (!(player instanceof Player p))
            return 0.0F;

        return NBTUtil.deserializeAwakenLevel(p);
    }

    private static void storeAwakenLevelAsNBT
            (
                    Entity player,
                    float amount
            )
    {
        if (!(player instanceof Player p))
            return;

        NBTUtil.serializeAwakenLevel(p, amount);
    }

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
}