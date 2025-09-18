package io.github.cloudburst.sidebarpotion.mixin;

import io.github.cloudburst.sidebarpotion.SidebarPotionMod;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.network.packet.s2c.play.EntityStatusEffectS2CPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityStatusEffectS2CPacket.class)
public class EntityStatusEffectS2CPacketMixin {
    @Mutable
    @Final
    @Shadow
    private byte flags;

    @Inject(method = "<init>(ILnet/minecraft/entity/effect/StatusEffectInstance;Z)V", at = @At("RETURN"))
    private void onInit(int entityId, StatusEffectInstance effect, boolean keepFading, CallbackInfo ci) {
        if (SidebarPotionMod.CONFIG.hideDefaultGui)
            this.flags = (byte) (this.flags & ~4);
    }
}
