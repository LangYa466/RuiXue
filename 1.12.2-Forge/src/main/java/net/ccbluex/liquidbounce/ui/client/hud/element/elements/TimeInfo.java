package net.ccbluex.liquidbounce.ui.client.hud.element.elements;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.Render2DEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.ui.font.Fonts;
import net.minecraft.client.Minecraft;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

@ModuleInfo(name = "TimeInfo", description = "TimeInfo", chinesename = "时间显示",category = ModuleCategory.VULGAR)
public class TimeInfo extends Module {
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final String sysTime1URL = "http://quan.suning.com/getSysTime.do";
    private static final String sysTime2URL = "http://quan.suning.com/getSysTime.do";

    /**
     * 从 URL 获取当前系统时间，并返回格式化后的日期字符串。
     */
    private static String getSystemTime(String url) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setRequestMethod("GET");

        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }

        String content = sb.toString();
        String timestamp = content.replaceAll("[^0-9]", "");

        Date date = new Date(Long.parseLong(timestamp));
        return dateFormat.format(date);
    }
    @EventTarget
    public void Render2d(Render2DEvent event){
        String sysTime2;
        try {
            String sysTime1 = getSystemTime(sysTime1URL);
            sysTime2 = getSystemTime(sysTime2URL);
            Fonts.tenacitybold35.drawString("系统时间1: " + sysTime1, 20, 20, 0xFFFFFF);
            Fonts.tenacitybold35.drawString("系统时间2: " + sysTime2, 10, 10, 0xFFFFFF);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
