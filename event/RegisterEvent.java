package com.fomdev.awaken.event;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraftforge.eventbus.api.Event;

import java.util.function.Supplier;

public class RegisterEvent extends Event
{
    private final ResourceKey<? extends Registry<?>> key;

    public RegisterEvent(
            ResourceKey<? extends Registry<?>> key
    )
    {
        this.key = key;
    }

    public boolean ifMatch(ResourceKey<? extends Registry<?>> key, Supplier<Boolean> procedure)
    {
        if (key.isFor(this.key))
        {
            return procedure.get();
        }

        return false;
    }
}