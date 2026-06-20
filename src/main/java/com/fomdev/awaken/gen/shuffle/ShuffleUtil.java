package com.fomdev.awaken.gen.shuffle;

import com.fomdev.awaken.attribute.AttributeManager;
import com.fomdev.awaken.attribute.SetAttribute;
import com.fomdev.awaken.enchanting.Alignment;
import com.fomdev.awaken.enchanting.Aspect;
import com.fomdev.awaken.enchanting.EnchantmentRegister;
import com.fomdev.awaken.forging.ForgeUtils;
import com.fomdev.awaken.forging.UpgradeTier;
import com.fomdev.awaken.gen.DifficultyHandler;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.quality.QualityUtil;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.Title;
import com.fomdev.awaken.title.TitleRegister;
import com.fomdev.awaken.util.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

import java.util.*;

import static com.fomdev.awaken.util.Util.getSlot;

public class ShuffleUtil
{
    public static final List<ResourceLocation> armorPatterns = new ArrayList<>();
    public static final List<ResourceLocation> armorTrims = new ArrayList<>();
    public static final Map<EnchantmentCategory, List<Enchantment>> enchantments = new HashMap<>();
    public static final Random random = new Random(System.currentTimeMillis());

    public static void addAvailableEnchantment(
            EnchantmentCategory category,
            Enchantment ench
    )
    {
        enchantments.computeIfAbsent(category, c -> new ArrayList<>()).add(ench);
    }

    public static void addAvailablePattern(
            ResourceLocation pattern
    )
    {
        armorPatterns.add(pattern);
    }

    public static void addAvailableTrim(
            ResourceLocation trim
    )
    {
        armorTrims.add(trim);
    }

    public static void shuffleForItemStack(
            ServerLevel dimLevel,
            ItemStack stack
    )
    {
        Awaken.LOGGER.info(Arrays.toString(armorPatterns.toArray()));
        Awaken.LOGGER.info(Arrays.toString(armorTrims.toArray()));

        float currentDifficulty = DifficultyHandler.getLevelDifficulty(dimLevel);
        if (currentDifficulty == 0)
            return;

        int min = random.nextInt((int) (currentDifficulty <= 0.0F? 1.0F: currentDifficulty)) * 10 + 1;
        int max = random.nextInt((int) (currentDifficulty <= 0.0F? 1.0F: currentDifficulty)) * 10 + 1;
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

        if (quality == null) // Essential!
            return;

        NBTUtil.serializeQuality(stack, quality);

        if (prefix != null) NBTUtil.putPrefix(stack, prefix);
        if (suffix != null) NBTUtil.putSuffix(stack, suffix);
        if (title != null) NBTUtil.serializeTitle(stack, title);

        boolean shouldAddAttack = random.nextBoolean() && stack.getItem() instanceof DiggerItem;
        boolean shouldAddSpeed = random.nextBoolean() && stack.getItem() instanceof DiggerItem;
        boolean shouldAddProtection = random.nextBoolean() && stack.getItem() instanceof ArmorItem;
        boolean shouldAddArmor = random.nextBoolean() && stack.getItem() instanceof ArmorItem;

        if (shouldAddAttack)

        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ATTACK_DAMAGE,
                    "shuffle_" + UUID.randomUUID(),
                    "shuffleutil",
                    random.nextDouble(10.0F * currentDifficulty) * quality.factor(),
                    AttributeModifier.Operation.ADDITION,
                    EquipmentSlot.MAINHAND
            );
        }

        if (shouldAddSpeed)
        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ATTACK_SPEED,
                    "shuffle_" + UUID.randomUUID(),
                    "shuffleutil",
                    random.nextDouble(10.0F * currentDifficulty) * quality.factor(),
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
                    random.nextDouble(5.0F * currentDifficulty) * quality.factor(),
                    AttributeModifier.Operation.ADDITION,
                    stack.getEquipmentSlot()
            );
        }

        if (shouldAddArmor)
        {
            AttributeUtil.putAttribute(
                    stack,
                    Attributes.ARMOR,
                    "shuffle_" + UUID.randomUUID(),
                    "shuffleutil",
                    random.nextDouble(5.0F * currentDifficulty) * quality.factor(),
                    AttributeModifier.Operation.ADDITION,
                    stack.getEquipmentSlot()
            );
        }

        if (stack.getItem() instanceof ArmorItem && random.nextBoolean())
        {
            ResourceLocation pattern = shufflePattern(random);
            ResourceLocation trim = shuffleTrim(random);

            NBTUtil.setTrim(stack, trim, pattern);
        }

        NBTUtil.serializeMaxForgeLevel(stack, tiers.length + random.nextInt(5) + 2);
        Arrays.stream(tiers).forEach(tier -> ForgeUtils.forgeStack(stack, tier));

        Arrays.stream(alignments).forEach(align -> NBTUtil.putEnchantmentAlignment(stack, align));
        Arrays.stream(defaultAspects).forEach(aspect -> NBTUtil.putEnchantmentAspect(stack, aspect));

//        shuffleAttributes(random, getSlot(stack), random.nextInt(6) / 5).forEach(attr -> applyAttribute(stack, attr));
    }

    public static void shuffleForItemStackHardcore(
            ServerLevel dimLevel,
            ItemStack stack
    )
    {

    }

    private static void applyAttribute(
            ItemStack stack,
            Util.AttributeHolder holder
    )
    {
        AttributeUtil.putAttribute(
                stack,
                holder.attr(),
                "shuffled_attr_" + UUID.randomUUID(),
                "vanilla",
                holder.amount(),
                holder.operation(),
                getSlot(stack)
        );
    }

    private static List<Util.AttributeHolder> shuffleAttributes(
            Random random,
            EquipmentSlot slot,
            double diff,
            int atMost
    )
    {
        List<Attribute> used = new ArrayList<>();
        List<Util.AttributeHolder> holders = new ArrayList<>();

        for (int i = 0; i < atMost; i++)
        {
            SetAttribute attribute = Util.weightedShuffle(random, diff, AttributeManager.getAttributes(slot));
            if (attribute == null)
                continue;

            if (used.contains(attribute.attribute()))
                continue;

            used.add(attribute.attribute());
            holders.add(new Util.AttributeHolder(attribute.attribute(), attribute.amount(), attribute.operation()));
        }

        return holders;
    }

    private static Enchantment shuffleEnchantment(
            Random random,
            EnchantmentCategory category
    )
    {
        return enchantments.get(category).get(random.nextInt(enchantments.get(category).size()));
    }

    private static ResourceLocation shufflePattern(
            Random random
    )
    {
        return armorPatterns.get(random.nextInt(armorPatterns.size()));
    }

    private static ResourceLocation shuffleTrim(
            Random random
    )
    {
        return armorTrims.get(random.nextInt(armorTrims.size()));
    }
}