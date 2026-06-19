package com.fomdev.awaken.mixin;

import com.fomdev.awaken.gen.shuffle.ShuffleUtil;
import com.fomdev.awaken.init.Awaken;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public class MixinEnchantmentDataGather
{
    @Final
    @Shadow
    public int getMaxLevel() { return 0; }

    @Inject(method = "getMaxLevel", at = @At("RETURN"), cancellable = true)
    public void setLevel(CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(cir.getReturnValue() == null || cir.getReturnValue() <= 1? 1: 20);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    public void onConstruct(Enchantment.Rarity rarity, EnchantmentCategory category, EquipmentSlot[] slots, CallbackInfo ci)
    {
        Enchantment self = (Enchantment) (Object) this;

        Awaken.LOGGER.info("ASM> Notified enchantment: {}", self.getClass());
        if (self.isCurse())
            return;

        ShuffleUtil.addAvailableEnchantment(self.category, self);
    }
}