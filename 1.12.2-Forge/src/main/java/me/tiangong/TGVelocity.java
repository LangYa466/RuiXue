package me.tiangong;

import net.ccbluex.liquidbounce.event.EventTarget;
import net.ccbluex.liquidbounce.event.PacketEvent;
import net.ccbluex.liquidbounce.features.module.Module;
import net.ccbluex.liquidbounce.features.module.ModuleCategory;
import net.ccbluex.liquidbounce.features.module.ModuleInfo;
import net.ccbluex.liquidbounce.value.BoolValue;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketConfirmTransaction;

@ModuleInfo(name = "CanCelC0F", description = "CanCelC0F", chinesename = "无C0F",category = ModuleCategory.VULGAR)
public class TGVelocity extends Module {
    private final BoolValue cancelc0f = new BoolValue("CancelC0f", true);
    @EventTarget
    public void onPacket(PacketEvent event) {
        Packet packet = (Packet) event.getPacket();
        if (cancelc0f.get()) if (packet instanceof CPacketConfirmTransaction) //CancelC0F
            event.cancelEvent();
    }
}
