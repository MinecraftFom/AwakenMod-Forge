package com.fomdev.awaken.mixin;

import com.fomdev.awaken.nbt.NBTUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DiggerItem.class)
public class MixinEfficiencyCapability
{
    @Final
    @Shadow
    private TagKey<Block> blocks;

    @Inject(method = "getDestroySpeed", at = @At("RETURN"), cancellable = true)
    public void customDestroySpeed(ItemStack stack, BlockState state, CallbackInfoReturnable<Float> callback)
    {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.contains(NBTUtil.nbtEfficiencyCapability))
            callback.setReturnValue(stack.getDestroySpeed(state));

        callback.setReturnValue(state.is(blocks) ? tag.getFloat(NBTUtil.nbtEfficiencyCapability) : 1.0F);
    }
}