package com.fomdev.awaken.enchanting;

import org.jetbrains.annotations.NotNull;

import java.awt.*;

public interface Aspect
{
    @NotNull Color  color();
    @NotNull String id();

    static Aspect of(Color color, String id)
    {
        return new Aspect()
        {
            @Override
            public @NotNull Color color()
            {
                return color;
            }

            @Override
            public @NotNull String id()
            {
                return id;
            }
        };
    }

    static AspectProvider of(int amount, @NotNull Aspect aspect)
    {
        return new AspectProvider()
        {
            @Override
            public int amount()
            {
                return amount;
            }

            @Override
            public @NotNull Aspect aspect()
            {
                return aspect;
            }
        };
    }

    interface AspectProvider
    {
                 int    amount();
        @NotNull Aspect aspect();
    }
}