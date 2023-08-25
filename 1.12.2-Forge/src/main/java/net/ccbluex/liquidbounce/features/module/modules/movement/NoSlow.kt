/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.*
import net.ccbluex.liquidbounce.api.minecraft.util.IEnumFacing
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.injection.backend.wrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.createUseItemPacket
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.item.ItemSword
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.*
import net.minecraft.network.play.server.SPacketHeldItemChange
import net.minecraft.util.EnumFacing
import skid.PacketUtils
import java.util.*

@ModuleInfo(
    name = "NoSlow", description = "Cancels slowness effects caused by soulsand and using items.",
    category = ModuleCategory.MOVEMENT,
    chinesename = "无减速"
)
class NoSlow : Module() {

    private val modeValue = ListValue(
        "PacketMode",
        arrayOf(
            "Grim反作弊2",
            "Grim反作弊",
            "Vanilla",
            "AAC",
            "AAC5",
            "Matrix",
            "Vulcan",
            "Custom",
            "SwitchItem"
        ),
        "Vanilla"
    )
    private val antiSwitchItem = BoolValue("AntiSwitchItem", false)
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)
    private val customOnGround = BoolValue("CustomOnGround", false)
    private val customDelayValue = IntegerValue("CustomDelay", 60, 10, 200)

    // Soulsand
    val soulsandValue = BoolValue("Soulsand", false)

    private val timer = MSTimer()
    private var pendingFlagApplyPacket = false
    private val msTimer = MSTimer()
    private var sendBuf = false
    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var nextTemp = false
    private var waitC03 = false
    private var lastBlockingStat = false

    private val killAura = LiquidBounce.moduleManager.getModule(KillAura::class.java) as KillAura

    private fun isBlock(): Boolean {
        return mc.thePlayer!!.isBlocking || killAura.blockingStatus
    }


    private fun onPre(event: MotionEvent): Boolean {
        return event.eventState == EventState.PRE
    }

    private fun onPost(event: MotionEvent): Boolean {
        return event.eventState == EventState.POST
    }

    private val isBlocking: Boolean
        get() = (mc.thePlayer!!.isUsingItem || (LiquidBounce.moduleManager[KillAura::class.java] as KillAura).blockingStatus) && mc.thePlayer!!.heldItem != null && mc.thePlayer!!.heldItem!!.item is ItemSword

    override fun onDisable() {
        timer.reset()
        msTimer.reset()
        pendingFlagApplyPacket = false
        sendBuf = false
        packetBuf.clear()
        nextTemp = false
        waitC03 = false
    }

    private fun sendPacket(
        event: MotionEvent,
        sendC07: Boolean,
        sendC08: Boolean,
        delay: Boolean,
        delayValue: Long,
        onGround: Boolean
    ) {
        val digging = classProvider.createCPacketPlayerDigging(
            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
            WBlockPos(-1, -1, -1),
            EnumFacing.DOWN.wrap()
        )
        val blockPlace =
            createUseItemPacket(WEnumHand.OFF_HAND)
        if (onGround && !mc.thePlayer!!.onGround) {
            return
        }

        if (sendC07 && onPre(event)) {
            if (delay && timer.hasTimePassed(delayValue)) {
                mc.netHandler.addToSendQueue(digging)
            } else if (!delay) {
                mc.netHandler.addToSendQueue(digging)
            }
        }
        if (sendC08 && onPost(event)) {
            if (delay && timer.hasTimePassed(delayValue)) {
                mc.netHandler.addToSendQueue(blockPlace)
                timer.reset()
            }
        }
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return

        if (!MovementUtils.isMoving) {
            return
        }

        when (modeValue.get().toLowerCase()) {
            "custom" -> {
                sendPacket(event, sendC07 = true, sendC08 = true,
                    delay = true, delayValue = customDelayValue.get().toLong(), onGround = customOnGround.get())
            }

            "vanilla" -> {
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionX
                mc.thePlayer!!.motionY = mc.thePlayer!!.motionY
                mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ
            }
            "Grim反作弊"->{
                if((event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) && !mc.thePlayer!!.isBlocking && classProvider.isItemFood(mc.thePlayer!!.heldItem!!.item) || classProvider.isItemPotion(mc.thePlayer!!.heldItem!!.item)){
                    if(mc.thePlayer!!.isUsingItem && mc.thePlayer!!.itemInUseCount >= 1){
                        mc2.connection!!.sendPacket(CPacketHeldItemChange((mc2.player.inventory.currentItem+1)%9))
                        mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                    }
                }
                if (event.eventState == EventState.PRE && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                        WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)))
                }
            }

            "Grim反作弊2" -> {
                val item = thePlayer.heldItem?.item
                if (classProvider.isItemBlock(item)) return

                if (event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) {
                    if (!classProvider.isItemSword(mc.thePlayer!!.inventory.getCurrentItemInHand()))
                        if (mc.thePlayer!!.isUsingItem && mc.thePlayer!!.itemInUseCount >= 1) {
                            mc2.connection!!.sendPacket(CPacketHeldItemChange((mc2.player.inventory.currentItem + 1) % 9))
                            mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                        }
                }
                if (event.eventState == EventState.PRE && mc.thePlayer!!.itemInUse != null && mc.thePlayer!!.itemInUse!!.item != null) {
                    if (classProvider.isItemSword(mc.thePlayer!!.inventory.getCurrentItemInHand()))
                        if (mc.thePlayer!!.isUsingItem || mc.thePlayer!!.isBlocking || isBlock()) {
                            mc.netHandler.addToSendQueue(
                                classProvider.createCPacketPlayerDigging(
                                    ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                                    WBlockPos.ORIGIN,
                                    classProvider.getEnumFacing(EnumFacingType.DOWN)
                                )
                            )
                        }
                }
            }

            "switchitem" -> {
                PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(thePlayer.inventory.currentItem % 8 + 1))
                PacketUtils.sendPacketNoEvent(CPacketHeldItemChange(thePlayer.inventory.currentItem))
            }

            "grimfix" -> {
                if (event.eventState == EventState.PRE && !thePlayer.isBlocking && !classProvider.isItemPotion(thePlayer.heldItem!!.item) && !classProvider.isItemFood(
                        thePlayer.heldItem!!.item
                    ) && !classProvider.isItemBow(thePlayer.heldItem!!.item)
                ) {
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                            WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                } else if ((event.eventState == EventState.PRE && thePlayer.itemInUse != null && thePlayer.itemInUse!!.item != null) && !thePlayer.isBlocking) {
                    if (thePlayer.isUsingItem && thePlayer.itemInUseCount >= 1) {
                        mc2.connection!!.sendPacket(CPacketHeldItemChange((mc2.player.inventory.currentItem + 1) % 9))
                        mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                    }
                }
                if (event.eventState == EventState.PRE && thePlayer.itemInUse != null && thePlayer.itemInUse!!.item != null) {
                    if (thePlayer.isUsingItem && thePlayer.itemInUseCount >= 1) {
                        mc2.connection!!.sendPacket(CPacketHeldItemChange((mc2.player.inventory.currentItem + 1) % 9))
                        mc2.connection!!.sendPacket(CPacketHeldItemChange(mc2.player.inventory.currentItem))
                    }
                }
            }

            "aac" -> {
                if (thePlayer.ticksExisted % 3 == 0) {
                    sendPacket(event, sendC07 = true, sendC08 = false,
                        delay = false, delayValue = 0, onGround = false)
                } else {
                    sendPacket(event, sendC07 = false, sendC08 = true,
                        delay = false, delayValue = 0, onGround = false)
                }
            }

            "aac5" -> {
                if (thePlayer.isUsingItem || thePlayer.isBlocking || isBlock()) {
                    mc.netHandler.addToSendQueue(
                        createUseItemPacket(
                            WEnumHand.MAIN_HAND
                        )
                    )
                    mc.netHandler.addToSendQueue(
                        createUseItemPacket(
                            WEnumHand.OFF_HAND
                        )
                    )
                }
            }
        }


    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet.unwrap()
        if (antiSwitchItem.get() && packet is SPacketHeldItemChange && (mc.thePlayer!!.isUsingItem || mc.thePlayer!!.isBlocking)) {
            event.cancelEvent()
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(packet.heldItemHotbarIndex))
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
        }

        if (modeValue.equals("Matrix") || modeValue.equals("Vulcan") && nextTemp) {
            if ((packet is CPacketPlayerDigging || packet is ICPacketPlayerBlockPlacement) && isBlocking) {
                event.cancelEvent()
            }
            event.cancelEvent()
        } else if (packet is CPacketPlayer || packet is CPacketAnimation || packet is CPacketEntityAction || packet is CPacketUseEntity || packet is CPacketPlayerDigging || packet is ICPacketPlayerBlockPlacement) {
            if (modeValue.equals("Vulcan") && waitC03 && packet is ICPacketPlayer) {
                waitC03 = false
                return
            }
            packetBuf.add(packet as Packet<INetHandlerPlayServer>)
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if ((modeValue.equals("Matrix") || modeValue.equals("Vulcan")) && (lastBlockingStat || isBlocking)) {
            if (msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false
                classProvider.createCPacketPlayerDigging(
                    ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                    WBlockPos(-1, -1, -1),
                    EnumFacing.DOWN as IEnumFacing
                )
                if (packetBuf.isNotEmpty()) {
                    var canAttack = false
                    for (packet in packetBuf) {
                        if (packet is CPacketPlayer) {
                            canAttack = true
                        }
                        if (!((packet is ICPacketUseEntity || packet is ICPacketAnimation) && !canAttack)) {
                            PacketUtils.sendPacketNoEvent(packet)
                        }
                    }
                    packetBuf.clear()
                }
            }
            if (!nextTemp) {
                lastBlockingStat = isBlocking
                if (!isBlocking) {
                    return
                }
                nextTemp = true
                waitC03 = modeValue.equals("Vulcan")
                msTimer.reset()
            }
        }
    }

    @EventTarget
    fun onSlowDown(event: SlowDownEvent) {
        val heldItem = mc.thePlayer!!.heldItem?.item

        event.forward = getMultiplier(heldItem, true)
        event.strafe = getMultiplier(heldItem, false)
    }

    private fun getMultiplier(item: IItem?, isForward: Boolean): Float {
        return when {
            classProvider.isItemFood(item) || classProvider.isItemPotion(item) || classProvider.isItemBucketMilk(item) -> {
                if (isForward) this.consumeForwardMultiplier.get() else this.consumeStrafeMultiplier.get()
            }

            classProvider.isItemSword(item) -> {
                if (isForward) this.blockForwardMultiplier.get() else this.blockStrafeMultiplier.get()
            }

            classProvider.isItemBow(item) -> {
                if (isForward) this.bowForwardMultiplier.get() else this.bowStrafeMultiplier.get()
            }

            else -> 0.2F
        }
    }

    override val tag: String
        get() = modeValue.get()

}
