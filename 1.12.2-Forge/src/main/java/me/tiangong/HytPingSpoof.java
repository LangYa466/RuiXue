/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package me.tiangong;

import net.ccbluex.liquidbounce.api.minecraft.network.IPacket;
import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.event.UpdateEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.utils.timer.TimeUtils;
import net.ccbluex.liquidbounce.value.IntegerValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketClientStatus;
import net.minecraft.network.play.client.CPacketKeepAlive;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@ModuleInfo(name = "HytPingSpoof", description = "Fixed.",chinesename = "花雨庭假延迟", category = ModuleCategory.VULGAR)
public class HytPingSpoof extends Module {

    private final IntegerValue maxDelayValue = new IntegerValue("MaxDelay", 1000, 0, 5000) {
        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int minDelayValue = HytPingSpoof.this.minDelayValue.get();

            if(minDelayValue > newValue)
                set(minDelayValue);
        }
    };

    private final IntegerValue minDelayValue = new IntegerValue("MinDelay", 500, 0, 5000) {

        @Override
        protected void onChanged(final Integer oldValue, final Integer newValue) {
            final int maxDelayValue = HytPingSpoof.this.maxDelayValue.get();

            if(maxDelayValue < newValue)
                set(maxDelayValue);
        }
    };

    private final HashMap<Packet<?>, Long> packetsMap = new HashMap<>();

    @Override
    public void onDisable() {
        packetsMap.clear();
    }

    @EventTarget
    public void onPacket(final PacketEvent event) {
        final Packet packet = (Packet) event.getPacket();

        if ((packet instanceof CPacketKeepAlive || packet instanceof CPacketClientStatus) && !(mc.getThePlayer().isDead() || mc.getThePlayer().getHealth() <= 0) && !packetsMap.containsKey(packet)) {
            event.cancelEvent();

            synchronized(packetsMap) {
                packetsMap.put(packet, System.currentTimeMillis() + TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get()));
            }
        }
    }

    @EventTarget(ignoreCondition = true)
    public void onUpdate(final UpdateEvent event) {
        try {
            synchronized(packetsMap) {
                for(final Iterator<Map.Entry<Packet<?>, Long>> iterator = packetsMap.entrySet().iterator(); iterator.hasNext(); ) {
                    final Map.Entry<Packet<?>, Long> entry = iterator.next();

                    if(entry.getValue() < System.currentTimeMillis()) {
                        mc.getNetHandler().addToSendQueue((IPacket) entry.getKey());
                        iterator.remove();
                    }
                }
            }
        }catch(final Throwable t) {
            t.printStackTrace();
        }
    }

}
