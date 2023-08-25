package me.tiangong;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.client.hud.designer.GuiHudDesigner;

@ModuleInfo(name = "HUDDsigner", description = "HUDDsigner", category = ModuleCategory.VULGAR)
public class HUDDsigner extends Module {
    @EventTarget
    public void onRender() {
        mc.displayGuiScreen(classProvider.wrapGuiScreen(new GuiHudDesigner()));
    }
}

