/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */

package net.ccbluex.liquidbounce.api.minecraft.nbt

interface IJsonToNBT {
    fun getTagFromJson(s: String): INBTTagCompound
}