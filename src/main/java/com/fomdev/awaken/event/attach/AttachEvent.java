package com.fomdev.awaken.event.attach;

import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
public class AttachEvent<T, CT> extends Event // T: type, CT: container type
{
    public final T entry;
    public final CT container;
    public final int lvl;

    public AttachEvent(
            T entry,
            CT container,
            int lvl
    )
    {
        this.entry = entry;
        this.container = container;
        this.lvl = lvl;
    }

    @Override
    public boolean isCancelable()
    {
        return true;
    }
}