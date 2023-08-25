package skid;

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.minecraft.network.Packet
import net.minecraft.network.play.INetHandlerPlayServer
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketEntityAction
import net.minecraft.network.play.client.CPacketHeldItemChange
import net.minecraft.network.play.client.CPacketKeepAlive

object PacketUtils : MinecraftInstance() {
    private val packets = ArrayList<Packet<INetHandlerPlayServer>>()

    @JvmStatic
    fun handleSendPacket(packet: Packet<*>): Boolean {
        if (packets.contains(packet)) {
            packets.remove(packet)
            return true
        }
        return false
    }

    @JvmStatic
    fun sendPacketNoEvent(packet: Packet<INetHandlerPlayServer>) {
        packets.add(packet)
        mc.netHandler.addToSendQueue(packet as IPacket)
    }

    @JvmStatic
    fun getPacketType(packet: Packet<*>): PacketType {
        val className=packet.javaClass.simpleName
        if(className.startsWith("C",ignoreCase = true)){
                return PacketType.CLIENTSIDE
        }else if(className.startsWith("S",ignoreCase = true)){
                return PacketType.SERVERSIDE
        }
        return PacketType.UNKNOWN
    }

    enum class PacketType {
        SERVERSIDE,
        CLIENTSIDE,
        UNKNOWN
    }
    fun send(cPacketEntityAction: CPacketEntityAction) {}
    fun send(cPacketKeepAlive: CPacketKeepAlive) {}
    fun send(packet: CPacketHeldItemChange){}
    fun send(cPacketConfirmTransaction: CPacketConfirmTransaction) {}
}