package net.ccbluex.liquidbounce.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HWIDUtil {

    public static String GetHWID() {
        try {

            String base64Encoded = java.util.Base64.getEncoder().encodeToString(GetHardwareInfo().getBytes());

            String rot13Encoded = ROT13Encode(base64Encoded);

            return rot13Encoded;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String GetHardwareInfo() throws UnsupportedEncodingException, NoSuchAlgorithmException {
        StringBuilder s = new StringBuilder();
        String main = System.getenv("PROCESS_IDENTIFIER") + System.getenv("COMPUTERNAME");
        byte[] bytes = main.getBytes("UTF-8");
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        byte[] md5 = messageDigest.digest(bytes);
        int i = 0;
        for (byte b : md5) {
            s.append(Integer.toHexString((b & 0xFF) | 0x300), 0, 3);
            if (i != md5.length - 1) {
                s.append("-");
            }
            i++;
        }
        return s.toString();
    }
    private static String ROT13Encode(String input) {
        StringBuilder result = new StringBuilder();
        for (char c : input.toCharArray()) {
            if ((c >= 'A' && c <= 'M') || (c >= 'a' && c <= 'm')) {
                c += 13;
            } else if ((c >= 'N' && c <= 'Z') || (c >= 'n' && c <= 'z')) {
                c -= 13;
            }
            result.append(c);
        }
        return result.toString();
    }
}