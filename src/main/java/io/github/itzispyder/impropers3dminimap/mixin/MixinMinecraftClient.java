package io.github.itzispyder.impropers3dminimap.mixin;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.client.Impropers3DMinimapClient;
import io.github.itzispyder.impropers3dminimap.mixininterface.FontManagerAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.FontManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public abstract class MixinMinecraftClient {

    @Shadow @Final private FontManager fontManager;

    @Inject(method = "stop", at = @At("HEAD"))
    public void stop(CallbackInfo ci) {
        Impropers3DMinimap.config.save();
    }

    @Inject(method = "onFontOptionsChanged", at = @At("TAIL"))
    public void initFont(CallbackInfo ci) {
        Impropers3DMinimapClient system = Impropers3DMinimapClient.getInstance();
        String modId = "impropers3dminimap";
        FontManagerAccessor fonts = (FontManagerAccessor) this.fontManager;

        system.textRenderer = fonts.createRenderer(Identifier.of(modId, "segoe-ui"));
    }
}
