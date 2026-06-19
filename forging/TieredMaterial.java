package com.fomdev.awaken.forging;

import com.fomdev.flib.entry.entries.AbstractItem;
import org.jetbrains.annotations.NotNull;

public class TieredMaterial extends AbstractItem
{
    private final String modid;
    private final String name;
    protected final UpgradeTier tier;

    public TieredMaterial(String modid, String id, UpgradeTier tier)
    {
        super(id);
        this.modid = modid;
        this.name = id;
        this.tier = tier;
    }

    @Override
    public @NotNull AbstractItem build()
    {
        super.build();
        ForgeUtils.registerTier(modid, name, tier, this.repr);
        return this;
    }
}