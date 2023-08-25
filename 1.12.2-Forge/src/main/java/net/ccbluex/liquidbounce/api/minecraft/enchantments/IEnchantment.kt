/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */

package net.ccbluex.liquidbounce.api.minecraft.enchantments

interface IEnchantment {
    val effectId: Int

    fun getTranslatedName(level: Int): String
}