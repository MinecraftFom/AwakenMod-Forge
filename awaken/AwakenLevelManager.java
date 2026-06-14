package com.fomdev.awaken.awaken;

import com.fomdev.awaken.event.PlayerLevelUpgradeEvent;
import com.fomdev.awaken.nbt.NBTUtil;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

public class AwakenLevelManager
{
    public static final Random random = new Random();

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

        AwakenLevel level = getLevelOf(p);

        if (level != null)
        {
            AwakenLevel nextLevel = AwakenLevelRegister.getNextLevel(level);

            if (nextLevel != null && result >= nextLevel.min())
            {
                if (player instanceof ServerPlayer sp)
                {
                    MinecraftForge.EVENT_BUS.fire(new PlayerLevelUpgradeEvent(sp, level, nextLevel));
                }
            }
        }

        storeAwakenLevelAsNBT(p, result); // Update the value for the player
        return result;
    }

    public static String localize(
            String id
    )
    {
        return "level." + id + ".name";
    }

    @Nullable
    public static AwakenLevel getLevelOf(Player player)
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
}