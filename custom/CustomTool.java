package com.fomdev.awaken.custom;

import com.fomdev.awaken.enchanting.Alignment;
import com.fomdev.awaken.enchanting.Aspect;
import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.Title;
import net.minecraft.world.item.ItemStack;

public interface CustomTool
{
    ItemStack getToolBase();
    Prefix prefix();
    Suffix suffix();
    Title title();
    UpgradeTier[] tiers();
    Alignment.AlignmentProvider[] alignments();
    Aspect.AspectProvider[] aspects();
}