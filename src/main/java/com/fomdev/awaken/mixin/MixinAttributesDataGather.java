package com.fomdev.awaken.mixin;

import com.fomdev.awaken.gen.shuffle.ShuffleUtil;
import com.fomdev.awaken.init.Awaken;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.world.entity.ai.attributes.Attribute.class)
public class MixinAttributesDataGather
{
    @Inject(method = "<init>", at = @At("RETURN"))
    public void gatherData(String p0, double p1, CallbackInfo ci)
    {
        net.minecraft.world.entity.ai.attributes.Attribute self = (net.minecraft.world.entity.ai.attributes.Attribute) (Object) this;
        if (self instanceof RangedAttribute ranged)
            ShuffleUtil.addAvailableAttribute(ranged);
        else
            ShuffleUtil.addAvailableAttribute(self);

        Awaken.LOGGER.info("ASM> Found available attribute {}", self.getDescriptionId());
    }
}