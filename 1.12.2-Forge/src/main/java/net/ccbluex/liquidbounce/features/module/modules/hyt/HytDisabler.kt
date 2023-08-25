package net.ccbluex.liquidbounce.features.module.modules.hyt


import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketConfirmTransaction
import net.minecraft.network.play.client.CPacketKeepAlive
import skid.PacketUtils
import java.util.HashMap


@ModuleInfo(name = "HytDisabler", description = "修复版",chinesename = "花雨庭禁用器", category = ModuleCategory.VULGAR)
class HytDisabler : Module() {

    val modeValue = ListValue(
        "Mode",
        arrayOf(
            "花雨庭",
            "花雨庭2"
        ), "花雨庭"
    )

    private val minDelayValue: IntegerValue = object : IntegerValue("延迟发包最小时间", 500, 0, 5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxDelayValue = maxDelayValue.get()

            if (maxDelayValue < newValue)
                set(maxDelayValue)
        }
    }

    private val maxDelayValue: IntegerValue = object : IntegerValue("延迟发包最大时间", 1000, 0, 5000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minDelayValue = minDelayValue.get()

            if (minDelayValue > newValue)
                set(minDelayValue)
        }
    }

    // debug
    private val debugValue = BoolValue("日志", true)
    private val packetsMap = HashMap<IPacket, Long>()

    // variables
    private val keepAlives = arrayListOf<CPacketKeepAlive>()
    private val transactions = arrayListOf<CPacketConfirmTransaction>()
    private val msTimer = MSTimer()

    fun debug(s: String) {
        if (debugValue.get())
            ClientUtils.displayChatMessage("§7[§3§l花雨庭禁用器§7]§f $s")
    }
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        when (modeValue.get().toLowerCase()) {
            "花雨庭2" -> {
                if (packet is CPacketKeepAlive && (keepAlives.size <= 0 || packet != keepAlives[keepAlives.size - 1])) {
                    debug(LiquidBounce.CLIENT_NAME + "c00 added")
                    keepAlives.add(packet)
                    event.cancelEvent()
                }
                if (packet is CPacketConfirmTransaction && (transactions.size <= 0 || packet != transactions[transactions.size - 1])) {
                    debug(LiquidBounce.CLIENT_NAME + "c0f added")
                    transactions.add(packet)
                    event.cancelEvent()
                }
            }

            "花雨庭" -> {
                if ((classProvider.isCPacketKeepAlive(packet) || classProvider.isCPacketClientStatus(packet)) && !(mc.thePlayer!!.isDead || mc.thePlayer!!.health <= 0) && !packetsMap.containsKey(
                        packet
                    )
                ) {
                    event.cancelEvent()
                    debug("C0F-*1")
                    synchronized(packetsMap) {
                        packetsMap.put(
                            packet,
                            System.currentTimeMillis() + TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
                        )
                    }
                }
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    fun onUpdate(event: UpdateEvent) {
        when (modeValue.get().toLowerCase()) {
            "花雨庭" -> {
                try {
                    synchronized(packetsMap) {
                        val iterator = packetsMap.entries.iterator()

                        while (iterator.hasNext()) {
                            val entry = iterator.next()

                            if (entry.value < System.currentTimeMillis()) {
                                mc.netHandler.addToSendQueue(entry.key)
                                iterator.remove()
                            }
                        }
                    }
                } catch (t: Throwable) {
                    t.printStackTrace()
                }
                when (modeValue.get().toLowerCase()) {
                    "花雨庭2" -> {
                        if (msTimer.hasTimePassed(3000L) && keepAlives.size > 0 && transactions.size > 0) {
                            PacketUtils.send(keepAlives[keepAlives.size - 1])
                            PacketUtils.send(transactions[transactions.size - 1])

                            debug(LiquidBounce.CLIENT_NAME + "c00 no.${keepAlives.size - 1} sent.")
                            debug(LiquidBounce.CLIENT_NAME + "c0f no.${transactions.size - 1} sent.")
                            keepAlives.clear()
                            transactions.clear()
                            msTimer.reset()
                        }
                    }
                }
            }
        }
    }

    override val tag: String
        get() = modeValue.get()

    override fun onDisable() {
        when (modeValue.get().toLowerCase()) {
            "花雨庭" -> {
                packetsMap.clear()
            }
        }
    }
}




