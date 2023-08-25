package net.ccbluex.liquidbounce.features.module.modules.render

import me.tiangong.FakeFPS
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Render2DEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.feng.FontLoaders

import net.ccbluex.liquidbounce.utils.ketamine.BloomUtil
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.tenacity.ColorUtil
import net.ccbluex.liquidbounce.utils.tenacity.render.GradientUtil
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.ccbluex.liquidbounce.value.TextValue
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.ScaledResolution
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.util.ResourceLocation

import java.awt.Color



@ModuleInfo(name = "Logo", description = ":)",chinesename = "标识", category = ModuleCategory.RENDER)
class Logo : Module() {
    val logomode = ListValue("LogoMode", arrayOf("Rise", "Tenacity", "None"), "Rise")
    val clolormode = ListValue(
        "ColorMode",
        arrayOf("Rainbow", "Light Rainbow", "Static", "Double Color", "Tenacity", "Default", "Fade", "Analogous"),
        "Tenacity"
    )
    val degree = ListValue("Degree", arrayOf("30", "-30"), "-30")
    val movingColors = BoolValue("Moving Colors", false)
    val hueInterpolation = BoolValue("Hue Interpolate", false)
    val WhiteInfo = BoolValue("WhiteInfo", false)
    val clientname = TextValue("ClientName", "瑞雪")
    private val colorRedValue = IntegerValue("R", 255, 0, 255)
    private val colorGreenValue = IntegerValue("G", 255, 0, 255)
    private val colorBlueValue = IntegerValue("B", 255, 0, 255)
    private val bottomLeftText: MutableMap<String, String> = LinkedHashMap()

    private fun mixColors(color1: Color, color2: Color): Color {
        return if (movingColors.get()) {
            ColorUtil.interpolateColorsBackAndForth(
                15,
                1,
                color1,
                color2,
                hueInterpolation.get()
            )
        } else {
            ColorUtil.interpolateColorC(color1, color2, 0F)
        }
    }

    fun getClientColor(): Color {
        return Color(hudMod.r.get(), hudMod.g.get(), hudMod.b.get())
    }

    fun getAlternateClientColor(): Color {
        return Color(hudMod.r2.get(), hudMod.g2.get(), hudMod.b2.get())
    }

    val clientColors: Array<Color>
        get() {
            val firstColor: Color
            val secondColor: Color
            when (clolormode.get().toLowerCase()) {
                "light rainbow" -> {
                    firstColor = ColorUtil.rainbow(15, 1, .6f, 1f, 1f)
                    secondColor = ColorUtil.rainbow(15, 40, .6f, 1f, 1f)
                }

                "rainbow" -> {
                    firstColor = ColorUtil.rainbow(15, 1, 1f, 1f, 1f)
                    secondColor = ColorUtil.rainbow(15, 40, 1f, 1f, 1f)
                }

                "double color" -> {
                    firstColor = mixColors(
                        getClientColor(),
                        getAlternateClientColor()
                    )
                    secondColor = mixColors(
                        getAlternateClientColor(),
                        getClientColor()
                    )
                }

                "static" -> {
                    firstColor = Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get())
                    secondColor = firstColor
                }

                "tenacity" -> {
                    firstColor =
                        mixColors(getClientColor(), getAlternateClientColor())
                    secondColor =
                        mixColors(getAlternateClientColor(), getClientColor())
                }

                "fade" -> {
                    firstColor =
                        ColorUtil.fade(
                            15, 1, Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()),
                            1F
                        )
                    secondColor =
                        ColorUtil.fade(
                            15,
                            50,
                            Color(colorRedValue.get(), colorGreenValue.get(), colorBlueValue.get()),
                            1F
                        )
                }

                "analogous" -> {
                    val SB = if (degree.get() == "30") 0 else 1
                    val analogous =
                        ColorUtil.getAnalogousColor(getClientColor())[SB]
                    firstColor = mixColors(getClientColor(), analogous)
                    secondColor = mixColors(analogous, getClientColor())
                }


