package com.fomdev.awaken.event.attach;

import com.fomdev.awaken.spore.Pollinate;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PollinateAttachEntityEvent extends EntityAttachEvent<Pollinate>
{
    public PollinateAttachEntityEvent(
            Pollinate pollinate,
            int lvl,
            Entity entity
    )
    {
        super(pollinate, lvl, entity);
    }
}