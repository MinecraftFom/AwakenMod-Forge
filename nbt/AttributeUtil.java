package com.fomdev.awaken.nbt;

import com.fomdev.awaken.init.Awaken;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.core.UUIDUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class AttributeUtil
{
    public static final String CUSTOM_NBT_NAMESPACE = "awakenNBT";
    public static final String CUSTOM_NBT_SIGNATURE = "awakenNBTSign";

    private static final AttributeHolder EMPTY = new AttributeHolder(null, null, null, null, null);

    public static void clearAttribute(
            ItemStack stack,
            String ns
    )
    {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            return;

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        nbtTag.remove(ns);
    }

    public static void delAttribute(
            ItemStack stack,
            String ns,
            String id
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            tag.put(CUSTOM_NBT_NAMESPACE, new CompoundTag());

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        if (!nbtTag.contains(ns))
            return;

        CompoundTag dataTag = nbtTag.getCompound(ns);
        dataTag.remove(id);
    }

    public static void delNamespace(
            ItemStack stack,
            String ns
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            return;

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        if (!nbtTag.contains(ns))
            return;

        nbtTag.remove(ns);
    }

    public static Multimap<String, Multimap<Attribute, AttributeModifier>> getModifiers(
            ItemStack stack,
            EquipmentSlot slot
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            return HashMultimap.create();

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        if (nbtTag.isEmpty())
            return HashMultimap.create();

        Multimap<String, Multimap<Attribute, AttributeModifier>> map = HashMultimap.create();

        for (String nsKey: nbtTag.getAllKeys())
        {
            CompoundTag nsTag = nbtTag.getCompound(nsKey);

            for (String mdKey: nsTag.getAllKeys())
            {
                CompoundTag mdTag = nsTag.getCompound(mdKey);

                AttributeHolder modifier = getModifierFromCache(mdTag);

                if (modifier.slot != null && modifier.slot != slot)
                    continue;

                Attribute attr = modifier.attr;
                String id = modifier.id;
                Double amount = modifier.amount;
                AttributeModifier.Operation operation = modifier.operation;

                if (attr == null || id == null || amount == null || operation == null)
                    continue;

                AttributeModifier md = new AttributeModifier(modifier.uuid == null? UUID.randomUUID(): modifier.uuid, id, amount, operation);

                if (!map.containsKey(mdKey))
                    map.put(mdKey, HashMultimap.create());

                map.get(mdKey).forEach(m -> m.put(attr, md));
            }
        }

        return map;
    }

    public static Multimap<Attribute, AttributeModifier> getModifiersSimple(
            ItemStack stack,
            EquipmentSlot slot
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            return HashMultimap.create();

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        if (nbtTag.isEmpty())
            return HashMultimap.create();

        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();

        for (String nsKey: nbtTag.getAllKeys())
        {
            CompoundTag nsTag = nbtTag.getCompound(nsKey);

            for (String mdKey: nsTag.getAllKeys())
            {
                CompoundTag mdTag = nsTag.getCompound(mdKey);

                AttributeHolder modifier = getModifierFromCache(mdTag);

                if (modifier.slot != null && modifier.slot != slot)
                    continue;

                Attribute attr = modifier.attr;
                String id = modifier.id;
                Double amount = modifier.amount;
                AttributeModifier.Operation operation = modifier.operation;

                if (attr == null || id == null || amount == null || operation == null)
                    continue;

                AttributeModifier md = new AttributeModifier(modifier.uuid == null? UUID.randomUUID(): modifier.uuid, id, amount, operation);
                map.put(attr, md);
            }
        }

        return map;
    }

    public static void putAttribute(
            ItemStack stack,
            Attribute attribute,
            String id,
            String ns,
            Double amount,
            AttributeModifier.Operation operation,
            EquipmentSlot slot
    )
    {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            tag.put(CUSTOM_NBT_NAMESPACE, new CompoundTag());

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        if (!nbtTag.contains(ns))
            nbtTag.put(ns, new CompoundTag());

        CompoundTag nsTag = nbtTag.getCompound(ns);
        nsTag.put(id, new CompoundTag());
        CompoundTag modifier = nsTag.getCompound(id);
        setModifierToCache(modifier, new AttributeHolder(
                attribute,
                id,
                slot,
                amount,
                operation
        ));
    }

    private static AttributeHolder getModifierFromCache(
            CompoundTag attr
    )
    {
        String id = attr.getString("name");
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(attr.getString("attr")));
        double amount = attr.getDouble("amount");
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(attr.getInt("operation"));
        String sid = attr.getString("slot");
        int[] uuid = attr.getIntArray("uuid");
        if (uuid.length == 0)
        {
            setUUID(attr);
        }

        uuid = attr.getIntArray("uuid");

        return new AttributeHolder(
                attribute,
                id,
                sid.isEmpty()? null: EquipmentSlot.byName(sid),
                amount,
                operation,
                UUIDUtil.uuidFromIntArray(uuid)
        );
    }

    private static void setModifierToCache(
            CompoundTag cns,
            AttributeHolder holder
    )
    {
        // Of course, the result WON'T be null
        Attribute attr = holder.attr;
        if (attr == null)
            return;

        Optional<ResourceKey<Attribute>> attrid = ForgeRegistries.ATTRIBUTES.getResourceKey(attr);
        if (attrid.isEmpty())
            return;

        cns.putString("attr", String.valueOf(attrid.get().location()));
        cns.putString("id", holder.id());
        cns.putDouble("amount", holder.amount());
        cns.putInt("operation", holder.operation().toValue());
        cns.putString("slot", holder.slot().getName());
    }

    private static void setUUID(
            CompoundTag attr
    )
    {
        attr.putIntArray("uuid", UUIDUtil.uuidToIntArray(UUID.randomUUID()));
    }

    private record AttributeHolder(
            Attribute attr,
            String id,
            EquipmentSlot slot,
            Double amount,
            AttributeModifier.Operation operation,
            @Nullable UUID uuid
    ){
        public AttributeHolder(
                Attribute attr,
                String id,
                EquipmentSlot slot,
                Double amount,
                AttributeModifier.Operation operation
        )
        {
            this(attr, id, slot, amount, operation, null);
        }
    }
}