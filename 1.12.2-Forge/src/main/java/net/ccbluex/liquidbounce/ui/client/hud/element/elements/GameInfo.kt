package net.ccbluex.liquidbounce.ui.client.hud.element.elements
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.hyt.AutoGG
import net.ccbluex.liquidbounce.features.module.modules.hyt.AutoL
import net.ccbluex.liquidbounce.features.module.modules.render.Gident
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.ServerUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.Minecraft
import org.lwjgl.opengl.GL11
import skid.blur.BlurBuffer
import skid.render.Palette
import skid.render.VisualUtils
import java.awt.Color

@ElementInfo(name = "游戏信息2")
class GameInfo2(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private val GameInfo = ListValue("Mode", arrayOf("Default"), "Default")

    private val blur = BoolValue("Old-Blur", false)
    private val textredValue = IntegerValue("Old-TextRed", 255, 0, 255)
    private val textgreenValue = IntegerValue("Old-TextRed", 244, 0, 255)
    private val textblueValue = IntegerValue("Old-TextBlue", 255, 0, 255)
    private val textblueae = IntegerValue("Old-Textalpha", 255, 0, 255)
    private val brightnessValue = FloatValue("Brightness", 1f, 0f, 1f)
    private val redValue = IntegerValue("Text-R", 255, 0, 255)
    private val greenValue = IntegerValue("Text-G", 255, 0, 255)
    private val blueValue = IntegerValue("Text-B", 255, 0, 255)
    private val newRainbowIndex = IntegerValue("NewRainbowOffset", 1, 1, 50)
    private val astolfoRainbowOffset = IntegerValue("AstolfoOffset", 5, 1, 20)
    private val astolfoclient = IntegerValue("AstolfoRange", 109, 1, 765)
    private val astolfoRainbowIndex = IntegerValue("AstolfoIndex", 109, 1, 300)
    private val saturationValue = FloatValue("Saturation", 0.9f, 0f, 1f)
    private val colorModeValue = ListValue("Color", arrayOf("Custom", "Rainbow", "Fade", "Astolfo", "NewRainbow","Gident"), "Custom")
    private val distanceValue = IntegerValue("Distance", 0, 0, 400)
    private val gradientAmountValue = IntegerValue("Gradient-Amount", 25, 1, 50)
    private var fontValue = FontValue("Font", Fonts.font35)
    private var GameInfoRows = 0

    override val drawElement: Border
        get() {
        val floatX = renderX.toFloat()
        val floatY = renderY.toFloat()
        val barLength1 = (163f).toDouble()
        val colorMode = colorModeValue.get()
        val color = Color(redValue.get(), greenValue.get(), blueValue.get(), 192).rgb
        var Borderx1 = 0
        var Bordery1 = 0
        var Borderx2 = 0
        var Bordery2 = 0
            val autoL = LiquidBounce.moduleManager.getModule(AutoL::class.java) as AutoL
        if (GameInfo.get().equals("Default", true)) {
            Borderx1 += 0
            Bordery1 +=this.GameInfoRows* 18 + 12
            Borderx2 +=176
            Bordery2 +=78
            if(blur.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
                BlurBuffer.blurArea(floatX, floatY + 8  , 176F, 70F )
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            RenderUtils.drawRect(0F, this.GameInfoRows * 18F + 25F, 176F, 80F, Color(redValue.get(), greenValue.get(), blueValue.get(), 0).rgb)
            RenderUtils.drawShadowWithCustomAlpha(0F, 12.5F, 176F, 64F, 255F)
            RenderUtils.drawRect(0F, 11.0F, 176F, 77F , Color(20, 19, 18, 170).rgb)
            fontValue.get().drawStringWithShadow("游戏信息", 3, this.GameInfoRows * 18 + 14, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("帧率:" + Minecraft.getDebugFPS(), 7, this.GameInfoRows * 18 + 26, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("延迟:" + EntityUtils.getPing(mc.thePlayer!!).toString(), 7, this.GameInfoRows * 18 + 36, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("X:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posX) + " " + "Y:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posY) + " " + "Z:" + Text.DECIMAL_FORMAT.format(mc.thePlayer!!.posZ), 7, this.GameInfoRows * 18 + 47, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("服务器IP:" + ServerUtils.getRemoteIp(), 7, this.GameInfoRows * 18 + 56, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), textblueae.get()).rgb)
            fontValue.get().drawStringWithShadow("击杀:${autoL.kill}", 7, this.GameInfoRows * 18 + 68, Color(textredValue.get(), textgreenValue.get(), textblueValue.get(), 255).rgb)
            for (i in 0 until gradientAmountValue.get()) {
                val barStart = i.toDouble() / gradientAmountValue.get().toDouble() * barLength1
                val barEnd = (i + 1).toDouble() / gradientAmountValue.get().toDouble() * barLength1
                RenderUtils.drawGradientSideways(
                    8 + barStart  -8 , 10.0, 8 + barEnd + 5, 11.0,
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                            Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(),1),
                            (Math.abs(
                                System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )

                        else -> color
                    },
                    when {
                        colorMode.equals("Fade", ignoreCase = true) -> Palette.fade2(
                            Color(
                                redValue.get(),
                                greenValue.get(),
                                blueValue.get()
                            ), i * distanceValue.get(), 1000
                        ).rgb
                        colorMode.equals("Astolfo", ignoreCase = true) -> VisualUtils.Astolfo(
                            i * distanceValue.get(),
                            saturationValue.get(),
                            brightnessValue.get(),
                            astolfoRainbowOffset.get(),
                            astolfoRainbowIndex.get(),
                            astolfoclient.get().toFloat()
                        )
                        colorMode.equals(
                            "Gident",
                            ignoreCase = true
                        ) -> VisualUtils.getGradientOffset(
                            Color(Gident.redValue.get(), Gident.greenValue.get(), Gident.blueValue.get()),
                            Color(Gident.redValue2.get(), Gident.greenValue2.get(), Gident.blueValue2.get(), 1),
                            (Math.abs(
                                System.currentTimeMillis() / Gident.gidentspeed.get().toDouble() + i * distanceValue.get()
                            ) / 10)
                        ).rgb
                        colorMode.equals(
                            "NewRainbow",
                            ignoreCase = true
                        ) -> VisualUtils.getRainbow(
                            i * distanceValue.get(),
                            newRainbowIndex.get(),
                            saturationValue.get(),
                            brightnessValue.get()
                        )
                        else -> color
                    }
                )

            }
        }
        return   Border(Borderx1.toFloat(), Bordery1.toFloat(), Borderx2.toFloat(), Bordery2.toFloat())
    }
}
