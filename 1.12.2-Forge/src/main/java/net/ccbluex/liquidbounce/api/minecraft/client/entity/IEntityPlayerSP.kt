/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */

package net.ccbluex.liquidbounce.api.minecraft.client.entity

import net.ccbluex.liquidbounce.api.minecraft.client.network.IINetHandlerPlayClient
import net.ccbluex.liquidbounce.api.minecraft.util.IIChatComponent
import net.ccbluex.liquidbounce.api.minecraft.util.IMovementInput
import net.minecraft.block.state.IBlockState
import net.minecraft.client.settings.KeyBinding

@Suppress("INAPPLICABLE_JVM_NAME")
interface IEntityPlayerSP : IAbstractClientPlayer, IBlockState {
    var horseJumpPowerCounter: Int
    var horseJumpPower: Float

    val sendQueue: IINetHandlerPlayClient
    val movementInput: IMovementInput

    val isHandActive: Boolean

    var serverSprintState: Boolean

    fun sendChatMessage(msg: String)
    fun respawnPlayer()
    fun addChatMessage(component: IIChatComponent)
    fun closeScreen()
    fun setSneaking(sneakingKeyBinding: Int, on: Boolean) {
        KeyBinding.setKeyBindState(sneakingKeyBinding, on)
    }
}