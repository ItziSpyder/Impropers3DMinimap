package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings;

import io.github.itzispyder.impropers3dminimap.config.SettingSection;
import io.github.itzispyder.impropers3dminimap.config.types.EnumSetting;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive.ScrollPanelElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive.TextBoxElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.WindowElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings.types.BooleanSettingElement;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModeSelectionWindow extends WindowElement {

    private final EnumSetting<?> setting;
    private final TextBoxElement search;
    private final ScrollPanelElement panel;

    public ModeSelectionWindow(int x, int y, EnumSetting<?> setting) {
        super("Options", x, y, 75, 150);
        this.setting = setting;
        this.search = new TextBoxElement(x + 5, y + 20, width - 10);
        this.search.setDefaultText("§7Find Mode i.e");
        this.search.keyPressCallbacks.add((key, click, scancode, modifiers) -> filterQuery());
        this.panel = new ScrollPanelElement(x, y + 40, width, height - 40) {
            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                if (mc.currentScreen instanceof GuiScreen screen) {
                    screen.selected = this;
                }
            }
        };
        this.filterQuery();
        this.addChild(search);
        this.addChild(panel);
    }

    @Override
    public void onRender(DrawContext context, int mouseX, int mouseY) {

    }

    public void filterQuery() {
        panel.clearChildren();
        SettingSection scDict = new SettingSection("mode-values");

        List<Enum<?>> temp = new ArrayList<>();
        for (var mode : setting.getArray()) {
            if (mode.name().toLowerCase().contains(search.getLowercaseQuery())) {
                temp.add(mode);
            }
        }

        temp = temp.stream().limit(100).sorted(Comparator.comparing(e -> Math.abs(e.name().length() - search.getQuery().length()))).toList();
        for (var mode : temp) {
            scDict.add(scDict.createBoolSetting()
                    .name(mode.name())
                    .def(setting.getVal() == mode)
                    .onSettingChange(s ->  {
                        setting.setVal(mode);
                        filterQuery();
                    })
                    .build()
            );
        }

        SettingSectionElement scElement = new SettingSectionElement(scDict, panel.x + 5, panel.y + 2, panel.width - 10);

        for (var child : scElement.getChildren())
            if (child instanceof BooleanSettingElement bool)
                bool.setRound(true);

        panel.addChild(scElement);
        panel.recalculatePositions();
    }

    @Override
    public void onClose() {
        if (mc.currentScreen instanceof GuiScreen screen) {
            screen.removeChild(this);
        }
    }

    @Override
    public void onClick(double mouseX, double mouseY, int button) {
        super.onClick(mouseX, mouseY, button);
        if (mc.currentScreen instanceof GuiScreen screen) {
            if (search.isHovered((int)mouseX, (int)mouseY)) {
                screen.selected = search;
            }
        }
    }
}
