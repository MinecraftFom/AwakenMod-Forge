package com.fomdev.awaken.addon;

import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.flib.entry.entries.AbstractItem;
import com.fomdev.flib.load.register.ItemHolder;
import com.fomdev.flib.load.register.TabbedHolder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@ItemHolder(stream = false, namespace = AwakenRPG.MODID)
@TabbedHolder
public class AdditionalContent
{
    @TabbedHolder.Tab
    public static ResourceKey<CreativeModeTab> tab = CreativeModeTabs.BUILDING_BLOCKS;

    @TabbedHolder.Tabbed
    @ItemHolder.Hold(id = "fom477")
    public static AbstractItem authorItem;

    @TabbedHolder.Tabbed
    @ItemHolder.Hold
    public static AbstractItem sponsorItem = new AbstractItem("modic_m.sponsor.lol")
    {
        @Override
        public @NotNull AbstractItem build()
        {
            this.repr =  new Item(new Item.Properties()) {
                @Override
                public void appendHoverText(ItemStack p_41421_, @Nullable Level p_41422_, List<Component> p_41423_, TooltipFlag p_41424_)
                {
                    p_41423_.add(Component.literal("Oh piggod! \n Modic_M no money no kill me \n Modic_M lll i kill you hahaha lololo"));
                }
            };

            return this;
        }
    };
}