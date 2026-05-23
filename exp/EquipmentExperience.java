package com.fomdev.awaken.exp;

import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.NBTUtil;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class EquipmentExperience
{
    public static final int   defaultInitialExperienceRequirement = 2000;
    public static final float defaultMaxExperienceFactor = 5.5F;

    private static final Random random = new Random(System.currentTimeMillis());

    @SubscribeEvent
    public static void handleArmorEvents(LivingHurtEvent event)
    {
        Entity entity = event.getEntity();
        if (entity.getCommandSenderWorld().isClientSide()) return; // Only accepts server side procedure
        if (!(entity instanceof Player player)) return; // Only handles player event, otherwise, players can cheat by using armor stand

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack boots = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack shield = player.getUseItem();

        if (helmet.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(helmet, random.nextInt(2), 0);
            NBTUtil.updateExp(helmet, 0.02F);
            syncLevelWithForgeCount(helmet);
        }
        else if (chest.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(chest, random.nextInt(4), 0);
            NBTUtil.updateExp(chest, 0.02F);
            syncLevelWithForgeCount(chest);
        }
        else if (legs.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(legs, random.nextInt(3), 0);
            NBTUtil.updateExp(legs, 0.02F);
            syncLevelWithForgeCount(legs);
        }
        else if (boots.getItem() != Items.AIR)
        {
            NBTUtil.addExpValue(boots, random.nextInt(1), 0);
            NBTUtil.updateExp(boots, 0.02F);
            syncLevelWithForgeCount(boots);
        }
        else if (shield.getItem() instanceof ShieldItem)
        {
            NBTUtil.addExpValue(shield, random.nextInt(2), 0);
            NBTUtil.updateExp(shield, 0.01F);
            syncLevelWithForgeCount(shield);
        }
    }

    @SubscribeEvent
    public static void handleBowEvents(ProjectileImpactEvent event)
    {
        Entity shoot = event.getProjectile().getOwner();
        if (shoot == null) return;
        if (Objects.requireNonNull(shoot).getCommandSenderWorld().isClientSide()) return;
        if (!(shoot instanceof Player player)) return;
        if (event.getRayTraceResult().getType() != HitResult.Type.ENTITY) return;

        ItemStack mainHand = player.getMainHandItem();
        ItemStack offHand = player.getOffhandItem();
        if (mainHand.getItem() instanceof BowItem)
        {
            NBTUtil.addExpValue(mainHand, random.nextInt(5), 0);
            NBTUtil.updateExp(mainHand, 0.03F);
            syncLevelWithForgeCount(mainHand);
        }
        else if (offHand.getItem() instanceof BowItem)
        {
            // nor: I WILL NEVER HOLD BOWS ON MY OFF HAND!!!
            NBTUtil.addExpValue(offHand, random.nextInt(5), 0);
            NBTUtil.updateExp(offHand, 0.03F);
            syncLevelWithForgeCount(offHand);
        }
    }

    @SubscribeEvent
    public static void handleHoeEvents(BlockEvent.BlockToolModificationEvent event)
    {
        ItemStack stack = event.getHeldItemStack();
        if (Objects.requireNonNull(event.getPlayer()).getCommandSenderWorld().isClientSide()) return; // Only accepts server side procedure
        if (!(stack.getItem() instanceof HoeItem)) return;
        if (event.getToolAction() != ToolActions.HOE_TILL) return; // Makes sure the event was considered as hoe till
        if (event.getState().getBlock() == Blocks.FARMLAND) return; // Makes sure no bug will occur
        if (event.getFinalState().getBlock() != Blocks.FARMLAND) return; // Makes sure the event was fully done

        NBTUtil.addExpValue(stack, random.nextInt(1), 0);
        NBTUtil.updateExp(stack, 0.01F);
        syncLevelWithForgeCount(stack);
    }

    @SubscribeEvent
    public static void handleToolEvents(BlockEvent.BreakEvent event)
    {
        Player player = event.getPlayer();
        if (player.getCommandSenderWorld().isClientSide()) return; // Only accepts server side procedure
        ItemStack stack = player.getMainHandItem(); // Gets the using item
        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(stack.getItem());
        if (slot == null)
            return;

        switch (slot)
        {
            case AXE, PICK, SHOVE ->
            {
                NBTUtil.addExpValue(stack, random.nextInt(2), 0);
                NBTUtil.updateExp(stack, 0.01F); // REWARD
                syncLevelWithForgeCount(stack);
            }
        }
    }

    @SubscribeEvent
    public static void handleWeaponEvents(AttackEntityEvent event)
    {
        Player entity = event.getEntity();
        if (entity.getCommandSenderWorld().isClientSide()) return; // Only accepts server side procedure

        ItemStack stack = entity.getMainHandItem(); // Gets the using item

        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(stack.getItem());
        if (slot == null)
            return;

        switch (slot)
        {
            case AXE, SWORD ->
            {
                NBTUtil.addExpValue(stack, random.nextInt(4), 0);
                NBTUtil.updateExp(stack, 0.03F); // REWARD
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