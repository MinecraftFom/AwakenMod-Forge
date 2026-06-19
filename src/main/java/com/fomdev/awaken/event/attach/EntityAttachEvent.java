package com.fomdev.awaken.event.attach;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EntityAttachEvent<T> extends AttachEvent<T, Entity>
{
    public EntityAttachEvent(
            T entry,
            int lvl,
            Entity entity
    )
    {
        super(entry, entity, lvl);
    }
}