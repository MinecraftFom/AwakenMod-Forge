package com.fomdev.awaken.nbt;

import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.UUID;

public class AttributeUtil
{
    public static final String CUSTOM_NBT_NAMESPACE = "awakenNBT";
    public static final String CUSTOM_NBT_SIGNATURE = "awakenNBTSign";

    private static final AttributeHolder EMPTY = new AttributeHolder(null, null, null, null, null);

    public static void clearAttribute(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();

        tag.remove(CUSTOM_NBT_NAMESPACE);
    }

    public static void delAttribute(
            ItemStack stack,
            String id
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            tag.put(CUSTOM_NBT_NAMESPACE, new CompoundTag());

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        nbtTag.remove(id);
    }

    public static void putAttribute(
            ItemStack stack,
            Attribute attribute,
            String id,
            Double amount,
            AttributeModifier.Operation operation,
            EquipmentSlot slot
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            tag.put(CUSTOM_NBT_NAMESPACE, new CompoundTag());

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);

        nbtTag.put(id, new CompoundTag());
        CompoundTag modifier = nbtTag.getCompound(id);
        setModifier(modifier, new AttributeHolder(
                attribute,
                id,
                slot,
                amount,
                operation
        ));

        syncPersistentDataToAttribute(stack);
    }

    public static void syncPersistentDataToAttribute(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("AttributeModifiers"))
            tag.put("AttributeModifiers", new ListTag());

        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            return;

        ListTag attrTag = tag.getList("AttributeModifiers", 9);
        CompoundTag persistDataTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);

        clearModTag(attrTag);

        for (String id: persistDataTag.getAllKeys())
        {
            CompoundTag mtag = persistDataTag.getCompound(id);
            String attr = mtag.getString("attr");
            double amount = persistDataTag.getDouble("amount");
            int operation = persistDataTag.getInt("operation");
            String slot = persistDataTag.getString("slot");

            CompoundTag modifier = new CompoundTag();
            modifier.putString("AttributeName", attr);
            modifier.putDouble("Amount", amount);
            modifier.putString("Slot", slot);
            modifier.putInt("Operation", operation);
            modifier.putString(CUSTOM_NBT_SIGNATURE, id);
            modifier.putIntArray("UUID", UUIDUtil.uuidToIntArray(UUID.randomUUID()));

            attrTag.add(modifier);
        }
    }

    public static void syncAttributeDataToPersistent(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains("AttributeModifiers"))
            return;

        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            tag.put(CUSTOM_NBT_NAMESPACE, new CompoundTag());

        ListTag attrTag = tag.getList("AttributeModifiers", 9);
        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);

        for (Tag modifier: attrTag)
        {
            if (!(modifier instanceof CompoundTag mtag))
                continue;

            AttributeHolder holder = getModifier(mtag);
            if (holder == EMPTY)
                continue;

            if (!nbtTag.contains(holder.id()))
                nbtTag.put(holder.id(), new CompoundTag());

            setModifier(nbtTag.getCompound(holder.id()), holder);
        }
    }

    private static void clearModTag(
            ListTag attr
    )
    {
        for (Tag tag: attr)
        {
            if (!(tag instanceof CompoundTag mtag))
                continue;

            if (mtag.contains(CUSTOM_NBT_SIGNATURE))
                attr.remove(mtag);
        }
    }

    private static AttributeHolder getModifier(
            CompoundTag attr
    )
    {
        if (!attr.contains(CUSTOM_NBT_SIGNATURE))
            return EMPTY;

        String id = attr.getString("AttributeName");
        Double amount = attr.getDouble("Amount");
        EquipmentSlot slot = EquipmentSlot.byName(attr.getString("Slot"));
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(attr.getInt("Operation"));
        String name = attr.getString(CUSTOM_NBT_SIGNATURE);

        return new AttributeHolder(ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(id)), name,  slot, amount, operation);
    }

    private static void setModifier(
            CompoundTag cns,
            AttributeHolder holder
    )
    {
        // Of course, the result WON'T be null
        cns.putString("attr", String.valueOf(ForgeRegistries.ATTRIBUTES.getResourceKey(holder.attr()).get().location()));
        cns.putString("id", holder.id());
        cns.putDouble("amount", holder.amount());
        cns.putInt("operation", holder.operation().toValue());
        cns.putString("slot", holder.slot().getName());
    }

    private record AttributeHolder(
            Attribute attr,
            String id,
            EquipmentSlot slot,
            Double amount,
            AttributeModifier.Operation operation
    ){};
}