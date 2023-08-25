package me.tiangong

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.event.WorldEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayClient
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.server.*
import net.minecraft.network.play.server.SPacketEntity.*
import skid.PacketUtils
import java.util.*
import java.util.concurrent.LinkedBlockingQueue

@ModuleInfo(name = "GrimFullVelocity", description = "GrimFullVelocity", chinesename = "Grim反作弊反击退", category = ModuleCategory.VULGAR)
class GrimFullVelocity : Module() {

    private val cancelPacketValue = IntegerValue("GroundTicks",6,0,100)
    private val AirCancelPacketValue = IntegerValue("AirTicks",6,0,100)
    private val OnlyGround = BoolValue("OnlyGround",false)
    private val OnlyMove = BoolValue("OnlyMove",true)
    private val TestNoMove = BoolValue("TestNoMove",true)
    private val CancelS12 = BoolValue("CancelS12",true)
    private val CancelSpacket = BoolValue("CancelSpacket",true)
    private val CancelSpacket1 = BoolValue("CancelSpacket1",false)
    private val CancelCpacket = BoolValue("CancelCpacket",false)
    private val CancelCpacket1 = BoolValue("CancelCpacket1",false)
    private val TestValue = BoolValue("Test",false)
    private val TestValue1 = IntegerValue("TestValue",4,0,100)
    private val TestValue2 = BoolValue("Test1",false)
    private val Safe = BoolValue("SafeMode",false)
    private val AutoDisable = ListValue("AutoDisable", arrayOf("Normal","Silent"), "Normal")
    private val SilentTicks = IntegerValue("AutoDisableSilentTicks",4,0,100)
    private val DeBug = BoolValue("Debug",false)
    private var resetPersec = 8
    private var grimTCancel = 0
    private var updates = 0
    private var S08 = 0
    private val packets = LinkedBlockingQueue<IPacket>()
    private val inBus = LinkedList<Packet<INetHandlerPlayClient>>()
    private val inBus1 = LinkedList<Packet<INetHandlerPlayClient>>()
    private val outBus = LinkedList<Packet<INetHandlerPlayServer>>()
    private var disableLogger = false
    override fun onEnable() {
        inBus.clear()
        inBus1.clear()
        outBus.clear()
        grimTCancel = 0
        S08 = 0
       // val HytDisabler = LiquidBounce.moduleManager.getModule(HytDisabler::class.java) as HytDisabler
        //   HytDisabler.modeValue.set("HytSpartan")
    //    HytDisabler.state = true
    }
    override fun onDisable(){
        while (inBus.size > 0) {
            inBus.poll()?.processPacket(mc2.connection)
        }
        while (inBus1.size > 0) {
            inBus.poll()?.processPacket(mc2.connection)
        }
        while (outBus.size > 0) {
            val upPacket = outBus.poll() ?: continue
            PacketUtils.sendPacketNoEvent(upPacket)
            if (DeBug.get()) {
            }
        }
        S08 = 0
        blink()
        inBus1.clear()
        outBus.clear()
        inBus.clear()
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return
        val packet = event.packet
        val packet1 = event.packet.unwrap()
        if(packet1 is SPacketPlayerPosLook){
           if(AutoDisable.get().equals("Normal",true)){
               state = false
           }
            if(AutoDisable.get().equals("Silent",true)){
                S08 = SilentTicks.get()
            }
        }
        if ((OnlyGround.get() && !thePlayer.onGround) || (OnlyMove.get() && !MovementUtils.isMoving) || S08 != 0) {
            return
        }
        if(packet1 is SPacketEntityVelocity){
            val packetEntityVelocity = packet.asSPacketEntityVelocity()
            if (((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer ))
                return
            if(TestValue2.get()){
                inBus.add(packet1 as Packet<INetHandlerPlayClient>)
            }
        }

        if (classProvider.isSPacketEntityVelocity(packet)) {
            val packetEntityVelocity = packet.asSPacketEntityVelocity()

            if (((mc.theWorld?.getEntityByID(packetEntityVelocity.entityID) ?: return) != thePlayer ) || (Safe.get() && grimTCancel != 0))
                return
            if(TestNoMove.get()){
                if (CancelS12.get()) {
                    if(MovementUtils.isMoving) {
                        if (DeBug.get()) {
                        }
                        event.cancelEvent()
                    }else {
                        if (thePlayer.onGround) {
                            if (DeBug.get()) {
                            }
                            packetEntityVelocity.motionX = 0
                            packetEntityVelocity.motionY = 0
                            packetEntityVelocity.motionZ = 0
                        }else{
                            if (DeBug.get()) {
                            }
                            event.cancelEvent()
                        }
                    }
                }
            }else {
                if (CancelS12.get()) {
                    if (DeBug.get()) {
                    }
                    event.cancelEvent()
                }
            }
            if (thePlayer.onGround) {
                grimTCancel =  cancelPacketValue.get()
            } else {
                grimTCancel = AirCancelPacketValue.get()
            }
        }
        if (CancelSpacket.get()) {
            if (packet1 !is SPacketConfirmTransaction && (packet1::class.java!!.getSimpleName()
                    .startsWith("S", true)) && (grimTCancel > 0)
            ) {
                if ((mc.theWorld?.getEntityByID(packet.asSPacketEntityVelocity().entityID)
                        ?: return) == thePlayer
                ) {
                    return
                }
                event.cancelEvent()
                inBus.add(packet1 as Packet<INetHandlerPlayClient>)
                grimTCancel--
            }
            if (packet1 is SPacketConfirmTransaction && (grimTCancel > 0)) {
                event.cancelEvent()
                if (DeBug.get()) {
                }
            }
        }
        if (CancelSpacket1.get()) {
            if (((grimTCancel > 0))&& ((packet1 is S17PacketEntityLookMove)||(packet1 is S16PacketEntityLook)||(packet1 is S15PacketEntityRelMove)||(packet1 is SPacketEntityAttach)||(packet1 is SPacketEntityTeleport)||(packet1 is SPacketEntity)|| (packet1 is SPacketEntityVelocity&&(mc.theWorld?.getEntityByID(SPacketEntityVelocity().entityID) ?: return) != thePlayer))) {
                event.cancelEvent()
                inBus.add(packet1 as Packet<INetHandlerPlayClient>)
            }
            if (packet1 is SPacketConfirmTransaction&& ((grimTCancel > 0))) {
                event.cancelEvent()
                if(TestValue.get()){
                    if(grimTCancel <= TestValue1.get()){
                        inBus.add(packet1 as Packet<INetHandlerPlayClient>)
                        if (DeBug.get()) {
                        }
                    }
                }
                if(TestValue2.get()){
                    inBus.add(packet1 as Packet<INetHandlerPlayClient>)
                }
                grimTCancel--
                if (DeBug.get()) {
                }
            }
        }
        if (CancelCpacket.get()) {
            if (packet1 !is CPacketConfirmTransaction && packet1::class.java.getSimpleName()
                    .startsWith("C", true) && (grimTCancel > 0)
            ) {
                event.cancelEvent()
                grimTCancel--
                outBus.add(packet1 as Packet<INetHandlerPlayServer>)
            }
            if (packet1 is SPacketConfirmTransaction && (grimTCancel > 0)) {
                event.cancelEvent()
                if (DeBug.get()) {
                }
            }
        }
        if(CancelCpacket1.get()){
            if (classProvider.isCpacket(packet)&& (grimTCancel > 0) ) {
                if (mc.thePlayer == null || disableLogger)
                    return
                event.cancelEvent()
                packets.add(packet)
            }
            if (packet1 is SPacketConfirmTransaction && (grimTCancel > 0)) {
                event.cancelEvent()
                grimTCancel--
                if (DeBug.get()) {
                }
            }
        }
        if(TestValue2.get()){

        }
    }
    @EventTarget
    fun onWorld(event: WorldEvent) {
        outBus.clear()
        inBus.clear()
    }
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(S08 > 0){
            if (DeBug.get()) {
            }
            S08--
        }
        mc.netHandler ?: return
        if ((!inBus.isEmpty()&& grimTCancel == 0)||S08>0) {
            while (inBus.size > 0) {
                inBus.poll()?.processPacket(mc2.connection)
                if (DeBug.get()) {
                }
            }
        }

        if (!outBus.isEmpty() && grimTCancel == 0 ) {
            while (outBus.size > 0) {
                val upPacket = outBus.poll() ?: continue
                PacketUtils.sendPacketNoEvent(upPacket)
                if (DeBug.get()) {
                }
            }
        }
        if(grimTCancel == TestValue1.get()){
            blink()
        }
        updates++
        if (resetPersec > 0) {
            if (updates >= 0 || updates >= resetPersec) {
                updates = 0
                if (grimTCancel > 0){
                    grimTCancel--
                }
            }
        }
    }
    override val tag: String
        get() = packets.size.toString()

    private fun blink() {
        try {
            disableLogger = true

            while (!packets.isEmpty()) {
                mc.netHandler.networkManager.sendPacket(packets.take())
            }

            disableLogger = false
        } catch (e: Exception) {
            e.printStackTrace()
            disableLogger = false
        }

    }
}