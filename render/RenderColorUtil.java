package com.fomdev.awaken.render;

import com.fomdev.awaken.quality.Quality;
import net.minecraftforge.client.event.RenderTooltipEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.List;

public class RenderColorUtil
{
    public static final int alpha = 0xA4;

    public static void applyColor(
            @NotNull RenderTooltipEvent.Color event,
            @Nullable ColorComponent component
    )
    {
        if (component == null)
            return;

        event.setBorderStart(component.bdStart.getRGB());
        event.setBorderEnd(component.bdEnd.getRGB());
        event.setBackgroundStart(component.bgStart.getRGB());
        event.setBackgroundEnd(component.bgEnd.getRGB());
    }

    public static ColorComponent continueColor(
            Quality quality
     )
    {
        Color bgStart = quality.color().get(0);
        Color bgEnd = quality.color().get(1);

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

        return new ColorComponent(bdS, bdE, bgS, bgE);
    }

    public static ColorComponent multipleColor(
            Quality quality
    )
    {
        long millis = System.currentTimeMillis() / 100; // Get the timer
        List<Color> colors = quality.color();

        if (colors.isEmpty())
            return null;

        if (colors.size() == 1) {
            Color color = colors.get(0);
            int r = color.getRed();
            int g = color.getGreen();
            int b = color.getBlue();

            Color bg = new Color(r, g, b, alpha);
            Color bd = new Color(255 - r, 255 - g, 255 - b);

            return new ColorComponent(bd, bg);
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

        return new ColorComponent(bd, bg);
    }

    public static ColorComponent singleColor(
            Quality quality
    )
    {
        Color color = quality.color().get(0);
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        Color borderColor = new Color(255 - red, 255 - green, 255 - blue);
        Color contentsColor = new Color(red, green, blue, alpha);

        return new ColorComponent(borderColor, contentsColor);
    }

    public record ColorComponent(
            Color bdStart,
            Color bdEnd,
            Color bgStart,
            Color bgEnd
    ) {
        public ColorComponent(
                Color bd,
                Color bg
        )
        {
            this(bd, bd, bg, bg);
        }
    }

    private static int clamp(int v, int min, int max)
    {
        return Math.max(min, Math.min(max, v));
    }
}