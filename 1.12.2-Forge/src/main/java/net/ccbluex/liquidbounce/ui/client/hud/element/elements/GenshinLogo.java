package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import net.ccbluex.liquidbounce.ui.client.hud.element.Border;
import net.ccbluex.liquidbounce.ui.client.hud.element.Element;
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo;
import net.ccbluex.liquidbounce.utils.render.RenderUtils;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.util.ResourceLocation;

@ElementInfo(name = "原神图标")
public class GenshinLogo extends Element {
    public IntegerValue size = new IntegerValue("大小",100,50,1024);
    private static final ListValue logo = new ListValue("Mode",
            new String[]{"图标1", "图标2"},
            "图标2");
    @Override
    public Border getDrawElement() {
        final int x = 1;
        final int y = 1;
        if (logo.get().equals("图标2")) {
            RenderUtils.drawImage2(new ResourceLocation("liquidbounce/logo/Genshin1.png"), x, y, size.get(), size.get());
        }
        if (logo.get().equals("图标2")) {
            RenderUtils.drawImage2(new ResourceLocation("liquidbounce/logo/GenShin2.png"), x, y, size.get(), size.get());
        }
        return new Border(x,y,size.get(),size.get());
    }
}
