package com.fomdev.awaken.util;

public class LimitlessValue
{
    private final double min;

    public LimitlessValue(
            Number min
    )
    {
        this.min = min.doubleValue();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Number num))
            return false;

        return min <= num.doubleValue();
    }
}