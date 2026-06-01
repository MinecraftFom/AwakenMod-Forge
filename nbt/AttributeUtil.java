package com.fomdev.awaken.nbt;

import com.fomdev.awaken.init.Awaken;
import com.google.common.collect.Multimap;
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
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
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
        syncAttributeDataToPersistent(stack);

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

        syncPersistentDataToAttribute(stack);
    }

    public static void syncAttributeDataToPersistent(
            ItemStack stack
    )
    {
        syncAttributeDataToPersistent$0(stack);
        syncAttributeDataToPersistent$1(stack);
    }

    public static void syncPersistentDataToAttribute(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();

        if (!tag.contains(CUSTOM_NBT_NAMESPACE))
            return;

        if (!tag.contains("AttributeModifiers"))
            tag.put("AttributeModifiers", new ListTag());

        ListTag attrTag = tag.getList("AttributeModifiers", 9);
        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);

        for (String key: nbtTag.getAllKeys())
        {
            CompoundTag nsTag = nbtTag.getCompound(key);
            for (String id: nsTag.getAllKeys())
            {
                CompoundTag modifyTag = nsTag.getCompound(id);

                AttributeHolder holder = getModifierFromCache(modifyTag);
                CompoundTag mtag = new CompoundTag();
                setModifierToAttribute(mtag, holder);

                attrTag.add(mtag);
            }
        }
    }

    public static void syncAttributeDataToPersistent$0(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(CUSTOM_NBT_NAMESPACE))
            return;

        tag.put(CUSTOM_NBT_NAMESPACE, new CompoundTag());

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        if (!nbtTag.contains("vanilla"))
            nbtTag.put("vanilla", new CompoundTag());

        CompoundTag vanillaTag = nbtTag.getCompound("vanilla");
        Arrays.stream(EquipmentSlot.values()).forEach(slot -> {
            Multimap<Attribute, AttributeModifier> map = stack.getAttributeModifiers(slot);
            if (map.isEmpty())
                return;

            map.forEach((k, v) -> {
                UUID id = UUID.randomUUID();
                AttributeHolder holder = new AttributeHolder(
                        k,
                        id.toString(),
                        slot,
                        v.getAmount(),
                        v.getOperation()
                );

                CompoundTag mtag = new CompoundTag();
                setModifierToCache(mtag, holder);

                vanillaTag.put(id.toString(), mtag);

                Awaken.LOGGER.info("INFO: FOUND ATTR " + mtag);
            });
        });
    }

    public static void syncAttributeDataToPersistent$1(
            ItemStack stack
    )
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (tag.contains(CUSTOM_NBT_NAMESPACE)) // Only ran once initially
            return;

        tag.put(CUSTOM_NBT_NAMESPACE, new CompoundTag());

        if (!tag.contains("AttributeModifiers"))
            return;

        CompoundTag nbtTag = tag.getCompound(CUSTOM_NBT_NAMESPACE);
        if (!nbtTag.contains("vanilla"))
            nbtTag.put("vanilla", new CompoundTag());

        ListTag attrTag = tag.getList("AttributeModifiers", 9);
        CompoundTag vanillaTag = nbtTag.getCompound("vanilla");

        clearModTags(attrTag);

        for (Tag t: attrTag)
        {
            if (!(t instanceof CompoundTag mtag))
                continue;

            AttributeHolder holder = getModifierFromAttribute(mtag);
            CompoundTag modifyTag = new CompoundTag();

            setModifierToCache(modifyTag, holder);
            vanillaTag.put(UUID.randomUUID().toString(), modifyTag);

            Awaken.LOGGER.info("Found attribute: " + modifyTag);
        }
    }

    private static void clearModTags(
            ListTag tag
    )
    {
        tag.removeIf(t -> {
            if (!(t instanceof CompoundTag mtag)) return false;
            return mtag.contains(CUSTOM_NBT_SIGNATURE);
        });
    }

    private static AttributeHolder getModifierFromAttribute(
            CompoundTag attr
    )
    {
        String id = attr.getString("AttributeName");
        Double amount = attr.getDouble("Amount");
        EquipmentSlot slot = EquipmentSlot.byName(attr.getString("Slot"));
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(attr.getInt("Operation"));
        UUID uuid = UUIDUtil.uuidFromIntArray(attr.getIntArray("UUID"));

        return new AttributeHolder(ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(id)), null,  slot, amount, operation, uuid);
    }

    private static AttributeHolder getModifierFromCache(
            CompoundTag attr
    )
    {
        String id = attr.getString("name");
        Attribute attribute = ForgeRegistries.ATTRIBUTES.getValue(ResourceLocation.parse(attr.getString("attr")));
        double amount = attr.getDouble("amount");
        AttributeModifier.Operation operation = AttributeModifier.Operation.fromValue(attr.getInt("operation"));
        EquipmentSlot slot = EquipmentSlot.byName(attr.getString("slot"));

        return new AttributeHolder(
                attribute,
                id,
                slot,
                amount,
                operation
        );
    }

    private static void setModifierToAttribute(
            CompoundTag cns,
            AttributeHolder holder
    )
    {
        cns.putString("AttributeName", String.valueOf(ForgeRegistries.ATTRIBUTES.getResourceKey(holder.attr).get().location()));
        cns.putString(CUSTOM_NBT_SIGNATURE, holder.id());
        cns.putString("Slot", holder.slot().getName());
        cns.putDouble("Amount", holder.amount());
        cns.putInt("Operation", holder.operation().toValue());
        cns.putIntArray("UUID", holder.uuid == null? UUIDUtil.uuidToIntArray(UUID.randomUUID()): UUIDUtil.uuidToIntArray(holder.uuid()));
    }

    private static void setModifierToCache(
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