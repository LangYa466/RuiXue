/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.movement

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.KillAura
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketAnimation
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketPlayer
import net.minecraft.network.play.client.CPacketUseEntity
import java.util.*

@ModuleInfo(name = "NoSlow", description = "真防", chinesename = "花雨庭无减速",
        category = ModuleCategory.MOVEMENT)
class NoSlow2 : Module() {

    private val newgrim = BoolValue("NoC09", true)
    private val blockForwardMultiplier = FloatValue("BlockForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val blockStrafeMultiplier = FloatValue("BlockStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val consumeForwardMultiplier = FloatValue("ConsumeForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val consumeStrafeMultiplier = FloatValue("ConsumeStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val bowForwardMultiplier = FloatValue("BowForwardMultiplier", 1.0F, 0.2F, 1.0F)
    private val bowStrafeMultiplier = FloatValue("BowStrafeMultiplier", 1.0F, 0.2F, 1.0F)

    private val blockpacket = BoolValue("BlockPacket", false)
    private val blockDelay = FloatValue("BlockPacketDelay", 0.0F, 0.0F, 20.0F)
    private val foodpacket = BoolValue("FoodPacket", false)
    private val vulcan = BoolValue("Vulcan", false)

    val Debug = BoolValue("Debug", true)
    var float = 0
    private val msTimer = MSTimer()

    private var packetBuf = LinkedList<Packet<INetHandlerPlayServer>>()
    private var nextTemp = false
    private var waitC03 = false

    @EventTarget
    private fun onPre(event : MotionEvent): Boolean {
        return event.eventState == EventState.PRE
    }

    @EventTarget
    private fun onPost(event : MotionEvent): Boolean {
        return event.eventState == EventState.POST
    }
    private val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
    private fun isBlock(): Boolean {
        return mc.thePlayer!!.isBlocking || killAura.blockingStatus
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        val thePlayer = mc.thePlayer ?: return
        val heldItem = thePlayer.heldItem
        if (this.foodpacket.get()) {
            if (heldItem == null || !classProvider.isItemFood(heldItem.item) || !MovementUtils.isMoving || !mc.gameSettings.keyBindUseItem.isKeyDown) {return}
            if(Debug.get()){
                ClientUtils.displayChatMessage("§7[§8§lNoSlow§7] §3FoodPacket(C09)")
            }
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange((mc.thePlayer!!.inventory.currentItem + 1) % 9))
            mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange((mc.thePlayer!!.inventory.currentItem) % 9))
        }
        if (this.newgrim.get()) {
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
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)))
                    }
            }
        }
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent){
        val thePlayer = mc.thePlayer ?: return
        val heldItem = thePlayer.heldItem
        val killAura = LiquidBounce.moduleManager[KillAura::class.java] as KillAura
        if(this.blockpacket.get()){
            float++
            if (heldItem != null) {
                if ( classProvider.isItemSword(heldItem.item) && MovementUtils.isMoving && (thePlayer.isBlocking||thePlayer.isUsingItem||mc.gameSettings.keyBindUseItem.isKeyDown||killAura.blockingStatus)) {
                    if(float > blockDelay.get()){
                        if(Debug.get()){
                            ClientUtils.displayChatMessage("§7[§8§lNoSlow§7] §3BlockPacket(C09)")
                        }
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange((mc.thePlayer!!.inventory.currentItem + 1) % 9))
                        mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange((mc.thePlayer!!.inventory.currentItem) % 9))
                        float = 0
                    }
                }
            }
        }
        if(this.vulcan.get()) {
            if (heldItem == null || !classProvider.isItemFood(heldItem.item) || !MovementUtils.isMoving || !mc.gameSettings.keyBindUseItem.isKeyDown) {return}
            if (msTimer.hasTimePassed(230) && nextTemp) {
                nextTemp = false
                if (packetBuf.isNotEmpty()) {
                    var canAttack = false
                    for (packet in packetBuf) {
                        if (packet is CPacketPlayer) {
                            canAttack = true
                        }
                        if (!((packet is CPacketUseEntity || packet is CPacketAnimation) && !canAttack)) {
                            mc.thePlayer!!.sendQueue.networkManager.unwrap().sendPacket(packet)
                        }
                    }
                    packetBuf.clear()
                }
            }
            if(!nextTemp) {
                nextTemp = true
                waitC03 = false
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

}
