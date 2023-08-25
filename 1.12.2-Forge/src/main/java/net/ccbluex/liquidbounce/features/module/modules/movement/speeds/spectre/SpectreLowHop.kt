/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.spectre

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class SpectreLowHop : SpeedMode("SpectreLowHop") {
    override fun onMotion() {
        if (!MovementUtils.isMoving || mc.thePlayer!!.movementInput.jump) return
        if (mc.thePlayer!!.onGround) {
            MovementUtils.strafe(1.1f)
            mc.thePlayer!!.motionY = 0.15
            return
        }
        MovementUtils.strafe()
    }

    override fun onUpdate() {}
    override fun onMove(event: MoveEvent) {}
}