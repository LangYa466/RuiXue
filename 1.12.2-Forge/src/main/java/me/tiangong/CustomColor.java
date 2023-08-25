package me.tiangong;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.ccbluex.liquidbounce.value.FloatValue;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

@ModuleInfo(name = "CustomColor", description = "CustomColor", category = ModuleCategory.VULGAR)
public class CustomColor extends Module {
    public static final IntegerValue r = new IntegerValue("R", 0, 0, 255);
    public static final IntegerValue g = new IntegerValue("G", 255, 0, 255);
    public static final IntegerValue b = new IntegerValue("B", 255, 0, 255);
    public static final IntegerValue r2 = new IntegerValue("R2", 255, 0, 255);
    public static final IntegerValue g2 = new IntegerValue("G2", 255, 0, 255);
    public static final IntegerValue b2 = new IntegerValue("B2", 255, 0, 255);
    public static final IntegerValue a = new IntegerValue("A", 100, 0, 255);
    public static final IntegerValue a2 = new IntegerValue("A2", 100, 0, 255);
    public static final FloatValue ra = new FloatValue("Radius", 4.5f, 0.1f, 8.0f);
    public static final IntegerValue gradientSpeed = new IntegerValue("ColorSpeed", 100, 10, 1000);
    public static final BoolValue hueInterpolation = new BoolValue("Interpolate", false);
    public static final BoolValue chatPosition = new BoolValue("chatPosition", false);
    private float tempY = 65;
    private float tempHeight = 65;
    @EventTarget
    public void onRender2D(Render2DEvent event) {

        ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());

    }
}
