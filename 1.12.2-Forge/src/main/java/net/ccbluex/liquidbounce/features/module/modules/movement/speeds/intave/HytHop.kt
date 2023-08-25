package net.ccbluex.liquidbounce.features.module.modules.movement.speeds.intave

import net.ccbluex.liquidbounce.event.MoveEvent
import net.ccbluex.liquidbounce.features.module.modules.movement.speeds.SpeedMode
import net.ccbluex.liquidbounce.utils.MovementUtils

class HytHop : SpeedMode("HytHop"){
    override fun onMotion() {
    }

    override fun onUpdate() {
        mc.gameSettings.keyBindJump.pressed = mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)
        if (MovementUtils.isMoving) {
            if (mc.thePlayer!!.onGround) {
                mc.gameSettings.keyBindJump.pressed = false
                mc.timer.timerSpeed = 1.0f
                mc.thePlayer!!.jump()
            }
            
             if (mc.thePlayer!!.motionY > 0.003) {
                mc.thePlayer!!.motionX *= 1.0015
                mc.thePlayer!!.motionZ *= 1.0015
                mc.timer.timerSpeed = 1.06f
             }
        }
       
    }

    override fun onMove(event: MoveEvent) {}
}