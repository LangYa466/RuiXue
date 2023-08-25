/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.module.modules.hyt

import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.BoolValue
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemArmor

@ModuleInfo(name = "Teams", description = "修复版2",chinesename = "团队", category = ModuleCategory.VULGAR)
class Teams : Module() {

    private val scoreboardValue = BoolValue("ScoreboardTeam-计分板队伍", false)
    private val colorValue = BoolValue("Color-颜色", false)
    private val gommeSWValue = BoolValue("GommeSW-我不知道", false)
    private val armorColorValue = BoolValue("ArmorColor-装备颜色", true)

    /**
     * Check if [entity] is in your own team using scoreboard, name color or team prefix
     */
    fun isInYourTeam(entity: IEntityLivingBase): Boolean {
        val thePlayer = mc.thePlayer ?: return false

        if (scoreboardValue.get() && thePlayer.team != null && entity.team != null &&
            thePlayer.team!!.isSameTeam(entity.team!!)
        )
            return true

        val displayName = thePlayer.displayName

        if (armorColorValue.get()) {
            val entityPlayer = entity.asEntityPlayer()
            if (thePlayer.inventory.armorInventory[3] != null && entityPlayer.inventory.armorInventory[3] != null) {
                val myHead = thePlayer.inventory.armorInventory[3]
                val myItemArmor = myHead!!.item!!.asItemArmor()


                val entityHead = entityPlayer.inventory.armorInventory[3]
                var entityItemArmor = myHead.item!!.asItemArmor()

                if (myItemArmor.getColor(myHead) == entityItemArmor.getColor(entityHead!!)) {
                    return true
                }
            }
        }

        if (gommeSWValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            if (targetName.startsWith("T") && clientName.startsWith("T"))
                if (targetName[1].isDigit() && clientName[1].isDigit())
                    return targetName[1] == clientName[1]
        }

        if (colorValue.get() && displayName != null && entity.displayName != null) {
            val targetName = entity.displayName!!.formattedText.replace("§r", "")
            val clientName = displayName.formattedText.replace("§r", "")
            return targetName.startsWith("§${clientName[1]}")
        }

        return false
    }
}
