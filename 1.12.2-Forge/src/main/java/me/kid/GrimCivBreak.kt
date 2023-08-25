/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package me.kid

import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.utils.block.BlockUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.FloatValue
import net.minecraft.init.Blocks
import java.awt.Color

@ModuleInfo(name = "GrimCivBreak", description = "1", category = ModuleCategory.WORLD, chinesename = "Grim合法秒挖")
class GrimCivBreak : Module() {
    private val breakDamage = FloatValue("BreakDamage",0f,0f,2f)
    private val range = FloatValue("Range", 5F, 1F, 6F)
    private var blockPos: WBlockPos? = null
    private var breaking = false
    private var speed = 0f

    override fun onEnable() {
        blockPos = null
        breaking = false
        speed = 0f
    }

    @EventTarget
    fun onBlockClick(event: ClickBlockEvent) {
        if (classProvider.isBlockBedrock(event.clickedBlock?.let { BlockUtils.getBlock(it) }))
            return


        blockPos = event.clickedBlock
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent){
            if(breaking){
                speed += try {
                    blockPos!!.getBlock()!!.getPlayerRelativeBlockHardness(mc.thePlayer!!, mc.theWorld!!, blockPos!!)
                } catch (ex: Exception) {
                    ex.printStackTrace()
                    return
                }
                if(speed > breakDamage.get() && BlockUtils.getCenterDistance(blockPos!!) <= range.get()){
                    try {
                        mc.theWorld!!.setBlockState(blockPos, Blocks.AIR.defaultState, 11)
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                        return
                    }
                    mc.netHandler.addToSendQueue(
                        classProvider.createCPacketPlayerDigging(
                            ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK,
                            blockPos!!,
                            classProvider.getEnumFacing(EnumFacingType.DOWN)
                        )
                    )
                }
            }

    }

    @EventTarget
    fun onPacket(event: PacketEvent){
        val packet = event.packet
        if(classProvider.isCPacketPlayerDigging(packet)){
            if(packet.asCPacketPlayerDigging().action == ICPacketPlayerDigging.WAction.START_DESTROY_BLOCK){
               breaking = true
                speed = 0f
            }
            if(packet.asCPacketPlayerDigging().action == ICPacketPlayerDigging.WAction.STOP_DESTROY_BLOCK){
                blockPos = null
                breaking = false
                speed = 0f
            }
        }
    }

    @EventTarget fun onMotion(event: MotionEvent){
        when(event.eventState){
            EventState.POST ->{
                if(breaking){
                    mc.netHandler.addToSendQueue(classProvider.createCPacketAnimation())
                }
            }
        }
    }
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        RenderUtils.drawBlockBox(blockPos ?: return, Color.RED, true)
    }
}