package com.fomdev.awaken.evthandle;

import com.fomdev.awaken.awaken.AwakenLevel;
import com.fomdev.awaken.awaken.AwakenLevelManager;
import com.fomdev.awaken.awaken.AwakenLevelRegister;
import com.fomdev.awaken.gen.DifficultyHandler;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.register.item.FunctionalItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class ItemEvents
{
    @SubscribeEvent
    public static void onDifficultyDetectorRun(TickEvent.PlayerTickEvent event)
    {
        if (event.player == null)
            return;

        if (event.player.getMainHandItem().getItem() == FunctionalItems.AWAKEN_DIFFICULTY_DETECTOR.get() || event.player.getOffhandItem().getItem() == FunctionalItems.AWAKEN_DIFFICULTY_DETECTOR.get())
        {
            if (!(event.player instanceof ServerPlayer serverPlayer))
                return;

            ServerLevel level = serverPlayer.serverLevel();
            serverPlayer.connection.send(
                    new ClientboundSetActionBarTextPacket(
                            Component.translatable(
                                    "chat.difficulty.info"
                            ).append(
                                    Component.literal
                                            (
                                                    ": " + DifficultyHandler.getLevelDifficulty(serverPlayer.serverLevel()) + " (" + level.dimensionTypeId().location() + ")"
                                            )
                            ).withStyle(
                                    ChatFormatting.AQUA
                            )
                    )
            );
        }
    }

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
}