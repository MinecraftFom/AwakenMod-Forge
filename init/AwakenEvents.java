package com.fomdev.awaken.init;

import com.fomdev.awaken.awaken.AwakenLevel;
import com.fomdev.awaken.awaken.AwakenLevelManager;
import com.fomdev.awaken.awaken.AwakenLevelRegister;
import com.fomdev.awaken.forging.ForgeUtils;
import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.register.item.FunctionalItems;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;

@Mod.EventBusSubscriber
public class AwakenEvents
{
    @SubscribeEvent
    public static void onLevelDetectorRun(ItemTossEvent event)
    {
        ItemStack stack = event.getEntity().getItem();
        AwakenLevel level = AwakenLevelRegister.getLevel(NBTUtil.deserializeAwakenLevel(event.getPlayer()));

        if (stack.getItem() == FunctionalItems.AWAKEN_LEVEL_DETECTOR.get() && stack.getCount() == 1)
        {
            if (level != null)
            {
                float lvl = NBTUtil.deserializeAwakenLevel(event.getPlayer());

                event.getPlayer().sendSystemMessage(
                        Component
                                .translatable("chat.current_awaken_level.msg")
                                .append(
                                        Component
                                                .translatable(AwakenLevelManager.localize(level.id())
                                                )
                                )
                                .append(
                                        Component
                                                .literal(" [" + lvl + "]")
                                )
                                .withStyle(ChatFormatting.GREEN));
            }
            else event.getPlayer().sendSystemMessage(Component.translatable("chat.current_awaken_level.err"));

            event.cancel();
            event.getPlayer().addItem(stack);
        }
    }

    @SubscribeEvent
    public static void listenAwakenCommand(ServerChatEvent event)
    {
        Player player = event.getPlayer();
        String message = event.getMessage().getString();

        if (message.startsWith("!!awaken") && player.getName().getString().equals("Dev")) // Only Dev can use this
        {
            String num = message.split(" ")[1];
            float fl_num = Float.parseFloat(num);

            AwakenLevelManager.awaken(player, fl_num, 0);
        }

        if (message.startsWith("!!attribute") && player.getName().getString().equals("Dev"))
        {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() != Items.AIR)
            {
                AttributeUtil.putAttribute(
                        stack,
                        Attributes.LUCK,
                        "awa",
                        "test",
                        7.8D,
                        AttributeModifier.Operation.ADDITION,
                        EquipmentSlot.MAINHAND
                );
            }
        }

        if (message.startsWith("!!forge") && player.getName().getString().equals("Dev"))
        {
            ItemStack stack = player.getMainHandItem();
            if (stack.getItem() != Items.AIR)
            {
                ForgeUtils.forgeStack(stack, ForgeUtils.coalTier);
            }
        }
    }
}