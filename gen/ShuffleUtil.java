package com.fomdev.awaken.gen;

import com.fomdev.awaken.enchanting.Alignment;
import com.fomdev.awaken.enchanting.Aspect;
import com.fomdev.awaken.enchanting.EnchantmentRegister;
import com.fomdev.awaken.forging.ForgeUtils;
import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.quality.QualityUtil;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.Title;
import com.fomdev.awaken.title.TitleRegister;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;
import java.util.Random;
import java.util.UUID;

public class ShuffleUtil
{
    public static final Random random = new Random(System.currentTimeMillis());

    public static void shuffleForItemStack(
            ServerLevel dimLevel,
            ItemStack stack
    )
    {
        float currentDifficulty = DifficultyHandler.getLevelDifficulty(dimLevel);
        if (currentDifficulty == 0)
            return;

        int min = random.nextInt((int) currentDifficulty) * 10 + 1;
        int max = random.nextInt((int) currentDifficulty) * 10 + 1;
        if (min == max)
            max++;
        if (min < max)
        {
            int m1 = min;
            min = max;
            max = m1;
        }

        Prefix prefix = TitleRegister.shufflePrefix(random, min, max, currentDifficulty);
        Suffix suffix = TitleRegister.shuffleSuffix(random, min, max, currentDifficulty);
        Title title = TitleRegister.shuffleTitle(random, min, max, currentDifficulty);
        UpgradeTier[] tiers = ForgeUtils.shuffle(random, max - min, max);
        Alignment.AlignmentProvider[] alignments = EnchantmentRegister.shuffleAlignments(random, max - min);
        Aspect.AspectProvider[] defaultAspects = EnchantmentRegister.shuffleAspects(random, max - min);
        Quality quality = QualityUtil.shuffleQuality(random, currentDifficulty);

        boolean shouldAddAttack = random.nextBoolean() && stack.getItem() instanceof DiggerItem;
        boolean shouldAddSpeed = random.nextBoolean() && stack.getItem() instanceof DiggerItem;
        boolean shouldAddProtection = random.nextBoolean() && stack.getItem() instanceof ArmorItem;
        boolean shouldAddArmor = random.nextBoolean() && stack.getItem() instanceof ArmorItem;

        if (shouldAddAttack)
        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ATTACK_SPEED,
                    "shuffle_" + UUID.randomUUID(),
                    "shuffleutil",
                    random.nextDouble(10.0F * currentDifficulty),
                    AttributeModifier.Operation.ADDITION,
                    EquipmentSlot.MAINHAND
            );
        }

        if (shouldAddSpeed)
        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ATTACK_DAMAGE,
                    "shuffle_" + UUID.randomUUID(),
                    "shuffleutil",
                    random.nextDouble(10.0F * currentDifficulty),
                    AttributeModifier.Operation.ADDITION,
                    EquipmentSlot.MAINHAND
            );
        }

        if (shouldAddProtection)
        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ARMOR_TOUGHNESS,
                    "shuffle_" + UUID.randomUUID(),
                    "shuffleutil",
                    random.nextDouble(5.0F * currentDifficulty),
                    AttributeModifier.Operation.ADDITION,
                    EquipmentSlot.MAINHAND
            );
        }

        if (shouldAddArmor)
        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ARMOR,
                    "shuffle_" + UUID.randomUUID(),
                    "shuffleutil",
                    random.nextDouble(5.0F * currentDifficulty),
                    AttributeModifier.Operation.ADDITION,
                    EquipmentSlot.MAINHAND
            );
        }

        if (quality != null) NBTUtil.serializeQuality(stack, quality);

        if (prefix != null) NBTUtil.putPrefix(stack, prefix);
        if (suffix != null) NBTUtil.putSuffix(stack, suffix);
        if (title != null) NBTUtil.serializeTitle(stack, title);
        TitleRegister.syncStackPrefix(stack);
        TitleRegister.syncStackSuffix(stack);
        TitleRegister.syncStackTitle(stack);

        NBTUtil.serializeMaxForgeLevel(stack, tiers.length + random.nextInt(5) + 2);
        Arrays.stream(tiers).forEach(tier -> ForgeUtils.forgeStack(stack, tier));

        Arrays.stream(alignments).forEach(align -> NBTUtil.putEnchantmentAlignment(stack, align));
        Arrays.stream(defaultAspects).forEach(aspect -> NBTUtil.putEnchantmentAspect(stack, aspect));
    }
}