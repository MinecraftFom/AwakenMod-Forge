package com.fomdev.awaken.event.attach;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ItemStackAttachEvent<T> extends AttachEvent<T, ItemStack>
{
    public ItemStackAttachEvent(
            T entry,
            int lvl,
            ItemStack stack
    )
    {
        super(entry, stack, lvl);
    }
}