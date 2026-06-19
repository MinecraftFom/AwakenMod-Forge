package com.fomdev.awaken.mixin;

import com.fomdev.awaken.register.trims.material.TrimMaterialRegister;
import com.fomdev.flib.util.ColorUtil;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimMaterials;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(TrimMaterials.class)
public abstract class MixinTrimMaterialRegister
{
    @Shadow
    private static void register(BootstapContext<TrimMaterial> bootstrap, ResourceKey<TrimMaterial> key, Item item, Style style, float factor) {}

    @Shadow
    private static void register(BootstapContext<TrimMaterial> bootstrap, ResourceKey<TrimMaterial> key, Item item, Style style, float factor, Map<ArmorMaterial, String> special) {}

    @Inject(method = "bootstrap", at = @At("RETURN"), cancellable = true)
    private static void bootstrap(BootstapContext<TrimMaterial> bootstrap, CallbackInfo ci)
    {
        for (TrimMaterialRegister.TrimMaterialHolder holder: TrimMaterialRegister.MATERIAL_LIST)
        {
            if (holder.special().isEmpty()) register(bootstrap, holder.id(), holder.represent(), Style.EMPTY.withColor(ColorUtil.colorToTextColor(holder.color())), holder.factor());
            else register(bootstrap, holder.id(), holder.represent(), Style.EMPTY.withColor(ColorUtil.colorToTextColor(holder.color())), holder.factor(), holder.special());
        }
    }
}