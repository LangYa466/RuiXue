package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import org.jetbrains.annotations.Nullable;
@ElementInfo(name = "Test")
public class Test extends Element {

    @Nullable
    @Override
    public Border getDrawElement() {
        RenderUtils.drawRect(1,1,50,50,true);
        return new Border(0f, 0f, 60, 60);
    }
}


