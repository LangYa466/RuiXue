/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.MotionEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo

@ModuleInfo(name = "NoAttackDelay", category = ModuleCategory.COMBAT, description = "", chinesename = "无攻击延迟")
object NoClickDelay : Module() {

    @EventTarget
    fun onMotion(event: MotionEvent) {
       // if (mc.thePlayer != null && mc.theWorld != null) {
        //    mc.rightClickDelayTimer = 0
        }
}