package com.fomdev.awaken.register.item;

import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.flib.entry.entries.AbstractItem;
import com.fomdev.flib.load.register.ItemHolder;
import com.fomdev.flib.load.register.ItemHolder.Hold;
import com.fomdev.flib.load.register.TabbedHolder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;

@ItemHolder(stream = false, namespace = AwakenContent.MODID)
@TabbedHolder(stream = true)
public class MaterialItems
{
    @TabbedHolder.Tab
    public static final ResourceKey<CreativeModeTab> tab = CreativeModeTabs.INGREDIENTS;

    @Hold(id = "abropht") public static AbstractItem abropht;
    @Hold(id = "behesel") public static AbstractItem behesel;
    @Hold(id = "cryphen") public static AbstractItem cryphen;
    @Hold(id = "dyollte") public static AbstractItem dyollte;
    @Hold(id = "pysocke") public static AbstractItem pysocke;
    @Hold(id = "thazyto") public static AbstractItem thazyto;
    @Hold(id = "zyzon") public static AbstractItem zyzon;

    @Hold(id = "daydreams") public static AbstractItem daydreams;
    @Hold(id = "nightmare") public static AbstractItem nightmare;
}