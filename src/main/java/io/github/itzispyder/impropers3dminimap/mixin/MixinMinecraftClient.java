package io.github.itzispyder.impropers3dminimap.mixin;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        Impropers3DMinimap.config.save();
    }
}
