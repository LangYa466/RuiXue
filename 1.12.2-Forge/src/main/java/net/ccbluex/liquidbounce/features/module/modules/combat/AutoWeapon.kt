/*
 * FDPClient Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge by LiquidBounce.
 * https://github.com/SkidderMC/FDPClient/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.api.minecraft.enchantments.IEnchantment
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.item.ItemUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.enchantment.Enchantment
import net.minecraft.item.ItemSword
import net.minecraft.item.ItemTool
import net.minecraft.network.play.client.CPacketUseEntity
import net.minecraft.network.play.client.CPacketHeldItemChange
import skid.PacketUtils

@ModuleInfo(name = "AutoWeapon",description = "AutoWeapon", category = ModuleCategory.COMBAT, chinesename = "自动选择最好的武器")
object AutoWeapon : Module() {

    private val onlySwordValue = BoolValue("OnlySword", false)
    private val silentValue = BoolValue("SpoofItem", false)
    private val ticksValue = IntegerValue("SpoofTicks", 10, 1, 20)

    private var attackEnemy = false
    private var spoofedSlot = 0

    @EventTarget
    fun onAttack(event: AttackEvent) {
        attackEnemy = true
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (event.packet.unwrap() is CPacketUseEntity && event.packet.unwrap() == CPacketUseEntity.Action.ATTACK &&
                attackEnemy
        ) {
            attackEnemy = false
            // Find the best weapon in hotbar (#Kotlin Style)
            val (slot, _) = (0..8)
                .map { Pair(it, mc.thePlayer!!.inventory.getStackInSlot(it)) }
                .filter { it.second != null && (it.second!!.item is ItemSword || (it.second!!.item is ItemTool && !onlySwordValue.get())) }
                .maxBy {
                    it.second!!.getAttributeModifier("generic.attackDamage").first().amount + 1.25 * ItemUtils.getEnchantment(it.second, Enchantment.getEnchantmentByID(16) as IEnchantment?)
                } ?: return

            if (slot == mc.thePlayer!!.inventory.currentItem) { // If in hand no need to swap
                return
            }

            // Switch to best weapon
            if (silentValue.get()) {
                PacketUtils.send(CPacketHeldItemChange(slot))
                spoofedSlot = ticksValue.get()
            } else {
                mc.thePlayer!!.inventory.currentItem = slot
                mc.playerController.updateController()
            }

            // Resend attack packet
            mc.netHandler.addToSendQueue(event.packet)
            event.cancelEvent()
        }
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        // Switch back to old item after some time
        if (spoofedSlot > 0) {
            if (spoofedSlot == 1) {
                PacketUtils.send(CPacketHeldItemChange(mc.thePlayer!!.inventory.currentItem))
            }
            spoofedSlot--
        }
    }
}