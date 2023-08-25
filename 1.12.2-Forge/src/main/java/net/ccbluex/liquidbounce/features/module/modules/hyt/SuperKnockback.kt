/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.Rotation
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.network.play.client.CPacketEntityAction
import skid.PacketUtils

@ModuleInfo(name = "SuperKnockback", category = ModuleCategory.COMBAT, description = "Superkb", chinesename = "超级击退")
class SuperKnockback : Module() {

    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val modeValue = ListValue("Mode", arrayOf("Wtap", "Legit", "Silent", "SprintReset", "SneakPacket"), "Silent")
    private val onlyMoveValue = BoolValue("OnlyMove", true)
    private val onlyMoveForwardValue = BoolValue("OnlyMoveForward", true)
    private val onlyGroundValue = BoolValue("OnlyGround", false)
    private val delayValue = IntegerValue("Delay", 0, 0, 500)

    private var ticks = 0

    val timer = MSTimer()

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (event.targetEntity is EntityLivingBase) {
            if (event.targetEntity.hurtTime > hurtTimeValue.get() || !timer.hasTimePassed(delayValue.get().toLong()) ||
                (!MovementUtils.isMoving && onlyMoveValue.get()) || (!mc.thePlayer!!.onGround && onlyGroundValue.get())) {
                return
            }

            if (onlyMoveForwardValue.get() && RotationUtils.getRotationDifference(Rotation(MovementUtils.movingYaw, mc.thePlayer!!.rotationPitch), Rotation(mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch)) > 35) {
                return
            }

            when (modeValue.get().toLowerCase()) {

                "wtap" ->  ticks = 2


                "legit" -> {
                    ticks = 2
                }

                "silent" -> {
                    ticks = 1
                }

                "sprintreset" -> {
                    PacketUtils.send(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.STOP_SPRINTING))
                }

                "sneakpacket" -> {
                    if (mc.thePlayer!!.sprinting) {
                        mc.thePlayer!!.sprinting = true
                    }
                    PacketUtils.send(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.STOP_SPRINTING))
                    PacketUtils.send(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.START_SNEAKING))
                    PacketUtils.send(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.START_SPRINTING))
                    PacketUtils.send(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.STOP_SNEAKING))
                    mc.thePlayer!!.serverSprintState = true
                }
            }
            timer.reset()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (modeValue.equals("Legit")) {
            if (ticks == 2) {
                mc.gameSettings.keyBindForward.pressed = false
                ticks = 1
            } else if (ticks == 1) {
                mc.gameSettings.keyBindForward.pressed = true
                ticks = 0
            }
        }
        if (modeValue.equals("Wtap")) {
            if (ticks == 2) {
                mc.thePlayer!!.sprinting = false
                ticks = 1
            } else if (ticks == 1) {
                mc.thePlayer!!.sprinting = true
                ticks = 0
            }
        }
        if (modeValue.equals("Silent")) {
            if (ticks == 1) {
                PacketUtils.send(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.STOP_SPRINTING))
                ticks = 2
            } else if (ticks == 2) {
                PacketUtils.send(CPacketEntityAction(mc2.player, CPacketEntityAction.Action.START_SPRINTING))
                ticks = 0
            }
        }
    }

    override val tag: String
        get() = modeValue.get()
}