package com.fomdev.awaken.gen;

import com.fomdev.awaken.init.Awaken;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
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

@Mod.EventBusSubscriber(modid = Awaken.MODID)
public class DifficultyHandler
{
    public static final String namespace = "awakenedDifficulty";
    public static final Map<ServerLevel, SavedDifficultyLevel> levels = new HashMap<>();

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
            Difficulty diff = level.getDifficulty();

            float diffFactor = switch (diff)
            {
                case PEACEFUL -> 1.0F;
                case EASY -> 2.0F;
                case NORMAL -> 10.0F;
                case HARD -> 20.0F;
            };

            int playerCount = level.players().size();


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

        levels.get(serverLevel).setDirty();
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

    public static class SavedDifficultyLevel extends SavedData
    {
        private int level = 0;

        private SavedDifficultyLevel() {}

        public static SavedDifficultyLevel load(CompoundTag tag)
        {
            if (!tag.contains(namespace))
                tag.putInt(namespace, 0);

            SavedDifficultyLevel data = new SavedDifficultyLevel();
            data.level = tag.getInt(namespace);

            return data;
        }

        public int getLevel()
        {
            return this.level;
        }

        public void setLevel(int level)
        {
            this.level = level;
        }

        @Override
        public @NotNull CompoundTag save(@NotNull CompoundTag tag)
        {
            tag.putInt(namespace, level);
            return tag;
        }
    }
}