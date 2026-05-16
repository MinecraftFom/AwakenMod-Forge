package com.fomdev.awaken.exp;

import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.nbt.NBTUtil;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class EquipmentExperience
{
    public static final int   defaultInitialExperienceRequirement = 2000;
    public static final float defaultMaxExperienceFactor = 5.5F;

    @SubscribeEvent
    public static void handleArmorEvents(LivingHurtEvent event)
    {
        LivingEntity entity = event.getEntity();
        if (!(entity instanceof Player player)) return; // Only handles player event, otherwise, players can cheat by using armor stand

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack shield = player.getUseItem();

        if (helmet.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(helmet, 2, 0);
            NBTUtil.updateExp(helmet, defaultMaxExperienceFactor, 0.02F);
            syncLevelWithForgeCount(helmet);
        }

        if (chest.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(chest, 4, 0);
            NBTUtil.updateExp(chest, defaultMaxExperienceFactor, 0.02F);
            syncLevelWithForgeCount(chest);
        }

        if (legs.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(legs, 3, 0);
            NBTUtil.updateExp(legs, defaultMaxExperienceFactor, 0.02F);
            syncLevelWithForgeCount(legs);
        }

        if (boots.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(boots, 1, 0);
            NBTUtil.updateExp(boots, defaultMaxExperienceFactor, 0.02F);
            syncLevelWithForgeCount(boots);
        }

        if (shield.getItem() instanceof ShieldItem)
        {
            NBTUtil.addExpValue(shield, 2, 0);
            NBTUtil.updateExp(shield, defaultMaxExperienceFactor, 0.01F);
            syncLevelWithForgeCount(shield);
        }
    }

    @SubscribeEvent
    public static void handleHoeEvents(BlockEvent.BlockToolModificationEvent event)
    {
        ItemStack stack = event.getHeldItemStack();
        if (!(stack.getItem() instanceof HoeItem)) return;

        NBTUtil.addExpLevel(stack, 1, 0);
        NBTUtil.updateExp(stack, defaultMaxExperienceFactor, 0.01F);
        syncLevelWithForgeCount(stack);
    }

    @SubscribeEvent
    public static void handleToolEvents(BlockEvent.BreakEvent event)
    {
        Player player = event.getPlayer();
        ItemStack stack = player.getUseItem(); // Gets the using item

        switch (UpgradeTier.castSlot(stack.getItem()))
        {
            case AXE, PICK, SHOVE ->
            {
                NBTUtil.addExpValue(stack, 2, 0);
                NBTUtil.updateExp(stack, defaultMaxExperienceFactor, 0.01F); // REWARD
                syncLevelWithForgeCount(stack);
            }
        }
    }

    @SubscribeEvent
    public static void handleWeaponEvents(AttackEntityEvent event)
    {
        Player player = event.getEntity();
        ItemStack stack = player.getUseItem(); // Gets the using item

        switch (UpgradeTier.castSlot(stack.getItem()))
        {
            case AXE, BOW, SWORD ->
            {
                NBTUtil.addExpValue(stack, 4, 0);
                NBTUtil.updateExp(stack, defaultMaxExperienceFactor, 0.03F); // REWARD
                syncLevelWithForgeCount(stack);
            }
        }
    }

    private static void syncLevelWithForgeCount(
            ItemStack stack
    )
    {
        int level = NBTUtil.getCurrentExpLevel(stack);
        NBTUtil.serializeMaxForgeLevel(stack, level);
    }
}