package com.fomdev.awaken.render;

import com.fomdev.awaken.exp.EquipmentExperience;
import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.quality.QualityUtil;
import com.fomdev.awaken.util.ColorUtil;
import com.mojang.datafixers.util.Either;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class CustomTooltipRenders
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderExpAndLevel(RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();
        if (UpgradeTier.castSlot(stack.getItem()) == null)
            return;

        int exp = NBTUtil.getCurrentExp(stack);
        int max = NBTUtil.getMaxExp(stack);
        int lvl = NBTUtil.getCurrentExpLevel(stack);

        double percent = (100.0F / (float) max) * exp;
        Quality quality = NBTUtil.deserializeQuality(stack);
        float factor = quality == null? EquipmentExperience.defaultMaxExperienceFactor: quality.factor();
        String limited = String.format("%.1f", percent);
        String word = "EXP: [" + lvl + "] " + exp + "/" + max + " (" + limited + "%)";
        event.getTooltipElements().add(event.getTooltipElements().size(), Either.left(Component.literal(word).withStyle(ChatFormatting.GREEN)));
        event.getTooltipElements().add(event.getTooltipElements().size(), Either.left(Component.translatable("tooltip.next_level.msg").append(Component.literal("" + factor)).withStyle(ChatFormatting.GREEN)));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderQualityTooltip(RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();
        Quality quality = NBTUtil.deserializeQuality(stack);
        if (quality == null)
            return;

        Color color = quality.color();
        Style colorStyle = Style.EMPTY.withColor(ColorUtil.colorToTextColor(color));

        event.getTooltipElements().add(1, Either.left(Component.translatable("tooltip.quality.msg").append(Component.translatable(QualityUtil.localize(quality.id()))).setStyle(colorStyle)));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderQualityTooltip(RenderTooltipEvent.Color event)
    {
        ItemStack stack = event.getItemStack();
        Quality quality = NBTUtil.deserializeQuality(stack);
        Awaken.LOGGER.info("Rendering tooltip for stack {}", stack.toString());
        if (quality == null)
            return;

        Color color = quality.color();
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = 0xAF;

        Color borderColor = new Color(255 - red, 255 - green, 255 - blue);
        Color contentsColor = new Color(red, green, blue, alpha);

        event.setBackground(contentsColor.getRGB());
        event.setBorderStart(borderColor.getRGB());
        event.setBorderEnd(borderColor.getRGB());
    }
}