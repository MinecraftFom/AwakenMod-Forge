package com.fomdev.awaken.bonus;

import net.minecraft.world.item.ItemStack;

public interface LootBonusInstance
{
    double chance(float factor);
    ItemStack drop();
}