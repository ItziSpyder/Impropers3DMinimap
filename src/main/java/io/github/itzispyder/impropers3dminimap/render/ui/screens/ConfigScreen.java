package io.github.itzispyder.impropers3dminimap.render.ui.screens;

import io.github.itzispyder.impropers3dminimap.Impropers3DMinimap;
import io.github.itzispyder.impropers3dminimap.config.Config;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.AbstractElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.SettingWindow;
import io.github.itzispyder.impropers3dminimap.util.minecraft.RenderUtils;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;

public class ConfigScreen extends GuiScreen {

    private final SettingWindow window;

    public ConfigScreen() {
        super("Improper Minimap Config Scren");

        int w = RenderUtils.width();
        int h = RenderUtils.height();
        Config config = Impropers3DMinimap.config;

        this.window = new SettingWindow(config, 0, 0, "Improper's 3D Minimap", "Minimap but 3D!") {
            @Override
            public void onClose() {
                ConfigScreen.this.removeChild(this);
            }
        };
        this.window.moveTo((w - window.width) / 2, (h - window.height) / 2);
        this.addChild(window);

        AbstractElement settings = AbstractElement.create()
                .pos(mc.getWindow().getScaledWidth() - 10 - 50, 10)
                .dimensions(50, 15)
                .onRender(AbstractElement.RENDER_BUTTON.apply(() -> "Edit Huds"))
                .onPress(button -> mc.execute(() -> mc.setScreen(new HudEditScreen())))
                .build();
        this.addChild(settings);
    }

    @Override
    public void baseRender(DrawContext context, int mouseX, int mouseY, float delta) {

    }

    public SettingWindow getWindow() {
        return window;
    }

    @Override
    public void resize(MinecraftClient client, int width, int height) {
        client.setScreen(new ConfigScreen());
    }
}
