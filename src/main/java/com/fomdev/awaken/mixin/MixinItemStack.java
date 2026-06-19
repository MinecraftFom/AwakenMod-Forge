package com.fomdev.awaken.mixin;

import com.fomdev.awaken.nbt.AttributeUtil;
import com.fomdev.awaken.nbt.AwakenAttributeUtil;
import com.fomdev.awaken.nbt.NBTUtil;
import com.fomdev.awaken.quality.Quality;
import com.fomdev.awaken.render.RenderColorUtil;
import com.fomdev.awaken.title.Prefix;
import com.fomdev.awaken.title.Suffix;
import com.fomdev.awaken.title.Title;
import com.fomdev.awaken.title.TitleRegister;
import com.fomdev.flib.util.ColorUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.UUID;

@Mixin(ItemStack.class)
public abstract class MixinItemStack
{
    @Shadow
    public abstract CompoundTag getOrCreateTag();

    @Shadow
    @javax.annotation.Nullable
    private CompoundTag tag;

    @Shadow
    public abstract boolean hasTag();

    @Shadow
    public abstract Item getItem();

    @Inject(method = "getHoverName", at = @At("RETURN"), cancellable = true)
    public void setFancyDisplayName(CallbackInfoReturnable<Component> cir)
    {
        ItemStack stack = (ItemStack) (Object) this;
        Prefix prefix = NBTUtil.deserializePrefixes(stack);
        Suffix suffix = NBTUtil.deserializeSuffixes(stack);
        Title title = NBTUtil.deserializeTitle(stack);
        Quality quality = NBTUtil.deserializeQuality(stack);

        if (prefix == null || suffix == null || title == null)
            return;

        Component origin = cir.getReturnValue();

        Component prefixComponent = Component.translatable(TitleRegister.localizePrefix(prefix.id()));
        Component suffixComponent = Component.translatable(TitleRegister.localizeSuffix(suffix.id()));
        Component titleComponent = Component.translatable(TitleRegister.localizeTitle(title.id()));

        MutableComponent result = Component.empty().append(prefixComponent).append("-").append(titleComponent).append(" ").append(origin).append(" (").append(suffixComponent).append(")");

        if (quality != null)
        {
            RenderColorUtil.ColorComponent component = switch (quality.colorPattern()) {
                case SINGLE -> RenderColorUtil.singleColor(quality);
                case MULTIPLE -> RenderColorUtil.multipleColor(quality);
                case CONTINUE -> RenderColorUtil.continueColor(quality);
            };

            if (component != null)
                result.withStyle(Style.EMPTY.withColor(ColorUtil.colorToTextColor(component.bgStart())));
        }

        cir.setReturnValue(result);
    }

    /**
     * @author Fom477
     * @reason Can't archieve this simply by injecting
     */
    @Overwrite
    public void addAttributeModifier(Attribute attr, AttributeModifier modifier, @Nullable EquipmentSlot slot)
    {
        assert tag != null;

        ItemStack stack = (ItemStack) (Object) this;
        getOrCreateTag();

        AttributeUtil.putAttribute(stack, attr, UUID.randomUUID().toString(), "additional", modifier.getAmount(), modifier.getOperation(), slot);
    }

    // Don't know what to comment
    // Nyan!
    @Inject(method = "getAttributeModifiers", at = @At("RETURN"), cancellable = true)
    public void getAttributeModifiers(EquipmentSlot slot, CallbackInfoReturnable<Multimap<Attribute, AttributeModifier>> cir)
    {
        assert tag != null;

        ItemStack stack = (ItemStack) (Object) this;

        Multimap<Attribute, AttributeModifier> map = HashMultimap.create();
        Multimap<Attribute, AttributeModifier> multimap = AttributeUtil.getModifiersSimple(stack, slot);
        Multimap<Attribute, AttributeModifier> tiers;
        if (stack.getEquipmentSlot() != null)
            tiers = stack.getEquipmentSlot().isArmor()? AwakenAttributeUtil.getTierAttributes$Armor(stack, slot): AwakenAttributeUtil.getTierAttributes$Tool(stack, slot);
        else // Perhaps this won't happen...
        {
            tiers = HashMultimap.create();
            tiers.putAll(AwakenAttributeUtil.getTierAttributes$Armor(stack, slot));
            tiers.putAll(AwakenAttributeUtil.getTierAttributes$Tool(stack, slot));
        }

        map.putAll(multimap);
        map.putAll(tiers);
        map.putAll(AwakenAttributeUtil.getTitleAttributes(stack, slot));

        cir.setReturnValue(map);
    }

    @Inject(method = "getMaxDamage", at = @At("RETURN"), cancellable = true)
    public void getMaxDamage(CallbackInfoReturnable<Integer> cir)
    {
        ItemStack stack = (ItemStack) (Object) this;
        int origin = getItem().getMaxDamage(stack);
        origin += AwakenAttributeUtil.getDurability(stack);
        cir.setReturnValue(origin);
    }
}