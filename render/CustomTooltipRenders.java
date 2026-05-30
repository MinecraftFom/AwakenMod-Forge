package com.fomdev.awaken.render;

import com.fomdev.awaken.enchanting.Alignment;
import com.fomdev.awaken.enchanting.Aspect;
import com.fomdev.awaken.enchanting.EnchantmentRegister;
import com.fomdev.awaken.exp.EquipmentExperience;
import com.fomdev.awaken.forging.ForgeUtils;
import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.quality.QualityUtil;
import com.fomdev.awaken.register.item.FunctionalItems;
import com.fomdev.flib.util.ColorUtil;
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
import java.util.List;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class CustomTooltipRenders
{
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderAlignments(RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();

        List<Alignment.AlignmentProvider> alignments = List.of(NBTUtil.getAlignments(stack));
        if (alignments.isEmpty())
            return;

        int index = event.getTooltipElements().indexOf(Either.left(Component.EMPTY));

        for (Alignment.AlignmentProvider provider: alignments)
        {
            event.getTooltipElements().add(index, Either.left(
                    Component.translatable(
                            EnchantmentRegister.localizeAspect(provider.alignment().id())
                    ).append(
                            Component.literal(": " + provider.level())
                    ).withStyle(
                            Style.EMPTY.withColor(ColorUtil.colorToTextColor(provider.alignment().color()))
                    )
            ));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderAspects(RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();

        List<Aspect.AspectProvider> aspects = List.of(NBTUtil.getAspects(stack));
        if (aspects.isEmpty())
            return;

        int index = event.getTooltipElements().indexOf(Either.left(Component.EMPTY));

        for (Aspect.AspectProvider provider: aspects)
        {
            event.getTooltipElements().add(index, Either.left(
                    Component.translatable(
                            EnchantmentRegister.localizeAspect(provider.aspect().id())
                    ).append(
                            Component.literal(": " + provider.amount())
                    ).withStyle(
                            Style.EMPTY.withColor(ColorUtil.colorToTextColor(provider.aspect().color()))
                    )
            ));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderAwakenLevel(RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == FunctionalItems.AWAKEN_LEVEL_DETECTOR.get())
        {
            event.getTooltipElements().add(1, Either.left(Component.translatable("tooltip.toss_to_use.msg").withStyle(ChatFormatting.AQUA)));
            event.getTooltipElements().add(2, Either.left(Component.translatable("tooltip.toss_all_to_eliminate.msg").withStyle(ChatFormatting.AQUA)));
            event.getTooltipElements().add(3, Either.left(Component.translatable("tooltip.debug_use.warning").withStyle(ChatFormatting.RED)));
        }
    }

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
    public static void onRenderForge(RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();
        List<UpgradeTier> tiers = NBTUtil.getForgeTiers(stack);
        if (tiers.isEmpty())
            return;

        int max = NBTUtil.deserializeMaxForgeLevel(stack);
        int used = NBTUtil.deserializeForgeLevel(stack);

        if (max == 0)
            return;

        event.getTooltipElements().add(2, Either.left(
                Component.translatable("tooltip.forging_slots.msg")
                        .append(Component.literal(": " + used + " / " + max))
                        .withStyle(ChatFormatting.GOLD)
        ));

        event.getTooltipElements().add(3, Either.left(Component.EMPTY));

        for (int i = 0; i < tiers.size(); i++)
        {
            UpgradeTier tier = tiers.get(i);
            event.getTooltipElements().add(i + 4, Either.left(
                    Component
                            .literal((i + 1) + ": ")
                            .append(
                                    Component.translatable(
                                            ForgeUtils.localize(tier.id())
                                    )
                            )
                            .withStyle(
                                    Style.EMPTY.withColor(
                                            ColorUtil.colorToTextColor(tier.color())
                                    )
                            )
                    )
            );
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderQualityTooltip(RenderTooltipEvent.GatherComponents event)
    {
        ItemStack stack = event.getItemStack();
        Quality quality = NBTUtil.deserializeQuality(stack);
        if (quality == null)
            return;

        Color color = quality.color().get(0); // GET BASEMENT COLOR
        Style colorStyle = Style.EMPTY.withColor(ColorUtil.colorToTextColor(color));

        event.getTooltipElements().add(1, Either.left(Component.translatable("tooltip.quality.msg").append(Component.translatable(QualityUtil.localize(quality.id()))).setStyle(colorStyle)));
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderQualityTooltip(RenderTooltipEvent.Color event)
    {
        ItemStack stack = event.getItemStack();
        Quality quality = NBTUtil.deserializeQuality(stack);
        if (quality == null)
            return;

        switch (quality.colorPattern())
        {
            case SINGLE ->
            {
                Color color = quality.color().get(0);
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

            case MULTIPLE ->
            {
                int alpha = 0xA4;

                long millis = System.currentTimeMillis() / 100; // Get the timer
                List<Color> colors = quality.color();

                if (colors.isEmpty())
                    return;

                if (colors.size() == 1)
                {
                    Color color = colors.get(0);
                    int r = color.getRed();
                    int g = color.getGreen();
                    int b = color.getBlue();

                    Color bg = new Color(r, g, b, alpha);
                    Color bd = new Color(255 - r, 255 - g, 255 - b);

                    event.setBackgroundStart(bg.getRGB());
                    event.setBackgroundEnd(bg.getRGB());
                    event.setBorderStart(bd.getRGB());
                    event.setBorderEnd(bd.getRGB());

                    return;
                }

                int duration = 10;
                int cycleTime = duration * colors.size();
                long currentCycleTime = millis % cycleTime;

                int cInd = (int) (currentCycleTime / duration);
                float prog = (float) (currentCycleTime % duration) / duration;

                Color bgStart = colors.get(cInd);
                Color bgEnd = colors.get((cInd + 1) % colors.size());

                int rD = (bgEnd.getRed() - bgStart.getRed());
                int gD = (bgEnd.getGreen() - bgStart.getGreen());
                int bD = (bgEnd.getBlue() - bgStart.getBlue());

                int r = (int) (bgStart.getRed() + rD * prog);
                int g = (int) (bgStart.getGreen() + gD * prog);
                int b = (int) (bgStart.getBlue() + bD * prog);

                Color bg = new Color(r, g, b, alpha);
                Color bd = new Color(255 - r, 255 - g, 255 - b);

                event.setBackgroundStart(bg.getRGB());
                event.setBackgroundEnd(bg.getRGB());
                event.setBorderStart(bd.getRGB());
                event.setBorderEnd(bd.getRGB());
            }

            case CONTINUE ->
            {
                Color bgStart = quality.color().get(0);
                Color bgEnd = quality.color().get(1);

                int alpha = 0xA4;

                long millis = System.currentTimeMillis();

                float offStart = (float) (Math.sin(millis * 0.001) + 1) / 2;
                float offEnd = (float) (Math.sin(millis * 0.0012 + Math.PI) + 1) / 2;

                int rD = bgEnd.getRed() - bgStart.getRed();
                int gD = bgEnd.getGreen() - bgStart.getGreen();
                int bD = bgEnd.getBlue() - bgStart.getBlue();

                int rS = clamp((int) (bgStart.getRed() + rD * offStart), 0, 255);
                int gS = clamp((int) (bgStart.getGreen() + gD * offStart), 0, 255);
                int bS = clamp((int) (bgStart.getBlue() + bD * offStart), 0, 255);

                int rE = clamp((int) (bgEnd.getRed() - rD * offEnd), 0, 255);
                int gE = clamp((int) (bgEnd.getGreen() - gD * offEnd), 0, 255);
                int bE = clamp((int) (bgEnd.getBlue() - bD * offEnd), 0, 255);

                Color bgS = new Color(rS, gS, bS, alpha);
                Color bgE = new Color(rE, gE, bE, alpha);
                Color bdS = new Color(255 - rS, 255 - gS, 255 - bS);
                Color bdE = new Color(255 - rE, 255 - gE, 255 - bE);

                event.setBackgroundStart(bgS.getRGB());
                event.setBackgroundEnd(bgE.getRGB());
                event.setBorderStart(bdS.getRGB());
                event.setBorderEnd(bdE.getRGB());
            }
        }
    }

    private static int clamp(int v, int min, int max)
    {
        return Math.max(min, Math.min(max, v));
    }
}