package com.fomdev.awaken.util;

import net.minecraft.network.chat.TextColor;

import java.awt.*;

public class ColorUtil
{
    public static TextColor colorToTextColor(Color color)
    {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();

        return TextColor.parseColor(String.format("#%02X%02X%02X", red, green, blue));
    }
}