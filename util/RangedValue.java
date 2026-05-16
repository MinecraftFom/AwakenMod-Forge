package com.fomdev.awaken.util;

public class RangedValue
{
    private final double min;
    private final double max;

    public RangedValue(
            final double min,
            final double max
    )
    {
        this.min = min;
        this.max = max;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (!(obj instanceof Number num))
            return false;

        return min <= num.doubleValue() && num.doubleValue() <= max;
    }
}