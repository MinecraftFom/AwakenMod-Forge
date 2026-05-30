package com.fomdev.awaken.forging;

import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.flib.util.Suggested;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class ForgeUtils
{
    private static final Map<ResourceLocation, UpgradeTier.CompoundTierContainer> registeredTiers = new HashMap<>();

    public static final int defaultMaxForgingCounts = 2;

    // Public fields that announces default capabilities for the tiers
    // Including vanilla tiers
    // TODO: append custom tiers
    // TODO: append compat mod tiers
    public static final UpgradeTier coalTier;
    public static final UpgradeTier copperTier;
    public static final UpgradeTier diamondTier;
    public static final UpgradeTier goldTier;
    public static final UpgradeTier ironTier;
    public static final UpgradeTier netheriteTier;
    public static final UpgradeTier obsidianTier;

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
        AtomicReference<ResourceLocation> location = new AtomicReference<>();

        registeredTiers.forEach((k, v) -> {
            if (v.tier() == tier)
                location.set(k);
        });

        return location.get();
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

        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Double>>     armor;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Integer>>    durability;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Integer>>    enchant;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>>      protection;

        if ((armor = tier.armor()) != null && armor.get(slot) != null)
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ARMOR,
                    tier.id() + "_armor",
                    "forgeutil",
                    armor.get(slot).value() * (1 + factor),
                    constructOperation(armor.get(slot).operation()),
                    stack.getEquipmentSlot()
            );

        if ((durability = tier.durability()) != null && durability.get(slot) != null)
        {
            int original = stack.getMaxDamage();
            int result = (int) (directCalculateInteger(original, durability.get(slot).value(), durability.get(slot).operation()) * (1 + factor));

            NBTUtil.setMaxDamage(
                    stack,
                    result
            );
        }

        if ((enchant = tier.enchant()) != null && enchant.get(slot) != null)
            NBTUtil.addEnchantValue(stack, (int) (enchant.get(slot).value() * (1 + factor)), enchant.get(slot).operation());

        if ((protection = tier.protection()) != null && protection.get(slot) != null) // Actually strength
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ARMOR_TOUGHNESS,
                    tier.id() + "_protection",
                    "forgeutil",
                    protection.get(slot).value().doubleValue() * (1 + factor),
                    constructOperation(protection.get(slot).operation()),
                    stack.getEquipmentSlot()
            );

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

        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>>   attack;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Integer>> durability;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>>   efficiency;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Integer>> enchant;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>>   fortune;
        Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Double>>  speed;

        if ((attack = tier.attack()) != null && attack.get(slot) != null)
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ATTACK_DAMAGE,
                    tier.id() + "_attack_damage",
                    "forgeutil",
                    attack.get(slot).value().doubleValue() * (1 + factor),
                    constructOperation(attack.get(slot).operation()),
                    stack.getEquipmentSlot()
            );

        if ((durability = tier.durability()) != null && durability.get(slot) != null)
        {
            int original = stack.getMaxDamage();
            int result = (int) (directCalculateInteger(original, durability.get(slot).value(), durability.get(slot).operation()) * (1 + factor));

            NBTUtil.setMaxDamage(
                    stack,
                    result
            );
        }

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

        if ((fortune = tier.fortune()) != null && fortune.get(slot) != null)
        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.LUCK,
                    tier.id() + "_fortune_mainhand",
                    "forgeutil",
                    fortune.get(slot).value().doubleValue() * (1 + factor),
                    constructOperation(fortune.get(slot).operation()),
                    EquipmentSlot.MAINHAND
            );

            AttributeUtil.putAttribute(
                    stack,
                    Attributes.LUCK,
                    tier.id() + "_fortune_offhand",
                    "forgeutil",
                    fortune.get(slot).value().doubleValue() * (1 + factor),
                    constructOperation(fortune.get(slot).operation()),
                    EquipmentSlot.OFFHAND
            );
        }

        if ((speed = tier.speed()) != null)
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ATTACK_SPEED,
                    tier.id() + "_attack_speed",
                    "forgeutil",
                    speed.get(slot).value() * (1 + factor),
                    constructOperation(speed.get(slot).operation()),
                    EquipmentSlot.MAINHAND
            );

        return stack;
    }

    static
    {
        // Building vanilla support
        // Please don't ask why the
        // mod id belongs to awaken
        // mod, I don't know either
        // Coal support
        coalTier = registerTier( // C + 4
                AwakenRPG.MODID,
                "coal",
                UpgradeTier.StreamTierBuilder.of(Color.BLACK, "coal")
                        .setEfficiencySingle(UpgradeTier.all, (param) -> 0.5)
                        .setSpeedSingle(UpgradeTier.all, (param) -> 1)
                        .build(),
                Items.COAL
        );

        // Copper support
        copperTier = registerTier( // Cu + 1 / + 2
                AwakenRPG.MODID,
                "copper",
                UpgradeTier.StreamTierBuilder.of(Color.ORANGE, "copper")
                        .setAttackSingle(UpgradeTier.tools, (param) -> 1.75) // hmm... copper hurts? doesn't it? CuO may bring unexpected biotic infection
                        .build(),
                Items.COPPER_INGOT
        );

        diamondTier = registerTier( // C + 1
                AwakenRPG.MODID,
                "diamond",
                UpgradeTier.StreamTierBuilder.of(Color.CYAN, "diamond")
                        .setArmorSingle(UpgradeTier.armors, (param) -> 1.25F)
                        .setDurabilityCompound(UpgradeTier.all, (param) -> new UpgradeTier.CompoundIntegerModifier(2.25, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setEfficiencyCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundFloatModifier(1.05F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setEnchantSingle(UpgradeTier.all, (param) -> 6)
                        .setFortuneCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundFloatModifier(1.25F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setProtectionSingle(UpgradeTier.armors, (param) -> 0.5F)
                        .setSpeedSingle(UpgradeTier.tools, (param) -> 1.75F)
                        .build(),
                Items.DIAMOND
        );

        // Gold support
        goldTier = registerTier(
                AwakenRPG.MODID,
                "gold",
                UpgradeTier.StreamTierBuilder.of(Color.YELLOW, "gold")
                        .setDurabilityCompound(UpgradeTier.all, (param) -> new UpgradeTier.CompoundIntegerModifier(60, UpgradeTier.TierModifierOperation.MINUS))
                        .setEnchantSingle(UpgradeTier.all, (param) -> 10)
                        .setFortuneCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundFloatModifier(1.5F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .build(),
                Items.GOLD_INGOT,
                Items.GOLD_NUGGET
        );

        // Iron support
        ironTier = registerTier(
                AwakenRPG.MODID,
                "iron",
                UpgradeTier.StreamTierBuilder.of(Color.LIGHT_GRAY, "iron")
                        .setAttackSingle(UpgradeTier.tools, (param) -> 1.75F)
                        .setDurabilityCompound(UpgradeTier.all, (param) -> new UpgradeTier.CompoundIntegerModifier(1.35F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setProtectionCompound(UpgradeTier.armors, (param) -> new UpgradeTier.CompoundFloatModifier(1.05F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setSpeedCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundDoubleModifier(0.1, UpgradeTier.TierModifierOperation.MINUS))
                        .build(),
                Items.IRON_NUGGET,
                Items.IRON_INGOT
        );

        // Netherite support
        netheriteTier = registerTier(
                AwakenRPG.MODID,
                "netherite",
                UpgradeTier.StreamTierBuilder.of(Color.DARK_GRAY, "netherite")
                        .setArmorSingle(UpgradeTier.armors, (param) -> 1.35F)
                        .setDurabilityCompound(UpgradeTier.all, (param) -> new UpgradeTier.CompoundIntegerModifier(5.4F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setEfficiencyCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundFloatModifier(1.25F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setEnchantSingle(UpgradeTier.all, (param) -> 10)
                        .setFortuneCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundFloatModifier(1.35F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setProtectionSingle(UpgradeTier.armors, (param) -> 2)
                        .setSpeedSingle(UpgradeTier.tools, (param) -> 1.5F)
                        .build(),
                Items.NETHERITE_INGOT
        );

        // Obsidian Support
        // As we know as a common sense,
        // obsidian is fragile, isn't it?
        obsidianTier = registerTier(
                AwakenRPG.MODID,
                "obsidian",
                UpgradeTier.StreamTierBuilder.of(Color.MAGENTA, "obsidian")
                        .setArmorSingle(UpgradeTier.armors, (param) -> 1.5F)
                        .setDurabilityCompound(UpgradeTier.all, (param) -> new UpgradeTier.CompoundIntegerModifier(2.1F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setEfficiencyCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundFloatModifier(3.4F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setEnchantSingle(UpgradeTier.all, (param) -> 20)
                        .setFortuneCompound(UpgradeTier.tools, (param) -> new UpgradeTier.CompoundFloatModifier(1.34F, UpgradeTier.TierModifierOperation.MULTIPLY))
                        .setProtectionSingle(UpgradeTier.armors, (param) -> 3.2F)
                        .setSpeedSingle(UpgradeTier.tools, (param) -> 2.6F)
                        .build(),
                Items.CRYING_OBSIDIAN,
                Items.OBSIDIAN
        );
    }

    public static UpgradeTier[] shuffle(
            Random random,
            int lvl,
            int max
    )
    {
        if (registeredTiers.isEmpty())
            return new UpgradeTier[]{};

        int count = random.nextInt(Math.abs(lvl / (lvl - max)));

        List<UpgradeTier> tiers = new ArrayList<>();

        for (int i = 0; i < count; i++)
        {
            tiers.add(registeredTiers.values().toArray(UpgradeTier.CompoundTierContainer[]::new)[random.nextInt(registeredTiers.size())].tier());
        }

        return tiers.toArray(UpgradeTier[]::new);
    }
}