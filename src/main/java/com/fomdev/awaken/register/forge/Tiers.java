package com.fomdev.awaken.register.forge;

import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.init.AwakenRPG;
import com.fomdev.awaken.register.AwakenRegistries;
import com.fomdev.flib.load.ForceLoad;
import net.minecraft.world.item.Items;

import java.awt.*;

import static com.fomdev.awaken.forging.ForgeUtils.registerTier;

@ForceLoad(AwakenRegistries.SIG_AWAKEN_TIER)
public class Tiers
{
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
}