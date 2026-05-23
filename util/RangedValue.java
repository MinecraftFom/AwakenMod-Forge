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
        if (obj instanceof Number num)
            return min <= num.doubleValue() && num.doubleValue() <= max;
        if (obj instanceof RangedValue rv)
            return (rv.min <= min && max <= rv.max) || (min <= rv.min && rv.max <= max);

        return false;
    }
}