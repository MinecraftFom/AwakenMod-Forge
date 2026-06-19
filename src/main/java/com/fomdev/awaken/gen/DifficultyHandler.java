package com.fomdev.awaken.gen;

import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.nbt.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class DifficultyHandler
{
    public static final Random random = new Random(System.currentTimeMillis());
    public static final String namespace = "awakenedDifficulty";
    public static final Map<ServerLevel, SavedDifficultyLevel> levels = new HashMap<>();

    public static final Map<ResourceLocation, Float> dimensionFactor = new HashMap<>();

    public static float getLevelDifficulty(
            ServerLevel level
    )
    {
        if (!levels.containsKey(level))
            return 0.0F;

        SavedDifficultyLevel lvl = levels.get(level);
        if (lvl == null)
            return 0.0F;

        return lvl.getLevel();
    }

    public static void registerDimensionFactor(
            ResourceLocation dimensionID,
            Float factor
    )
    {
        if (dimensionFactor.containsKey(dimensionID))
            return;

        dimensionFactor.put(dimensionID, factor);
    }

    public static void setLevelDifficulty(
            ServerLevel level,
            float diff
    )
    {
        if (!levels.containsKey(level))
            levels.put(level, readDifficulty(level));

        SavedDifficultyLevel sl = levels.get(level);
        if (sl != null)
            sl.setLevel(diff);
    }

    @SubscribeEvent
    public static void onLevelDayPassed(
            TickEvent.LevelTickEvent event
    )
    {
        if (!(event.level instanceof ServerLevel level))
            return;

        if (level.getDayTime() % 24000 == 0) // A new day
        {
            int day = Math.toIntExact(level.getDayTime() / 24000);
            if (day == 0)
                return; // The first day has no difficulty

            Difficulty diff = level.getDifficulty();

            float diffFactor = switch (diff)
            {
                case PEACEFUL -> 1.0F;
                case EASY -> 2.0F;
                case NORMAL -> 10.0F;
                case HARD -> 20.0F;
            };

            int playerCount = level.getServer().getPlayerList().getPlayerCount();
            if (playerCount == 0)
                return;

            Float[] levels = level.getServer().getPlayerList().getPlayers().stream().map(NBTUtil::deserializeAwakenLevel).toArray(Float[]::new);
            float total = 0.0F;
            for (Float fl: levels) total += fl;
            float average = total / playerCount;
            float offset = day * diffFactor;

            if (average == 0)
                average = 0.01F;

            if (offset == 0)
                offset = 0.01F;

            float diffOffset = random.nextFloat(offset);
            if (diffOffset == 0)
                diffOffset = 0.01F;

            float randValue = random.nextFloat(diffOffset * average) / day;
            if (randValue == 0)
                randValue = 0.01F;

            float currentDifficulty = getLevelDifficulty(level);
            float dimedValue = randValue * getDimensionFactor(level);
            setLevelDifficulty(level, currentDifficulty + dimedValue * 100);
        }
    }

    @SubscribeEvent
    public static void onSyncData(
            LevelEvent.Load event
    )
    {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;

        levels.put(serverLevel, readDifficulty(serverLevel));
    }

    @SubscribeEvent
    public static void onSaveData(
            LevelEvent.Unload event
    )
    {
        if (!(event.getLevel() instanceof ServerLevel serverLevel))
            return;

        SavedDifficultyLevel level = levels.get(serverLevel);
        if (level != null)
            level.setDirty();
    }

    public static SavedDifficultyLevel readDifficulty(
            ServerLevel level
    )
    {
        DimensionDataStorage storage = level.getDataStorage();

        return storage.computeIfAbsent(
                SavedDifficultyLevel::load,
                SavedDifficultyLevel::new,
                namespace
        );
    }

    private static float getDimensionFactor(
            ServerLevel level
    )
    {
        ResourceLocation location = level.dimensionTypeId().location();

        if (!dimensionFactor.containsKey(location))
            return 1.0F;

        Float factor = dimensionFactor.get(location);
        if (factor == null)
            return 1.0F;

        return factor;
    }

    public static class SavedDifficultyLevel extends SavedData
    {
        private float level = 0.0F;

        public SavedDifficultyLevel() {}

        public static SavedDifficultyLevel load(CompoundTag tag)
        {
            SavedDifficultyLevel data = new SavedDifficultyLevel();
            data.level = tag.contains(namespace)? tag.getFloat(namespace): 0.0F;

            return data;
        }

        public float getLevel()
        {
            return this.level;
        }

        public void setLevel(float level)
        {
            this.level = level;
            this.setDirty();
        }

        @Override
        public @NotNull CompoundTag save(@NotNull CompoundTag tag)
        {
            tag.putFloat(namespace, this.level);
            return tag;
        }
    }

    static
    {
        registerDimensionFactor(
                ResourceLocation.parse("minecraft:overworld"),
                0.45F
        );

        registerDimensionFactor(
                ResourceLocation.parse("minecraft:the_nether"),
                1.25F
        );

        registerDimensionFactor(
                ResourceLocation.parse("minecraft:the_end"),
                3.24F
        );
    }
}