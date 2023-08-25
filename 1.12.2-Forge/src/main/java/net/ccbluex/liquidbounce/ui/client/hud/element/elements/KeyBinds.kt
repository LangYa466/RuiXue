package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import skid.ShadowUtils
import java.awt.Color

@ElementInfo(name = "按键显示")
class KeyBinds : Element() {
    val onlyState = BoolValue("OnlyModuleState", false)
    private val radiusValue = FloatValue("Radius", 4.25f, 0f, 10f)
    private var anmitY = 0F
    override val drawElement: Border
        get() {
        var y2 = 0
        anmitY = RenderUtils.getAnimationState2(anmitY.toDouble(),(17 + getmoduley()).toFloat().toDouble(), 200.0).toFloat()


        //draw Background
        RenderUtils.drawRoundedRect(
            0f,
            0f,
            114f,
            anmitY,
            radiusValue.get(),
            Color(32, 30, 30).rgb
        )
        GL11.glTranslated(-renderX, -renderY, 0.0)
        GL11.glScalef( 1F,  1F,  1F)


            GL11.glPushMatrix()
            GL11.glTranslated(renderX, renderY, 0.0)
            GL11.glScalef(scale, scale, scale)
            GlStateManager.enableBlend()
            GlStateManager.disableTexture2D()
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
            RenderUtils.fastRoundedRect(                0f,
                0f,
                114f,
                anmitY,
                0f
            )
            GlStateManager.enableTexture2D()
            GlStateManager.disableBlend()
            GL11.glPopMatrix()
        GL11.glPopMatrix()
        GL11.glScalef(scale, scale, scale)
        GL11.glTranslated(renderX, renderY, 0.0)



        //draw Title
        val fwidth = 114f/2F-(Fonts.font40.getStringWidth("KeyBinds")/2F)
        Fonts.font40.drawString("KeyBinds", fwidth, 4.5f, -1, true)

        //draw Module Bind
        for (module in LiquidBounce.moduleManager.modules) {
            if (module.keyBind == 0) continue
            if (onlyState.get()) {
                if (!module.state) continue
            }
            Fonts.font30.drawString(module.name, 3f, y2 + 19f, -1, true)
            Fonts.font30.drawString(
                if (module.state) "[True]" else "[False]",
                (108 - Fonts.font30.getStringWidth(  if (module.state)"[True]" else "[False]")).toFloat(),
                y2 + 21f,
                if (module.state) Color(255, 255, 255).rgb else Color(100, 100, 100).rgb,
                true
            )
            y2 += 12
        }
        return Border(0f, 0f, 114f, (17 + getmoduley()).toFloat())
    }

    fun getmoduley(): Int {
        var y = 0
        for (module in LiquidBounce.moduleManager.modules) {
            if (module.keyBind == 0) continue
            if (onlyState.get()) {
                if (!module.state) continue
            }
            y += 12
        }
        return y
    }
}