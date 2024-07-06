package io.github.itzispyder.impropers3dminimap.mixininterface;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.util.Identifier;

public interface FontManagerAccessor {

    TextRenderer createRenderer(Identifier fontId);
}
