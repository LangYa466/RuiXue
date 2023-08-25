/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.combat

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.WEnumHand
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketPlayerDigging
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketUseEntity
import net.ccbluex.liquidbounce.api.minecraft.potion.PotionType
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WMathHelper
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.api.minecraft.world.IWorldSettings
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.exploit.MultiActions
import net.ccbluex.liquidbounce.features.module.modules.hyt.*
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.player.Blink
import net.ccbluex.liquidbounce.features.module.modules.render.FreeCam
import net.ccbluex.liquidbounce.injection.backend.Backend
import net.ccbluex.liquidbounce.injection.backend.unwrap
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.extensions.isAnimal
import net.ccbluex.liquidbounce.utils.extensions.isClientFriend
import net.ccbluex.liquidbounce.utils.extensions.isMob
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.effect.EntityLightningBolt
import net.minecraft.item.ItemBlock
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock
import net.minecraft.network.play.server.SPacketSpawnGlobalEntity
import net.minecraft.util.math.MathHelper
import org.lwjgl.input.Keyboard
import org.lwjgl.opengl.GL11
import org.lwjgl.util.glu.Cylinder
import skid.EaseUtils
import java.awt.Color
import java.util.*
import kotlin.math.*

@ModuleInfo(name = "KillAura", category = ModuleCategory.COMBAT,
    keyBind = Keyboard.KEY_R, description = ".", chinesename = "杀戮光环")
/**
 * OPTIONS
 */
class KillAura : Module() {
    /**
     * OPTIONS
     */

