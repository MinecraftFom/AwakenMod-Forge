package com.fomdev.awaken.event.attach;

import com.fomdev.awaken.spore.Spore;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class SporeAttachItemStackEvent extends ItemStackAttachEvent<Spore>
{
    public SporeAttachItemStackEvent(
            Spore spore,
            int lvl,
            ItemStack stack
    )
    {
        super(spore, lvl, stack);
    }
}