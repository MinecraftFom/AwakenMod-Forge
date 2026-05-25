package com.fomdev.awaken.register.item;

import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.flib.entry.entries.AbstractItem;
import com.fomdev.flib.load.register.ItemHolder;
import net.minecraft.world.item.Item;

@ItemHolder(stream = false, namespace = AwakenContent.MODID)
public class FunctionalItems
{
    @ItemHolder.Hold
    public static AbstractItem AWAKEN_LEVEL_DETECTOR = AbstractItem.of("awaken_level_dectector", new Item(new Item.Properties()));
}