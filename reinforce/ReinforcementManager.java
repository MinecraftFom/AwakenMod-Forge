package com.fomdev.awaken.reinforce;

import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.flib.util.ColorUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;

public class ReinforcementManager
{
    public static void addLevel(
            ItemStack stack,
            float level,
            int operation
    )
    {
        NBTUtil.addReinforceValue(stack, level, operation);
        updateStack(stack);
    }

    public static void updateStack(
            ItemStack stack
    )
    {
        ReinforcementLevels current = NBTUtil.deserializeReinforcement(stack);
        if (current == null)
        {
            NBTUtil.serializeReinforce(stack, ReinforcementLevels.NORMAL);
            current = ReinforcementLevels.NORMAL;
        }

        if (current == ReinforcementLevels.UNBREAKABLE)
            return; // HIGHEST LEVEL

        ReinforcementLevels next = ReinforcementLevels.getNextLevel(current);
        float level = NBTUtil.getCurrentReinforce(stack);

        if (next.getRequired() <= level)
        {
            NBTUtil.addReinforceValue(stack, next.getRequired(), 1);
            NBTUtil.serializeReinforce(stack, next);

            Style style = Style.EMPTY.withColor(ColorUtil.colorToTextColor(next.getColor()));
            stack.setHoverName(Component.translatable(next.getName()).withStyle(style));

            int durability = stack.getMaxDamage();
            NBTUtil.setMaxDamage(stack, (int) (durability * next.getDurability()));
        }
    }
}