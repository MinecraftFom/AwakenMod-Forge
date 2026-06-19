package com.fomdev.awaken.mixin;

import net.minecraft.world.item.enchantment.*;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {ArrowDamageEnchantment.class, ArrowKnockbackEnchantment.class, ArrowPiercingEnchantment.class, DamageEnchantment.class, DigDurabilityEnchantment.class, DiggingEnchantment.class, FireAspectEnchantment.class, FishingSpeedEnchantment.class, FrostWalkerEnchantment.class, KnockbackEnchantment.class, LootBonusEnchantment.class, OxygenEnchantment.class, ProtectionEnchantment.class, QuickChargeEnchantment.class, SoulSpeedEnchantment.class, SweepingEdgeEnchantment.class, SwiftSneakEnchantment.class, ThornsEnchantment.class, TridentImpalerEnchantment.class, TridentLoyaltyEnchantment.class, TridentRiptideEnchantment.class, WaterWalkerEnchantment.class})
public abstract class MixinEnchantment
{
    @Inject(method = "getMaxLevel", at = @At("RETURN"), cancellable = true)
    public void setLevel(CallbackInfoReturnable<Integer> cir)
    {
        cir.setReturnValue(20);
    }
}