package com.fomdev.awaken.event.attach;

import com.fomdev.awaken.spore.Spore;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class SporeAttachEntityEvent extends EntityAttachEvent<Spore>
{
    public SporeAttachEntityEvent(
            Spore spore,
            int lvl,
            Entity entity
    )
    {
        super(spore, lvl, entity);
    }
}