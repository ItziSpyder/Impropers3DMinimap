package io.github.itzispyder.impropers3dminimap.render.ui.elements.config.settings;

import io.github.itzispyder.impropers3dminimap.config.SettingSection;
import io.github.itzispyder.impropers3dminimap.config.types.DictionarySetting;
import io.github.itzispyder.impropers3dminimap.render.ui.GuiScreen;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.AbstractElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive.ScrollPanelElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.common.interactive.TextBoxElement;
import io.github.itzispyder.impropers3dminimap.render.ui.elements.config.WindowElement;
import net.minecraft.client.gui.DrawContext;

import java.util.*;

public class DictionaryLookupWindow extends WindowElement {

    private final DictionarySetting<?> setting;
    private final TextBoxElement search;
    private final ScrollPanelElement panel;
    private final List<String> currentFiltered = new ArrayList<>();

    public DictionaryLookupWindow(int x, int y, DictionarySetting<?> setting) {
        super("Dictionary Editor", x, y, 200, 200);
        this.setting = setting;
        this.search = new TextBoxElement(x + 5, y + 20, width - 10);
        this.search.setDefaultText("§7Search Key i.e  -  \"false\" or \"true\" to filter enabled");
        this.search.keyPressCallbacks.add((key, click, scancode, modifiers) -> filterQuery());
        this.panel = new ScrollPanelElement(x, y + 60, width, height - 65) {
            @Override
            public void onClick(double mouseX, double mouseY, int button) {
                if (mc.currentScreen instanceof GuiScreen screen) {
                    screen.selected = this;
                }
            }
        };
        this.filterQuery();
        this.addChild(AbstractElement.create()
                .pos(x + 5, y + 40)
                .dimensions((width - 30) / 3, 10)
                .onPress(button -> {
                    Map<String, Boolean> map = new HashMap<>();
                    for (String s : currentFiltered)
                        map.put(s, true);
                    setting.getVal().overwrite(map, true);
                    filterQuery();
                })
                .onRender(AbstractElement.RENDER_BUTTON.apply(() -> "Enable Results"))
                .build());
        this.addChild(AbstractElement.create()
                .pos(x + 15 + (width - 30) / 3, y + 40)
                .dimensions((width - 30) / 3, 10)
                .onPress(button -> {
                    Map<String, Boolean> map = new HashMap<>();
                    for (String s : currentFiltered)
                        map.put(s, false);
                    setting.getVal().overwrite(map, true);
                    filterQuery();
                })
                .onRender(AbstractElement.RENDER_BUTTON.apply(() -> "Disable Results"))
                .build());
        this.addChild(AbstractElement.create()
                .pos(x + 25 + (width - 30) / 3 * 2, y + 40)
                .dimensions((width - 30) / 3, 10)
                .onPress(button -> {
                    var dict = setting.getVal().getDictionary();
                    for (String s : currentFiltered)
                        dict.put(s, !dict.getOrDefault(s, false));
                    setting.getVal().overwrite(dict, true);
                    filterQuery();
                })
                .onRender(AbstractElement.RENDER_BUTTON.apply(() -> "Invert Results"))
                .build());
        this.addChild(search);
        this.addChild(panel);
    }

    @Override
    public void onRender(DrawContext context, int mouseX, int mouseY) {

    }

    public void filterQuery() {
        panel.clearChildren();
        currentFiltered.clear();
        SettingSection scDict = new SettingSection("dictionary");

        List<Map.Entry<String, Boolean>> temp = new ArrayList<>();
        for (Map.Entry<String, Boolean> entry : setting.getVal().getDictionary().entrySet()) {
            if (matchesQuery(entry)) {
                temp.add(entry);
                currentFiltered.add(entry.getKey());
            }
        }

        temp = temp.stream().limit(100).sorted(Comparator.comparing(e -> Math.abs(e.getKey().length() - search.getQuery().length()))).toList();
        for (Map.Entry<String, Boolean> entry : temp) {
            scDict.add(scDict.createBoolSetting()
                    .name(entry.getKey())
                    .def(entry.getValue())
                    .onSettingChange(s -> setting.getVal().overwrite(Map.of(entry.getKey(), s.getVal()), true))
                    .build()
            );
        }

        SettingSectionElement scElement = new SettingSectionElement(scDict, panel.x + 5, panel.y + 2, panel.width - 10);
        panel.addChild(scElement);
        panel.recalculatePositions();
    }

    private boolean matchesQuery(Map.Entry<String, Boolean> entry) {
        String match = entry.getKey().toLowerCase() + ":" + (entry.getValue() ? "true/on/enabled" : "false/off/disabled");
        String repl = "\s+|[_-]+";
        String q = search.getLowercaseQuery();

        if (q.startsWith(":") && q.length() > 1) {
            return entry.getKey().replaceAll(repl, "").equalsIgnoreCase(q.replaceAll(repl, "").substring(1));
        }
        return match.contains(q);
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
