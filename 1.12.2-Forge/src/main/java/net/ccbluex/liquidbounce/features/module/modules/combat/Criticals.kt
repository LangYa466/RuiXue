
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.StatType
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.network.IPacket
import net.ccbluex.liquidbounce.event.AttackEvent
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.PacketEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Fly
import net.ccbluex.liquidbounce.utils.MinecraftInstance
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.network.play.client.CPacketPlayer
import org.json.XMLTokener


@ModuleInfo(name = "Criticals", description = "Automatically deals critical hits.",chinesename = "刀刀暴击", category = ModuleCategory.COMBAT)
class Criticals : Module() {

    val modeValue = ListValue("Mode", arrayOf("HytGrimacPacket","HytGrimacpacket2","hytpacket", "HytPacketTG","HytGrim-Y", "Packet", "SuperPacket", "NoGround", "TPHop","Jump","GrimFake"), "Packet")
    val delayValue = IntegerValue("Delay", 0, 0, 500)
    private val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private var attacks = 0

    private fun fakeJump() {
        val thePlayer = mc.thePlayer ?: return

        thePlayer.isAirBorne = true
        thePlayer.triggerAchievement(classProvider.getStatEnum(StatType.JUMP_STAT))
    }

    val msTimer = MSTimer()

    override fun onEnable() {
        val thePlayer = mc.thePlayer ?: return

        if (modeValue.get().equals("NoGround", ignoreCase = true)) {
            thePlayer.jump()
        }
    }

