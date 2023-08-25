/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.utils

import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

object MovementUtils : MinecraftInstance() {

    val movingYaw: Float
        get() = (direction * 180f / Math.PI).toFloat()
    fun setMotion(speed: Double) {
        var forward = mc.thePlayer!!.movementInput.moveForward.toDouble()
        var strafe = mc.thePlayer!!.movementInput.moveStrafe.toDouble()
        var yaw = mc.thePlayer!!.rotationYaw
        if (forward == 0.0 && strafe == 0.0) {
            mc.thePlayer!!.motionX = 0.0
            mc.thePlayer!!.motionZ = 0.0
        } else {
            if (forward != 0.0) {//1
                if (strafe > 0.0) {
                    yaw += (if (forward > 0.0) -45 else 45).toFloat()
                } else if (strafe < 0.0) {
                    yaw += (if (forward > 0.0) 45 else -45).toFloat()
                }
                strafe = 0.0
                if (forward > 0.0) {
                    forward = 1.0
                } else if (forward < 0.0) {
                    forward = -1.0
                }
            }
            val cos = Math.cos(Math.toRadians(yaw + 90.0f.toDouble()))
            val sin = Math.sin(Math.toRadians(yaw + 90.0f.toDouble()))
            mc.thePlayer!!.motionX = (forward * speed * cos
                    + strafe * speed * sin)
            mc.thePlayer!!.motionZ = (forward * speed * sin
                    - strafe * speed * cos)
        }
    }
    val speed: Float
        get() = sqrt(mc.thePlayer!!.motionX * mc.thePlayer!!.motionX + mc.thePlayer!!.motionZ * mc.thePlayer!!.motionZ).toFloat()
    @JvmStatic
    fun getScaffoldRotation(yaw: Float, strafe: Float): Float {
        var rotationYaw = yaw
        rotationYaw += 180f
        val forward = -0.5f
        if (strafe < 0f) rotationYaw -= 90f * forward
        if (strafe > 0f) rotationYaw += 90f * forward
        return rotationYaw
    }
    @JvmStatic
    val isMoving: Boolean
        get() = mc.thePlayer != null && (mc.thePlayer!!.movementInput.moveForward != 0f || mc.thePlayer!!.movementInput.moveStrafe != 0f)

    fun hasMotion(): Boolean {
        return mc.thePlayer!!.motionX != 0.0 && mc.thePlayer!!.motionZ != 0.0 && mc.thePlayer!!.motionY != 0.0
    }
    fun isOnGround(height: Double): Boolean {
        return if (!mc.theWorld!!.getCollidingBoundingBoxes(mc.thePlayer!!, mc.thePlayer!!.entityBoundingBox.offset(0.0, -height, 0.0)).isEmpty()) {
            true
        } else {
            false
        }
    }
    @JvmStatic
    @JvmOverloads
    fun strafe(speed: Float = this.speed) {
        if (!isMoving) return
        val yaw = direction
        val thePlayer = mc.thePlayer!!
        thePlayer.motionX = -sin(yaw) * speed
        thePlayer.motionZ = cos(yaw) * speed
    }

    @JvmStatic
    fun forward(length: Double) {
        val thePlayer = mc.thePlayer!!
        val yaw = Math.toRadians(thePlayer.rotationYaw.toDouble())
        thePlayer.setPosition(thePlayer.posX + -sin(yaw) * length, thePlayer.posY, thePlayer.posZ + cos(yaw) * length)
    }

    @JvmStatic
    val direction: Double
        get() {
            val thePlayer = mc.thePlayer!!
            var rotationYaw = thePlayer.rotationYaw
            if (thePlayer.moveForward < 0f) rotationYaw += 180f
            var forward = 1f
            if (thePlayer.moveForward < 0f) forward = -0.5f else if (thePlayer.moveForward > 0f) forward = 0.5f
            if (thePlayer.moveStrafing > 0f) rotationYaw -= 90f * forward
            if (thePlayer.moveStrafing < 0f) rotationYaw += 90f * forward
            return Math.toRadians(rotationYaw.toDouble())
        }
}