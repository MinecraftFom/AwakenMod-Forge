package com.fomdev.awaken.mixin;

import com.fomdev.awaken.gen.shuffle.ShuffleUtil;
import com.fomdev.awaken.init.Awaken;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(TrimMaterial.class)
public class MixinTrimMaterialDataGather
{
    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    public void gatherInitializedData(String assetName, Holder<?> ingredient, float itemModelIndex, Map<?, ?> overrideArmorMaterials, Component description, CallbackInfo ci)
    {
        TrimMaterial material = (TrimMaterial) (Object) this;
        ShuffleUtil.addAvailableTrim(ResourceLocation.parse(material.assetName()));

        Awaken.LOGGER.info("ASM> Notified trim material {}", material.assetName());
    }
}