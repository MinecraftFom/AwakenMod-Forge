package com.fomdev.awaken.enchanting;

import com.fomdev.awaken.forging.UpgradeTier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.function.BiConsumer;

public interface Alignment
{
    String id();
    Color color();

    @NotNull List<Aspect.AspectProvider> aspects();

    UpgradeTier.TierModifierSlot[] slots();
    Class<? extends Event> evtRequired();
    void onEvent(@NotNull Event event, int lvl);
    int  maxLevel();

    static Alignment of(
            String id,
            Color color,
            UpgradeTier.TierModifierSlot[] slots,
            List<Aspect.AspectProvider> requiredAspects,
            Class<? extends Event> eventRequired,
            int maxLevel,
            BiConsumer<Event, Integer> procedure
    )
    {
        return new Alignment()
        {
            @Override
            public String id()
            {
                return id;
            }

            @Override
            public Color color()
            {
                return color;
            }

            @Override
            public UpgradeTier.TierModifierSlot[] slots()
            {
                return slots;
            }

            @Override
            public @NotNull List<Aspect.AspectProvider> aspects()
            {
                return requiredAspects;
            }

            @Override
            public Class<? extends Event> evtRequired()
            {
                return eventRequired;
            }

            @Override
            public void onEvent(@NotNull Event event, int lvl)
            {
                if (event.getClass().isInstance(this.evtRequired()))
                    procedure.accept(event, lvl);
            }

            @Override
            public int maxLevel()
            {
                return maxLevel;
            }
        };
    }

    record AlignmentProvider
            (
                    Alignment alignment,
                    int level
            )
    {};
}