package com.fomdev.awaken.evthandle;

import com.fomdev.awaken.event.attach.PollinateAttachEntityEvent;
import com.fomdev.awaken.event.attach.PollinateAttachItemStackEvent;
import com.fomdev.awaken.event.attach.SporeAttachEntityEvent;
import com.fomdev.awaken.event.attach.SporeAttachItemStackEvent;
import com.fomdev.awaken.gen.DifficultyHandler;
import com.fomdev.awaken.gen.shuffle.entries.spore.SpawnPollinate;
import com.fomdev.awaken.gen.shuffle.entries.spore.SpawnSpore;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.spore.Pollinate;
import com.fomdev.awaken.spore.Spore;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class SporeAttachEvents
{
    public static final Random random = new Random();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPollinateAttachEntity(
            PollinateAttachEntityEvent event
    )
    {
        NBTUtil.putPollinate(event.container, event.entry, event.lvl);

        if (!(event.container instanceof ServerPlayer sp))
            return;

        sp.connection.send(
                new ClientboundSetActionBarTextPacket(
                        Component.translatable(
                                "chat.attach_pollinate_entity.msg"
                        ).withStyle(
                                ChatFormatting.GREEN
                        )
                )
        );
    }

    @SubscribeEvent
    public static void onSporeAttachEntity(
            SporeAttachEntityEvent event
    )
    {
        NBTUtil.putSpore(event.container, event.entry, event.lvl);

        if (!(event.container instanceof ServerPlayer sp))
            return;

        sp.connection.send(
                new ClientboundSetActionBarTextPacket(
                        Component.translatable(
                                "chat.attach_spore_entity.msg"
                        ).withStyle(
                                ChatFormatting.RED
                        )
                )
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPollinateAttach(
            PollinateAttachItemStackEvent event
    )
    {
        NBTUtil.putPollinate(event.container, event.entry, event.lvl);

        if (!(event.container.getEntityRepresentation() instanceof ServerPlayer sp))
            return;

        sp.connection.send(
                new ClientboundSetActionBarTextPacket(
                        Component.translatable(
                                "chat.attach_pollinate_stack.msg"
                        ).withStyle(
                                ChatFormatting.GREEN
                        )
                )
        );
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onSporeAttach(
            SporeAttachItemStackEvent event
    )
    {
        if (!(event instanceof SporeAttachItemStackEvent))
            return;

        NBTUtil.putSpore(event.container, event.entry, event.lvl);

        if (!(event.container.getEntityRepresentation() instanceof ServerPlayer sp))
            return;

        sp.connection.send(
                new ClientboundSetActionBarTextPacket(
                        Component.translatable(
                                "chat.attach_spore_stack.msg"
                        ).withStyle(
                                ChatFormatting.RED
                        )
                )
        );
    }

    @SubscribeEvent
    public static void onAttachEntity(
            LivingEvent.LivingTickEvent event
    )
    {
        if (System.currentTimeMillis() % (int) (1 * Math.pow(10, 7)) != 0)
            return;

        if (!(event.getEntity().level() instanceof ServerLevel sl))
            return;

        double diff = DifficultyHandler.getLevelDifficulty(sl);

        if (random.nextInt(101) != 100)
        {
            Spore spore = SpawnSpore.shuffle(random, diff);
            int lvl = random.nextInt(5);

            MinecraftForge.EVENT_BUS.fire(new SporeAttachEntityEvent(spore, lvl, event.getEntity()));
        } else
        {
            Pollinate pollinate = SpawnPollinate.shuffle(random, diff);
            int lvl = random.nextInt(3);

            MinecraftForge.EVENT_BUS.fire(new PollinateAttachEntityEvent(pollinate, lvl, event.getEntity()));
        }
    }

    @SubscribeEvent
    public static void onAttachStack(
            TickEvent.PlayerTickEvent event
    )
    {
        if (System.currentTimeMillis() % (int) (1 * Math.pow(10, 7)) != 0)
            return; // Ensures it should last long enough between each term

        if (random.nextInt(101) != 100)
            return; // Ensures it should be a small chance

        if (!(event.player.level() instanceof ServerLevel sl))
            return;

        double diff = DifficultyHandler.getLevelDifficulty(sl);

        List<ItemStack> availableStacks = Stream.of(
                event.player.getItemBySlot(EquipmentSlot.MAINHAND),
                event.player.getItemBySlot(EquipmentSlot.OFFHAND),
                event.player.getItemBySlot(EquipmentSlot.HEAD),
                event.player.getItemBySlot(EquipmentSlot.CHEST),
                event.player.getItemBySlot(EquipmentSlot.LEGS),
                event.player.getItemBySlot(EquipmentSlot.FEET)
        ).filter(i -> !i.is(Items.AIR)).toList();

        if (availableStacks.isEmpty())
            return;

        if (random.nextInt(101) != 100) // negative effect
        {
            Spore spore = SpawnSpore.shuffle(random, diff);
            int lvl = random.nextInt(5);

            MinecraftForge.EVENT_BUS.fire(new SporeAttachItemStackEvent(spore, lvl, availableStacks.get(random.nextInt(availableStacks.size()))));
        } else
        {
            Pollinate pollinate = SpawnPollinate.shuffle(random, diff);
            int lvl = random.nextInt(3);

            MinecraftForge.EVENT_BUS.fire(new PollinateAttachItemStackEvent(pollinate, lvl, availableStacks.get(random.nextInt(availableStacks.size()))));
        }
    }
}