package com.fomdev.awaken.forging;

import com.fomdev.awaken.event.RegisterEvent;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenContent;
import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.flib.interpreter.ForceLoader;
import com.fomdev.flib.util.Suggested;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.awt.*;
import java.util.*;
import java.util.List;

@Mod.EventBusSubscriber(modid = AwakenContent.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ForgeUtils
{
    private static final Map<ResourceLocation, UpgradeTier.CompoundTierContainer> registeredTiers = new HashMap<>();
    private static boolean frozen = false;

    public static final int defaultMaxForgingCounts = 2;

    public static ItemStack forgeStack
            (
                    ItemStack stack,
                    UpgradeTier tier
            )
    {
        AttributeUtil.clearAttribute(stack, "forgeutil");

        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(stack.getItem());
        if (slot == null)
            return stack;

        return switch (slot)
        {
            case BOOT, CHEST, HEAD, LEGS -> forgeArmor(stack, tier);
            case AXE, BOW, HOE, PICK, SHIELD, SHOVE, SWORD -> forgeTool(stack, tier);
        };
    }

    @Nullable
    public static ResourceLocation getID
            (
                    UpgradeTier tier
            )
    {
        for (Map.Entry<ResourceLocation, UpgradeTier.CompoundTierContainer> entry : registeredTiers.entrySet())
        {
            if (entry.getValue().tier().equals(tier)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Nullable
    public static UpgradeTier getTier
            (
                    ResourceLocation location
            )
    {
        UpgradeTier.CompoundTierContainer container = registeredTiers.get(location);
        if (container == null)
            return null;

        return container.tier();
    }

    @Nullable
    public static UpgradeTier getTier
            (
                    String id
            )
    {
        for (UpgradeTier.CompoundTierContainer container: registeredTiers.values())
        {
            if (container.tier().id().equals(id))
                return container.tier();
        }

        return null;
    }

    public static String localize
            (
                    String id
            )
    {
        return "tier."+id+".name";
    }

    public static void registerReprFor
            (
                    ResourceLocation location,
                    ItemLike... repr            )
    {
        if (!registeredTiers.containsKey(location))
            throw new NullPointerException("Invalid register id: " + location + ", not registered");

        registeredTiers.get(location).addRepr(repr);
    }

    public static UpgradeTier registerTier
            (
                    ResourceLocation location,
                    UpgradeTier.CompoundTierContainer container
            )
    {
        if (frozen)
            throw new IllegalStateException("Forge Tier register state frozen");

        if (registeredTiers.containsKey(location))
            throw new IllegalStateException("Invalid register id: " + location + ", already registered.");

        registeredTiers.put(location, container);
        return container.tier();
    }

    public static UpgradeTier registerTier
            (
                    ResourceLocation location,
                    UpgradeTier tier,
                    ItemLike... material
            )
    {
        return registerTier(location, new UpgradeTier.CompoundTierContainer(tier, material));
    }

    @Suggested
    public static UpgradeTier registerTier
            (
                    String modid,
                    String name,
                    UpgradeTier tier,
                    ItemLike... material
            )
    {
        return registerTier(ResourceLocation.fromNamespaceAndPath(modid, name), tier, material);
    }

    @SubscribeEvent
    public static void register(FMLCommonSetupEvent event)
    {
        Awaken.LOGGER.info("FU> Freezing registry on FML-Common-Setup");
        ForceLoader.forceLoad(AwakenRegistries.SIG_AWAKEN_TIER);
        MinecraftForge.EVENT_BUS.fire(new RegisterEvent(AwakenRegistries.AWAKEN_TIER));
        frozen = true;
        Awaken.LOGGER.info("FU> Forging Tier registry state frozen");
    }

    private static double constructNumber
            (
                    @Range(from = 0, to = 3) int operation,
                    double value
            )
    {
        return switch (operation)
        {
            case 0, 2 -> value;
            case 1    -> - value;
            case 3    -> 1 / value;
            default -> throw new IllegalArgumentException("Illegal argument, should be 0 ~ 3, got " + operation);
        };
    }

    private static AttributeModifier.Operation constructOperation
            (
                    @Range(from = 0, to = 3) int operation
            )
    {
        return switch (operation)
        {
            case 0, 1 -> AttributeModifier.Operation.ADDITION;
            case 2, 3 -> AttributeModifier.Operation.MULTIPLY_BASE;
            default -> throw new IllegalArgumentException("Illegal argument, should be 0 ~ 3, got " + operation);
        };
    }

    private static double directCalculateDouble
            (
                    Number original,
                    Number number,
                    int operation
            )
    {
        double value = original.doubleValue();
        double num = number.doubleValue();
        return switch (operation)
        {
            case 0 -> value + num;
            case 1 -> value - num;
            case 2 -> value * num;
            case 3 -> value / num;
            default -> throw new IllegalStateException("Invalid compound tier operation found");
        };
    }

    private static float directCalculateFloat
            (
                    Number original,
                    Number number,
                    int operation
            )
    {
        float value = original.floatValue();
        float num = number.floatValue();
        return switch (operation)
        {
            case 0 -> value + num;
            case 1 -> value - num;
            case 2 -> value * num;
            case 3 -> value / num;
            default -> throw new IllegalStateException("Invalid compound tier operation found");
        };
    }

    private static int directCalculateInteger
            (
                    Number original,
                    Number number,
                    int operation
            )
    {
        int value = original.intValue();
        int num = number.intValue();
        return switch (operation)
        {
            case 0 -> value + num;
            case 1 -> value - num;
            case 2 -> value * num;
            case 3 -> value / num;
            default -> throw new IllegalStateException("Invalid compound tier operation found");
        };
    }

    private static long directCalculateLong
            (
                    Number original,
                    Number number,
                    int operation
            )
    {
        long value = original.longValue();
        long num = number.longValue();
        return switch (operation)
        {
            case 0 -> value + num;
            case 1 -> value - num;
            case 2 -> value * num;
            case 3 -> value / num;
            default -> throw new IllegalStateException("Invalid compound tier operation found");
        };
    }

    private static short directCalculateShort
            (
                    Number original,
                    Number number,
                    int operation
            )
    {
        short value = original.shortValue();
        short num = number.shortValue();
        return (short) switch (operation)
        {
            case 0 -> value + num;
            case 1 -> value - num;
            case 2 -> value * num;
            case 3 -> value / num;
            default -> throw new IllegalStateException("Invalid compound tier operation found");
        };
    }

    private static ItemStack forgeArmor
            (
                    ItemStack stack,
                    UpgradeTier tier
            )
    {
        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(stack.getItem());

        if (slot == null)
            return stack;

        if (!(stack.getItem() instanceof ArmorItem item))
            throw new IllegalArgumentException("Invalid argument type: not an armor");

        if (!NBTUtil.addForged(stack, 1))
            return stack; // Makes sure it won't cause any errors

        NBTUtil.putForgeTier(stack, tier);

        Quality quality = NBTUtil.deserializeQuality(stack);
        float factor = quality == null? 0.0F: quality.factor();

        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Integer>>    enchant;

        if ((enchant = tier.enchant()) != null && enchant.get(slot) != null)
            NBTUtil.addEnchantValue(stack, (int) (enchant.get(slot).value() * (1 + factor)), enchant.get(slot).operation());

        return stack;
    }

    private static ItemStack forgeTool
            (
                ItemStack stack,
                UpgradeTier tier
            )
    {
        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(stack.getItem());

        if (slot == null)
            return stack;

        if (!NBTUtil.addForged(stack, 1))
            return stack; // Makes sure it won't cause any errors

        NBTUtil.putForgeTier(stack, tier);

        Quality quality = NBTUtil.deserializeQuality(stack);
        float factor = quality == null? 0.0F: quality.factor();

        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>>   efficiency;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Integer>> enchant;

        if ((efficiency = tier.efficiency()) != null && efficiency.get(slot) != null)
        {
            float original = NBTUtil.getEfficiency(stack);
            if (original == -1) original = 0;

            float result = switch (efficiency.get(slot).operation())
            {
                case 0 -> original + efficiency.get(slot).value();
                case 1 -> original - efficiency.get(slot).value();
                case 2 -> original * efficiency.get(slot).value();
                case 3 -> original / efficiency.get(slot).value();
                default -> throw new IllegalArgumentException("Unsupported operation");
            };

            NBTUtil.setEfficiency(stack, result * (1 + factor));
        }

        if ((enchant = tier.enchant()) != null && enchant.get(slot) != null)
            NBTUtil.addEnchantValue(stack, (int) (enchant.get(slot).value() * (1 + factor)), enchant.get(slot).operation());

        return stack;
    }

    public static UpgradeTier[] shuffle(
            Random random,
            int lvl,
            int max
    )
    {
        if (registeredTiers.isEmpty())
            return new UpgradeTier[]{};

        int offset = Math.abs(lvl / (lvl - max));
        if (offset == 0)
            offset = 1;

        int count = random.nextInt(offset);

        List<UpgradeTier> tiers = new ArrayList<>();

        for (int i = 0; i < count; i++)
        {
            tiers.add(registeredTiers.values().toArray(UpgradeTier.CompoundTierContainer[]::new)[random.nextInt(registeredTiers.size())].tier());
        }

        return tiers.toArray(UpgradeTier[]::new);
    }
}