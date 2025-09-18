package io.github.cloudburst.sidebarpotion.mixin;

import io.github.cloudburst.sidebarpotion.SidebarPotionMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onStatusEffectApplied", at = @At("HEAD"))
    private void onStatusEffectAppliedHead(StatusEffectInstance effect, Entity source, CallbackInfo ci) {
        var player = (ServerPlayerEntity)(Object)this;
        SidebarPotionMod.POTION_SIDEBAR.addPlayer(player.networkHandler);
    }
}
