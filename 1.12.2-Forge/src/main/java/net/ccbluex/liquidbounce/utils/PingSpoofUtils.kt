package net.ccbluex.liquidbounce.utils

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.utils.timer.TimeUtils

object PingSpoofUtils : Listenable{
    private val packetQueue = hashMapOf<IPacket, Long>()
    private var isPingSpoof = false
    private var isPingSpoofSendPacket = true
    private var maxDelay = 0
    private var minDelay = 0

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if(!isPingSpoof) packetQueue.clear()
        if (isPingSpoof) {
            val packet = event.packet

            if ((MinecraftInstance.classProvider.isCPacketKeepAlive(packet) || MinecraftInstance.classProvider.isCPacketClientStatus(packet))
                    && !(MinecraftInstance.mc.thePlayer!!.isDead || MinecraftInstance.mc.thePlayer!!.health <= 0) && !packetQueue.containsKey(packet)) {
                event.cancelEvent()

                synchronized(packetQueue) {
                    packetQueue[packet] = System.currentTimeMillis() + TimeUtils.randomDelay(minDelay, maxDelay)
                }
            }
        }
    }

    fun setPingSpoofSendPacket(isStart: Boolean){
        isPingSpoofSendPacket = isStart
    }

    fun setDelay(max:Int,min:Int){
        maxDelay = max
        minDelay = min
    }

    fun setPingSpoof(isStart:Boolean){
        isPingSpoof = isStart
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if(!isPingSpoof) packetQueue.clear()
        if (isPingSpoof && isPingSpoofSendPacket) {
            synchronized(packetQueue) {
                packetQueue.filter {
                    it.value >= System.currentTimeMillis()
                }.forEach { (packet, time) ->
                    MinecraftInstance.mc.netHandler.addToSendQueue(packet)
                    packetQueue.remove(packet, time)
                }
            }
        }
    }
    override fun handleEvents() = isPingSpoof
}