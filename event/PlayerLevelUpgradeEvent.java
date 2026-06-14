package com.fomdev.awaken.event;

import com.fomdev.awaken.awaken.AwakenLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class PlayerLevelUpgradeEvent extends Event
{
    public final ServerPlayer entity;
    public final AwakenLevel original;
    public final AwakenLevel current;

    public PlayerLevelUpgradeEvent(
            ServerPlayer entity,
            AwakenLevel origin,
            AwakenLevel current
    )
    {
        this.entity = entity;
        this.original = origin;
        this.current = current;
    }

    @Override
    public boolean isCancelable()
    {
        return false;
    }
}