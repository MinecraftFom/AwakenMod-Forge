package com.fomdev.awaken.awaken;

import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.HealthUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.flib.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class AwakenLevelManager
{
    public static final Random random = new Random();

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

        AwakenLevel level = getLevelOf(p);

        if (level != null)
        {
            AwakenLevel nextLevel = AwakenLevelRegister.getNextLevel(level);

            if (nextLevel != null && result >= nextLevel.min())
            {
                if (player instanceof ServerPlayer sp)
                {
                    sp.connection.send(
                            new ClientboundSetActionBarTextPacket(
                                    Component
                                            .translatable(
                                                    "chat.congrates_player_upgrade.msg"
                                            )
                                            .append(
                                                    Component.literal(
                                                            ": "
                                                    )
                                            )
                                            .append(
                                                    Component.translatable(
                                                            AwakenLevelManager.localize(
                                                                    nextLevel.id()
                                                            )
                                                    )
                                            )
                                            .withStyle(ChatFormatting.GOLD)
                            )
                    );

                    int awakenLevel = AwakenLevelRegister.getLevel(nextLevel);
                    if (awakenLevel > 0)
                        HealthUtil.setMaxHealthPersistent(sp, p.getMaxHealth() + random.nextInt(awakenLevel));
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

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event)
    {
        Player player = event.getEntity();

        awaken(player, NBTUtil.deserializeAwakenLevel(event.getOriginal()), 0);
    }

    @SubscribeEvent
    public static void onPlayerKill(LivingDeathEvent event)
    {
        if (!(event.getSource().getEntity() instanceof Player player))
            return;

        if (!(event.getEntity() instanceof Monster))
            return;

        awaken(player, random.nextFloat(20.0F), 0);
    }

    @SubscribeEvent
    public static void onPlayerWake(PlayerWakeUpEvent event)
    {
        Player player = event.getEntity();

        awaken(player, random.nextFloat(10.0F), event.wakeImmediately()? 1: 0);
    }

    @SubscribeEvent
    public static void onDeathPlayerPunish(LivingDeathEvent event)
    {
        if (!(event.getEntity() instanceof Player player))
            return;

        float level = NBTUtil.deserializeAwakenLevel(player);
        AwakenLevel awakenLevel = AwakenLevelRegister.getLevel(level);
        int lvl = AwakenLevelRegister.getLevel(awakenLevel);
        awaken(player, random.nextInt((int) level / (10 * lvl)), 1);
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