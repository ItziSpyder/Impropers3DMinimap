package io.github.itzispyder.impropers3dminimap.mixin;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class MixinClientPlayerEntity {

    @Inject(method = "init", at = @At("RETURN"))
    public void init(CallbackInfo ci) {
        Impropers3DMinimap.radar.onJoin();
    }
}
