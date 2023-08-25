/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.feng.FontDrawer
import net.ccbluex.liquidbounce.feng.FontLoaders
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.minecraft.client.gui.ScaledResolution
import skid.render.RoundedUtil
import java.awt.Color
import java.text.DecimalFormat
import kotlin.math.pow

@ModuleInfo(name = "HUD", description = "Toggles visibility of the HUD.",chinesename = "页面", category = ModuleCategory.RENDER, array = false)
class HUD : Module() {
    private val hotbar = BoolValue("Hotbar", true)
    val ColorItem = BoolValue("HotbarRect", true)
    val hotbarEaseValue = BoolValue("HotbarEase", true)
    val inventoryParticle = BoolValue("InventoryParticle", true)
    val hueInterpolation = BoolValue("Hue Interpolate", true)
    val hideHotBarValue = BoolValue("HideHotbar", false)
    val betterHotbarValue = BoolValue("BetterHotbar", true)
    val fontChatValue = BoolValue("FontChat", true)
    val chatRect = BoolValue("ChatRect", true)
    val chatAnimValue = BoolValue("ChatAnimation", true)
    private val RenderArmor = BoolValue("RenderArmor", true)
    private val OtherRender = BoolValue("OtherRender", true)
    val blurValue = BoolValue("Blur", false)
    val r = IntegerValue("Red", 255, 0, 255)
    val g = IntegerValue("Green", 255, 0, 255)
    val b = IntegerValue("Blue", 255, 0, 255)

    val r2 = IntegerValue("Red2", 255, 0, 255)
    val g2 = IntegerValue("Green2", 255, 0, 255)
    val b2 = IntegerValue("Blue2", 255, 0, 255)
    val BlurStrength = FloatValue("BlurStrength", 15F, 0f, 30F)//这是模糊°
    val Radius = IntegerValue("BlurRadius", 10 , 1 , 50 )
    var High1123 = 0.0
    private var easingValue = 0
    fun getHotbar(): BoolValue {
        return hotbar
    }

    val sr = ScaledResolution(mc2)
    val left: Int = sr.getScaledWidth() / 2 + 91
    val top: Int = sr.getScaledHeight() - 100
    val x = 380
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        if (classProvider.isGuiHudDesigner(mc.currentScreen))
            return
        if (!mc2.ingameGUI.chatGUI.chatOpen) {
            if (OtherRender.get()) {
                High1123 = if (mc2.ingameGUI.chatGUI.chatOpen) {
                    (RenderUtils.height() - 35).toDouble()
                } else {
                    if (mc.playerController.isNotCreative && RenderArmor.get()) {
                        onArmor(mc.thePlayer!!) }
                    (RenderUtils.height() - 20).toDouble()
                }
                (RenderUtils.height() - 20).toDouble()
            }
        }
        LiquidBounce.hud.render(false)
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        LiquidBounce.hud.update()
    }
    fun getHotbarEasePos(x: Int): Int {
        if (!state || !hotbarEaseValue.get()) return x
        easingValue = x
        return easingValue
    }
    private fun onArmor(target: IEntityLivingBase) {
    }
    @EventTarget
    fun onKey(event: KeyEvent) {
        LiquidBounce.hud.handleKey('a', event.key)
    }

    @EventTarget(ignoreCondition = true)
    fun onScreen(event: ScreenEvent) {
        if (mc.theWorld == null || mc.thePlayer == null) return
        if (state && blurValue.get() && !mc.entityRenderer.isShaderActive() && event.guiScreen != null &&
            !(classProvider.isGuiChat(event.guiScreen) || classProvider.isGuiHudDesigner(event.guiScreen))) mc.entityRenderer.loadShader(classProvider.createResourceLocation("liquidbounce" + "/blur.json")) else if (mc.entityRenderer.shaderGroup != null &&
            mc.entityRenderer.shaderGroup!!.shaderGroupName.contains("liquidbounce/blur.json")) mc.entityRenderer.stopUseShader()
    }

    init {
        state = true
    }
}