    // CPS - Attack speed
    val maxCPS: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minCPS.get()
            if (i > newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), this.get())
        }
    }
    val minCPS: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxCPS.get()
            if (i < newValue) set(i)

            attackDelay = TimeUtils.randomClickDelay(this.get(), maxCPS.get())
        }
    }
    private val helperValue = BoolValue("Helper", true)
    private val killAura2Value = BoolValue("KillAura2", true)
    private val stopMoveValue = BoolValue("StopMove", true)
    private val noC03Value = BoolValue("NoC03", true)
    private val multiActionsValue = BoolValue("MultiActions", true)
    val turnOnTime = MSTimer()

    val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val cooldownValue = FloatValue("Cooldown", 1f, 0f, 1f)
    // Range
    val rangeValue = FloatValue("Range", 3.7f, 1f, 8f)
    val groundRangeValue = FloatValue("GroundRange", 3.7f, 1f, 8f)
    val airRangeValue = FloatValue("AirRange", 3.7f, 1f, 8f)
    val ghostValue = FloatValue("ghostRange", 3.7f, 1f, 8f)
    private val BlockRangeValue = FloatValue("BlockRange", 3f, 0f, 8f)
    private val throughWallsRangeValue = FloatValue("ThroughWallsRange", 3f, 0f, 8f)
    private val rangeSprintReducementValue = FloatValue("RangeSprintReducement", 0f, 0f, 0.4f)
    private val rangeReducementValue = FloatValue("RangeReducement", 0f, 0f, 0.4f)
    private val attackhighReducementValue = FloatValue("attackhighReducement", 0f, 0f, 0.4f)
    private val hurtairReducementValue = FloatValue("hurtairReducement", 0f, 0f, 0.4f)
    // Modes
    private val priorityValue = ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime", "HYT"), "Distance")
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")
    private val switchDelayValue = IntegerValue("SwitchDelay", 700, 0, 2000)
    // Bypass
    private val autoDisableValue = BoolValue("AutoDisable-Velocity", false)
    private val autoDisable2Value = BoolValue("AutoDisable-NoS32", false)
    private val hytValue = BoolValue("HuaYuTing-Auto-Range", true)
    private val swingValue = BoolValue("Swing", true)
    val keepSprintValue = BoolValue("KeepSprint", true)
    private val stopSprintAir = BoolValue("StopSprintOnAir",true)
    val Rangefix = BoolValue("Rangefix",true)
    val AirBypass = BoolValue("AirBypass",true)
    val BackBypass = BoolValue("BackBypass",true)
    val ChatGpt = BoolValue("ChatGptRotations",true)
    val Wtap = BoolValue("Wtap",false)
    val Wtap2 = BoolValue("Wtap2",false)
    val cpsdown = BoolValue("cpsdown",false)
    private val noBlocking = BoolValue("NoBlocking", false)
    private val noBadPacketsValue = BoolValue("NoBadPackets", false)
    private val hyt180fovfixValue = BoolValue("Hyt180FovFix",false)
    private var ticks = 0
    private val binds = arrayOf(
        mc.gameSettings.keyBindForward
    )

    // AutoBlock
    private val afterAttackValue = BoolValue("AutoBlock-AfterAttack", false)
    private val blockModeValue = ListValue("AutoBlock", arrayOf("Off", "HYTPit", "Packet", "AfterTick","CatBounce"), "Packet")
    private val interactAutoBlockValue = BoolValue("InteractAutoBlock", true)
    private var blocking = false
    private var hitable1 = false

    // Raycast
    private val raycastValue = BoolValue("RayCast", true)
    private val raycastIgnoredValue = BoolValue("RayCastIgnored", false)
    private val livingRaycastValue = BoolValue("LivingRayCast", true)
    private val aacValue = BoolValue("AAC", false)
    private val rotations = ListValue("RotationMode", arrayOf("Auto","Cat","SearchHead","Vanilla","BackTrack","Test","Test2","HYTSpartan","HytRotation","FullDown"), "Test")
    private val silentRotationValue = BoolValue("SilentRotation", true)
    private val rotationStrafeValue = ListValue("Strafe", arrayOf("Off", "Strict", "Silent", "silentfix"), "Off")
    private val randomCenterValue = BoolValue("RandomCenter", true)
    private val outborderValue = BoolValue("Outborder", false)
    // Turn Speed
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 360f, 0f, 360f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 360f, 0f, 360f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }
    // Predict
    private val predictValue = BoolValue("Predict", true)

    private val maxPredictSize: FloatValue = object : FloatValue("MaxPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize.get()
            if (v > newValue) set(v)
        }
    }

    private val minPredictSize: FloatValue = object : FloatValue("MinPredictSize", 1f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize.get()
            if (v < newValue) set(v)
        }
    }
    // Lighting
    private val lightingValue = BoolValue("Lighting", false)
    private val lightingModeValue = ListValue("Lighting-Mode", arrayOf("Dead", "Attack"), "Dead")
    private val lightingSoundValue = BoolValue("Lighting-Sound", true)
    private val fovValue = FloatValue("FOV", 180f, 0f, 180f)
    private val fixfovValue = FloatValue("FixFOV", 30f, 0f, 180f)
    private val hitableValue = BoolValue("AlwaysHitable",true)
    // Bypass
    private val failRateValue = FloatValue("FailRate", 0f, 0f, 100f)
    private val fakeSwingValue = BoolValue("FakeSwing", true)
    private val noInventoryAttackValue = BoolValue("NoInvAttack", false)
    private val noInventoryDelayValue = IntegerValue("NoInvDelay", 200, 0, 500)
    private val limitedMultiTargetsValue = IntegerValue("LimitedMultiTargets", 0, 0, 50)
    // Visuals
    private val markValue = ListValue("Mark", arrayOf("Liquid","FDP","Block","Jello", "Plat", "Red", "Sims", "None","CatBounce"),"FDP")
    private val jelloRed = IntegerValue("jelloRed", 255, 0, 255)
    private val jelloGreen = IntegerValue("jelloGreen", 255, 0, 255)
    private val jelloBlue = IntegerValue("jelloBlue", 255, 0, 255)
    private val fakeSharpValue = BoolValue("FakeSharp", true)
    private val circleValue=BoolValue("Circle",true)
    private val circleRed = IntegerValue("CircleRed", 255, 0, 255)
    private val circleGreen = IntegerValue("CircleGreen", 255, 0, 255)
    private val circleBlue = IntegerValue("CircleBlue", 255, 0, 255)
    private val circleAlpha = IntegerValue("CircleAlpha", 255, 0, 255)
    private val circleAccuracy = IntegerValue("CircleAccuracy", 15, 0, 60)
    private val circleThickness = IntegerValue("circleThickness", 5, 0, 15)
    /**
     * MODULE
     */

    // Target
    var target: IEntityLivingBase? = null
    private var currentTarget: IEntityLivingBase? = null
    private var packetSent = false
    private var hitable = false
    private var blockingnew = true
    private val discoveredTargets = mutableListOf<EntityLivingBase>()
    private val prevTargetEntities = mutableListOf<Int>()
    private var lastTarget: IEntityLivingBase? = null

    // Other
    private var oldRange = 0F

    // Attack delay
    private val attackTimer = MSTimer()
    private var attackDelay = 0L
    private var clicks = 0
    private val switchTimer = MSTimer()
    // Container Delay
    private var containerOpen = -1L

    // Fake block status
    var blockingStatus = false
    private var espAnimation = 0.0
    private var isUp = true
    var syncEntity: IEntityLivingBase? = null

    companion object {
        @JvmStatic
        var killCounts = 0
    }

    fun getRange(): Float {
        return if (mc.thePlayer == null || mc.theWorld == null || mc2.world == null || mc2.player == null) {
            rangeValue.get()
        } else if (!hytValue.get()) {
            rangeValue.get()
        } else {
            if (mc.thePlayer!!.onGround) {
                rangeValue.get()
            } else {
                3F
            }
        }
    }


    /**
     * Enable kill aura module
     */
    override fun onEnable() {
        if (autoDisableValue.get() && LiquidBounce.moduleManager.getModule(Velocity2::class.java).state) {
            LiquidBounce.moduleManager.getModule(Velocity2::class.java).state = false
        }

        mc.thePlayer ?: return
        mc.theWorld ?: return

        updateTarget()
    }

    /**
     * Disable kill aura module
     */
    override fun onDisable() {
        target = null
        currentTarget = null
        lastTarget = null
        hitable = false
        packetSent = false
        prevTargetEntities.clear()
        attackTimer.reset()
        clicks = 0

        if (killAura2Value.get()) LiquidBounce.moduleManager.getModule(KillAura2::class.java).state = false
        if (stopMoveValue.get()) LiquidBounce.moduleManager.getModule(StopMove::class.java).state = false
        if (noC03Value.get()) LiquidBounce.moduleManager.getModule(NoC03::class.java).state = false
        if (multiActionsValue.get()) LiquidBounce.moduleManager.getModule(MultiActions::class.java).state = false

        stopBlocking()
    }

    /**
     * Motion event
     */
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.POST) {
            packetSent = false
        }
        if (this.stopSprintAir.get()) {
            if (mc.thePlayer!!.onGround) {
                this.keepSprintValue.set(true)
            } else {
                this.keepSprintValue.set(false)
            }
        }
        if (event.eventState == EventState.POST) {
            target ?: return
            currentTarget ?: return

            // Update hitable
            updateHitable()

            if (packetSent && noBadPacketsValue.get()) {
                return
            }

            // AutoBlock
            if (blockModeValue.get().equals("aftertick", true) && canBlock) {
                startBlocking(currentTarget!!, hitable)
            }
            if (blockModeValue.get().equals("hytpit", ignoreCase = true) && canBlock) {
                mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.heldItem, WEnumHand.MAIN_HAND))
                blockingStatus = true
            }
            if (blockModeValue.get().equals("CatBounce", ignoreCase = true) && canBlock) {
                blockingStatus = true
            }

            if (switchTimer.hasTimePassed(switchDelayValue.get().toLong()) || targetModeValue.get() != "Switch") {
                prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)
                switchTimer.reset()
            }

            return

        }
        if (rotationStrafeValue.get().equals("Off", true))
            update()
    }

    /**
     * Strafe event
     */
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        if (rotationStrafeValue.get().equals("Off", true))
            return

        update()

        if (currentTarget != null && RotationUtils.targetRotation != null) {
            when (rotationStrafeValue.get().toLowerCase()) {
                "strict" -> {
                    val (yaw) = RotationUtils.targetRotation ?: return
                    var strafe = event.strafe
                    var forward = event.forward
                    val friction = event.friction

                    var f = strafe * strafe + forward * forward

                    if (f >= 1.0E-4F) {
                        f = sqrt(f)

                        if (f < 1.0F)
                            f = 1.0F

                        f = friction / f
                        strafe *= f
                        forward *= f

                        val yawSin = sin((yaw * Math.PI / 180F).toFloat())
                        val yawCos = cos((yaw * Math.PI / 180F).toFloat())

                        val player = mc.thePlayer!!

                        player.motionX += strafe * yawCos - forward * yawSin
                        player.motionZ += forward * yawCos + strafe * yawSin
                    }
                    event.cancelEvent()
                }
                "silent" -> {
                    update()

                    RotationUtils.targetRotation.applyStrafeToPlayer(event)
                    event.cancelEvent()
                }
                "silentfix" -> {
                    if (event.isCancelled) {
                        return
                    }
                    var isSilent = true
                    val (yaw) = RotationUtils.targetRotation ?: return
                    var strafe = event.strafe
                    var forward = event.forward
                    var friction = event.friction
                    var factor = strafe * strafe + forward * forward

                    var angleDiff = ((WMathHelper.wrapAngleTo180_float(mc.thePlayer!!.rotationYaw - yaw - 22.5f - 135.0f) + 180.0).toDouble() / (45.0).toDouble()).toInt()
                    //alert("Diff: " + angleDiff + " friction: " + friction + " factor: " + factor);
                    var calcYaw = if(isSilent) { yaw + 45.0f * angleDiff.toFloat() } else yaw

                    var calcMoveDir = Math.max(Math.abs(strafe), Math.abs(forward)).toFloat()
                    calcMoveDir = calcMoveDir * calcMoveDir
                    var calcMultiplier = MathHelper.sqrt(calcMoveDir / Math.min(1.0f, calcMoveDir * 2.0f)).toFloat()

                    if (isSilent) {
                        when (angleDiff) {
                            1, 3, 5, 7, 9 -> {
                                if ((Math.abs(forward) > 0.005 || Math.abs(strafe) > 0.005) && !(Math.abs(forward) > 0.005 && Math.abs(strafe) > 0.005)) {
                                    friction = friction / calcMultiplier
                                } else if (Math.abs(forward) > 0.005 && Math.abs(strafe) > 0.005) {
                                    friction = friction * calcMultiplier
                                }
                            }
                        }
                    }
                    if (factor >= 1.0E-4F) {
                        factor = MathHelper.sqrt(factor).toFloat()


                        if (factor < 1.0F) {
                            factor = 1.0F
                        }

                        factor = friction / factor
                        strafe *= factor
                        forward *= factor

                        val yawSin = MathHelper.sin((calcYaw * Math.PI / 180F).toFloat())
                        val yawCos = MathHelper.cos((calcYaw * Math.PI / 180F).toFloat())

                        mc.thePlayer!!.motionX += strafe * yawCos - forward * yawSin
                        mc.thePlayer!!.motionZ += forward * yawCos + strafe * yawSin
                    }
                    event.cancelEvent()
                }
            }
        }
    }

    fun update() {
        if (cancelRun || (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())))
            return
        // Update target
        updateTarget()

        if (target == null) {
            stopBlocking()
            return
        }



        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("Switch", ignoreCase = true) && isEnemy(currentTarget))
            target = currentTarget
    }
    @EventTarget
    fun onMove(event: MoveEvent){
        if (AirBypass.get() && !mc.thePlayer!!.isDead && mc.thePlayer!!.health > 0){
            if (mc.thePlayer!!.onGround){
                if (rangeValue.get() != groundRangeValue.get()) rangeValue.set(groundRangeValue.get())
            } else {
                if (rangeValue.get() != airRangeValue.get()) rangeValue.set(airRangeValue.get())
            }
        }
    }
    /**
     * Update event
     */
    @EventTarget
    fun onPacket(event: PacketEvent) {
        val e = event.packet.unwrap()
        if (blocking && !hitable && e is CPacketPlayerTryUseItemOnBlock) {
            event.cancelEvent()
        }
    }
    @EventTarget
    fun onAttack(event: AttackEvent) {
        ticks = 2
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent) {
        if (autoDisable2Value.get()) {
            LiquidBounce.moduleManager.getModule(NoS32::class.java).state = false
        }

        if (BackBypass.get() && mc.gameSettings.keyBindBack.isKeyDown){
            if (fovValue.get() != 30F) fovValue.set(30F)
        } else if (BackBypass.get()) if (fovValue.get() != 180F) fovValue.set(180F)
        if (mc.thePlayer!!.isDead || mc.thePlayer!!.health <= 0){
            if (rangeValue.get() != ghostValue.get()) rangeValue.set(ghostValue.get())
        }
        if (blockModeValue.get().equals("CatBounce", ignoreCase = true) && canBlock) {
            hitable1 = RotationUtils.isFaced(target, 0.05)
            if (target != null && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)) {
                if (mc.thePlayer!!.getDistanceToEntity(target!!) <= BlockRangeValue.get()) {
                    mc.gameSettings.keyBindUseItem.pressed = true
                    blocking = true
                }
            } else if (blocking) {
                mc.gameSettings.keyBindUseItem.pressed = false
                blocking = false
            }
        }
        if (Rangefix.get()) {
            for (entity in mc.theWorld!!.loadedEntityList) {
                val distance = mc.thePlayer!!.getDistanceToEntityBox(entity)
                val hurtTime = when {
                    distance <= 3.2f -> 10
                    distance <= 3.3f -> 9
                    distance <= 3.4f -> 8
                    distance <= 3.5f -> 7
                    distance <= 3.6f -> 4
                    distance <= 3.7f -> 3
                    distance <= 3.8f -> 2
                    distance <= 4.0f -> 1
                    else -> null
                }
                hurtTime?.let {
                    if (hurtTimeValue.get() != it) hurtTimeValue.set(it)
                }
            }
        }
        if (hyt180fovfixValue.get()) {
            if (RotationUtils.getRotationDifference(target) > fixfovValue.get() && mc.thePlayer!!.onGround) {
                if(rotationStrafeValue.get() != "Strict") rotationStrafeValue.set("Strict")

            } else {
                if(rotationStrafeValue.get() != "Silent") rotationStrafeValue.set("Silent")

            }
        }
        if (Wtap.get()) {
            if (mc.thePlayer!!.onGround) {
                if (target?.onGround == false && target!!.posY.toInt() > mc2.player.posY.toInt()) {
                    if (mc.gameSettings.keyBindForward.isKeyDown) {
                        mc.gameSettings.keyBindForward.pressed = false
                    }
                } else if (target == null || currentTarget == null || target?.onGround == true) {
                    for (bind in binds) {
                        bind.pressed = mc.gameSettings.isKeyDown(bind)
                    }
                }
            }
        }
        if (Wtap2.get()) {
            if (ticks == 2) {
                mc2.player.isSprinting = false
                ticks = 1
            } else if (ticks == 1) {
                mc2.player.isSprinting = true
                ticks = 0
            }
        }
        if (cpsdown.get()) {
            if (target != null && target!!.hurtTime > 0){
                if (maxCPS.get() != 8) maxCPS.set(8)
                if (minCPS.get() != 8) minCPS.set(8)
            } else {
                if (maxCPS.get() != 18) maxCPS.set(18)
                if (minCPS.get() != 15) minCPS.set(15)
            }
        }
        if (ChatGpt.get()){
            if (target!!.posY.toInt() > mc2.player.posY.toInt()) {
                if (rotations.get() != "FullDown") rotations.set("FullDown")
            } else if (target!!.posY.toInt() == mc2.player.posY.toInt()) {
                if (rotations.get() != "HytRotation") rotations.set("HytRotation")
            } else {
                if (rotations.get() != "SearchHead") rotations.set("SearchHead")
            }
        }
        if (lightingValue.get()) {
            when (lightingModeValue.get().toLowerCase()) {
                "dead" -> {
                    if (target != null) {
                        lastTarget = if (lastTarget == null) {
                            target
                        } else {
                            if (lastTarget!!.health <= 0) {
                                mc.netHandler2.handleSpawnGlobalEntity(SPacketSpawnGlobalEntity(EntityLightningBolt(mc2.world,
                                    lastTarget!!.posX, lastTarget!!.posY, lastTarget!!.posZ, true)))
                                if (lightingSoundValue.get()) mc    .soundHandler.playSound("entity.lightning.impact", 0.5f)
                            } //ambient.weather.thunder
                            target
                        }
                    } else {
                        if (lastTarget != null && lastTarget!!.health <= 0) {
                            mc.netHandler2.handleSpawnGlobalEntity(SPacketSpawnGlobalEntity(EntityLightningBolt(mc2.world,
                                lastTarget!!.posX, lastTarget!!.posY, lastTarget!!.posZ, true)))
                            if (lightingSoundValue.get()) mc.soundHandler.playSound("entity.lightning.impact", 0.5f)
                            lastTarget = target
                        }
                    }
                }

                "attack" -> {
                    mc.netHandler2.handleSpawnGlobalEntity(SPacketSpawnGlobalEntity(EntityLightningBolt(mc2.world,
                        target!!.posX, target!!.posY, target!!.posZ, true)))
                    if (lightingSoundValue.get()) mc.soundHandler.playSound("entity.lightning.impact", 0.5f)
                }
            }
        }

        if (syncEntity != null && syncEntity!!.isDead) {
            ++killCounts
            syncEntity = null
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }
        if (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            currentTarget = null
            hitable = false
            if (classProvider.isGuiContainer(mc.currentScreen)) containerOpen = System.currentTimeMillis()
            return
        }


        if (target != null && currentTarget != null && (Backend.MINECRAFT_VERSION_MINOR == 8 || mc.thePlayer!!.getCooledAttackStrength(0.0F) >= cooldownValue.get())) {
            while (clicks > 0) {
                runAttack()
                clicks--
            }
        }
    }



    private fun esp(entity : IEntityLivingBase, partialTicks : Float, radius : Float) {
        GL11.glPushMatrix()
        GL11.glDisable(3553)
        skid.GLUtils.startSmooth()
        GL11.glDisable(2929)
        GL11.glDepthMask(false)
        GL11.glLineWidth(1.0F)
        GL11.glBegin(3)
        val x: Double = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * partialTicks - mc.renderManager.viewerPosX
        val y: Double = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * partialTicks - mc.renderManager.viewerPosY
        val z: Double = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * partialTicks - mc.renderManager.viewerPosZ
        for (i in 0..360) {
            val rainbow = Color(Color.HSBtoRGB((mc.thePlayer!!.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f, 0.7f, 1.0f))
            GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
            GL11.glVertex3d(x + radius * cos(i * 6.283185307179586 / 45.0), y + espAnimation, z + radius * sin(i * 6.283185307179586 / 45.0))
        }
        GL11.glEnd()
        GL11.glDepthMask(true)
        GL11.glEnable(2929)
        skid.GLUtils.endSmooth()
        GL11.glEnable(3553)
        GL11.glPopMatrix()
    }

    private fun drawESP(entity: IEntityLivingBase, color: Int, e: Render3DEvent) {
        val x: Double =
            entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * e.partialTicks.toDouble() - mc.renderManager.renderPosX
        val y: Double =
            entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * e.partialTicks.toDouble() - mc.renderManager.renderPosY
        val z: Double =
            entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * e.partialTicks.toDouble() - mc.renderManager.renderPosZ
        val radius = 0.15f
        val side = 4
        GL11.glPushMatrix()
        GL11.glTranslated(x, y + 2, z)
        GL11.glRotatef(-entity.width, 0.0f, 1.0f, 0.0f)
        RenderUtils.glColor(color)
        RenderUtils.enableSmoothLine(1.5F)
        val c = Cylinder()
        GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
        c.drawStyle = 100012
        RenderUtils.glColor(Color(80,255,80,200))
        c.draw(0F, radius, 0.3f, side, 1)
        c.drawStyle = 100012
        GL11.glTranslated(0.0, 0.0, 0.3)
        c.draw(radius, 0f, 0.3f, side, 1)
        GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
        c.drawStyle = 100011
        GL11.glTranslated(0.0, 0.0, -0.3)
        RenderUtils.glColor(color)
        c.draw(0F, radius, 0.3f, side, 1)
        c.drawStyle = 100011
        GL11.glTranslated(0.0, 0.0, 0.3)
        c.draw(radius, 0F, 0.3f, side, 1)
        RenderUtils.disableSmoothLine()
        GL11.glPopMatrix()
    }

    /**
     * Render event
     */
    @EventTarget
    fun onRender3D(event: Render3DEvent) {
        if (circleValue.get()) {
            GL11.glPushMatrix()
            GL11.glTranslated(
                mc.thePlayer!!.lastTickPosX + (mc.thePlayer!!.posX - mc.thePlayer!!.lastTickPosX) * mc.timer.renderPartialTicks - mc.renderManager.renderPosX,
                mc.thePlayer!!.lastTickPosY + (mc.thePlayer!!.posY - mc.thePlayer!!.lastTickPosY) * mc.timer.renderPartialTicks - mc.renderManager.renderPosY,
                mc.thePlayer!!.lastTickPosZ + (mc.thePlayer!!.posZ - mc.thePlayer!!.lastTickPosZ) * mc.timer.renderPartialTicks - mc.renderManager.renderPosZ
            )
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_DEPTH_TEST)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)

            GL11.glLineWidth(circleThickness.get().toFloat())
            GL11.glColor4f(circleRed.get().toFloat() / 255.0F, circleGreen.get().toFloat() / 255.0F, circleBlue.get().toFloat() / 255.0F, circleAlpha.get().toFloat() / 255.0F)
            GL11.glRotatef(90F, 1F, 0F, 0F)
            GL11.glBegin(GL11.GL_LINE_STRIP)

            val theta = 2.0 * Math.PI / circleAccuracy.get()

            for (i in 0 until circleAccuracy.get()) {
                val x = (getRange() * cos(i * theta)).toFloat()
                val y = (getRange() * sin(i * theta)).toFloat()
                GL11.glVertex2f(x, y)
            }
            GL11.glVertex2f(getRange(), 0F)

            GL11.glEnd()

            GL11.glDisable(GL11.GL_BLEND)
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glEnable(GL11.GL_DEPTH_TEST)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)

            GL11.glPopMatrix()
        }

        if (cancelRun) {
            target = null
            currentTarget = null
            hitable = false
            stopBlocking()
            return
        }

        if (noInventoryAttackValue.get() && (classProvider.isGuiContainer(mc.currentScreen) ||
                    System.currentTimeMillis() - containerOpen < noInventoryDelayValue.get())) {
            target = null
            currentTarget = null
            hitable = false
            if (classProvider.isGuiContainer(mc.currentScreen)) containerOpen = System.currentTimeMillis()
            return
        }

        target ?: return

        when (markValue.get().toLowerCase()) {
            "liquid" -> {
                RenderUtils.drawPlatform(target!!, if (target!!.hurtTime <= 0) Color(37, 126, 255, 170) else Color(255, 0, 0, 170))
            }
            "plat" -> RenderUtils.drawPlatform(
                target!!,
                if (hitable) Color(37, 126, 255, 70) else Color(255, 0, 0, 70)
            )
            "block" -> {
                val bb = target!!.entityBoundingBox
                target!!.entityBoundingBox = bb.expand(0.2, 0.2, 0.2)
                RenderUtils.drawEntityBox(target!!, if (target!!.hurtTime <= 0) Color.PINK else Color(208, 2, 5, 70), true)
                target!!.entityBoundingBox = bb
            }
            "red" -> {
                RenderUtils.drawPlatform(target!!, if (target!!.hurtTime <= 0) Color(255, 255, 255, 255) else Color(124, 215, 255, 255))
            }
            "sims" -> {
                val radius = 0.15f
                val side = 4
                GL11.glPushMatrix()
                GL11.glTranslated(
                    target!!.lastTickPosX + (target!!.posX - target!!.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX,
                    (target!!.lastTickPosY + (target!!.posY - target!!.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + target!!.height * 1.1,
                    target!!.lastTickPosZ + (target!!.posZ - target!!.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                )
                GL11.glRotatef(-target!!.width, 0.0f, 1.0f, 0.0f)
                GL11.glRotatef((mc.thePlayer!!.ticksExisted + mc.timer.renderPartialTicks) * 5, 0f, 1f, 0f)
                RenderUtils.glColor(if (target!!.hurtTime <= 0) Color(80, 255, 80) else Color(255, 0, 0))
                RenderUtils.enableSmoothLine(1.5F)
                val c = Cylinder()
                GL11.glRotatef(-90.0f, 1.0f, 0.0f, 0.0f)
                c.draw(0F, radius, 0.3f, side, 1)
                c.drawStyle = 100012
                GL11.glTranslated(0.0, 0.0, 0.3)
                c.draw(radius, 0f, 0.3f, side, 1)
                GL11.glRotatef(90.0f, 0.0f, 0.0f, 1.0f)
                GL11.glTranslated(0.0, 0.0, -0.3)
                c.draw(0F, radius, 0.3f, side, 1)
                GL11.glTranslated(0.0, 0.0, 0.3)
                c.draw(radius, 0F, 0.3f, side, 1)
                RenderUtils.disableSmoothLine()
                GL11.glPopMatrix()
            }
            "fdp" -> {
                val drawTime = (System.currentTimeMillis() % 1500).toInt()
                val drawMode = drawTime > 750
                var drawPercent = drawTime / 750.0
                //true when goes up
                if (!drawMode) {
                    drawPercent = 1 - drawPercent
                } else {
                    drawPercent -= 1
                }
                drawPercent = EaseUtils.easeInOutQuad(drawPercent)
                GL11.glPushMatrix()
                GL11.glDisable(3553)
                GL11.glEnable(2848)
                GL11.glEnable(2881)
                GL11.glEnable(2832)
                GL11.glEnable(3042)
                GL11.glBlendFunc(770, 771)
                GL11.glHint(3154, 4354)
                GL11.glHint(3155, 4354)
                GL11.glHint(3153, 4354)
                GL11.glDisable(2929)
                GL11.glDepthMask(false)

                val bb = target!!.entityBoundingBox
                val radius = (bb.maxX - bb.minX) + 0.3
                val height = bb.maxY - bb.minY
                val x = target!!.lastTickPosX + (target!!.posX - target!!.lastTickPosX) * event.partialTicks - mc.renderManager.viewerPosX
                val y = (target!!.lastTickPosY + (target!!.posY - target!!.lastTickPosY) * event.partialTicks - mc.renderManager.viewerPosY) + height * drawPercent
                val z = target!!.lastTickPosZ + (target!!.posZ - target!!.lastTickPosZ) * event.partialTicks - mc.renderManager.viewerPosZ
                GL11.glLineWidth((radius * 5f).toFloat())
                GL11.glBegin(3)
                for (i in 0..360) {
                    val rainbow = Color(Color.HSBtoRGB((mc.thePlayer!!.ticksExisted / 70.0 + sin(i / 50.0 * 1.75)).toFloat() % 1.0f, 0.7f, 1.0f))
                    GL11.glColor3f(rainbow.red / 255.0f, rainbow.green / 255.0f, rainbow.blue / 255.0f)
                    GL11.glVertex3d(x + radius * cos(i * 6.283185307179586 / 45.0), y, z + radius * sin(i * 6.283185307179586 / 45.0))
                }
                GL11.glEnd()

                GL11.glDepthMask(true)
                GL11.glEnable(2929)
                GL11.glDisable(2848)
                GL11.glDisable(2881)
                GL11.glEnable(2832)
                GL11.glEnable(3553)
                GL11.glPopMatrix()
            }

            "jello" -> {
                val drawTime = (System.currentTimeMillis() % 2000).toInt()
                val drawMode = drawTime > 1000
                var drawPercent = drawTime / 1000.0

                //true when goes up
                if (!drawMode) {
                    drawPercent = 1 - drawPercent
                } else {
                    drawPercent -= 1
                }
                drawPercent = EaseUtils.easeInOutQuad(drawPercent)
                val points = mutableListOf<WVec3>()
                val bb = target!!.entityBoundingBox
                val radius = bb.maxX - bb.minX
                val height = bb.maxY - bb.minY
                val posX = target!!.lastTickPosX + (target!!.posX - target!!.lastTickPosX) * mc.timer.renderPartialTicks
                var posY = target!!.lastTickPosY + (target!!.posY - target!!.lastTickPosY) * mc.timer.renderPartialTicks

                if (drawMode) {
                    posY -= 0.5
                } else {
                    posY += 0.5
                }
                val posZ = target!!.lastTickPosZ + (target!!.posZ - target!!.lastTickPosZ) * mc.timer.renderPartialTicks
                for (i in 0..360 step 7) {
                    points.add(WVec3(posX - sin(i * Math.PI / 180F) * radius, posY + height * drawPercent, posZ + cos(i * Math.PI / 180F) * radius))
                }
                points.add(points[0])
                //draw
                mc.entityRenderer.disableLightmap()
                GL11.glPushMatrix()
                GL11.glDisable(GL11.GL_TEXTURE_2D)
                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
                GL11.glEnable(GL11.GL_LINE_SMOOTH)
                GL11.glEnable(GL11.GL_BLEND)
                GL11.glDisable(GL11.GL_DEPTH_TEST)
                GL11.glBegin(GL11.GL_LINE_STRIP)
                val baseMove = (if (drawPercent > 0.5) {
                    1 - drawPercent
                } else {
                    drawPercent
                }) * 2
                val min = (height / 60) * 20 * (1 - baseMove) * (if (drawMode) {
                    -1
                } else {
                    1
                })
                for (i in 0..20) {
                    var moveFace = (height / 60F) * i * baseMove
                    if (drawMode) {
                        moveFace = -moveFace
                    }
                    val firstPoint = points[0]
                    GL11.glVertex3d(firstPoint.xCoord - mc.renderManager.viewerPosX, firstPoint.yCoord - moveFace - min - mc.renderManager.viewerPosY, firstPoint.zCoord - mc.renderManager.viewerPosZ)
                    GL11.glColor4f(jelloRed.get().toFloat() / 255.0F, jelloGreen.get().toFloat() / 255.0F, jelloBlue.get().toFloat() / 255.0F, 0.7F * (i / 20F))
                    for (vec3 in points) {
                        GL11.glVertex3d(
                            vec3.xCoord - mc.renderManager.viewerPosX, vec3.yCoord - moveFace - min - mc.renderManager.viewerPosY,
                            vec3.zCoord - mc.renderManager.viewerPosZ
                        )
                    }
                    GL11.glColor4f(0F, 0F, 0F, 0F)
                }
                GL11.glEnd()
                GL11.glEnable(GL11.GL_DEPTH_TEST)
                GL11.glDisable(GL11.GL_LINE_SMOOTH)
                GL11.glDisable(GL11.GL_BLEND)
                GL11.glEnable(GL11.GL_TEXTURE_2D)
                GL11.glPopMatrix()
            }

        }

        if (currentTarget != null && attackTimer.hasTimePassed(attackDelay) &&
            currentTarget!!.hurtTime <= hurtTimeValue.get()) {
            clicks++
            attackTimer.reset()
            attackDelay = TimeUtils.randomClickDelay(minCPS.get(), maxCPS.get())
        }
        if (this.markValue.get().toLowerCase().equals("catbounce") && !targetModeValue.get().equals("Multi", ignoreCase = true))
            RenderUtils.drawCircleESP(target, 0.67, Color.RED.rgb, true)
    }
    /**
     * Handle entity move
     */
    @EventTarget
    fun onEntityMove(event: EntityMovementEvent) {
        val movedEntity = event.movedEntity

        if (target == null || movedEntity != currentTarget)
            return

        updateHitable()
    }

    /**
     * Attack enemy
     */
    private fun runAttack() {
        target ?: return
        currentTarget ?: return
        val thePlayer = mc.thePlayer ?: return
        val theWorld = mc.theWorld ?: return

        // Settings
        val failRate = failRateValue.get()
        val swing = swingValue.get()
        val multi = targetModeValue.get().equals("Multi", ignoreCase = true)
        val openInventory = aacValue.get() && classProvider.isGuiContainer(mc.currentScreen)
        val failHit = failRate > 0 && Random().nextInt(100) <= failRate

        // Close inventory when open
        if (openInventory)
            mc.netHandler.addToSendQueue(classProvider.createCPacketCloseWindow())

        // Check is not hitable or check failrate

        if (!hitable || failHit) {
            if (swing && (fakeSwingValue.get() || failHit))
                thePlayer.swingItem()
        } else {
            // Attack
            if (!multi) {
                attackEntity(currentTarget!!)
            } else {
                var targets = 0

                for (entity in theWorld.loadedEntityList) {
                    val distance = thePlayer.getDistanceToEntityBox(entity)

                    if (classProvider.isEntityLivingBase(entity) && isEnemy(entity) && distance <= getRange(entity)) {
                        attackEntity(entity.asEntityLivingBase())

                        targets += 1

                        if (limitedMultiTargetsValue.get() != 0 && limitedMultiTargetsValue.get() <= targets)
                            break
                    }
                }
            }

            prevTargetEntities.add(if (aacValue.get()) target!!.entityId else currentTarget!!.entityId)

            if (target == currentTarget)
                target = null
        }

        // Open inventory
        if (openInventory)
            mc.netHandler.addToSendQueue(createOpenInventoryPacket())

        if (currentTarget != null && currentTarget!!.hurtTime > 0 && helperValue.get()) {
            turnOnTime.reset()
            if (killAura2Value.get()) LiquidBounce.moduleManager.getModule(KillAura2::class.java).state = true
            if (stopMoveValue.get()) LiquidBounce.moduleManager.getModule(StopMove::class.java).state = true
            if (noC03Value.get()) LiquidBounce.moduleManager.getModule(NoC03::class.java).state = true
            if (multiActionsValue.get()) LiquidBounce.moduleManager.getModule(MultiActions::class.java).state = true
        }

        if (turnOnTime.hasTimePassed(1000L)) {
            if (killAura2Value.get()) LiquidBounce.moduleManager.getModule(KillAura2::class.java).state = false
            if (stopMoveValue.get()) LiquidBounce.moduleManager.getModule(StopMove::class.java).state = false
            if (noC03Value.get()) LiquidBounce.moduleManager.getModule(NoC03::class.java).state = false
            if (multiActionsValue.get()) LiquidBounce.moduleManager.getModule(MultiActions::class.java).state = false
        }
    }

    /**
     * Update current target
     */
    private fun updateTarget() {
        // Reset fixed target to null
        target = null

        // Settings
        val hurtTime = hurtTimeValue.get()
        val fov = fovValue.get()
        val switchMode = targetModeValue.get().equals("Switch", ignoreCase = true)

        // Find possible targets
        val targets = mutableListOf<IEntityLivingBase>()

        val theWorld = mc.theWorld!!
        val thePlayer = mc.thePlayer!!

        for (entity in theWorld.loadedEntityList) {
            if (!classProvider.isEntityLivingBase(entity) || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(entity.entityId)))
                continue

            val distance = thePlayer.getDistanceToEntityBox(entity)
            val entityFov = RotationUtils.getRotationDifference(entity)

            if (distance <= maxRange && (fov == 180F || entityFov <= fov) && entity.asEntityLivingBase().hurtTime <= hurtTime)
                targets.add(entity.asEntityLivingBase())
        }

        // Sort targets by priority
        when (priorityValue.get().toLowerCase()) {
            "distance" -> targets.sortBy { thePlayer.getDistanceToEntityBox(it) } // Sort by distance
            "health" -> targets.sortBy { it.health } // Sort by health
            "direction" -> targets.sortBy { RotationUtils.getRotationDifference(it) } // Sort by FOV
            "livingtime" -> targets.sortBy { -it.ticksExisted } // Sort by existence
            "hytarmor" -> targets.sortBy { it.hurtResistantTime } // Sort by armor
        }

        // Find best target
        for (entity in targets) {
            // Update rotations to current target
            if (!updateRotations(entity)) // when failed then try another target
                continue

            // Set target to current entity
            target = entity
            return
        }

        // Cleanup last targets when no target found and try again
        if (prevTargetEntities.isNotEmpty()) {
            prevTargetEntities.clear()
            updateTarget()
        }
    }

    /**
     * Check if [entity] is selected as enemy with current target options and other modules
     */
    private fun isEnemy(entity: IEntity?): Boolean {
        if (classProvider.isEntityLivingBase(entity) && entity != null && (EntityUtils.targetDead || isAlive(entity.asEntityLivingBase())) && entity != mc.thePlayer) {
            if (!EntityUtils.targetInvisible && entity.invisible)
                return false

            if (EntityUtils.targetPlayer && classProvider.isEntityPlayer(entity)) {
                val player = entity.asEntityPlayer()

                if (player.spectator || AntiBot.isBot(player))
                    return false

                if (player.isClientFriend() && !LiquidBounce.moduleManager[NoFriends::class.java].state)
                    return false

                val teams = LiquidBounce.moduleManager[Teams::class.java] as Teams

                return !teams.state || !teams.isInYourTeam(entity.asEntityLivingBase())
            }

            return EntityUtils.targetMobs && entity.isMob() || EntityUtils.targetAnimals && entity.isAnimal()
        }

        return false
    }

    /**
     * Attack [entity]
     */
    private fun attackEntity(entity: IEntityLivingBase) {
        if (packetSent && noBadPacketsValue.get()) return
        // Stop blocking
        val thePlayer = mc.thePlayer!!

        if (thePlayer.isBlocking || blockingStatus) {
            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM,
                WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)))
            if (afterAttackValue.get()) blockingStatus = false
        }

        // Call attack event
        LiquidBounce.eventManager.callEvent(AttackEvent(entity))

        // Attack target
        if (swingValue.get() && Backend.MINECRAFT_VERSION_MINOR == 8)
            thePlayer.swingItem()

        packetSent = true

        mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(entity, ICPacketUseEntity.WAction.ATTACK))

        if (swingValue.get() && Backend.MINECRAFT_VERSION_MINOR != 8)
            thePlayer.swingItem()

        if (keepSprintValue.get()) {
            // Critical Effect
            if (thePlayer.fallDistance > 0F && !thePlayer.onGround && !thePlayer.isOnLadder &&
                !thePlayer.isInWater && !thePlayer.isPotionActive(classProvider.getPotionEnum(PotionType.BLINDNESS)) && !thePlayer.isRiding)
                thePlayer.onCriticalHit(entity)

            // Enchant Effect
            if (functions.getModifierForCreature(thePlayer.heldItem, entity.creatureAttribute) > 0F)
                thePlayer.onEnchantmentCritical(entity)
        } else {
            if (mc.playerController.currentGameType != IWorldSettings.WGameType.SPECTATOR)
                thePlayer.attackTargetEntityWithCurrentItem(entity)
        }

        // Extra critical effects
        val criticals = LiquidBounce.moduleManager[Criticals::class.java] as Criticals

        for (i in 0..2) {
            // Critical Effect
            if (thePlayer.fallDistance > 0F && !thePlayer.onGround && !thePlayer.isOnLadder && !thePlayer.isInWater && !thePlayer.isPotionActive(classProvider.getPotionEnum(PotionType.BLINDNESS)) && thePlayer.ridingEntity == null || criticals.state && criticals.msTimer.hasTimePassed(criticals.delayValue.get().toLong()) && !thePlayer.isInWater && !thePlayer.isInLava && !thePlayer.isInWeb)
                thePlayer.onCriticalHit(target!!)

            // Enchant Effect
            if (functions.getModifierForCreature(thePlayer.heldItem, target!!.creatureAttribute) > 0.0f || fakeSharpValue.get())
                thePlayer.onEnchantmentCritical(target!!)
        }

        // Start blocking after attack
        if (blockModeValue.get().equals("packet", true) && (thePlayer.isBlocking || canBlock))
            startBlocking(entity, interactAutoBlockValue.get())

        @Suppress("ConstantConditionIf")
        if (Backend.MINECRAFT_VERSION_MINOR != 8) {
            thePlayer.resetCooldown()
        }

        @Suppress("ConstantConditionIf")
        if (Backend.MINECRAFT_VERSION_MINOR != 8) {
            thePlayer.resetCooldown()
        }
    }

    /**
     * Update killaura rotations to enemy
     */
    private fun updateRotations(entity: IEntity): Boolean {
        if (maxTurnSpeed.get() <= 0F)
            return true

        var boundingBox = entity.entityBoundingBox
        if (rotations.get().equals("Vanilla", ignoreCase = true)){
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        if (rotations.get().equals("auto", ignoreCase = true)){
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get()) {
                val randomSize = RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * randomSize,
                    (entity.posY - entity.prevPosY) * randomSize,
                    (entity.posZ - entity.prevPosZ) * randomSize
                )
            }

            val (_, rotation) = RotationUtils.searchBestPositionAndRotation(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox), false),
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            } else {
                limitedRotation.toPlayer(mc.thePlayer!!)
            }

            return true
        }
        if (rotations.get().equals("SearchHead", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.searchHead(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false
            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())
            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)
            return true
        }
        if (rotations.get().equals("cat", ignoreCase = true)){
            if (maxTurnSpeed.get() <= 0F)
                return true

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (vec, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation =   RotationUtils.limitAngleChange(RotationUtils.serverRotation, RotationUtils.toRotation(RotationUtils.getCenter(entity.entityBoundingBox),false),(Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        if (rotations.get().equals("BackTrack", ignoreCase = true) || rotations.get().equals("hytspartan", true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                RotationUtils.OtherRotation(boundingBox,RotationUtils.getCenter(entity.entityBoundingBox), predictValue.get(),
                    mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),maxRange), (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            }else {
                limitedRotation.toPlayer(mc.thePlayer!!)
                return true
            }
        }
        if (rotations.get().equals("FullDown", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.searchdown(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false
            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())
            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)
            return true
        }
        if (rotations.get().equals("HytRotation", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.lockView(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false
            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())
            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)
            return true
        }
        if (rotations.get().equals("Test", ignoreCase = true)) {
            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )
            val (_, rotation) = RotationUtils.lockView(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false
            //debug
            // ClientUtils.displayChatMessage((mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get()).toString())
            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get()) {
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            }else {
                limitedRotation.toPlayer(mc.thePlayer!!)
                return true
            }
        }
        if (rotations.get().equals("Test2", ignoreCase = true)) {
            if (maxTurnSpeed.get() <= 0F)
                return true

            var boundingBox = entity.entityBoundingBox

            if (predictValue.get())
                boundingBox = boundingBox.offset(
                    (entity.posX - entity.prevPosX - (mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posY - entity.prevPosY - (mc.thePlayer!!.posY - mc.thePlayer!!.prevPosY)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                    (entity.posZ - entity.prevPosZ - (mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
                )

            val (_, rotation) = RotationUtils.searchCenter(
                boundingBox,
                outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
                randomCenterValue.get(),
                predictValue.get(),
                mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
                maxRange
            ) ?: return false

            val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        if (predictValue.get())
            boundingBox = boundingBox.offset(
                (entity.posX - entity.prevPosX - (mc.thePlayer!!.posX - mc.thePlayer!!.prevPosX)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                (entity.posY - entity.prevPosY - (mc.thePlayer!!.posY - mc.thePlayer!!.prevPosY)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get()),
                (entity.posZ - entity.prevPosZ - (mc.thePlayer!!.posZ - mc.thePlayer!!.prevPosZ)) * RandomUtils.nextFloat(minPredictSize.get(), maxPredictSize.get())
            )

        val (_, rotation) = RotationUtils.searchCenter(
            boundingBox,
            outborderValue.get() && !attackTimer.hasTimePassed(attackDelay / 2),
            randomCenterValue.get(),
            predictValue.get(),
            mc.thePlayer!!.getDistanceToEntityBox(entity) < throughWallsRangeValue.get(),
            maxRange
        ) ?: return false

        val limitedRotation = RotationUtils.limitAngleChange(RotationUtils.serverRotation, rotation,
            (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat())

        if (silentRotationValue.get())
            RotationUtils.setTargetRotation(limitedRotation, if (aacValue.get()) 15 else 0)
        else
            limitedRotation.toPlayer(mc.thePlayer!!)

        return true
    }
    /**
     * Check if enemy is hitable with current rotations
     */
    private fun updateHitable() {
        if(hitableValue.get()){
            hitable = true
            return
        }
        // Disable hitable check if turn speed is zero
        if (maxTurnSpeed.get() <= 0F) {
            hitable = true
            return
        }

        val reach = min(maxRange.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(target!!)) + 1

        if (raycastValue.get()) {
            val raycastedEntity = RaycastUtils.raycastEntity(reach, object : RaycastUtils.EntityFilter {
                override fun canRaycast(entity: IEntity?): Boolean {
                    return (!livingRaycastValue.get() || (classProvider.isEntityLivingBase(entity) && !classProvider.isEntityArmorStand(entity))) &&
                            (isEnemy(entity) || raycastIgnoredValue.get() || aacValue.get() && mc.theWorld!!.getEntitiesWithinAABBExcludingEntity(entity, entity!!.entityBoundingBox).isNotEmpty())
                }

            })

            if (raycastValue.get() && raycastedEntity != null && classProvider.isEntityLivingBase(raycastedEntity)
                && (LiquidBounce.moduleManager[NoFriends::class.java].state || !(classProvider.isEntityPlayer(raycastedEntity) && raycastedEntity.asEntityPlayer().isClientFriend())))
                currentTarget = raycastedEntity.asEntityLivingBase()

            hitable = if (maxTurnSpeed.get() > 0F) currentTarget == raycastedEntity else true
        } else
            hitable = RotationUtils.isFaced(currentTarget, reach)
    }

    /**
     * Start blocking
     */
    private fun startBlocking(interactEntity: IEntity, interact: Boolean) {
        if (packetSent && noBadPacketsValue.get()) {
            return
        }
        if (interact) {
            val positionEye = mc.renderViewEntity?.getPositionEyes(1F)
            val expandSize = interactEntity.collisionBorderSize.toDouble()
            val boundingBox = interactEntity.entityBoundingBox.expand(expandSize, expandSize, expandSize)

            val (yaw, pitch) = RotationUtils.targetRotation ?: Rotation(mc.thePlayer!!.rotationYaw, mc.thePlayer!!.rotationPitch)
            val yawCos = cos(-yaw * 0.017453292F - Math.PI.toFloat())
            val yawSin = sin(-yaw * 0.017453292F - Math.PI.toFloat())
            val pitchCos = -cos(-pitch * 0.017453292F)
            val pitchSin = sin(-pitch * 0.017453292F)
            val range = min(maxRange.toDouble(), mc.thePlayer!!.getDistanceToEntityBox(interactEntity)) + 1
            val lookAt = positionEye!!.addVector(yawSin * pitchCos * range, pitchSin * range, yawCos * pitchCos * range)

            val movingObject = boundingBox.calculateIntercept(positionEye, lookAt) ?: return
            val hitVec = movingObject.hitVec

            mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(interactEntity, WVec3(
                hitVec.xCoord - interactEntity.posX,
                hitVec.yCoord - interactEntity.posY,
                hitVec.zCoord - interactEntity.posZ)
            ))
            mc.netHandler.addToSendQueue(classProvider.createCPacketUseEntity(interactEntity, ICPacketUseEntity.WAction.INTERACT))
        }
        mc.netHandler.addToSendQueue(createUseItemPacket(mc.thePlayer!!.inventory.getCurrentItemInHand(), WEnumHand.MAIN_HAND))

        blockingStatus = true
        packetSent = true
    }


    /**
     * Stop blocking
     */
    private fun stopBlocking() {

        if (blockingStatus) {
            if (packetSent && noBadPacketsValue.get()) {
                return
            }
            mc.netHandler.addToSendQueue(classProvider.createCPacketPlayerDigging(ICPacketPlayerDigging.WAction.RELEASE_USE_ITEM, WBlockPos.ORIGIN, classProvider.getEnumFacing(EnumFacingType.DOWN)))
            blockingStatus = false
            packetSent = true
        }
    }

    /**
     * Check if run should be cancelled
     */
    private val cancelRun: Boolean
        inline get() = mc.thePlayer!!.spectator || !isAlive(mc.thePlayer!!)
                || LiquidBounce.moduleManager[Blink::class.java].state
                || (noBlocking.get() && mc.thePlayer!!.isUsingItem && mc.thePlayer!!.heldItem?.item is ItemBlock)
                || LiquidBounce.moduleManager[FreeCam::class.java].state

    /**
     * Check if [entity] is alive
     */
    private fun isAlive(entity: IEntityLivingBase) = entity.entityAlive && entity.health > 0 ||
            aacValue.get() && entity.hurtTime > 5


    /**
     * Check if player is able to block
     */
    private val canBlock: Boolean
        inline get() = mc.thePlayer!!.heldItem != null && classProvider.isItemSword(mc.thePlayer!!.heldItem!!.item)

    /**
     * Range
     */

    private val maxRange: Float
        get() = max(getRange(), BlockRangeValue.get())

    private fun getRange(entity: IEntity) =
        (if (mc.thePlayer!!.getDistanceToEntityBox(entity) >= BlockRangeValue.get()) getRange()
        else getRange()) - (if (mc.thePlayer!!.sprinting) rangeSprintReducementValue.get() else 0F) -
                (if (!mc.thePlayer!!.sprinting) rangeReducementValue.get() else 0F) -
                (if (target!!.posY.toInt() > mc2.player.posY.toInt()) attackhighReducementValue.get() else 0F) -
                (if (mc.thePlayer!!.hurtTime > 0 && !mc.thePlayer!!.onGround) hurtairReducementValue.get() else 0F)

    /**
     * HUD Tag
     */
    override val tag: String
        get() =  targetModeValue.get() +"|"+getRange()

    val isBlockingChestAura: Boolean
        get() = state && target != null
}