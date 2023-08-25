package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.tiangong.CustomColor
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.modules.hyt.AutoGG
import net.ccbluex.liquidbounce.features.module.modules.hyt.AutoL
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.tenacity.ColorUtil
import net.ccbluex.liquidbounce.utils.tenacity.render.RoundedUtil
import net.ccbluex.liquidbounce.value.*
import skid.Recorder
import skid.Recorder.killCounts
import java.awt.Color
import java.text.SimpleDateFormat
import java.util.*


@ElementInfo(name = "游戏信息")
class GameInfo3(x: Double = 10.0, y: Double = 10.0, scale: Float = 1F) : Element(x, y, scale) {
    private val GameInfo = ListValue("Mode", arrayOf("Normal"), "Normal")
    private var fontValue = FontValue("Font", Fonts.font35)
    private val shadowValue = ListValue("Normal-Shadow", arrayOf("None", "Basic", "Thick"), "Thick")
    private val lineValue = BoolValue("Line", true)
    val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
    override val drawElement: Border
        get() {
            val gradientColor1 =
                Color(CustomColor.r.get(), CustomColor.g.get(), CustomColor.b.get(), CustomColor.a.get())
            val gradientColor2 =
                Color(CustomColor.r.get(), CustomColor.g.get(), CustomColor.b.get(), CustomColor.a.get())
            val gradientColor3 =
                Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get(), CustomColor.a2.get())
            val gradientColor4 =
                Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get(), CustomColor.a2.get())
            val fontRenderer = fontValue.get()
            val y2 = fontRenderer.fontHeight * 5 + 11.0.toInt()
            val x2 = 140.0.toInt()
            if (GameInfo.get().equals("normal", true)) {
                val autoL = LiquidBounce.moduleManager.getModule(AutoL::class.java) as AutoL
                val autogg = LiquidBounce.moduleManager.getModule(AutoGG::class.java) as AutoGG
                //drawShadow
                RoundedUtil.drawGradientRound(
                    -2f,
                    -2f,
                    x2.toFloat(),
                    y2.toFloat(),
                    CustomColor.ra.get(),
                    ColorUtil.applyOpacity(gradientColor4, .85f),
                    gradientColor1,
                    gradientColor3,
                    gradientColor2
                )

                Fonts.font40.drawCenteredString("游戏信息", 31.5F, 3f, Color.WHITE.rgb, true)
                fontRenderer.drawStringWithShadow(
                    "游玩时间: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}",
                    2,
                    (fontRenderer.fontHeight + 8f).toInt(),
                    Color.WHITE.rgb
                )
                fontRenderer.drawStringWithShadow(
                    "击杀玩家: " + autoL.kill,
                    2,
                    (fontRenderer.fontHeight * 2 + 8f).toInt(),
                    Color.WHITE.rgb
                )
                fontRenderer.drawStringWithShadow(
                    "胜利: " + autogg.win, 2,
                    (fontRenderer.fontHeight * 3 + 8f).toInt(), Color.WHITE.rgb
                )
                fontRenderer.drawStringWithShadow(
                    "游玩: " + autogg.totalPlayed, 2,
                    (fontRenderer.fontHeight * 4 + 8f).toInt(), Color.WHITE.rgb
                )
                if (GameInfo.get().equals("test", true)) {
                    RoundedUtil.drawRound(-2f, -2f, x2.toFloat(), y2.toFloat(), 0F, Color(0, 0, 0, 100))
                    //drawShadow
                    when (shadowValue.get()) {
                        "Basic" -> RenderUtils.drawShadow(-2.5f, -2.5f, x2.toFloat() + 1, y2.toFloat() + 1)
                        "Thick" -> {
                            RenderUtils.drawShadow(-2.5f, -2.5f, x2.toFloat() + 1, y2.toFloat() + 1)
                            RenderUtils.drawShadow(-2.5f, -2.5f, x2.toFloat() + 1, y2.toFloat() + 1)
                        }
                    }
                    if (lineValue.get()) {
                        RenderUtils.drawGradientSideways(
                            2.44,
                            fontRenderer.fontHeight + 2.5 + 0.0,
                            138.0 + -2.44f,
                            fontRenderer.fontHeight + 2.5 + 1.16f,
                            Color(CustomColor.r.get(), CustomColor.g.get(), CustomColor.b.get()).rgb,
                            Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get()).rgb
                        )
                    }
                    if (GameInfo.get().equals("test", true)) {
                        val DATE_FORMAT = SimpleDateFormat("HH:mm:ss")
                        Fonts.tenacitybold40.drawCenteredString(
                            "游戏信息",
                            x2.toFloat() / 2f,
                            3f,
                            Color.WHITE.rgb,
                            true
                        )
                        fontRenderer.drawStringWithShadow(
                            "游玩时间: ${DATE_FORMAT.format(Date(System.currentTimeMillis() - Recorder.startTime - 8000L * 3600L))}",
                            2,
                            (fontRenderer.fontHeight + 8f).toInt(),
                            Color.WHITE.rgb
                        )
                        fontRenderer.drawStringWithShadow(
                            "击杀数: $autoL.kill", 2,
                            (fontRenderer.fontHeight * 2 + 8f).toInt(), Color.WHITE.rgb
                        )
                        fontRenderer.drawStringWithShadow(
                            "胜利: " + autogg.win, 2,
                            (fontRenderer.fontHeight * 3 + 8f).toInt(), Color.WHITE.rgb
                        )
                        fontRenderer.drawStringWithShadow(
                            "游玩: " + autogg.totalPlayed, 2,
                            (fontRenderer.fontHeight * 4 + 8f).toInt(), Color.WHITE.rgb
                        )

                    }
                }
            }
            return Border(-2f, -2f, x2.toFloat(), y2.toFloat())
        }
}




