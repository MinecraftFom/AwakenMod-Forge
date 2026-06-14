package com.fomdev.awaken.attribute;

import com.fomdev.awaken.init.AwakenContent;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class AwakenAttributes
{
    private static final DeferredRegister<Attribute> ATTRIBUTE_REGISTER = DeferredRegister.create(ForgeRegistries.ATTRIBUTES, AwakenContent.MODID);

    public static final Attribute ATTRIBUTE_CRITICAL; // Critical hit chance
    public static final Attribute ATTRIBUTE_LENGTH; // Hand length

    public static void register(
            IEventBus bus
    )
    {
        ATTRIBUTE_REGISTER.register(bus);
    }

    private static Attribute register(
            String id,
            double def,
            double min,
            double max
    )
    {
        Attribute attr = new RangedAttribute("attribute.name.awaken." + id, def, min, max);
        ATTRIBUTE_REGISTER.register(id, () -> attr);
        return attr;
    }

    static
    {
        ATTRIBUTE_CRITICAL = register("critical", 0.01D, 0.0D, 1.0D);
        ATTRIBUTE_LENGTH = register("length", 4.0D, 3.0D, 10.0D);
    }
}