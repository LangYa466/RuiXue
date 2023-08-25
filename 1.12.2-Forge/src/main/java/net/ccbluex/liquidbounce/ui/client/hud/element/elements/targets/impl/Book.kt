/*
 * LiquidBounce+ Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/WYSI-Foundation/LiquidBouncePlus/
 */
package net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.impl

//import cc.paimon.LiquidBounce
import me.tiangong.CustomColor
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.client.entity.player.IEntityPlayer
import net.ccbluex.liquidbounce.features.module.modules.render.HUD
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Target
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.targets.TargetStyle
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.renderer.GlStateManager
import java.awt.Color
import kotlin.math.abs

class Book(inst: Target): TargetStyle("Book", inst, true) {

    val hurtTimeAnim = BoolValue("HurtTimeAnim", true)
    val borderColorMode = ListValue("Border-Color", arrayOf("Custom", "MatchBar", "None"), "None")
    val borderWidthValue = FloatValue("Border-Width", 3F, 0.5F, 5F)
    val borderRedValue = IntegerValue("Border-Red", 0, 0, 255)
    val borderGreenValue = IntegerValue("Border-Green", 0, 0, 255)
    val borderBlueValue = IntegerValue("Border-Blue", 0, 0, 255)
    val borderAlphaValue = IntegerValue("Border-Alpha", 0, 0, 255)

    private var lastTarget: IEntityLivingBase? = null

    override fun drawTarget(entity: IEntityLivingBase) {
        if (entity != lastTarget || easingHealth < 0 || easingHealth > entity.maxHealth ||
            abs(easingHealth - entity.health) < 0.01) {
            easingHealth = entity.health
        }

        val width = (38 + Fonts.font40.getStringWidth(entity.name!!))
                .coerceAtLeast(149)
                .toFloat()
        val color1 = Color(CustomColor.r.get(), CustomColor.g.get(),CustomColor.b.get(), 255)
        val color2 = Color(CustomColor.r2.get(), CustomColor.g2.get(), CustomColor.b2.get(), 255)
        val borderColor = getColor(Color(borderRedValue.get(), borderGreenValue.get(), borderBlueValue.get(), borderAlphaValue.get()))
        RenderUtils.drawGradientSideways(0.0,-1.1,width.toDouble(),1.1,color1.rgb,color2.rgb)
        // Draw rect box
        if (borderColorMode.get().equals("none", true))
            RenderUtils.drawRect(0F, 0F, width, 49f, targetInstance.bgColor.rgb)
        else
            RenderUtils.drawBorderedRect(0F, 0F, width, 49f, borderWidthValue.get(), if (borderColorMode.get().equals("matchbar", true)) targetInstance.barColor.rgb else borderColor.rgb, targetInstance.bgColor.rgb)
        RenderUtils.drawGradientSideways(4.0, 40.0, (easingHealth / entity.maxHealth) * (width-23.0),
                45.0, color1.rgb,color2.rgb)
        val stopPos=(5+((width-17-Fonts.sfbold30.getStringWidth(decimalFormat2.format(entity.maxHealth)))*(easingHealth/entity.maxHealth))).toInt()
        Fonts.sfbold30.drawString("${decimalFormat2.format(easingHealth)}", stopPos+5, 40, getColor(-1).rgb)

        updateAnim(entity.health)

        Fonts.font40.drawString("名称: "+entity.name!!, 42, 10, getColor(-1).rgb)
        Fonts.font35.drawString("距离: ${decimalFormat2.format(mc.thePlayer!!.getDistanceToEntityBox(entity))}", 42, 23, getColor(-1).rgb)

        // Draw info
        val playerInfo = mc.netHandler.getPlayerInfo(entity.uniqueID)
        if (playerInfo != null) {
//            Fonts.posterama35.drawString("Ping: ${playerInfo.responseTime.coerceAtLeast(0)}",
//                    36, 24, getColor(-1).rgb)

            // Draw head
            val locationSkin = playerInfo.locationSkin
            if (hurtTimeAnim.get()) {
                val scaleHT = (entity.hurtTime.toFloat() / entity.maxHurtTime.coerceAtLeast(1).toFloat()).coerceIn(0F, 1F)
                drawHead(locationSkin, 
                    2F + 15F * (scaleHT * 0.2F)+2f,
                    2F + 15F * (scaleHT * 0.2F)+2f,
                    1F - scaleHT * 0.2F, 
                    32, 32,
                    1F, 0.4F + (1F - scaleHT) * 0.6F, 0.4F + (1F - scaleHT) * 0.6F)
            } else
                drawHead(skin = locationSkin, width = 30, height = 30, alpha = 1F - targetInstance.getFadeProgress())
        }

        lastTarget = entity
    }

    override fun getBorder(entity: IEntityLivingBase?): Border? {
        entity ?: return Border(0F, 0F, 118F, 36F)
        val width = (38 + Fonts.font40.getStringWidth(entity.name!!))
                        .coerceAtLeast(118)
                        .toFloat()
        return Border(0F, 0F, width, 36F)
    }
}