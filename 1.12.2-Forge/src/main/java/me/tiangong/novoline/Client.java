package me.tiangong.novoline;

import me.tiangong.novoline.api.FontManager;
import me.tiangong.novoline.impl.SimpleFontManager;
import me.tiangong.novoline.api.FontManager;
import me.tiangong.novoline.impl.SimpleFontManager;
import net.minecraft.client.Minecraft;

import java.awt.*;

public class Client {
    public static double fontScaleOffset = 1;//round((double)1600/1080, 1) * s.getScaleFactor();//2.75;
    public static FontManager fontManager = SimpleFontManager.create();
    public static FontManager getFontManager() {
        return fontManager;
    }


    public static String name = "MoralWin";
    public static String version = "230519";

    public static int THEME_RGB_COLOR = new Color(36, 240, 0).getRGB();

    public static Client instance = new Client();

    public static ScaleUtils scaleUtils = new ScaleUtils(2);
    public static double deltaTime() {
        return Minecraft.getDebugFPS() > 0 ? (1.0000 / Minecraft.getDebugFPS()) : 1;
    }


}