                else -> {
                    firstColor = Color(-1)
                    secondColor = Color(-1)
                }
            }
            return arrayOf(firstColor, secondColor)
        }

    private fun drawInfo(clientColors: Array<Color>) {
        val sr = ScaledResolution(Minecraft.getMinecraft())
        bottomLeftText["Speed:"] = calculateBPS().toString()
        bottomLeftText["XYZ:"] = Math.round(mc.thePlayer!!.posX).toString() + " " + Math.round(
            mc.thePlayer!!.posY
        ) + " " + Math.round(mc.thePlayer!!.posZ)
        bottomLeftText["FPS:"] = FakeFPS.getfps().toString()
        val yOffset = 0.toFloat()
        if (WhiteInfo.get()) {
            var boldFontMovement = FontLoaders.F18.height + 2 + yOffset
            for ((key, value) in bottomLeftText) {
                FontLoaders.F18.drawStringWithShadow(
                    key + value,
                    2.0,
                    (sr.scaledHeight - boldFontMovement).toInt().toDouble(),
                    -1
                )
                boldFontMovement += (FontLoaders.F18.height + 2).toFloat()
            }
        } else {
            val height = ((FontLoaders.F18.height + 2) * bottomLeftText.keys.size).toFloat()
            val width = FontLoaders.F18.getStringWidth("XYZ:").toFloat()
            GradientUtil.applyGradientVertical(
                2f,
                sr.scaledHeight - (height + yOffset),
                width,
                height,
                1f,
                clientColors[0],
                clientColors[1]
            ) {
                var boldFontMovement = FontLoaders.F18.height + 2 + yOffset
                for ((key, value) in bottomLeftText) {
                    BloomUtil.drawAndBloom {
                        FontLoaders.F18.drawString(key + value, 2f, sr.scaledHeight - boldFontMovement, -1)
                    }
                    boldFontMovement += (FontLoaders.F18.height + 2).toFloat()
                }
            }
        }
    }

    private fun calculateBPS(): Double {
        val bps = Math.hypot(
            mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX,
            mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ
        ) * mc.timer.timerSpeed * 20
        return Math.round(bps * 100.0) / 100.0
    }


    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        val clientColors = clientColors
        if (logomode.get() == "Rise") {
            GradientUtil.applyGradientHorizontal(
                5f,
                5f,
               skid.CFont.FontLoaders.tenacitybold40.getStringWidth(clientname.get()).toFloat(),
                20f,
                1f,
                clientColors[0],
                clientColors[1]
            ) {
                RenderUtils.setAlphaLimit(0f)
                BloomUtil.drawAndBloom {
                  skid.CFont.FontLoaders.tenacitybold40.drawStringWithShadow(
                        clientname.get(),
                        5.0,
                        5.0,
                        Color(0, 0, 0, 0).rgb
                    )
                }
            }
            GlStateManager.resetColor()
           skid.CFont.FontLoaders.tenacitybold18.drawString(
               LiquidBounce.CLIENT_VERSION.toString(),
                (skid.CFont.FontLoaders.tenacitybold40.getStringWidth(clientname.get()) + 6).toFloat(),
                5f,
                clientColors[1].rgb
            )

        }
        if (logomode.get() == "Tenacity") {
            RenderUtils.drawImage4(ResourceLocation("liquidbounce/logoshadow.png"), 5, 5, 40, 40)
        }
        val text = String.format(
            " version  %s ",
            LiquidBounce.CLIENT_TIME
        )
        GradientUtil.applyGradientHorizontal(
            ((classProvider.createScaledResolution(mc).scaledWidth - FontLoaders.F18.getStringWidth(text) - 1).toFloat()),
            (((classProvider.createScaledResolution(mc).scaledHeight - FontLoaders.F18.height - 1).toFloat())),
            FontLoaders.F18.getStringWidth(text).toFloat(),
            9f,
            1f,
            clientColors[0],
            clientColors[1]
        ) {
            RenderUtils.setAlphaLimit(0f)
            BloomUtil.drawAndBloom {
                FontLoaders.F18.drawString(
                    text,
                    (classProvider.createScaledResolution(mc).scaledWidth - FontLoaders.F18.getStringWidth(text) - 1).toDouble()
                        .toFloat(),
                    ((classProvider.createScaledResolution(mc).scaledHeight - FontLoaders.F18.height - 1).toDouble()
                        .toFloat()),
                    Color(0, 0, 0, 0).rgb
                )
            }
        }
        drawInfo(clientColors)
        val sr = ScaledResolution(Minecraft.getMinecraft())
        val width = sr.scaledWidth.toFloat()
        val height = sr.scaledHeight.toFloat()
        // PlayTime
        if (classProvider.isGuiHudDesigner(mc.currentScreen)) return
        LiquidBounce.hud.render(false)
        GlStateManager.resetColor()
    }

    var hudMod = LiquidBounce.moduleManager.getModule(
        HUD::class.java
    ) as HUD

    init {
        state = true
    }


}

