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
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.event.RenderTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.awt.*;
import java.util.List;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class CustomTooltipRenders {
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderAlignments(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();

        List<Alignment.AlignmentProvider> alignments = List.of(NBTUtil.getAlignments(stack));
        if (alignments.isEmpty())
            return;

        int index = event.getTooltipElements().indexOf(Either.left(Component.EMPTY));

        for (Alignment.AlignmentProvider provider : alignments) {
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
    public static void onRenderAspects(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();

        List<Aspect.AspectProvider> aspects = List.of(NBTUtil.getAspects(stack));
        if (aspects.isEmpty())
            return;

        int index = event.getTooltipElements().indexOf(Either.left(Component.EMPTY));

        for (Aspect.AspectProvider provider : aspects) {
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
    public static void onRenderAwakenLevel(RenderTooltipEvent.GatherComponents event) {
        ItemStack stack = event.getItemStack();
        if (stack.getItem() == FunctionalItems.AWAKEN_LEVEL_DETECTOR.get()) {
            event.getTooltipElements().add(1, Either.left(Component.translatable("tooltip.toss_to_use.msg").withStyle(ChatFormatting.AQUA)));
            event.getTooltipElements().add(2, Either.left(Component.translatable("tooltip.toss_all_to_eliminate.msg").withStyle(ChatFormatting.AQUA)));
            event.getTooltipElements().add(3, Either.left(Component.translatable("tooltip.debug_use.warning").withStyle(ChatFormatting.RED)));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderQualityTooltip(RenderTooltipEvent.Color event) {
        ItemStack stack = event.getItemStack();
        Quality quality = NBTUtil.deserializeQuality(stack);
        if (quality == null)
            return;

        RenderColorUtil.ColorComponent component = switch (quality.colorPattern()) {
            case SINGLE -> RenderColorUtil.singleColor(quality);
            case MULTIPLE -> RenderColorUtil.multipleColor(quality);
            case CONTINUE -> RenderColorUtil.continueColor(quality);
        };

        RenderColorUtil.applyColor(event, component);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onRenderStack(RenderTooltipEvent.GatherComponents event)
    {
        List<Either<FormattedText, TooltipComponent>> textComponents = event.getTooltipElements();
        ItemStack stack = event.getItemStack();
//        textComponents.clear();

        int quality = onRenderQualityTooltip(event, 1);
        int forge = onRenderForge(event, quality);

        onRenderExpAndLevel(event);
    }

    private static int clamp(int v, int min, int max)
    {
        return Math.max(min, Math.min(max, v));
    }

    private static void onRenderExpAndLevel(RenderTooltipEvent.GatherComponents event)
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


    private static int onRenderForge(RenderTooltipEvent.GatherComponents event, int joinPoint)
    {
        ItemStack stack = event.getItemStack();
        List<UpgradeTier> tiers = NBTUtil.getForgeTiers(stack);
        if (tiers.isEmpty())
            return joinPoint;

        int max = NBTUtil.deserializeMaxForgeLevel(stack);
        int used = NBTUtil.deserializeForgeLevel(stack);

        if (max == 0)
            return joinPoint;

        event.getTooltipElements().add(joinPoint, Either.left(
                Component.translatable("tooltip.forging_slots.msg")
                        .append(Component.literal(": " + used + " / " + max))
                        .withStyle(ChatFormatting.GOLD)
        ));

        event.getTooltipElements().add(joinPoint + 1, Either.left(Component.EMPTY));

        for (int i = 0; i < tiers.size(); i++)
        {
            UpgradeTier tier = tiers.get(i);
            event.getTooltipElements().add(joinPoint + i + 4, Either.left(
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

        return joinPoint + 4 + tiers.size();
    }

    private static int onRenderQualityTooltip(RenderTooltipEvent.GatherComponents event, int joinPoint)
    {
        ItemStack stack = event.getItemStack();
        Quality quality = NBTUtil.deserializeQuality(stack);
        if (quality == null)
            return joinPoint;

        RenderColorUtil.ColorComponent component = switch (quality.colorPattern()) {
            case SINGLE -> RenderColorUtil.singleColor(quality);
            case MULTIPLE -> RenderColorUtil.multipleColor(quality);
            case CONTINUE -> RenderColorUtil.continueColor(quality);
        };

        Style colorStyle = component != null? Style.EMPTY.withColor(ColorUtil.colorToTextColor(component.bdStart())): Style.EMPTY;

        event.getTooltipElements().add(joinPoint, Either.left(Component.translatable("tooltip.quality.msg").append(Component.translatable(QualityUtil.localize(quality.id()))).setStyle(colorStyle)));

        return joinPoint + 1;
    }
}