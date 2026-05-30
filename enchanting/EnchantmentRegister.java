package com.fomdev.awaken.enchanting;

import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.NBTUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Mod.EventBusSubscriber(modid = AwakenRPG.MODID)
public class EnchantmentRegister
{
    private static final Map<ResourceLocation, Alignment> registeredAlignments = new HashMap<>();
    private static final Map<ResourceLocation, Aspect> registeredAspects = new HashMap<>();

    public static void enchant(ItemStack stack, Alignment alignment, int lvl)
    {
        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(stack.getItem());

        if (!Arrays.stream(alignment.slots()).toList().contains(slot))
            return;

        Aspect.AspectProvider[] providers = NBTUtil.getAspects(stack);
        Map<Aspect, Integer> mappedProviders = new HashMap<>();
        for (Aspect.AspectProvider provider: providers)
        {
            mappedProviders.put(provider.aspect(), provider.amount());
        }

        for (Aspect.AspectProvider provider: alignment.aspects())
        {
            Integer value = mappedProviders.get(provider.aspect());
            if (value == null || value < provider.amount() * lvl)
                return;
        }

        NBTUtil.putEnchantmentAlignment(stack, new Alignment.AlignmentProvider(alignment, lvl));
    }

    public static ResourceLocation getAlignmentId(Alignment alignment)
    {
        for (ResourceLocation location: registeredAlignments.keySet())
        {
            if (registeredAlignments.get(location) == alignment)
                return location;
        }

        return null;
    }

    public static ResourceLocation getAspectId(Aspect aspect)
    {
        for (ResourceLocation location: registeredAspects.keySet())
        {
            if (registeredAspects.get(location) == aspect)
                return location;
        }

        return null;
    }

    public static Alignment getAlignment(ResourceLocation location)
    {
        return registeredAlignments.get(location);
    }

    public static Alignment getAlignment(String modid, String id)
    {
        return getAlignment(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public static Aspect getAspect(ResourceLocation location)
    {
        return registeredAspects.get(location);
    }

    public static Aspect getAspect(String modid, String id)
    {
        return getAspect(ResourceLocation.fromNamespaceAndPath(modid, id));
    }

    public static Alignment registerAlignment(Alignment alignment, String modid)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, alignment.id());
        if (registeredAlignments.containsKey(location))
            throw new IllegalArgumentException("Registered alignment: " + location);

        return registeredAlignments.put(location, alignment);
    }

    public static Aspect registerAspect(Aspect aspect, String modid)
    {
        ResourceLocation location = ResourceLocation.fromNamespaceAndPath(modid, aspect.id());
        if (registeredAspects.containsKey(location))
            throw new IllegalArgumentException("Registered aspect: " + location);

        return registeredAspects.put(location, aspect);
    }

    @SubscribeEvent
    public static void onPlayerEvent(PlayerEvent event)
    {
        Player player = event.getEntity();

        if (player == null)
            return;

        ItemStack head = player.getItemBySlot(EquipmentSlot.HEAD);
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        ItemStack legs = player.getItemBySlot(EquipmentSlot.LEGS);
        ItemStack feet = player.getItemBySlot(EquipmentSlot.FEET);
        ItemStack off = player.getItemBySlot(EquipmentSlot.OFFHAND);
        ItemStack main = player.getItemBySlot(EquipmentSlot.MAINHAND);

        handleStackAlignment(event, head);
        handleStackAlignment(event, chest);
        handleStackAlignment(event, legs);
        handleStackAlignment(event, feet);
        handleStackAlignment(event, off);
        handleStackAlignment(event, main);
    }

    private static void handleStackAlignment(
            PlayerEvent event,
            ItemStack stack
    )
    {
        if (stack.getItem() != Items.AIR)
        {
            Alignment.AlignmentProvider[] providers = NBTUtil.getAlignments(stack);
            for (Alignment.AlignmentProvider prov: providers)
            {
                Alignment alignment = prov.alignment();
                if (alignment.evtRequired() != event.getClass())
                    continue;

                alignment.onEvent(event, prov.level());
            }
        }
    }
}