package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import me.tiangong.CustomColor
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.gui.IFontRenderer
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.BlurUtils
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.tenacity.ColorUtil
import net.ccbluex.liquidbounce.utils.tenacity.render.RoundedUtil
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import skid.EaseUtils
import java.awt.Color
import java.math.BigDecimal
import kotlin.math.max

/**
 * CustomHUD Notification element
 */
@ElementInfo(name = "功能提示", single = true)
class Notifications(
    x: Double = 0.0, y: Double = 30.0, scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.DOWN)): Element(x, y, scale, side) {

    /**
     * Example notification for CustomHUD designer
     */
    private val exampleNotification = Notification("这是一个无敌提示", "用来展示的无敌notif 我觉得这很酷.", NotifyType.INFO)
    companion object {
        val styleValue = ListValue("Mode", arrayOf("Noteless","Old","Tenacity","rect"), "Noteless")
        val fontValue = FontValue("Font", Fonts.font35)
    }

    /**
     * Draw element
     */
    override val drawElement: Border?
        get() {
            val notifications = mutableListOf<Notification>()
            for ((index, notify) in LiquidBounce.hud.notifications.withIndex()) {
                GL11.glPushMatrix()

                if (notify.drawNotification(index, Companion)) {
                    notifications.add(notify)
                }

                GL11.glPopMatrix()
            }
            for (notify in notifications) {
                LiquidBounce.hud.notifications.remove(notify)
            }

            if (classProvider.isGuiHudDesigner(mc.currentScreen)) {
                if (!LiquidBounce.hud.notifications.contains(exampleNotification))
                    LiquidBounce.hud.addNotification(exampleNotification)

                exampleNotification.fadeState = FadeState.STAY
                exampleNotification.displayTime = System.currentTimeMillis()
//            exampleNotification.x = exampleNotification.textLength + 8F

                return Border(-exampleNotification.width.toFloat(), -exampleNotification.height.toFloat(), 0F, 0F)
            }

            return null
        }

}
class Notification(
    val title: String,
    val content: String,
    val type: NotifyType,
    val time: Int = 1500,
    val animeTime: Int = 500
) {
     val blurValue = FloatValue("Blur", 1F,0.1F,10F)
    val whiteText = BoolValue("WhiteTextColor", true)
    val motionBlur = BoolValue("Motionblur", false)
     val alpha = IntegerValue("BackGroundAlpha", 170, 0, 255)
    val width = 100.coerceAtLeast(
        Fonts.font35.getStringWidth(this.title)
            .coerceAtLeast(Fonts.font35.getStringWidth(this.content)) + 10
    )
    val height = 30

    var fadeState = FadeState.IN
    var nowY = -height
    var displayTime = System.currentTimeMillis()
    var animeXTime = System.currentTimeMillis()
    var animeYTime = System.currentTimeMillis()

    /**
     * Draw notification
     */
    fun drawNotification(index: Int,sb: Notifications.Companion): Boolean {
        val mode = sb.styleValue.get()
        val font = sb.fontValue.get()
        val realY = -(index + 1) * height
        val nowTime = System.currentTimeMillis()
        val image = MinecraftInstance.classProvider.createResourceLocation("liquidbounce/notification/" + type.name + ".png")
        //Y-Axis Animation
        if (nowY != realY) {
            var pct = (nowTime - animeYTime) / animeTime.toDouble()
            if (pct > 1) {
                nowY = realY
                pct = 1.0
            } else {
                pct = EaseUtils.easeOutExpo(pct)
            }
            GL11.glTranslated(0.0, (realY - nowY) * pct, 0.0)
        } else {
            animeYTime = nowTime
        }
        GL11.glTranslated(0.0, nowY.toDouble(), 0.0)

        //X-Axis Animation
        var pct = (nowTime - animeXTime) / animeTime.toDouble()
        when (fadeState) {
            FadeState.IN -> {
                if (pct > 1) {
                    fadeState = FadeState.STAY
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = EaseUtils.easeOutExpo(pct)
            }

            FadeState.STAY -> {
                pct = 1.0
                if ((nowTime - animeXTime) > time) {
                    fadeState = FadeState.OUT
                    animeXTime = nowTime
                }
            }

            FadeState.OUT -> {
                if (pct > 1) {
                    fadeState = FadeState.END
                    animeXTime = nowTime
                    pct = 1.0
                }
                pct = 1 - EaseUtils.easeInExpo(pct)
            }

            FadeState.END -> {
                return true
            }
        }
        GL11.glTranslated(width - (width * pct), 0.0, 0.0)
        GL11.glTranslatef(-width.toFloat(), 0F, 0F)
        when (mode.toLowerCase()) {
            "tenacity" -> {
                var text = ""
                var n2: Int = Fonts.tenacitybold35.getStringWidth(content)
                var textLength = Math.max(n2, 0)
                val width2= textLength.toFloat() + 85.0f

                when(type){
                    NotifyType.SUCCESS->text = "o"
                    NotifyType.ERROR->text = "p"
                    NotifyType.INFO->text = "m"
                    NotifyType.WARNING->text = "r"
                }
                RoundedUtil.drawRound(38F,0F,width2 - 30F,28F,6F,type.renderColor)
                Fonts.tenacitycheck60.drawString(text,42F,9F,Color.WHITE.rgb)
                Fonts.font43.drawString(title,60F,3f,Color.white.rgb)
                Fonts.font35.drawString(content,60f,16f,Color.white.rgb)
            }
            "rect" -> {
                val height2 = 20
                val width2 = font.getStringWidth(content) + 53
                val hud = LiquidBounce.moduleManager.getModule(HUD::class.java) as HUD
                GL11.glScaled(pct,pct,pct)
                GL11.glTranslatef(-width2.toFloat()/2 , -height2.toFloat()/2, 0F)
                RenderUtils.drawGradientSideways(0.0, height2 - 1.7,
                    (width2 * ((nowTime - displayTime) / (animeTime * 2F + time))).toDouble(), height2.toDouble(), Color(hud.r.get(),hud.g.get(),hud.b.get()).rgb, Color(hud.r2.get(),hud.g2.get(),hud.b2.get()).rgb)
                font.drawString(
                    content + " (" + BigDecimal(((time - time * ((nowTime - displayTime) / (animeTime * 2F + time))) / 1000).toDouble()).setScale(1, BigDecimal.ROUND_HALF_UP).toString() + "s)",
                    9.8f, 6.5f, Color.WHITE.rgb,true)
                GlStateManager.resetColor()
                RenderUtils.drawShadow(0f, 0f, 110f, 20f)
            }
            "fdp" -> {
                var transY = nowY.toDouble()
                val textColor: Int = if (whiteText.get()) {
                    Color(255, 255, 255).rgb
                } else {
                    Color(10, 10, 10).rgb
                }
                val transX = width - (width * pct) - width
                if (blurValue.get() != 0f) {
                BlurUtils.draw(
                    4 + (0f + transX).toFloat() * 1F,
                    (0f + transY).toFloat() * 1F,
                    (width * 1F),
                    (27f - 5f) * 1F,
                    blurValue.get()
                )
            }

                val colors = Color(0, 0, 0, alpha.get() / 4)
            if (motionBlur.get()) {
                when (fadeState) {
                    FadeState.IN -> {
                        RenderUtils.drawRoundedCornerRect(3f, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
                        RenderUtils.drawRoundedCornerRect(3F, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
                    }

                    FadeState.STAY -> {
                        RenderUtils.drawRoundedCornerRect(3f, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
                        RenderUtils.drawRoundedCornerRect(3F, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
                    }

                    FadeState.OUT -> {
                        RenderUtils.drawRoundedCornerRect(4F, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
                        RenderUtils.drawRoundedCornerRect(5F, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
                    }

                    FadeState.END -> return false
                }
            } else {
                RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
                RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, width.toFloat(), 27f - 5f, 0f, colors.rgb)
            }
                    RenderUtils.drawRoundedCornerRect(0F + 3f, 0F, width.toFloat(), 27f - 5f,0f, colors.rgb)
                RenderUtils.drawShadowWithCustomAlpha(0F + 3f, 0F, width.toFloat() -3f, 27f - 5f, 240f)
            RenderUtils.drawRoundedCornerRect(
                0F + 3f,
                0F,
                max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)) + 5f, 0F),
                27f - 5f,
                2f,
                Color(0, 0, 0, 40).rgb
            )
                    Fonts.font27.drawString(title, 5.5F, 3.5F, textColor, false)
                Fonts.font20.drawString(content, 5.5F, 11.5F, textColor, false)
        }
            "noteless"->{
                RenderUtils.drawShadow((-22).toFloat(), 0F, (width + 22).toFloat(), height.toFloat())
                RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), type.renderColor)
                RenderUtils.drawRect(-22F, 0F, width.toFloat(), height.toFloat(), Color(0, 0, 0, 150))
                RenderUtils.drawRect(
                    -22F,
                    height - 2F,
                    max(width - width * ((nowTime - displayTime) / (animeTime * 2F + time)), -22F),
                    height.toFloat(),
                    type.renderColor
                )
                Fonts.font35.drawString(title, 7F, 4F, -1)
                Fonts.font30.drawString(content, 7F, 17F, -1)
                RenderUtils.drawImage(image, -19,  3, 22, 22)
            }
            "old"->{
                //-了4f（间距
                RoundedUtil.drawRound(-22f,-4f,width.toFloat()+10f,height.toFloat()-3f,14f,Color.WHITE)
                val color = Color(255, 192, 203)//黑色
                val color2 = Color(255, 192, 203) //粉色
                val color3 = Color(255, 255, 255) //白色
                Fonts.font35.drawStringWithShadow(title, 7, 4-4, color.getRGB())
                Fonts.font30.drawString(content, 7f, 17f-4, color.getRGB())
                var text = ""
                when(type){
                    NotifyType.SUCCESS->text = "A"
                    NotifyType.ERROR->text = "B"
                    NotifyType.INFO->text = "C"
                    NotifyType.WARNING->text = "D"
                }
                // ABCD
                // √ x i !
                // 🤣🤣🤣🤣🤣我都没告诉你你咋知道的？
                Fonts.noti80.drawString(text, -16f,  8f-4f,color.getRGB())
            }
        }

        MinecraftInstance.classProvider.getGlStateManager().resetColor()
        return false

            }
}

enum class NotifyType(var renderColor: Color) {
    SUCCESS(Color(0x60E066)),
    ERROR(Color(0xFF2F3A)),
    WARNING(Color(0xF5FD00)),
    INFO(Color(0x6490A7));
}


enum class FadeState { IN, STAY, OUT, END }