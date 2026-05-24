package com.fomdev.awaken.init;

import com.fomdev.awaken.awaken.AwakenLevel;
import com.fomdev.awaken.awaken.AwakenLevelManager;
import com.fomdev.awaken.awaken.AwakenLevelRegister;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.register.item.FunctionalItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class AwakenEvents
{
    @SubscribeEvent
    public static void onLevelDetectorRun(PlayerInteractEvent.RightClickEmpty event)
    {
        ItemStack stack = event.getItemStack();
        AwakenLevel level = AwakenLevelRegister.getLevel(NBTUtil.deserializeAwakenLevel(event.getEntity()));

//        if (stack.getItem() == FunctionalItems.AWAKEN_LEVEL_DETECTOR.get())
//        {
//            if (level != null) event.getEntity().sendSystemMessage(Component.translatable("chat.current_awaken_level.msg").append(Component.translatable(AwakenLevelManager.localize(level.id()))).append(Component.literal(" [" + NBTUtil.deserializeAwakenLevel(event.getEntity())+ "]")).withStyle(ChatFormatting.GREEN));
//            else event.getEntity().sendSystemMessage(Component.translatable("chat.current_awaken_level.err"));
//        }
    }
}