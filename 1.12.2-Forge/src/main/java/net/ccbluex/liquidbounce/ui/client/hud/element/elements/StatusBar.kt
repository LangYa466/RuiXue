package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.value.*
import skid.render.RoundedUtil
import java.awt.Color

@ElementInfo(name = "StatusBar")
class StatusBar(
) : Element() {
    val fontValue = FontValue("Font", Fonts.tenacitybold35)
    private var easingValue = 0
    private var easingHealth = 0f
    private var easingFood = 0f
    private var easingaromor = 0f
    private var easingExp= 0f
    override val drawElement: Border
        get() {
            val fontRenderer = fontValue.get()
            //血量
            fontRenderer.drawString(
                "Health:" + mc.thePlayer!!.health,
                2f,
                fontRenderer.fontHeight * 3f + 8f,
                Color.WHITE.rgb,
                true
            )
            RoundedUtil.drawRound(
                37F,
                25.5F,
                easingHealth / mc.thePlayer!!.maxHealth * 90.0f,
                6.0f,
                2F,
                Color(0, 95, 255)
            )
            //图标
            Fonts.ico2.drawString("s", 370F, 37F, Color(192, 192, 192, 255).rgb, true)
            return Border(0f, 0f, 10F, 20F)
        }
}