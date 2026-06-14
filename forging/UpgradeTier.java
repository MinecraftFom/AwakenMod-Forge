package com.fomdev.awaken.forging;

import com.fomdev.flib.util.Suggested;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

// The properties of the tier CAN BE NULL if not specified!!!
public interface UpgradeTier {
    Integer MODIFY_ACTION_ADDITION = 0;
    Integer MODIFY_ACTION_DIVISION = 1;
    Integer MODIFY_ACTION_MINUS    = 2;
    Integer MODIFY_ACTION_MULTIPLY = 3;
    List<TierModifierSlot> all = List.of(TierModifierSlot.AXE, TierModifierSlot.BOW, TierModifierSlot.BOOT, TierModifierSlot.CHEST, TierModifierSlot.HEAD, TierModifierSlot.HOE, TierModifierSlot.LEGS, TierModifierSlot.PICK, TierModifierSlot.SHIELD, TierModifierSlot.SHOVE, TierModifierSlot.SWORD);
    List<TierModifierSlot> armors = List.of(TierModifierSlot.BOOT, TierModifierSlot.CHEST, TierModifierSlot.LEGS, TierModifierSlot.HEAD, TierModifierSlot.SHIELD);
    List<TierModifierSlot> tools = List.of(TierModifierSlot.AXE, TierModifierSlot.BOW, TierModifierSlot.HOE, TierModifierSlot.PICK, TierModifierSlot.SHOVE, TierModifierSlot.SWORD);

    @NotNull Color color();
    @NotNull String id();

    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Double>>  armor()      { return null; }
    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Float>>   attack()     { return null; }
    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Integer>> durability() { return null; }
    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Float>>   efficiency() { return null; }
    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Integer>> enchant()    { return null; }
    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Float>>   fortune()    { return null; }
    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Float>>   protection() { return null; }
    default @Nullable Map<TierModifierSlot, CompoundTierModifier<Double>>  speed()      { return null; }

    static <T extends Number> CompoundTierModifier<T> of(T number)
    {
        return of(number, TierModifierOperation.ADD);
    }

    static <T extends Number> CompoundTierModifier<T> of(T number, TierModifierOperation operation)
    {
        return new CompoundTierModifier<>(number, operation);
    }

    static TierModifierSlot castSlot(Item item)
    {
        if (item instanceof AxeItem) return TierModifierSlot.AXE;
        if (item instanceof BowItem) return TierModifierSlot.BOW;
        if (item instanceof HoeItem) return TierModifierSlot.HOE;
        if (item instanceof PickaxeItem) return TierModifierSlot.PICK;
        if (item instanceof ShieldItem) return TierModifierSlot.SHIELD;
        if (item instanceof ShovelItem) return TierModifierSlot.SHOVE;
        if (item instanceof SwordItem) return TierModifierSlot.SWORD;
        if (item instanceof ArmorItem armor)
        {
            return switch (armor.getEquipmentSlot())
            {
                case CHEST -> TierModifierSlot.CHEST;
                case FEET -> TierModifierSlot.BOOT;
                case HEAD -> TierModifierSlot.HEAD;
                case LEGS -> TierModifierSlot.LEGS;
                default -> null;
            };
        }

        return null;
    }

    class StreamTierBuilder
    {
        private final Color color;
        private final String id;

        private final Map<TierModifierSlot, CompoundTierModifier<Double>>  armor;
        private final Map<TierModifierSlot, CompoundTierModifier<Float>>   attack;
        private final Map<TierModifierSlot, CompoundTierModifier<Integer>> durability;
        private final Map<TierModifierSlot, CompoundTierModifier<Float>>   efficiency;
        private final Map<TierModifierSlot, CompoundTierModifier<Integer>> enchant;
        private final Map<TierModifierSlot, CompoundTierModifier<Float>>   fortune;
        private final Map<TierModifierSlot, CompoundTierModifier<Float>>   protection;
        private final Map<TierModifierSlot, CompoundTierModifier<Double>>  speed;

        protected StreamTierBuilder(Color color, String id)
        {
            this.color = color;
            this.id    = id;

            armor      = new HashMap<>();
            attack     = new HashMap<>();
            durability = new HashMap<>();
            efficiency = new HashMap<>();
            enchant    = new HashMap<>();
            fortune    = new HashMap<>();
            protection = new HashMap<>();
            speed      = new HashMap<>();
        }

        public static StreamTierBuilder of(Color color, String id)
        {
            return new StreamTierBuilder(color, id);
        }

        public StreamTierBuilder setArmorCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Double>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Double> value = values.apply(slot);
                if (value == null) continue;

                armor.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setArmorSingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                armor.computeIfAbsent(slot, s -> UpgradeTier.of(value.doubleValue()));
            }

