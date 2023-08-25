package net.ccbluex.liquidbounce.features.module.modules.render;

import me.tiangong.CustomColor;
import me.tiangong.FakeFPS;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.ccbluex.liquidbounce.utils.tenacity.ColorUtil;
import net.ccbluex.liquidbounce.utils.tenacity.render.RoundedUtil;
import net.ccbluex.liquidbounce.value.ListValue;
import net.minecraft.client.Minecraft;

import java.awt.*;

@ModuleInfo(name = "HUDText" , description = "HUDText Tenacity Color." , category = ModuleCategory.RENDER)
public class CustomHUD extends Module {
    public static ListValue gsValue = new ListValue("NameMode", new String[]{"None","Tenacity"}, "Tenacity");

    @EventTarget
    public void onRender2D(final Render2DEvent event) {
        if (gsValue.get().equals("Tenacity")) {
            String text = "瑞雪" + " | " + LiquidBounce.CLIENT_VERSION + " | " + "Fps:" + FakeFPS.getfps().toString();
            RoundedUtil.drawGradientRound(3, 4, Fonts.font40.getStringWidth(text) + 4, 15,CustomColor.ra.get(),
                    ColorUtil.applyOpacity(new Color(CustomColor.r2.get(), CustomColor.g2.get(),CustomColor.b2.get(),CustomColor.a2.get()), .85f),
                    new Color(CustomColor.r.get(),CustomColor.g.get(),CustomColor.b.get(),CustomColor.a.get()),
                    new Color(CustomColor.r2.get(),CustomColor.g2.get(),CustomColor.b2.get(),CustomColor.a2.get()),
                    new Color(CustomColor.r.get(),CustomColor.g.get(),CustomColor.b.get(),CustomColor.a.get()));
            Fonts.font40.drawString(text, 5, 8, new Color(255, 255, 255, 255).getRGB());
        }
    }
}
