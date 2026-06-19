package com.fomdev.awaken.event.attach;

import com.fomdev.awaken.spore.Pollinate;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PollinateAttachItemStackEvent extends ItemStackAttachEvent<Pollinate>
{
    public PollinateAttachItemStackEvent(
            Pollinate pollinate,
            int lvl,
            ItemStack stack
    )
    {
        super(pollinate, lvl, stack);
    }
}