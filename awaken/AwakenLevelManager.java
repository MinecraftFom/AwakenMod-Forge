package com.fomdev.awaken.awaken;

import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.util.*;

@Mod.EventBusSubscriber
public class AwakenLevelManager {
    private static final Map<UUID, Float> awakenLevelCache = new HashMap<>();

    public static Float awaken(
            Entity player,
            int amount,
            int operation
    ) {
        if (!(player instanceof Player p))
            return 0.0F;

        float original = awakenLevelCache.computeIfAbsent(p.getUUID(), u -> 0.0F);
        float result = switch (operation) {
            case 0 -> original + amount;
            case 1 -> original - amount;
            case 2 -> original * amount;
            case 3 -> original / amount;
            default -> original;
        };

        if (awakenLevelCache.containsKey(p.getUUID()))
            awakenLevelCache.replace(p.getUUID(), result);
        else awakenLevelCache.put(p.getUUID(), result);

        storeAwakenLevelAsNBT(p); // Update the value for the player
        return result;
    }

    public static String localize(
            String id
    ) {
        return "level." + id + ".name";
    }

    @SubscribeEvent
    public static void onDeathPlayerPunish(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player))
            return;


    }

    @SubscribeEvent
    public static void onSyncPlayerDatToCache(EntityJoinLevelEvent event) {
        if (!(event.getEntity() instanceof Player p))
            return;

        Float awakenLevel = NBTUtil.deserializeAwakenLevel(p);
        if (awakenLevel == null)
            NBTUtil.serializeAwakenLevel(p, 0.0F);


        Awaken.LOGGER.info("Listened player login: Player {}, AwakenLevel {}", p.getName(), awakenLevel);
    }

    @SubscribeEvent
    public static void onSyncCacheDatToPlayer(EntityLeaveLevelEvent event) {
        if (!(event.getEntity() instanceof Player p))
            return;

        storeAwakenLevelAsNBT(p);
    }

    @SubscribeEvent
    public static void onSyncPlayerName(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        Component originalName = player.getName();
        AwakenLevel level = getLevelOf(player);
        if (level == null)
            return;

        player.setCustomName(Component.literal(originalName.getString()).setStyle(Style.EMPTY.withColor(ColorUtil.colorToTextColor(level.color()))));

        /* debug TODO: remove*/ player.sendSystemMessage(Component.translatable(localize(level.id())).withStyle(ChatFormatting.GREEN));
    }

    @Nullable
    private static AwakenLevel getLevelOf(Player player)
    {
        Float awakenLevel = readAwakenLevel(player);
        if (awakenLevel == null)
        {
            NBTUtil.serializeAwakenLevel(player, 0.0F);
            awakenLevel = 0.0F;
        }

        return AwakenLevelRegister.getLevel(awakenLevel);
    }

    private static Float readAwakenLevel
            (
                    Entity player
            ) {
        if (!(player instanceof Player p))
            return 0.0F;

        UUID uuid = p.getUUID();

        // Mtd. 1
        if (awakenLevelCache.containsKey(uuid))
            return awakenLevelCache.get(uuid);

        // Mtd. 2
        Float awakenLevel = readAwakenLevelFromNBT(player);
        return awakenLevelCache.put(uuid, awakenLevel);
    }

    private static Float readAwakenLevelFromNBT
            (
                    Entity player
            ) {
        if (!(player instanceof Player p))
            return 0.0F;

        return NBTUtil.deserializeAwakenLevel(p);
    }

    private static void storeAwakenLevelAsNBT
            (
                    Entity player
            )
    {
        if (!(player instanceof Player p))
            return;

        UUID uuid = p.getUUID(); // Gets the actual uuid of the player, not using GameProfile to support offline players

        // Firstly, get the nbt from the cache
        Float awakenLevel = awakenLevelCache.computeIfAbsent(uuid, id -> 0.0F); // If the uuid matches none, it will reset the value to 0.0F

        NBTUtil.serializeAwakenLevel(player, awakenLevel);
    }
}