            return this;
        }

        public StreamTierBuilder setAttackCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Float>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Float> value = values.apply(slot);
                if (value == null) continue;

                attack.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setAttackSingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                attack.computeIfAbsent(slot, s -> UpgradeTier.of(value.floatValue()));
            }

            return this;
        }

        public StreamTierBuilder setDurabilityCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Integer>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Integer> value = values.apply(slot);
                if (value == null) continue;

                durability.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setDurabilitySingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                durability.computeIfAbsent(slot, s -> UpgradeTier.of(value.intValue()));
            }

            return this;
        }

        public StreamTierBuilder setEfficiencyCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Float>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Float> value = values.apply(slot);
                if (value == null) continue;

                efficiency.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setEfficiencySingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                efficiency.computeIfAbsent(slot, s -> UpgradeTier.of(value.floatValue()));
            }

            return this;
        }

        public StreamTierBuilder setEnchantCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Integer>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Integer> value = values.apply(slot);
                if (value == null) continue;

               enchant.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setEnchantSingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                enchant.computeIfAbsent(slot, s -> UpgradeTier.of(value.intValue()));
            }

            return this;
        }

        public StreamTierBuilder setFortuneCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Float>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Float> value = values.apply(slot);
                if (value == null) continue;

                fortune.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setFortuneSingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                fortune.computeIfAbsent(slot, s -> UpgradeTier.of(value.floatValue()));
            }

            return this;
        }

        public StreamTierBuilder setProtectionCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Float>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Float> value = values.apply(slot);
                if (value == null) continue;

                protection.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setProtectionSingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                protection.computeIfAbsent(slot, s -> UpgradeTier.of(value.floatValue()));
            }

            return this;
        }

        public StreamTierBuilder setSpeedCompound
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, CompoundTierModifier<Double>> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                CompoundTierModifier<Double> value = values.apply(slot);
                if (value == null) continue;

                speed.computeIfAbsent(slot, s -> value);
            }

            return this;
        }

        @Suggested
        public StreamTierBuilder setSpeedSingle
                (
                        List<TierModifierSlot> slots,
                        Function<TierModifierSlot, Number> values
                )
        {
            for (TierModifierSlot slot: slots)
            {
                Number value = values.apply(slot);
                if (value == null) continue;

                speed.computeIfAbsent(slot, s -> UpgradeTier.of(value.doubleValue()));
            }

            return this;
        }

        public UpgradeTier build()
        {
            return new UpgradeTier()
            {
                @Override
                public @NotNull Color color()
                {
                    return color;
                }

                @Override
                public @NotNull String id()
                {
                    return id;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Double>> armor()
                {
                    return armor;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Float>> attack()
                {
                    return attack;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Integer>> durability()
                {
                    return durability;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Float>> efficiency()
                {
                    return efficiency;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Integer>> enchant()
                {
                    return enchant;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Float>> fortune()
                {
                    return fortune;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Float>> protection()
                {
                    return protection;
                }

                @Override
                public @org.jetbrains.annotations.Nullable Map<TierModifierSlot, CompoundTierModifier<Double>> speed()
                {
                    return speed;
                }
            };
        }
    }

    record CompoundTierContainer(UpgradeTier tier, List<ItemLike> repr)
    {
        public CompoundTierContainer
                (
                        UpgradeTier tier,
                        ItemLike... repr
                )
        {
            this(tier, Arrays.asList(repr));
        }

        public void addRepr
                (
                        ItemLike... repr
                )
        {
            this.repr.addAll(Arrays.asList(repr));
        }
    }

    // WARNING: Param <T> must be a number
    class CompoundTierModifier<T extends Number>
    {
        private final int operation;
        private final T value;

        public CompoundTierModifier
                (
                        final T value,
                        final int operation
                )
        {
            this.operation = operation;
            this.value = value;
        }

        public CompoundTierModifier
                (
                        final T value,
                        final TierModifierOperation operation
                )
        {
            this(value, operation.operation);
        }

        public int operation()
        {
            return operation;
        }

        public T value()
        {
            return value;
        }
    }

    class CompoundDoubleModifier extends CompoundTierModifier<Double>
    {
        public CompoundDoubleModifier
                (
                        final Number value,
                        final int operation
                )
        {
            super(value.doubleValue(), operation);
        }

        public CompoundDoubleModifier
                (
                        final Number value,
                        final TierModifierOperation operation
                )
        {
            super(value.doubleValue(), operation.operation);
        }
    }

    class CompoundFloatModifier extends CompoundTierModifier<Float>
    {
        public CompoundFloatModifier
                (
                        final Number value,
                        final int operation
                )
        {
            super(value.floatValue(), operation);
        }

        public CompoundFloatModifier
                (
                        final Number value,
                        final TierModifierOperation operation
                )
        {
            super(value.floatValue(), operation.operation);
        }
    }

    class CompoundIntegerModifier extends CompoundTierModifier<Integer>
    {
        public CompoundIntegerModifier
                (
                        final Number value,
                        final int operation
                )
        {
            super(value.intValue(), operation);
        }

        public CompoundIntegerModifier
                (
                        final Number value,
                        final TierModifierOperation operation
                )
        {
            super(value.intValue(), operation.operation);
        }
    }

    class CompoundLongModifier extends CompoundTierModifier<Long>
    {
        public CompoundLongModifier
                (
                        final Number value,
                        final int operation
                )
        {
            super(value.longValue(), operation);
        }

        public CompoundLongModifier
                (
                        final Number value,
                        final TierModifierOperation operation
                )
        {
            super(value.longValue(), operation.operation);
        }
    }

    class CompoundShortModifier extends CompoundTierModifier<Short>
    {
        public CompoundShortModifier
                (
                        final Number value,
                        final int operation
                )
        {
            super(value.shortValue(), operation);
        }

        public CompoundShortModifier
                (
                        final Number value,
                        final TierModifierOperation operation
                )
        {
            super(value.shortValue(), operation.operation);
        }
    }

    enum TierModifierOperation
    {
        ADD      (MODIFY_ACTION_ADDITION),
        DIVIDE   (MODIFY_ACTION_DIVISION),
        MINUS    (MODIFY_ACTION_MINUS),
        MULTIPLY (MODIFY_ACTION_MULTIPLY);

        final int operation;

        TierModifierOperation(final int operand)
        {
            this.operation = operand;
        }
    }

    enum TierModifierSlot
    {
        AXE   ,
        BOOT  ,
        BOW   ,
        CHEST ,
        LEGS  ,
        HEAD  ,
        HOE   ,
        PICK  ,
        SHIELD,
        SHOVE ,
        SWORD
    }
}