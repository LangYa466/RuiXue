/*
 * ColorByte Hacked Client
 * A free half-open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/SkidderRyF/ColorByte/
 */
package net.ccbluex.liquidbounce.utils.item

import net.ccbluex.liquidbounce.api.enums.BlockType
import net.ccbluex.liquidbounce.api.minecraft.item.IItem
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketClientStatus
import net.ccbluex.liquidbounce.event.ClickWindowEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.Listenable
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.minecraft.init.MobEffects
import net.minecraft.item.Item
import net.minecraft.item.ItemPotion
import net.minecraft.item.ItemStack
import net.minecraft.potion.PotionUtils

class InventoryUtils : MinecraftInstance(), Listenable {
    @EventTarget
    fun onClick(event: ClickWindowEvent?) {
        CLICK_TIMER.reset()
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val packet = event.packet
        if (classProvider.isCPacketPlayerBlockPlacement(packet)) CLICK_TIMER.reset()
    }

    override fun handleEvents(): Boolean {
        return true
    }

    companion object {
        val CLICK_TIMER = MSTimer()

        @JvmField
        val BLOCK_BLACKLIST = listOf(
            classProvider.getBlockEnum(BlockType.CHEST),
            classProvider.getBlockEnum(BlockType.ENDER_CHEST),
            classProvider.getBlockEnum(BlockType.TRAPPED_CHEST),
            classProvider.getBlockEnum(BlockType.ANVIL),
            classProvider.getBlockEnum(BlockType.SAND),
            classProvider.getBlockEnum(BlockType.WEB),
            classProvider.getBlockEnum(BlockType.TORCH),
            classProvider.getBlockEnum(BlockType.CRAFTING_TABLE),
            classProvider.getBlockEnum(BlockType.FURNACE),
            classProvider.getBlockEnum(BlockType.WATERLILY),
            classProvider.getBlockEnum(BlockType.DISPENSER),
            classProvider.getBlockEnum(BlockType.STONE_PRESSURE_PLATE),
            classProvider.getBlockEnum(BlockType.WODDEN_PRESSURE_PLATE),
            classProvider.getBlockEnum(BlockType.NOTEBLOCK),
            classProvider.getBlockEnum(BlockType.DROPPER),
            classProvider.getBlockEnum(BlockType.TNT),
            classProvider.getBlockEnum(BlockType.STANDING_BANNER),
            classProvider.getBlockEnum(BlockType.WALL_BANNER),
            classProvider.getBlockEnum(BlockType.REDSTONE_TORCH)
        )

        @JvmField
        val SKYWAR_BLOCK_BLACKLIST = listOf(
            classProvider.getBlockEnum(BlockType.CHEST), // 箱子
            classProvider.getBlockEnum(BlockType.ENDER_CHEST), // 末影箱
            classProvider.getBlockEnum(BlockType.TRAPPED_CHEST), // 陷阱箱
            classProvider.getBlockEnum(BlockType.ANVIL), // 铁砧
            classProvider.getBlockEnum(BlockType.SAND), // 沙子
            classProvider.getBlockEnum(BlockType.ENCHANTING_TABLE), // 附魔台
            classProvider.getBlockEnum(BlockType.TORCH), // 火把
            classProvider.getBlockEnum(BlockType.CRAFTING_TABLE), // 工作台
            classProvider.getBlockEnum(BlockType.FURNACE), // 熔炉
            classProvider.getBlockEnum(BlockType.WATERLILY), // 荷叶
            classProvider.getBlockEnum(BlockType.DISPENSER),
            classProvider.getBlockEnum(BlockType.DROPPER),
            classProvider.getBlockEnum(BlockType.LADDER), // 梯子
            classProvider.getBlockEnum(BlockType.BROWN_MUSHROOM_BLOCK), // 棕色蘑菇
            classProvider.getBlockEnum(BlockType.RED_MUSHROOM_BLOCK), // 红色蘑菇
            classProvider.getBlockEnum(BlockType.STANDING_BANNER),
            classProvider.getBlockEnum(BlockType.WALL_BANNER),
            classProvider.getBlockEnum(BlockType.STONE_PRESSURE_PLATE), // 石制压力板
            classProvider.getBlockEnum(BlockType.WODDEN_PRESSURE_PLATE), // 木制压力板
            classProvider.getBlockEnum(BlockType.NOTEBLOCK), // 音乐盒
            classProvider.getBlockEnum(BlockType.REDSTONE_TORCH) // 红石火把
        )

        @JvmStatic
        fun findItem(startSlot: Int, endSlot: Int, item: IItem): Int {
            for (i in startSlot until endSlot) {
                val stack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item == item) return i
            }
            return -1
        }

        @JvmStatic
        fun findItem(startSlot: Int, endSlot: Int, item: Item): Int {
            for (i in startSlot until endSlot) {
                val stack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (stack != null && stack.item == item) return i
            }
            return -1
        }

        fun hasSpaceHotbar(): Boolean {
            for (i in 36..44) {
                val stack = mc.thePlayer!!.inventory.getStackInSlot(i) ?: return true
            }
            return false
        }

        fun isPositivePotionEffect(id: String): Boolean {
            return id == MobEffects.REGENERATION.name || id == MobEffects.SPEED.name ||
                    id == MobEffects.INSTANT_HEALTH.name || id == MobEffects.NIGHT_VISION.name ||
                    id == MobEffects.JUMP_BOOST.name || id == MobEffects.INVISIBILITY.name ||
                    id == MobEffects.RESISTANCE.name || id == MobEffects.WATER_BREATHING.name ||
                    id == MobEffects.ABSORPTION.name || id == MobEffects.HASTE.name ||
                    id == MobEffects.STRENGTH.name || id == MobEffects.HEALTH_BOOST.name ||
                    id == MobEffects.FIRE_RESISTANCE.name
        }

        fun isSplashPotion(id: Int): Boolean {
            return mc.thePlayer!!.inventoryContainer.getSlot(id).stack!!.isSplash()
        }

        fun isPositivePotion(item: ItemPotion, stack: ItemStack): Boolean {
            PotionUtils.getEffectsFromStack(stack).forEach {
                if (isPositivePotionEffect(it.potion.name)) {
                    return true
                }
            }

            return false
        }

        fun openPacket() {
            mc.netHandler.addToSendQueue(classProvider.createCPacketClientStatus(ICPacketClientStatus.WEnumState.OPEN_INVENTORY_ACHIEVEMENT))
        }

        fun closePacket() {
            mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())
        }

        @JvmStatic
        fun findAutoBlockBlock(): Int {
            for (i in 36..44) {
                val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (itemStack != null && classProvider.isItemBlock(itemStack.item) && itemStack.stackSize > 0) {
                    val itemBlock = itemStack.item!!.asItemBlock()
                    val block = itemBlock.block
                    if (block.isFullCube(block.defaultState!!) && !BLOCK_BLACKLIST.contains(block)
                        && !classProvider.isBlockBush(block)
                    ) return i
                }
            }
            for (i in 36..44) {
                val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (itemStack != null && classProvider.isItemBlock(itemStack.item) && itemStack.stackSize > 0) {
                    val itemBlock = itemStack.item!!.asItemBlock()
                    val block = itemBlock.block
                    if (!BLOCK_BLACKLIST.contains(block) && !classProvider.isBlockBush(block)) return i
                }
            }
            return -1
        }
    }
}
