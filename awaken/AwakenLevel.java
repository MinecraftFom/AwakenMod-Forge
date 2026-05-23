package com.fomdev.awaken.awaken;

import java.awt.*;

public interface AwakenLevel
{
    String id();
    Color color();
    double min(); // Minimal generation level

    static AwakenLevel of(
            String id,
            Color  color,
            double min
    )
    {
        return new AwakenLevel()
        {
            @Override
            public String id()
            {
                return id;
            }

            @Override
            public Color color()
            {
                return color;
            }

            @Override
            public double min()
            {
                return min;
            }
        };
    }
}