    @EventTarget
    fun onAttack(event: AttackEvent) {
        if (classProvider.isEntityLivingBase(event.targetEntity)) {
            val thePlayer = mc.thePlayer ?: return
            val entity = event.targetEntity!!.asEntityLivingBase()

            if (!thePlayer.onGround || thePlayer.isOnLadder || thePlayer.isInWeb || thePlayer.isInWater ||
                thePlayer.isInLava || thePlayer.ridingEntity != null || entity.hurtTime > hurtTimeValue.get() ||
                LiquidBounce.moduleManager[Fly::class.java]!!.state || !msTimer.hasTimePassed(delayValue.get().toLong())
            )
                return

            val x = thePlayer.posX
            val y = thePlayer.posY
            val z = thePlayer.posZ

            when (modeValue.get().toLowerCase()) {
                "HytGrimacPacket" -> {
                    val iINetHandlerPlayClient = mc.netHandler
                    val iCPacketPlayer = classProvider.createCPacketPlayerPosition(x, y + 0.06250000001304, z, true)
                    val iPacket = iCPacketPlayer as IPacket
                    iINetHandlerPlayClient.addToSendQueue(iPacket)
                    val iCPacketPlayer2 = classProvider.createCPacketPlayerPosition(x, y + 0.00150000001304, z, false)
                    val iPacket2 = iCPacketPlayer2 as IPacket
                    iINetHandlerPlayClient.addToSendQueue(iPacket2)
                    val iCPacketPlayer3 = classProvider.createCPacketPlayerPosition(x, y + 0.014400000001304, z, false)
                    val iPacket3 = iCPacketPlayer3 as IPacket
                    iINetHandlerPlayClient.addToSendQueue(iPacket3)
                    val iCPacketPlayer4 = classProvider.createCPacketPlayerPosition(x, y + 0.001150000001304, z, false)
                    val iPacket4 = iCPacketPlayer4 as IPacket
                    iINetHandlerPlayClient.addToSendQueue(iPacket4)
                    val hashMap = XMLTokener.entity as HashMap<*, *>
                    val iEntity2 = hashMap["entity"] as IEntity
                    thePlayer.onCriticalHit(iEntity2)
                }
                "hytpacket" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.00010100001002, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.0010001014201, z, false))
                }
                "lowjump" -> thePlayer.motionY = 0.3425
                "jump" -> thePlayer.motionY = 0.42
                "visual" -> thePlayer.onCriticalHit(entity)
                "lowjump2" -> {
                    if (thePlayer.onGround && !mc.gameSettings.keyBindJump.isKeyDown && !MovementUtils.isMoving) {
                        thePlayer.motionY = 0.20
                    }
                }
                "hop" -> {
                    thePlayer.motionY = 0.1
                    thePlayer.fallDistance = 0.1f
                    thePlayer.onGround = false
                }
                "hytgrim-y" -> {
                    var n = 0
                    n = this.attacks;
                    this.attacks = n + 1;
                    if (this.attacks <= 6){

                    }else{
                        if(thePlayer.onGround)
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.01, z, false));
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 1.0E-10, z, false));
                        mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.114514, z, false));
                        this.attacks = 0;
                    }
                }
                "hytgrimacpacket2" -> {
                    if(thePlayer.isAirBorne)
                    mc.netHandler.addToSendQueue(
                        (classProvider.createCPacketPlayerPosition(
                            x,
                            y + 0.06250000001304,
                            z,
                            true
                        ) as IPacket)!!
                    )
                    mc.netHandler.addToSendQueue(
                        (classProvider.createCPacketPlayerPosition(
                            x,
                            y + 0.06150000001304,
                            z,
                            false
                        ) as IPacket)!!
                    )
                    this.msTimer.reset();
                }
                "hyttest" -> {
                    if(thePlayer.isAirBorne)
                        mc.netHandler.addToSendQueue(
                            (classProvider.createCPacketPlayerPosition(
                                x,
                                y + 0.06250000001304,
                                z,
                                true
                            ) as IPacket)!!
                        )
                    mc.netHandler.addToSendQueue(
                        (classProvider.createCPacketPlayerPosition(
                            x,
                            y + 0.06150000001304,
                            z,
                            false
                        ) as IPacket)!!
                    )
                    this.msTimer.reset();
                }
                "hytgrimacpacket" -> {
                    this.attacks = attacks + 1;
                    if (this.attacks <= 6) {

                    } else {
                        if (thePlayer.onGround)
                            mc.netHandler.addToSendQueue(
                                classProvider.createCPacketPlayerPosition(
                                    x,
                                    y + 0.01,
                                    z,
                                    false
                                )
                            );
                        mc.netHandler.addToSendQueue(
                            classProvider.createCPacketPlayerPosition(
                                x,
                                y + 1.0E-10,
                                z,
                                false
                            )
                        );
                        mc.netHandler.addToSendQueue(
                            classProvider.createCPacketPlayerPosition(
                                x,
                                y + 0.114514,
                                z,
                                false
                            )
                        );
                        this.attacks = 0;
                    }
                }
                    "tphop" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.02, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.01, z, false))
                    thePlayer.setPosition(x, y + 0.01, z)
                }
                "noground2" -> {
                    thePlayer.onCriticalHit(entity)
                }
                "packet" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.0625, z, true))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 1.1E-5, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y, z, false))
                }
                "packet2" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.11, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.1100013579, z, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(x, y + 0.0000013579, z, false))
                }
                "packet3" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.05000000074505806, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.012511000037193298, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, false))
                }
                "packet4" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.0031311231111, thePlayer.posZ, false))
                }
                "superpacket" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.1625, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 4.0E-6, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 1.0E-6, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayer(false))
                }
                "fakejump" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.42, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, false))
                }
                "fakejump2" -> {
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY + 0.42, thePlayer.posZ, false))
                    mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerPosition(thePlayer.posX, thePlayer.posY, thePlayer.posZ, false))
                    fakeJump()
                }
            }

            msTimer.reset()
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        val thePlayer = mc.thePlayer ?: return

        val packet = event.packet

        if (packet is CPacketPlayer && modeValue.get().equals("NoGround", ignoreCase = true))
            packet.onGround = false
        if(packet is CPacketPlayer && modeValue.get().equals("NoGround2", ignoreCase = true) && !thePlayer.onGround && !thePlayer.isCollidedVertically && thePlayer.fallDistance < 2)
            packet.onGround = true
    }

    override val tag: String?
        get() = modeValue.get() + " | " + attacks
}

