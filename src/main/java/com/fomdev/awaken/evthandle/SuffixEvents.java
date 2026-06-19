package com.fomdev.awaken.evthandle;

import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.title.Suffix;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class SuffixEvents
{
    @SubscribeEvent
    public static void handleSuffixEvent(TickEvent.PlayerTickEvent event)
    {
        Player player = event.player;
        if (player == null)
            return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack main = player.getItemBySlot(EquipmentSlot.MAINHAND);
        ItemStack off = player.getItemBySlot(EquipmentSlot.OFFHAND);

        handleItemEffects(helmet, player);
        handleItemEffects(chest, player);
        handleItemEffects(legs, player);
        handleItemEffects(feet, player);
        handleItemEffects(main, player);
        handleItemEffects(off, player);
    }

    private static void handleItemEffects(ItemStack stack, Player player)
    {
        if (stack.getItem() == Items.AIR)
            return;

        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        if (suffix == null)
            return;

        Arrays.stream(suffix.effects()).forEach(player::addEffect);
    }
}