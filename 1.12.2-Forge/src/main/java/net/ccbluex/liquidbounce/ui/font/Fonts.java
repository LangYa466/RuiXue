/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.ui.font;

import com.google.gson.*;
import net.ccbluex.liquidbounce.LiquidBounce;
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer;
import net.ccbluex.liquidbounce.utils.ClientUtils;
import net.ccbluex.liquidbounce.utils.MinecraftInstance;
import net.ccbluex.liquidbounce.utils.misc.HttpUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.*;
import java.lang.reflect.Field;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class Fonts extends MinecraftInstance {

    @FontDetails(fontName = "Minecraft Font")
    public static final IFontRenderer minecraftFont = mc.getFontRendererObj();
    private static final HashMap<FontInfo, IFontRenderer> CUSTOM_FONT_RENDERERS = new HashMap<>();
    @FontDetails(fontName = "sfbold100", fontSize = 40)
    public static IFontRenderer sfbold100;
    @FontDetails(fontName = "sfbold80", fontSize = 40)
    public static IFontRenderer sfbold80;
    @FontDetails(fontName = "sfbold40", fontSize = 40)
    public static IFontRenderer sfbold40;
    @FontDetails(fontName = "sfbold35", fontSize = 40)
    public static IFontRenderer sfbold35;
    @FontDetails(fontName = "sfbold30", fontSize = 40)
    public static IFontRenderer sfbold30;
    @FontDetails(fontName = "sfbold28", fontSize = 40)
    public static IFontRenderer sfbold28;
    @FontDetails(fontName = "Tenacitybold", fontSize = 18)
    public static IFontRenderer tenacitybold35;
    @FontDetails(fontName = "Tenacitybold", fontSize = 8)
    public static IFontRenderer tenacitybold15;
    @FontDetails(fontName = "Tenacitybold", fontSize = 18)
    public static IFontRenderer tenacitybold30;
    @FontDetails(fontName = "Tenacitybold", fontSize = 20)
    public static IFontRenderer tenacitybold40;
    @FontDetails(fontName = "Tenacitybold", fontSize = 80)
    public static IFontRenderer tenacitybold80;
    @FontDetails(fontName = "Tenacitybold", fontSize = 21)
    public static IFontRenderer tenacitybold43;
    @FontDetails(fontName = "Tenacitycheck", fontSize = 60)
    public static IFontRenderer tenacitycheck60;
    @FontDetails(fontName = "Tenacitycheck", fontSize = 70)
    public static IFontRenderer tenacitycheck70;
    @FontDetails(fontName = "Roboto Medium", fontSize = 20)
    public static IFontRenderer font20;
    @FontDetails(fontName = "Roboto Medium", fontSize = 25)
    public static IFontRenderer font25;
    @FontDetails(fontName = "Roboto Medium", fontSize = 27)
    public static IFontRenderer font27;
    @FontDetails(fontName = "Roboto Medium", fontSize = 30)
    public static IFontRenderer font30;
    @FontDetails(fontName = "Roboto Medium", fontSize = 35)
    public static IFontRenderer font35;
    @FontDetails(fontName = "Roboto Medium", fontSize = 40)
    public static IFontRenderer font40;
    @FontDetails(fontName = "Roboto Medium", fontSize = 43)
    public static IFontRenderer font43;
    @FontDetails(fontName = "Roboto Bold", fontSize = 180)
    public static IFontRenderer fontBold180;
    @FontDetails(fontName = "Ico", fontSize = 20)
    public static IFontRenderer ico1;
    @FontDetails(fontName = "Ico", fontSize = 20)
    public static IFontRenderer ico2;
    @FontDetails(fontName = "Product Sans", fontSize = 20)
    public static IFontRenderer productSans40;
    @FontDetails(fontName = "Product Sans", fontSize = 37)
    public static IFontRenderer productSans70;
    @FontDetails(fontName = "Product Sans", fontSize = 40)
    public static IFontRenderer productSans80;
    @FontDetails(fontName = "Notif", fontSize = 80)
    public static IFontRenderer noti80;
    @FontDetails(fontName = "Bangers", fontSize = 45)
    public static GameFontRenderer fontBangers;
    @FontDetails(fontName = "NB", fontSize = 18)
    public static IFontRenderer nbicon18;
    @FontDetails(fontName = "NB", fontSize = 20)
    public static IFontRenderer nbicon20;
    @FontDetails(fontName = "nailao", fontSize = 20)
    public static IFontRenderer nailao20;
    @FontDetails(fontName = "nailao", fontSize = 25)
    public static IFontRenderer nailao25;
    @FontDetails(fontName = "nailao", fontSize = 30)
    public static IFontRenderer nailao30;
    @FontDetails(fontName = "nailao", fontSize = 35)
    public static IFontRenderer nailao35;
    @FontDetails(fontName = "nailao", fontSize = 40)
    public static IFontRenderer nailao40;
    @FontDetails(fontName = "zhuyuan", fontSize = 20)
    public static IFontRenderer zhuyuan20;
    @FontDetails(fontName = "zhuyuan", fontSize = 25)
    public static IFontRenderer zhuyuan25;
    @FontDetails(fontName = "zhuyuan", fontSize = 30)
    public static IFontRenderer zhuyuan30;
    @FontDetails(fontName = "zhuyuan", fontSize = 35)
    public static IFontRenderer zhuyuan35;
    @FontDetails(fontName = "zhuyuan", fontSize = 40)
    public static IFontRenderer zhuyuan40;
    @FontDetails(fontName = "kaiti", fontSize = 20)
    public static IFontRenderer kaiti20;
    @FontDetails(fontName = "kaiti", fontSize = 25)
    public static IFontRenderer kaiti25;
    @FontDetails(fontName = "kaiti", fontSize = 30)
    public static IFontRenderer kaiti30;
    @FontDetails(fontName = "kaiti", fontSize = 35)
    public static IFontRenderer kaiti35;
    @FontDetails(fontName = "kaiti", fontSize = 40)
    public static IFontRenderer kaiti40;
    @FontDetails(fontName = "hancan", fontSize = 20)
    public static IFontRenderer hancan20;
    @FontDetails(fontName = "hancan", fontSize = 25)
    public static IFontRenderer hancan25;
    @FontDetails(fontName = "hancan", fontSize = 30)
    public static IFontRenderer hancan30;
    @FontDetails(fontName = "hancan", fontSize = 35)
    public static IFontRenderer hancan35;
    @FontDetails(fontName = "hancan", fontSize = 40)
    public static IFontRenderer hancan40;
    @FontDetails(fontName = "banshu", fontSize = 20)
    public static IFontRenderer banshu20;
    @FontDetails(fontName = "banshu", fontSize = 25)
    public static IFontRenderer banshu25;
    @FontDetails(fontName = "banshu", fontSize = 30)
    public static IFontRenderer banshu30;
    @FontDetails(fontName = "banshu", fontSize = 35)
    public static IFontRenderer banshu35;
    @FontDetails(fontName = "banshu", fontSize = 40)
    public static IFontRenderer banshu40;
    public static void loadFonts() {
        long l = System.currentTimeMillis();

        ClientUtils.getLogger().info("Loading Fonts.");

        downloadFonts();
        sfbold100 = classProvider.wrapFontRenderer(new GameFontRenderer(getsfbold(100)));
        sfbold80 = classProvider.wrapFontRenderer(new GameFontRenderer(getsfbold(80)));
        sfbold40 = classProvider.wrapFontRenderer(new GameFontRenderer(getsfbold(40)));
        sfbold35 = classProvider.wrapFontRenderer(new GameFontRenderer(getsfbold(35)));
        sfbold30 = classProvider.wrapFontRenderer(new GameFontRenderer(getsfbold(30)));
        sfbold28 = classProvider.wrapFontRenderer(new GameFontRenderer(getsfbold(28)));
        tenacitybold15 = classProvider.wrapFontRenderer(new GameFontRenderer(gett(15)));
        tenacitybold35 = classProvider.wrapFontRenderer(new GameFontRenderer(gett(35)));
        tenacitybold30 = classProvider.wrapFontRenderer(new GameFontRenderer(gett(30)));
        tenacitybold40 = classProvider.wrapFontRenderer(new GameFontRenderer(gett(40)));
        tenacitybold43 = classProvider.wrapFontRenderer(new GameFontRenderer(gett(43)));
        tenacitybold80 = classProvider.wrapFontRenderer(new GameFontRenderer(gett(80)));
        tenacitycheck60 = classProvider.wrapFontRenderer(new GameFontRenderer(getf(60)));
        tenacitycheck70 = classProvider.wrapFontRenderer(new GameFontRenderer(getf(70)));
        noti80 = classProvider.wrapFontRenderer(new GameFontRenderer(getsb(80)));
        ico1 = classProvider.wrapFontRenderer(new GameFontRenderer(gets(32)));
        ico2 = classProvider.wrapFontRenderer(new GameFontRenderer(gets2(32)));
        font20 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 20)));
        font25 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 25)));
        font27 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 207)));
        font30 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 30)));
        font35 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 35)));
        font40 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 40)));
        font43 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Medium.ttf", 43)));
        fontBold180 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("Roboto-Bold.ttf", 180)));
        productSans40 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("product-sans.ttf", 20)));
        productSans70 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("product-sans.ttf", 37)));
        productSans80 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("product-sans.ttf", 40)));
        nbicon18 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("newicon.ttf", 18)));
        nbicon20 = classProvider.wrapFontRenderer(new GameFontRenderer(getFont("newicon.ttf", 23)));
        fontBangers = new GameFontRenderer(getFont("Bangers-Regular.ttf", 45));
        nailao20 = classProvider.wrapFontRenderer(new GameFontRenderer(getnailao(30)));
        nailao25 = classProvider.wrapFontRenderer(new GameFontRenderer(getnailao(40)));
        nailao30 = classProvider.wrapFontRenderer(new GameFontRenderer(getnailao(50)));
        nailao35 = classProvider.wrapFontRenderer(new GameFontRenderer(getnailao(60)));
        nailao40 = classProvider.wrapFontRenderer(new GameFontRenderer(getnailao(70)));
        zhuyuan20 = classProvider.wrapFontRenderer(new GameFontRenderer(getzhuyuan(30)));
        zhuyuan25 = classProvider.wrapFontRenderer(new GameFontRenderer(getzhuyuan(40)));
        zhuyuan30 = classProvider.wrapFontRenderer(new GameFontRenderer(getzhuyuan(50)));
        zhuyuan35 = classProvider.wrapFontRenderer(new GameFontRenderer(getzhuyuan(60)));
        zhuyuan40 = classProvider.wrapFontRenderer(new GameFontRenderer(getzhuyuan(70)));
        kaiti20 = classProvider.wrapFontRenderer(new GameFontRenderer(getkaiti(30)));
        kaiti25 = classProvider.wrapFontRenderer(new GameFontRenderer(getkaiti(40)));
        kaiti30 = classProvider.wrapFontRenderer(new GameFontRenderer(getkaiti(50)));
        kaiti35 = classProvider.wrapFontRenderer(new GameFontRenderer(getkaiti(60)));
        kaiti40 = classProvider.wrapFontRenderer(new GameFontRenderer(getkaiti(70)));
        banshu20 = classProvider.wrapFontRenderer(new GameFontRenderer(getbanshu(30)));
        banshu25 = classProvider.wrapFontRenderer(new GameFontRenderer(getbanshu(40)));
        banshu30 = classProvider.wrapFontRenderer(new GameFontRenderer(getbanshu(50)));
        banshu35 = classProvider.wrapFontRenderer(new GameFontRenderer(getbanshu(60)));
        banshu40 = classProvider.wrapFontRenderer(new GameFontRenderer(getbanshu(70)));
        hancan20 = classProvider.wrapFontRenderer(new GameFontRenderer(gethancan(30)));
        hancan25 = classProvider.wrapFontRenderer(new GameFontRenderer(gethancan(40)));
        hancan30 = classProvider.wrapFontRenderer(new GameFontRenderer(gethancan(50)));
        hancan35 = classProvider.wrapFontRenderer(new GameFontRenderer(gethancan(60)));
        hancan40 = classProvider.wrapFontRenderer(new GameFontRenderer(gethancan(70)));
        net.ccbluex.liquidbounce.feng.FontLoaders.initFonts();
        try {
            CUSTOM_FONT_RENDERERS.clear();

            final File fontsFile = new File(LiquidBounce.fileManager.fontsDir, "fonts.json");

            if (fontsFile.exists()) {
                final JsonElement jsonElement = new JsonParser().parse(new BufferedReader(new FileReader(fontsFile)));

                if (jsonElement instanceof JsonNull)
                    return;

                final JsonArray jsonArray = (JsonArray) jsonElement;

                for (final JsonElement element : jsonArray) {
                    if (element instanceof JsonNull)
                        return;

                    final JsonObject fontObject = (JsonObject) element;

                    Font font = getFont(fontObject.get("fontFile").getAsString(), fontObject.get("fontSize").getAsInt());

                    CUSTOM_FONT_RENDERERS.put(new FontInfo(font), classProvider.wrapFontRenderer(new GameFontRenderer(font)));
                }
            } else {
                fontsFile.createNewFile();

                final PrintWriter printWriter = new PrintWriter(new FileWriter(fontsFile));
                printWriter.println(new GsonBuilder().setPrettyPrinting().create().toJson(new JsonArray()));
                printWriter.close();
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }

        ClientUtils.getLogger().info("Loaded Fonts. (" + (System.currentTimeMillis() - l) + "ms)");
    }

    private static void downloadFonts() {
        try {
            final File outputFile = new File(LiquidBounce.fileManager.fontsDir, "roboto.zip");

            if (!outputFile.exists()) {
                ClientUtils.getLogger().info("Downloading fonts...");
                HttpUtils.download(LiquidBounce.CLIENT_CLOUD + "/fonts/Roboto.zip", outputFile);
                ClientUtils.getLogger().info("Extract fonts...");
                extractZip(outputFile.getPath(), LiquidBounce.fileManager.fontsDir.getPath());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static Font getsfbold(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/sfbold.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font getnailao(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/nailao.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font gethancan(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/hancan.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font getzhuyuan(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/zhuyuan.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font getkaiti(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/kaiti.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font getbanshu(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/banshu.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font gett(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/tenacitybold.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font getf(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/tenacitycheck.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font getsb(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/notif.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font gets(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/icon.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    private static Font gets2(int size) {
        Font font;
        try {
            InputStream is = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/icon2.ttf")).getInputStream();
            font = Font.createFont(0, is);
            font = font.deriveFont(0, size);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("Error loading font");
            font = new Font("Posterama", 0, size);
        }
        return font;
    }
    public static IFontRenderer getFontRenderer(final String name, final int size) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                Object o = field.get(null);

                if (o instanceof IFontRenderer) {
                    FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    if (fontDetails.fontName().equals(name) && fontDetails.fontSize() == size)
                        return (IFontRenderer) o;
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        return CUSTOM_FONT_RENDERERS.getOrDefault(new FontInfo(name, size), minecraftFont);
    }

    public static FontInfo getFontDetails(final IFontRenderer fontRenderer) {
        for (final Field field : Fonts.class.getDeclaredFields()) {
            try {
                field.setAccessible(true);

                final Object o = field.get(null);

                if (o.equals(fontRenderer)) {
                    final FontDetails fontDetails = field.getAnnotation(FontDetails.class);

                    return new FontInfo(fontDetails.fontName(), fontDetails.fontSize());
                }
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        for (Map.Entry<FontInfo, IFontRenderer> entry : CUSTOM_FONT_RENDERERS.entrySet()) {
            if (entry.getValue() == fontRenderer)
                return entry.getKey();
        }

        return null;
    }

    public static List<IFontRenderer> getFonts() {
        final List<IFontRenderer> fonts = new ArrayList<>();

        for (final Field fontField : Fonts.class.getDeclaredFields()) {
            try {
                fontField.setAccessible(true);

                final Object fontObj = fontField.get(null);

                if (fontObj instanceof IFontRenderer) fonts.add((IFontRenderer) fontObj);
            } catch (final IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        fonts.addAll(Fonts.CUSTOM_FONT_RENDERERS.values());

        return fonts;
    }

    private static Font getFont(final String fontName, final int size) {
        try {
            InputStream inputStream = Minecraft.getMinecraft().getResourceManager().getResource(new ResourceLocation("liquidbounce/font/ali.ttf")).getInputStream();
            Font awtClientFont = Font.createFont(Font.TRUETYPE_FONT, inputStream);
            awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
            inputStream.close();
            return awtClientFont;
        } catch (final Exception e) {
            e.printStackTrace();

            return new Font("default", Font.PLAIN, size);
        }
    }

    private static void extractZip(final String zipFile, final String outputFolder) {
        final byte[] buffer = new byte[1024];

        try {
            final File folder = new File(outputFolder);

            if (!folder.exists()) folder.mkdir();

            final ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(zipFile));

            ZipEntry zipEntry = zipInputStream.getNextEntry();
            while (zipEntry != null) {
                File newFile = new File(outputFolder + File.separator + zipEntry.getName());
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fileOutputStream = new FileOutputStream(newFile);

                int i;
                while ((i = zipInputStream.read(buffer)) > 0)
                    fileOutputStream.write(buffer, 0, i);

                fileOutputStream.close();
                zipEntry = zipInputStream.getNextEntry();
            }

            zipInputStream.closeEntry();
            zipInputStream.close();
        } catch (final IOException e) {
            e.printStackTrace();
        }
    }

    public static class FontInfo {
        private final String name;
        private final int fontSize;

        public FontInfo(String name, int fontSize) {
            this.name = name;
            this.fontSize = fontSize;
        }

        public FontInfo(Font font) {
            this(font.getName(), font.getSize());
        }

        public String getName() {
            return name;
        }

        public int getFontSize() {
            return fontSize;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            FontInfo fontInfo = (FontInfo) o;

            if (fontSize != fontInfo.fontSize) return false;
            return Objects.equals(name, fontInfo.name);
        }

        @Override
        public int hashCode() {
            int result = name != null ? name.hashCode() : 0;
            result = 31 * result + fontSize;
            return result;
        }
    }

}