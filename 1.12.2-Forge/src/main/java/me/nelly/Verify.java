package me.nelly;

import liying.ClassObfuscator;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.minecraft.client.Minecraft;

import javax.swing.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
@ClassObfuscator
public class Verify {
    public static void ipverify() throws IOException {
        if (HttpUtils.get("https://gitcode.net/m0_62964839/vulgarsense/-/raw/master/IP").contains(getIP().trim())) {
            ClientUtils.getLogger().info("验证成功");
            JOptionPane.showMessageDialog(null, "IP验证成功！");
        } else {
            JOptionPane.showMessageDialog(null, "你的IP是:  " + getIP());
            Minecraft.getMinecraft().shutdown();
            System.exit(0);
        }
    }

    public static String getIP() {
        String ipAddress = "";

        try {
            InetAddress localhost = InetAddress.getLocalHost();
            ipAddress = localhost.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }

        return ipAddress;
    }
}
