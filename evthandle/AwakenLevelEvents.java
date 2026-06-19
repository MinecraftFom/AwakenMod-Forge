package com.fomdev.awaken.evthandle;

import com.fomdev.awaken.awaken.AwakenLevel;
import com.fomdev.awaken.awaken.AwakenLevelManager;
import com.fomdev.awaken.awaken.AwakenLevelRegister;
import com.fomdev.awaken.event.PlayerLevelUpgradeEvent;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.HealthUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.flib.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.fomdev.awaken.awaken.AwakenLevelManager.awaken;
import static com.fomdev.awaken.awaken.AwakenLevelManager.getLevelOf;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class AwakenLevelEvents
{
    public static final Random random = new Random();

    @SubscribeEvent
    public static void onLevelUp(PlayerLevelUpgradeEvent event)
    {
        event.entity.connection.send(
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
                                                        event.current.id()
                                                )
                                        )
                                )
                                .withStyle(ChatFormatting.GOLD)
                )
        );

        int awakenLevel = AwakenLevelRegister.getLevel(event.current);
        if (awakenLevel > 0)
            HealthUtil.setMaxHealthPersistent(event.entity, event.entity.getMaxHealth() + random.nextInt(awakenLevel));
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
}