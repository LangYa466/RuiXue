package me.tiangong

import net.ccbluex.liquidbounce.utils.render.RenderUtils

object AnimationUtils {
    var rotateDirection = 0f
    var delta = 0.0
    fun lstransition(now: Float, desired: Float, speed: Double): Float {
        val dif = Math.abs(desired - now).toDouble()
        val a = Math.abs((desired - (desired - Math.abs(desired - now))) / (100 - speed * 10)).toFloat()
        var x = now
        if (dif > 0) {
            if (now < desired) x += a * RenderUtils.deltaTime else if (now > desired) x -= a * RenderUtils.deltaTime
        } else x = desired
        if (Math.abs(desired - x) < 10.0E-3 && x != desired) x = desired
        return x
    }

    fun getAnimationState(animation: Float, finalState: Float, speed: Float): Float {
        var animation = animation
        val add = (delta * (speed / 1000f)).toFloat()
        if (animation < finalState) {
            if (animation + add < finalState) {
                animation += add
            } else {
                animation = finalState
            }
        } else if (animation - add > finalState) {
            animation -= add
        } else {
            animation = finalState
        }
        return animation
    }

    fun smoothAnimation(ani: Float, finalState: Float, speed: Float, scale: Float): Float {
        return getAnimationState(ani, finalState, (Math.max(10f, Math.abs(ani - finalState) * speed) * scale))
    }
}