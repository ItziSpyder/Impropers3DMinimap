package io.github.itzispyder.impropers3dminimap.render.ui.screens;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.AbstractElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.MoveableHudElement;
import io.github.itzispyder.impropers3dminimap.render.ui.hud.Hud;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class HudEditScreen extends GuiScreen {

    private final List<MoveableHudElement> huds;

    public HudEditScreen() {
        super("Edit Hud Screen");

        this.huds = Hud.huds().values().stream()
                .map(MoveableHudElement::new)
                .toList();
        huds.forEach(this::addChild);

        AbstractElement settings = AbstractElement.create()
                .pos(mc.getWindow().getScaledWidth() - 10 - 50, 10)
                .dimensions(50, 15)
                .onRender(AbstractElement.RENDER_BUTTON.apply(() -> "Back"))
                .onPress(button -> mc.execute(() -> mc.setScreen(new ConfigScreen())))
                .build();
        this.addChild(settings);
    }

    @Override
    public void baseRender(DrawContext context, int mouseX, int mouseY, float delta) {
        RenderUtils.fillRect(context, 0, 0, this.width, this.height, 0x90000000);

        RenderUtils.drawHorLine(context, 0, context.getScaledWindowHeight() / 2, context.getScaledWindowWidth(), 0xFF8C8C8C);
        RenderUtils.drawHorLine(context, 0, context.getScaledWindowHeight() - 10, context.getScaledWindowWidth(), 0xFF8C8C8C);
        RenderUtils.drawHorLine(context, 0, 10, context.getScaledWindowWidth(), 0xFF8C8C8C);
        RenderUtils.drawVerLine(context, context.getScaledWindowWidth() / 2, 0, context.getScaledWindowHeight(), 0xFF8C8C8C);
        RenderUtils.drawVerLine(context, context.getScaledWindowWidth() - 10, 0, context.getScaledWindowHeight(), 0xFF8C8C8C);
        RenderUtils.drawVerLine(context, 10, 0, context.getScaledWindowHeight(), 0xFF8C8C8C);

        if (selected != null && selected instanceof MoveableHudElement) {
            RenderUtils.drawHorLine(context, 0, selected.y, context.getScaledWindowWidth(), 0xFFFFFFFF);
            RenderUtils.drawVerLine(context, selected.x, 0, context.getScaledWindowHeight(), 0xFFFFFFFF);
            RenderUtils.drawHorLine(context, 0, selected.y + selected.height - 1, context.getScaledWindowWidth(), 0xFFFFFFFF);
            RenderUtils.drawVerLine(context, selected.x + selected.width - 1, 0, context.getScaledWindowHeight(), 0xFFFFFFFF);
        }

        for (int x = 0; x < this.width; x += 10) {
            RenderUtils.drawLine(context, x, 0, x, height, 0xFF505050);
        }
        for (int y = 0; y < this.height; y += 10) {
            RenderUtils.drawLine(context, 0, y, width, y, 0xFF505050);
        }
    }

    public List<MoveableHudElement> getHuds() {
        return huds;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        client.setScreen(new HudEditScreen());
    }

    @Override
    public void close() {
        Impropers3DMinimap.config.save();
        super.close();
    }
}
