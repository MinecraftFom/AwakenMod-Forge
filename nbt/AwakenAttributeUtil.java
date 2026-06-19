package com.fomdev.awaken.nbt;

import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.Title;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Range;

import java.util.*;

public class AwakenAttributeUtil
{
    public static int getDurability(
            ItemStack stack
    )
    {
        int p1 = getDurability$Title(stack);
        int p2 = getDurability$Tier(stack, p1);

        return p2;
    }

    public static Multimap<Attribute, AttributeModifier> getTierAttributes$Armor(
            ItemStack stack,
            EquipmentSlot eslot
    )
    {
        if (!eslot.isArmor() || !(stack.getItem() instanceof ArmorItem item) || item.getEquipmentSlot() != eslot)
            return HashMultimap.create();

        List<UpgradeTier> tiers = NBTUtil.getForgeTiers(stack);

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(item);
        Quality quality = NBTUtil.deserializeQuality(stack);
        float factor = quality == null? 0.0F: quality.factor();

        for (UpgradeTier tier: tiers)
        {
            Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Double>>     armor;
            Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>>      protection;

            if ((armor = tier.armor()) != null && armor.get(slot) != null && eslot == item.getEquipmentSlot())
                modifiers.put(
                        Attributes.ARMOR,
                        new AttributeModifier(
                                tier.id() + "_" + slot.name() + "_armor",
                                armor.get(slot).value() * (1 + factor),
                                constructOperation(armor.get(slot).operation())
                        )
                );

            if ((protection = tier.protection()) != null && protection.get(slot) != null && eslot == item.getEquipmentSlot())
                modifiers.put(
                        Attributes.ARMOR_TOUGHNESS,
                        new AttributeModifier(
                                tier.id() + "_" + slot.name() + "_toughness",
                                protection.get(slot).value() * (1 + factor),
                                constructOperation(protection.get(slot).operation())
                        )
                );
        }

        return modifiers;
    }

    public static Multimap<Attribute, AttributeModifier> getTierAttributes$Tool(
            ItemStack stack,
            EquipmentSlot eslot
    )
    {
        if (eslot.isArmor())
            return HashMultimap.create();

        List<UpgradeTier> tiers = NBTUtil.getForgeTiers(stack);

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        UpgradeTier.TierModifierSlot slot = UpgradeTier.castSlot(stack.getItem());
        Quality quality = NBTUtil.deserializeQuality(stack);
        float factor = quality == null? 0.0F: quality.factor();

        for (UpgradeTier tier: tiers)
        {
            Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>> attack;
            Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Float>>   fortune;
            Map<UpgradeTier.TierModifierSlot, UpgradeTier.CompoundTierModifier<Double>>  speed;

            if ((attack = tier.attack()) != null && attack.get(slot) != null && eslot == EquipmentSlot.MAINHAND)
                modifiers.put(
                        Attributes.ATTACK_DAMAGE,
                        new AttributeModifier(
                                tier.id() + "_" + slot.name() + "_attack",
                                attack.get(slot).value().doubleValue() * (1 + factor),
                                constructOperation(attack.get(slot).operation())
                        )
                );

            if ((fortune = tier.fortune()) != null && fortune.get(slot) != null)
            {
                 if (eslot == EquipmentSlot.MAINHAND)
                     modifiers.put(
                            Attributes.LUCK,
                            new AttributeModifier(
                                    tier.id() + "_mainhand_fortune",
                                    fortune.get(slot).value().doubleValue() * (1 + factor),
                                    constructOperation(fortune.get(slot).operation())
                            )
                     );

                if (eslot == EquipmentSlot.OFFHAND)
                    modifiers.put(
                            Attributes.LUCK,
                            new AttributeModifier(
                                    tier.id() + "_offhand_fortune",
                                    fortune.get(slot).value().doubleValue() * (1 + factor),
                                    constructOperation(fortune.get(slot).operation())
                            )
                    );
            }

            if ((speed = tier.speed()) != null && eslot == EquipmentSlot.MAINHAND)
                modifiers.put(
                        Attributes.ATTACK_SPEED,
                        new AttributeModifier(
                                tier.id() + "_" + slot.name() + "_speed",
                                speed.get(slot).value() * (1 + factor),
                                constructOperation(speed.get(slot).operation())
                        )
                );
        }

        return modifiers;
    }

    public static Multimap<Attribute, AttributeModifier> getTitleAttributes(
            ItemStack stack,
            EquipmentSlot slot
    )
    {
        Prefix prefix = NBTUtil.deserializePrefixes(stack);
        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        Title title = NBTUtil.deserializeTitle(stack);
        Quality quality = NBTUtil.deserializeQuality(stack);

        if (prefix == null || suffix == null || title == null) // To ensure it's legal
            return HashMultimap.create();

        Multimap<Attribute, AttributeModifier> modifiers = HashMultimap.create();
        Attribute trigger = suffix.triggerAttribute();
        float factor = suffix.modifyFactor();

        Title.CompoundAttribute attribute = title.attrs(quality == null? 1.0F: quality.factor());
        if (attribute.slot() != null && !Arrays.asList(attribute.slot()).contains(slot))
            return HashMultimap.create();

        modifiers.put(
                attribute.attr(),
                new AttributeModifier(
                        UUID.randomUUID(),
                        title.id() + attribute.attr().toString() + UUID.randomUUID(),
                        attribute.attr().equals(trigger)? attribute.amount() * factor: attribute.amount(),
                        attribute.operation()
                )
        );

        return modifiers;
    }

    private static int getDurability$Tier(
            ItemStack stack,
            int value
    )
    {
        List<UpgradeTier> tier = NBTUtil.getForgeTiers(stack);

        int result = value;
        for (UpgradeTier t: tier)
        {
            if (t.durability() == null)
                continue;

            UpgradeTier.CompoundTierModifier<Integer> container = Objects.requireNonNull(t.durability()).get(UpgradeTier.castSlot(stack.getItem()));
            result = switch (container.operation())
                {
                    case 0 -> result + container.value();
                    case 1 -> result - container.value();
                    case 2 -> result * container.value();
                    case 3 -> result / container.value();
                    default -> throw new IllegalStateException("Invalid compound tier operation found");
                };
        }

        return result;
    }

    private static int getDurability$Title(
            ItemStack stack
    )
    {
        Prefix prefix = NBTUtil.deserializePrefixes(stack);
        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        Title title = NBTUtil.deserializeTitle(stack);

        if (prefix == null || suffix == null || title == null)
            return 0;

        return prefix.additionalDurability() + suffix.additionalDurability() + title.additionalDurability();
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
}