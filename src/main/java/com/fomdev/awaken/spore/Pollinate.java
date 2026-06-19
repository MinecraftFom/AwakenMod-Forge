package com.fomdev.awaken.spore;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;

public interface Pollinate
{
    String id();
    Attribute attr();
    double amount(int lvl);
    EquipmentSlot[] suitableOn();

    record PollinateInstance(
            Pollinate pollinate,
            int lvl
    ) {}
}