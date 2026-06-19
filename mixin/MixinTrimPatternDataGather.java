package com.fomdev.awaken.mixin;

import com.fomdev.awaken.gen.ShuffleUtil;
import com.fomdev.awaken.init.Awaken;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TrimPattern.class)
public class MixinTrimPatternDataGather
{
    @Inject(method = "<init>", at = @At("RETURN"), cancellable = true)
    public void gatherInitializedData(ResourceLocation assetId, Holder<?> templateItem, Component description, CallbackInfo ci)
    {
        TrimPattern pattern = (TrimPattern) (Object) this;
        ShuffleUtil.addAvailablePattern(pattern.assetId());

        Awaken.LOGGER.info("ASM> Notified trim pattern {}", pattern.assetId());
    }
}