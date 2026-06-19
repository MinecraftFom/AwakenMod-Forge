package com.fomdev.awaken.bonus;

import com.fomdev.awaken.gen.DifficultyHandler;
import com.fomdev.awaken.init.Awaken;
import com.fomdev.awaken.init.AwakenContent;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

@Mod.EventBusSubscriber(modid = AwakenContent.MODID)
public class LooterManager
{
    private static final Random random = new Random();

    @SubscribeEvent
    public static void onLoot(LivingDropsEvent event)
    {
        if (!(event.getEntity() instanceof Mob mob))
            return;

        if (!BonusManager.registeredAdditionalLoots.containsKey(mob.getClass()))
            return;


        List<LootBonusInstance> instances = BonusManager.registeredAdditionalLoots.get(mob.getClass());
        int atMost = random.nextInt((int) (DifficultyHandler.getLevelDifficulty(Objects.requireNonNull(mob.getServer()).overworld()) * event.getLootingLevel() + 2)); // To ensure at least get one additional drop
        if (atMost == 0)
            return;

        List<LootBonusInstance> dropping = new ArrayList<>();
        for (int i = 0; i < atMost; i++)
            dropping.add(instances.get(random.nextInt(instances.size())));

        for (LootBonusInstance inst: dropping)
        {
            if (random.nextInt(100) > inst.chance(DifficultyHandler.getLevelDifficulty(Objects.requireNonNull(mob.getServer()).overworld())))
                continue;

            event.getDrops().add(new ItemEntity(mob.level(), mob.getX(), mob.getY(), mob.getZ(), inst.drop()));
        }
    }
}