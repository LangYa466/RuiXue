/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package net.ccbluex.liquidbounce.features.module.modules.world

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.enums.BlockType
import net.ccbluex.liquidbounce.api.enums.EnumFacingType
import net.ccbluex.liquidbounce.api.enums.StatType
import net.ccbluex.liquidbounce.api.minecraft.item.IItemStack
import net.ccbluex.liquidbounce.api.minecraft.network.play.client.ICPacketEntityAction
import net.ccbluex.liquidbounce.api.minecraft.util.IMovingObjectPosition
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.api.minecraft.util.WVec3
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.movement.Speed
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.*
import net.ccbluex.liquidbounce.utils.MovementUtils.isMoving
import net.ccbluex.liquidbounce.utils.MovementUtils.strafe
import net.ccbluex.liquidbounce.utils.block.BlockUtils.canBeClicked
import net.ccbluex.liquidbounce.utils.block.BlockUtils.getBlock
import net.ccbluex.liquidbounce.utils.block.BlockUtils.isReplaceable
import net.ccbluex.liquidbounce.utils.block.PlaceInfo
import net.ccbluex.liquidbounce.utils.block.PlaceInfo.Companion.get
import net.ccbluex.liquidbounce.utils.item.InventoryUtils.Companion.findAutoBlockBlock
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TickTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.block.BlockAir
import net.minecraft.client.renderer.GlStateManager
import net.minecraft.client.renderer.RenderHelper
import net.minecraft.item.ItemBlock
import net.minecraft.util.math.MathHelper
import org.apache.commons.lang3.RandomUtils
import org.lwjgl.opengl.GL11
import java.awt.Color
import java.util.*

@ModuleInfo(name = "Scaffold4", description = "自动在你脚下放置方块", category = ModuleCategory.WORLD)
class Scaffold4 : Module() {
    /**
     * OPTIONS (Scaffold)
     */
    // Mode
    val modeValue = ListValue("Mode", arrayOf("Normal", "Rewinside", "Expand"), "Normal")
    // Global settings
    // Delay
    private val placeableDelay = BoolValue("PlaceableDelay", false)
    private val maxDelayValue: IntegerValue = object : IntegerValue("MaxDelay", 0, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = minDelayValue.get()
            if (i > newValue) set(i)
        }
    }// { placeableDelay.get() } as IntegerValue
    private val minDelayValue: IntegerValue = object : IntegerValue("MinDelay", 0, 0, 1000) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val i = maxDelayValue.get()
            if (i < newValue) set(i)
        }
    }// { placeableDelay.get() } as IntegerValue

    // AutoBlock
    private val autoBlockMode = ListValue("AutoBlock", arrayOf("Spoof", "Switch", "Matrix", "Off"), "Spoof")
    private val stayAutoBlock = BoolValue("StayAutoBlock", false)// { !autoBlockMode.get().equals("Off") }
    private val swingValue = BoolValue("Swing", true)
    private val downValue = BoolValue("Down", false)
    private val searchValue = BoolValue("Search", true)
    private val placeModeValue = ListValue("PlaceTiming", arrayOf("Pre", "Post"), "Post")

    //make sprint compatible with tower.add sprint tricks
    @JvmField
    val sprintModeValue = ListValue("SprintMode", arrayOf("Same", "Ground", "Air", "PlaceOff", "None"), "None")

    // Eagle
    private val eagleValue = BoolValue("Eagle", false)
    private val eagleSilentValue = BoolValue("EagleSilent", false)
    private val blocksToEagleValue = IntegerValue("BlocksToEagle", 0, 0, 10)
    private val eagleEdgeDistanceValue = FloatValue("EagleEdgeDistance", 0.2f, 0f, 0.5f)

    // Expand
    private val omniDirectionalExpand = BoolValue("OmniDirectionalExpand", true)
    private val expandLengthValue = IntegerValue("ExpandLength", 5, 1, 6)

    // Other
    // SearchAccuracy
    private val searchAccuracyValue: IntegerValue = object : IntegerValue("SearchAccuracy", 8, 1, 24) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            if (maximum < newValue) {
                set(maximum)
            } else if (minimum > newValue) {
                set(minimum)
            }
        }
    }
    private val xzRangeValue = FloatValue("xzRange", 0.8f, 0.1f, 1.0f)
    private val yRangeValue = FloatValue("yRange", 0.8f, 0.1f, 1.0f)

    // Rotations
    private val rotationsValue = BoolValue("Rotations", true)

    // Basic stuff
    private val settlePitch = BoolValue("GrimAC", true)// { rotationsValue.get() }
    private val rotationModeValue = ListValue(
        "RotationMode",
        arrayOf("Hypixel", "Normal", "AAC", "Static", "Static2", "Static3", "Custom"),
        "Normal"
    )// { rotationsValue.get() }  // searching reason
    private val rotationLookupValue = ListValue("RotationLookup", arrayOf("Normal", "AAC", "Same"), "Normal")// { rotationsValue.get() }

    private val noHitCheckValue = BoolValue("NoHitCheck", false)
    private val HypixelYawValue = IntegerValue("HypixelYaw", 180, -360, 360)// { rotationModeValue.get().equals("Hypixel") }
    private val HypixelPitchValue = IntegerValue("HypixelPitch", 79, 60, 100)// { rotationModeValue.get().equals("Hypixel") }
    private val staticPitchValue = FloatValue("Static-Pitch", 86f, 80f, 90f)// { rotationModeValue.get().equals("Custom") }
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = minTurnSpeed.get()
            if (i > newValue) set(i)
        }
    }
    private val customYawValue = FloatValue("Custom-Yaw", 135f, -180f, 180f)
    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 180f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val i = maxTurnSpeed.get()
            if (i < newValue) set(i)
        }
    }// { rotationModeValue.get().contains("Static") } as FloatValue
    private val customPitchValue = FloatValue("Custom-Pitch", 86f, -90f, 90f)// { rotationModeValue.get().contains("Static") }
    private val keepRotationValue = BoolValue("KeepRotation", false)
    private val keepRotOnJumpValue = BoolValue("KeepRotOnJump", true)// { keepRotationValue.get() }
    private val keepLengthValue = IntegerValue("KeepRotationLength", 0, 0, 20)// { keepRotationValue.get() }
    private val placeConditionValue =
        ListValue("Place-Condition", arrayOf("Air", "FallDown", "NegativeMotion", "Always", "DelayAir"), "Always")
    private val placeConditionValue2 = ListValue("Place-Condition", arrayOf("DelayAir", "None"), "None")
    private val DELAY = FloatValue("AirDelay", 0f, 0f, 1000f)
    private val E114514 = FloatValue("Test", 0f, 0f, 0.50f)
    private val rotationStrafeValue = BoolValue("RotationStrafe", false)

    /**
     * OPTIONS (Tower)
     */
    private val towerEnabled = BoolValue("EnableTower", false)
    private val towerModeValue = ListValue(
        "TowerMode", arrayOf(
            "Jump", "Motion", "ConstantMotion", "MotionTP", "Packet", "Teleport", "AAC3.3.9", "AAC3.6.4", "Verus"
        ), "Motion"
    )// { towerEnabled.get() }
    private val towerPlaceModeValue = ListValue("Tower-PlaceTiming", arrayOf("Pre", "Post"), "Post")// { towerEnabled.get() }
    private val stopWhenBlockAbove = BoolValue("StopWhenBlockAbove", false)// { towerEnabled.get() }
    private val onJumpValue = BoolValue("OnJump", false)// { towerEnabled.get() }
    private val noMoveOnlyValue = BoolValue("NoMove", true)// { towerEnabled.get() }
    private val towerTimerValue = FloatValue("TowerTimer", 1f, 0.1f, 10f)// { towerEnabled.get() }

    // Jump mode
    private val jumpMotionValue = FloatValue("JumpMotion", 0.42f, 0.3681289f, 0.79f)// { towerEnabled.get() }
    private val jumpDelayValue = IntegerValue("JumpDelay", 0, 0, 20)// { towerEnabled.get() }

    // ConstantMotion
    private val constantMotionValue = FloatValue("ConstantMotion", 0.42f, 0.1f, 1f)// { towerEnabled.get() }
    private val constantMotionJumpGroundValue = FloatValue("ConstantMotionJumpGround", 0.79f, 0.76f, 1f)// { towerEnabled.get() }

    // Teleport
    private val teleportHeightValue = FloatValue("TeleportHeight", 1.15f, 0.1f, 5f)// { towerEnabled.get() }
    private val teleportDelayValue = IntegerValue("TeleportDelay", 0, 0, 20)// { towerEnabled.get() }
    private val teleportGroundValue = BoolValue("TeleportGround", true)// { towerEnabled.get() }
    private val teleportNoMotionValue = BoolValue("TeleportNoMotion", false)// { towerEnabled.get() }

    // Zitter
    private val zitterValue = BoolValue("Zitter", false)
    private val zitterModeValue = ListValue("ZitterMode", arrayOf("Teleport", "Smooth"), "Teleport")// { zitterValue.get() }
    private val zitterSpeed = FloatValue("ZitterSpeed", 0.13f, 0.1f, 0.3f)// { zitterValue.get() }
    private val zitterStrength = FloatValue("ZitterStrength", 0.072f, 0.05f, 0.2f)// { zitterValue.get() }
    private val zitterDelay = IntegerValue("ZitterDelay", 100, 0, 500)// { zitterValue.get() }

    // Game
    private val timerValue = FloatValue("Timer", 1f, 0.1f, 10f)
    private val speedModifierValue = FloatValue("SpeedModifier", 1f, 0f, 2f)
    private val counterDisplayValue =
        ListValue("Counter", arrayOf("Off", "Simple", "Advanced", "Sigma", "Novoline"), "Novoline")

    //    public final FloatValue xzMultiplier = new FloatValue("XZ-Multiplier", 1F, 0F, 4F);
    private val customSpeedValue = BoolValue("CustomSpeed", false)
    private val customMoveSpeedValue = FloatValue("CustomMoveSpeed", 0.3f, 0f, 5f)// { customSpeedValue.get() }

    // Safety
    private val sameYValue = BoolValue("SameY", false)
    private val autoJumpValue = BoolValue("AutoJump", false)
    private val smartSpeedValue = BoolValue("SmartSpeed", false)
    private val safeWalkValue = BoolValue("SafeWalk", true)
    private val airSafeValue = BoolValue("AirSafe", false)
    private val markValue = BoolValue("Mark", true)
    private val redValue = IntegerValue("Red", 0, 0, 255)// { markValue.get() }
    private val greenValue = IntegerValue("Green", 120, 0, 255)// { markValue.get() }
    private val blueValue = IntegerValue("Blue", 255, 0, 255)// { markValue.get() }
    private val alphaValue = IntegerValue("Alpha", 120, 0, 255)// { markValue.get() }
    private val autoDisableSpeedValue = BoolValue("AutoDisable-Speed", true)

    // Delay
    private val delayTimer = MSTimer()
    private val zitterTimer = MSTimer()
    private val airTimer = MSTimer()

    // Mode stuff
    private val timer = TickTimer()
    private var Ground = false
    private var air = 0f

    /**
     * MODULE
     */
    // Target block
    private var targetPlace: PlaceInfo? = null
    private var towerPlace: PlaceInfo? = null

    // Launch position
    private var launchY = 0
    private var faceBlock = false

    // Rotation lock
    private var lockRotation: Rotation? = null
    private var lookupRotation: Rotation? = null

    // Auto block slot
    private var slot = 0
    private var lastSlot = 0

    // Zitter Smooth
    private var zitterDirection = false
    private var delay: Long = 0

    // Eagle
    private var placedBlocksWithoutEagle = 0
    private var eagleSneaking = false

    // Down
    private var shouldGoDown = false

    // Render thingy
    private var progress = 0f
    private var lastMS = 0L
    private var jumpGround = 0.0
    private var verusState = 0
    private var verusJumped = false
    val isTowerOnly: Boolean
        get() = towerEnabled.get() && !onJumpValue.get()

    fun towerActivation(): Boolean {
        return towerEnabled.get() && (!onJumpValue.get() || mc.gameSettings.keyBindJump.isKeyDown) && (!noMoveOnlyValue.get() || !isMoving)
    }

    /**
     * Enable module
     */
    override fun onEnable() {
        airTimer.reset()
        if (mc.thePlayer == null) return
        progress = 0f
        launchY = mc.thePlayer!!.posY.toInt()
        lastSlot = mc.thePlayer!!.inventory.currentItem
        slot = mc.thePlayer!!.inventory.currentItem
        if (autoDisableSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed::class.java).state) {
            LiquidBounce.moduleManager.getModule(Speed::class.java).state = false
        }
        faceBlock = false
        lastMS = System.currentTimeMillis()
    }

    //Send jump packets, bypasses Hypixel.
    private fun fakeJump() {
        mc.thePlayer!!.isAirBorne = true
        mc.thePlayer!!.triggerAchievement(classProvider.getStatEnum(StatType.JUMP_STAT))
    }

    /**
     * Move player
     */
    private fun move(event: MotionEvent) {
        when (towerModeValue.get().toLowerCase()) {
            "jump" -> if (mc.thePlayer!!.onGround && timer.hasTimePassed(jumpDelayValue.get())) {
                fakeJump()
                mc.thePlayer!!.motionY = jumpMotionValue.get().toDouble()
                timer.reset()
            }

            "motion" -> if (mc.thePlayer!!.onGround) {
                fakeJump()
                mc.thePlayer!!.motionY = 0.42
            } else if (mc.thePlayer!!.motionY < 0.1) mc.thePlayer!!.motionY = 0.3

            "motiontp" -> if (mc.thePlayer!!.onGround) {
                fakeJump()
                mc.thePlayer!!.motionY = 0.42
            } else if (mc.thePlayer!!.motionY < 0.23) mc.thePlayer!!.setPosition(
                mc.thePlayer!!.posX, mc.thePlayer!!.posY.toInt().toDouble(), mc.thePlayer!!.posZ
            )

            "packet" -> if (mc.thePlayer!!.onGround && timer.hasTimePassed(2)) {
                fakeJump()
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.42, mc.thePlayer!!.posZ, false
                    )
                )
                mc.netHandler.addToSendQueue(
                    classProvider.createCPacketPlayerPosition(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + 0.76, mc.thePlayer!!.posZ, false
                    )
                )
                mc.thePlayer!!.setPosition(mc.thePlayer!!.posX, mc.thePlayer!!.posY + 1.08, mc.thePlayer!!.posZ)
                timer.reset()
            }

            "teleport" -> {
                if (teleportNoMotionValue.get()) mc.thePlayer!!.motionY = 0.0
                if ((mc.thePlayer!!.onGround || !teleportGroundValue.get()) && timer.hasTimePassed(teleportDelayValue.get())) {
                    fakeJump()
                    mc.thePlayer!!.setPositionAndUpdate(
                        mc.thePlayer!!.posX,
                        mc.thePlayer!!.posY + teleportHeightValue.get(),
                        mc.thePlayer!!.posZ
                    )
                    timer.reset()
                }
            }

            "constantmotion" -> {
                if (mc.thePlayer!!.onGround) {
                    fakeJump()
                    jumpGround = mc.thePlayer!!.posY
                    mc.thePlayer!!.motionY = constantMotionValue.get().toDouble()
                }
                if (mc.thePlayer!!.posY > jumpGround + constantMotionJumpGroundValue.get()) {
                    fakeJump()
                    mc.thePlayer!!.setPosition(
                        mc.thePlayer!!.posX, mc.thePlayer!!.posY.toInt()
                            .toDouble(), mc.thePlayer!!.posZ
                    )
                    mc.thePlayer!!.motionY = constantMotionValue.get().toDouble()
                    jumpGround = mc.thePlayer!!.posY
                }
            }

            "aac3.3.9" -> {
                if (mc.thePlayer!!.onGround) {
                    fakeJump()
                    mc.thePlayer!!.motionY = 0.4001
                }
                mc.timer.timerSpeed = 1f
                if (mc.thePlayer!!.motionY < 0) {
                    mc.thePlayer!!.motionY = -0.00000945
                    mc.timer.timerSpeed = 1.6f
                }
            }

            "aac3.6.4" -> if (mc.thePlayer!!.ticksExisted % 4 == 1) {
                mc.thePlayer!!.motionY = 0.4195464
                mc.thePlayer!!.setPosition(mc.thePlayer!!.posX - 0.035, mc.thePlayer!!.posY, mc.thePlayer!!.posZ)
            } else if (mc.thePlayer!!.ticksExisted % 4 == 0) {
                mc.thePlayer!!.motionY = -0.5
                mc.thePlayer!!.setPosition(mc.thePlayer!!.posX + 0.035, mc.thePlayer!!.posY, mc.thePlayer!!.posZ)
            }

            "verus" -> {
                if (!mc.theWorld!!.getCollidingBoundingBoxes(
                        mc.thePlayer!!,
                        mc.thePlayer!!.entityBoundingBox.offset(0.0, -0.01, 0.0)
                    ).isEmpty() && mc.thePlayer!!.onGround && mc.thePlayer!!.isCollidedVertically
                ) {
                    verusState = 0
                    verusJumped = true
                }
                if (verusJumped) {
                    strafe()
                    when (verusState) {
                        0 -> {
                            fakeJump()
                            mc.thePlayer!!.motionY = 0.41999998688697815
                            ++verusState
                        }

                        1 -> ++verusState
                        2 -> ++verusState
                        3 -> {
                            event.onGround = true
                            mc.thePlayer!!.motionY = 0.0
                            ++verusState
                        }

                        4 -> ++verusState
                    }
                    verusJumped = false
                }
                verusJumped = true
            }
        }
    }

    /**
     * Update event
     *
     * @param event
     */
    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        if (settlePitch.get()) {
            val x = mc.thePlayer!!.motionX
            val z = mc.thePlayer!!.motionZ
            if (mc.gameSettings.keyBindForward.isKeyDown) {
                if (z > 0.1) {
                    mc.thePlayer!!.rotationYaw = 360f
                }
                if (z < -0.1) {
                    mc.thePlayer!!.rotationYaw = 180f
                }
                if (x > 0.1) {
                    mc.thePlayer!!.rotationYaw = 270f
                }
                if (x < -0.1) {
                    mc.thePlayer!!.rotationYaw = 90f
                }
            }
        }
        if (placeConditionValue.get().equals("delayair", ignoreCase = true)) {
            air++
            if (!mc.thePlayer!!.onGround && air > DELAY.get()) {
                keepRotationValue.set(true)
                rotationsValue.set(true)
                Ground = true
                air = 0f
            } else {
                Ground = false
            }
            if (mc.thePlayer!!.onGround) {
                air = 0f
                keepRotationValue.set(false)
                rotationsValue.set(false)
            }
        }
        if (placeConditionValue2.get().equals("delayair", ignoreCase = true)) {
            air++
            if (!mc.thePlayer!!.onGround && air > DELAY.get()) {
                keepRotationValue.set(true)
                rotationsValue.set(true)
                Ground = true
                air = 0f
            } else {
                Ground = false
            }
            if (mc.thePlayer!!.onGround) {
                air = 0f
                keepRotationValue.set(false)
                rotationsValue.set(false)
            }
        }
        if (towerActivation()) {
            shouldGoDown = false
            mc.gameSettings.keyBindSneak.pressed = false
            mc.thePlayer!!.sprinting = false
            return
        }
        if (sprintModeValue.get().equals("PlaceOff", ignoreCase = true)) {
            mc.thePlayer!!.sprinting = true
            mc.thePlayer!!.motionX = mc.thePlayer!!.motionX
            mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ
        }
        mc.timer.timerSpeed = timerValue.get()
        shouldGoDown =
            downValue.get() && !sameYValue.get() && mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSneak) && blocksAmount > 1
        if (shouldGoDown) mc.gameSettings.keyBindSneak.pressed = false

        // scaffold custom speed if enabled
        if (customSpeedValue.get()) strafe(customMoveSpeedValue.get())
        if (mc.thePlayer!!.onGround) {
            val mode = modeValue.get()

            // Rewinside scaffold mode
            if (mode.equals("Rewinside", ignoreCase = true)) {
                strafe(0.2f)
                mc.thePlayer!!.motionY = 0.0
            }

            // Smooth Zitter
            if (zitterValue.get() && zitterModeValue.get().equals("smooth", ignoreCase = true)) {
                if (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed =
                    false
                if (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false
                if (zitterTimer.hasTimePassed(zitterDelay.get().toLong())) {
                    zitterDirection = !zitterDirection
                    zitterTimer.reset()
                }
                if (zitterDirection) {
                    mc.gameSettings.keyBindRight.pressed = true
                    mc.gameSettings.keyBindLeft.pressed = false
                } else {
                    mc.gameSettings.keyBindRight.pressed = false
                    mc.gameSettings.keyBindLeft.pressed = true
                }
            }

            // Eagle
            if (eagleValue.get() && !shouldGoDown) {
                var dif = 0.5
                if (eagleEdgeDistanceValue.get() > 0) {
                    for (i in 0..3) {
                        val WBlockPos = WBlockPos(
                            mc.thePlayer!!.posX + if (i == 0) -1 else if (i == 1) 1 else 0,
                            mc.thePlayer!!.posY - if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) 0.0 else 1.0,
                            mc.thePlayer!!.posZ + if (i == 2) -1 else if (i == 3) 1 else 0
                        )
                        val placeInfo = get(WBlockPos)
                        if (isReplaceable(WBlockPos) && placeInfo != null) {
                            var calcDif =
                                if (i > 1) mc.thePlayer!!.posZ - WBlockPos.z else mc.thePlayer!!.posX - WBlockPos.x
                            calcDif -= 0.5
                            if (calcDif < 0) calcDif *= -1.0
                            calcDif -= 0.5
                            if (calcDif < dif) dif = calcDif
                        }
                    }
                }
                if (placedBlocksWithoutEagle >= blocksToEagleValue.get()) {
                    val shouldEagle = mc.theWorld!!.getBlockState(
                        WBlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY - 1.0, mc.thePlayer!!.posZ)
                    ).block == classProvider.getBlockEnum(BlockType.AIR) || dif < eagleEdgeDistanceValue.get()
                    if (eagleSilentValue.get()) {
                        if (eagleSneaking != shouldEagle) {
                            mc.netHandler.addToSendQueue(
                                classProvider.createCPacketEntityAction(
                                    mc.thePlayer!!,
                                    if (shouldEagle) ICPacketEntityAction.WAction.START_SNEAKING else ICPacketEntityAction.WAction.STOP_SNEAKING
                                )
                            )
                        }
                        eagleSneaking = shouldEagle
                    } else mc.gameSettings.keyBindSneak.pressed = shouldEagle
                    placedBlocksWithoutEagle = 0
                } else placedBlocksWithoutEagle++
            }

            // Zitter
            if (zitterValue.get() && zitterModeValue.get().equals("teleport", ignoreCase = true)) {
                strafe(zitterSpeed.get())
                val yaw = Math.toRadians(mc.thePlayer!!.rotationYaw + if (zitterDirection) 90.0 else -90.0)
                mc.thePlayer!!.motionX = -Math.sin(yaw) * zitterStrength.get()
                mc.thePlayer!!.motionZ = Math.cos(yaw) * zitterStrength.get()
                zitterDirection = !zitterDirection
            }
        }
        if (sprintModeValue.get().equals("off", ignoreCase = true) || sprintModeValue.get()
                .equals("air", ignoreCase = true) && mc.thePlayer!!.onGround
        ) {
            mc.thePlayer!!.sprinting = true
        }
        if (sprintModeValue.get().equals("ground", ignoreCase = true)) {
            mc.thePlayer!!.sprinting = mc.thePlayer!!.onGround
        }
        //Auto Jump thingy
        if (shouldGoDown) {
            launchY = mc.thePlayer!!.posY.toInt() - 1
        } else if (!sameYValue.get()) {
            if (!autoJumpValue.get() && !(smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed::class.java).state) || mc.gameSettings.isKeyDown(
                    mc.gameSettings.keyBindJump
                ) || mc.thePlayer!!.posY < launchY
            ) launchY = mc.thePlayer!!.posY.toInt()
            if (autoJumpValue.get() && !LiquidBounce.moduleManager.getModule(Speed::class.java).state && isMoving && mc.thePlayer!!.onGround) {
                mc.thePlayer!!.jump()
            }
        }
    }

    @EventTarget
    fun onPacket(event: PacketEvent) {
        if (mc.thePlayer == null) return
        val packet = event.packet

        // AutoBlock
        if (classProvider.isCPacketHeldItemChange(packet)) {
            val packetHeldItemChange = packet.asCPacketHeldItemChange()
            slot = packetHeldItemChange.slotId
        }
    }

    @EventTarget //took it from applyrotationstrafe XD. staticyaw comes from bestnub.
    fun onStrafe(event: StrafeEvent) {
        if (lookupRotation != null && rotationStrafeValue.get()) {
            val dif =
                ((MathHelper.wrapDegrees(mc.thePlayer!!.rotationYaw - lookupRotation!!.yaw - 23.5f - 135) + 180) / 45).toInt()
            val yaw = lookupRotation!!.yaw
            val strafe = event.strafe
            val forward = event.forward
            val friction = event.friction
            var calcForward = 0f
            var calcStrafe = 0f
            when (dif) {
                0 -> {
                    calcForward = forward
                    calcStrafe = strafe
                }

                1 -> {
                    calcForward += forward
                    calcStrafe -= forward
                    calcForward += strafe
                    calcStrafe += strafe
                }

                2 -> {
                    calcForward = strafe
                    calcStrafe = -forward
                }

                3 -> {
                    calcForward -= forward
                    calcStrafe -= forward
                    calcForward += strafe
                    calcStrafe -= strafe
                }

                4 -> {
                    calcForward = -forward
                    calcStrafe = -strafe
                }

                5 -> {
                    calcForward -= forward
                    calcStrafe += forward
                    calcForward -= strafe
                    calcStrafe -= strafe
                }

                6 -> {
                    calcForward = -strafe
                    calcStrafe = forward
                }

                7 -> {
                    calcForward += forward
                    calcStrafe += forward
                    calcForward -= strafe
                    calcStrafe += strafe
                }
            }
            if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
                calcForward *= 0.5f
            }
            if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
                calcStrafe *= 0.5f
            }
            var f = calcStrafe * calcStrafe + calcForward * calcForward
            if (f >= 1.0E-4f) {
                f = MathHelper.sqrt(f)
                if (f < 1.0f) f = 1.0f
                f = friction / f
                calcStrafe *= f
                calcForward *= f
                val yawSin = MathHelper.sin((yaw * Math.PI / 180f).toFloat())
                val yawCos = MathHelper.cos((yaw * Math.PI / 180f).toFloat())
                mc.thePlayer!!.motionX = (calcStrafe * yawCos - calcForward * yawSin).toDouble()
                mc.thePlayer!!.motionZ = (calcForward * yawCos + calcStrafe * yawSin).toDouble()
            }
            event.cancelEvent()
        }
    }

    private fun shouldPlace(): Boolean {
        val placeDelayAir = placeConditionValue.get().equals("delayair", ignoreCase = true)
        val placeDelayAir2 = placeConditionValue2.get().equals("delayair", ignoreCase = true)
        val placeWhenAir = placeConditionValue.get().equals("air", ignoreCase = true)
        val placeWhenFall = placeConditionValue.get().equals("falldown", ignoreCase = true)
        val placeWhenNegativeMotion = placeConditionValue.get().equals("negativemotion", ignoreCase = true)
        val alwaysPlace = placeConditionValue.get().equals("always", ignoreCase = true)
        return (towerActivation()
                || alwaysPlace && mc.theWorld!!.getCollidingBoundingBoxes(
            mc.thePlayer!!, mc.thePlayer!!.entityBoundingBox
                .offset(0.0, -0.5, 0.0).expand(-E114514.get().toDouble(), 0.0, -E114514.get().toDouble())
        )
            .isEmpty()) || placeWhenAir && !mc.thePlayer!!.onGround || placeDelayAir && Ground || placeDelayAir2 && Ground || placeWhenFall && Objects.requireNonNull(
            mc.thePlayer
        )!!.fallDistance > 0 || placeWhenNegativeMotion && mc.thePlayer!!.motionY < 0
    }

    @EventTarget
    fun onMotion(event: MotionEvent) {
        // Lock Rotation
        if (rotationsValue.get() && keepRotationValue.get() && lockRotation != null) RotationUtils.setTargetRotation(
            RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                lockRotation,
                RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
            )
        )
        val mode = modeValue.get()
        val eventState = event.eventState
        if ((!rotationsValue.get() || noHitCheckValue.get() || faceBlock) && placeModeValue.get()
                .equals(eventState.stateName, ignoreCase = true) && !towerActivation()
        ) {
            place(false)
        }
        if (eventState === EventState.PRE && !towerActivation()) {
            if (!shouldPlace() || (if (!autoBlockMode.get()
                        .equals("Off", ignoreCase = true)
                ) findAutoBlockBlock() == -1 else mc.thePlayer!!.heldItem == null ||
                        mc.thePlayer!!.heldItem!!.item !is ItemBlock)
            ) return
            findBlock(mode.equals("expand", ignoreCase = true) && !towerActivation())
        }
        if (targetPlace == null) {
            if (placeableDelay.get()) delayTimer.reset()
        }
        if (!towerActivation()) {
            verusState = 0
            towerPlace = null
            return
        }
        mc.timer.timerSpeed = towerTimerValue.get()
        if (towerPlaceModeValue.get().equals(eventState.stateName, ignoreCase = true)) place(true)
        if (eventState === EventState.PRE) {
            towerPlace = null
            timer.update()
            val isHeldItemBlock = mc.thePlayer!!.heldItem != null && mc.thePlayer!!.heldItem!!.item is ItemBlock
            if (findAutoBlockBlock() != -1 || isHeldItemBlock) {
                launchY = mc.thePlayer!!.posY.toInt()
                if (towerModeValue.get().equals("verus", ignoreCase = true) || !stopWhenBlockAbove.get() || getBlock(
                        WBlockPos(
                            mc.thePlayer!!.posX,
                            mc.thePlayer!!.posY + 2, mc.thePlayer!!.posZ
                        )
                    ) is BlockAir
                ) move(event)
                val WBlockPos = WBlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY - 1.0, mc.thePlayer!!.posZ)
                if (mc.theWorld!!.getBlockState(WBlockPos).block is BlockAir) {
                    if (search(WBlockPos, true, true) && rotationsValue.get()) {
                        val vecRotation = RotationUtils.faceBlock(WBlockPos)
                        if (vecRotation != null) {
                            RotationUtils.setTargetRotation(
                                RotationUtils.limitAngleChange(
                                    RotationUtils.serverRotation,
                                    vecRotation.rotation,
                                    RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
                                )
                            )
                            towerPlace!!.vec3 = vecRotation.vec
                        }
                    }
                }
            }
        }
    }

    /**
     * Search for new target block
     */
    private fun findBlock(expand: Boolean) {
        val WBlockPosition = if (shouldGoDown) (if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) WBlockPos(
            mc.thePlayer!!.posX, mc.thePlayer!!.posY - 0.6, mc.thePlayer!!.posZ
        ) else WBlockPos(
            mc.thePlayer!!.posX, mc.thePlayer!!.posY - 0.6, mc.thePlayer!!.posZ
        ).down()) else if (!towerActivation() && (sameYValue.get() || (autoJumpValue.get() || smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(
                Speed::class.java
            ).state) && mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && launchY <= mc.thePlayer!!.posY
        ) WBlockPos(
            mc.thePlayer!!.posX, (launchY - 1).toDouble(), mc.thePlayer!!.posZ
        ) else if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) WBlockPos(
            mc.thePlayer!!
        ) else WBlockPos(mc.thePlayer!!.posX, mc.thePlayer!!.posY, mc.thePlayer!!.posZ).down()
        if (!expand && (!isReplaceable(WBlockPosition) || search(WBlockPosition, !shouldGoDown, false))) return
        if (expand) {
            val yaw = Math.toRadians((mc.thePlayer!!.rotationYaw + 180).toDouble())
            val x = if (omniDirectionalExpand.get()) Math.round(-Math.sin(yaw))
                .toInt() else mc.thePlayer!!.horizontalFacing.directionVec.x
            val z = if (omniDirectionalExpand.get()) Math.round(Math.cos(yaw))
                .toInt() else mc.thePlayer!!.horizontalFacing.directionVec.z
            for (i in 0 until expandLengthValue.get()) {
                if (search(WBlockPosition.add(x * i, 0, z * i), false, false)) return
            }
        } else if (searchValue.get()) {
            for (x in -1..1) for (z in -1..1) if (search(WBlockPosition.add(x, 0, z), !shouldGoDown, false)) return
        }
    }

    /**
     * Place target block
     */
    private fun place(towerActive: Boolean) {
        if (sprintModeValue.get().equals("PlaceOff", ignoreCase = true)) {
            mc.thePlayer!!.sprinting = false
            mc.thePlayer!!.motionX = mc.thePlayer!!.motionX
            mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ
        }
        if ((if (towerActive) towerPlace else targetPlace) == null) {
            if (placeableDelay.get()) delayTimer.reset()
            return
        }
        if (!towerActivation() && (!delayTimer.hasTimePassed(delay) && (sameYValue.get() || (autoJumpValue.get() || smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(
                Speed::class.java
            ).state) && mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump)) && launchY - 1 != (if (towerActive) towerPlace else targetPlace)!!.vec3.yCoord.toInt())
        ) return
        var blockSlot = -1
        var itemStack = mc.thePlayer!!.heldItem
        if (mc.thePlayer!!.heldItem == null || mc.thePlayer!!.heldItem!!.item !is ItemBlock) {
            if (autoBlockMode.get().equals("Off", ignoreCase = true)) return
            blockSlot = findAutoBlockBlock()
            if (blockSlot == -1) return
            if (autoBlockMode.get().equals("Matrix", ignoreCase = true) && blockSlot - 36 != slot) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(blockSlot - 36))
            }
            if (autoBlockMode.get().equals("Spoof", ignoreCase = true)) {
                mc.netHandler.addToSendQueue(classProvider.createCPacketHeldItemChange(blockSlot - 36))
                itemStack = mc.thePlayer!!.inventoryContainer.getSlot(blockSlot).stack
            } else {
                mc.thePlayer!!.inventory.currentItem = blockSlot - 36
                mc.playerController.updateController()
            }
        }

        // blacklist check
        if (itemStack?.item != null && itemStack.item is ItemBlock) {
            val itemBlock = itemStack.item!!.asItemBlock()
            val block = itemBlock.block
            if (InventoryUtils.BLOCK_BLACKLIST.contains(block) || !block.isFullCube(block.defaultState!!) || itemStack.stackSize <= 0) return
        }
        if (mc.playerController.onPlayerRightClick(
                mc.thePlayer!!,
                mc.theWorld!!,
                itemStack,
                (if (towerActive) towerPlace else targetPlace)!!.blockPos,
                (if (towerActive) towerPlace else targetPlace)!!.enumFacing,
                (if (towerActive) towerPlace else targetPlace)!!.vec3
            )
        ) {
            delayTimer.reset()
            delay = if (!placeableDelay.get()) 0L else TimeUtils.randomDelay(minDelayValue.get(), maxDelayValue.get())
            if (mc.thePlayer!!.onGround) {
                val modifier = speedModifierValue.get()
                mc.thePlayer!!.motionX = mc.thePlayer!!.motionX * modifier
                mc.thePlayer!!.motionZ = mc.thePlayer!!.motionZ * modifier
            }
            if (swingValue.get()) mc.thePlayer!!.swingItem() else mc.netHandler.addToSendQueue(classProvider.createCPacketAnimation())
        }

        // Reset
        if (towerActive) towerPlace = null else targetPlace = null
        if (!stayAutoBlock.get() && blockSlot >= 0 && !autoBlockMode.get()
                .equals("Switch", ignoreCase = true)
        ) mc.netHandler.addToSendQueue(
            classProvider.createCPacketHeldItemChange(
                mc.thePlayer!!.inventory.currentItem
            )
        )
    }

    /**
     * Disable scaffold module
     */
    override fun onDisable() {
        airTimer.reset()
        if (mc.thePlayer == null) return
        if (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindSneak)) {
            mc.gameSettings.keyBindSneak.pressed = false
            if (eagleSneaking) mc.netHandler.addToSendQueue(
                classProvider.createCPacketEntityAction(
                    mc.thePlayer!!, ICPacketEntityAction.WAction.STOP_SNEAKING
                )
            )
        }
        if (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindRight)) mc.gameSettings.keyBindRight.pressed = false
        if (mc.gameSettings.isKeyDown(mc.gameSettings.keyBindLeft)) mc.gameSettings.keyBindLeft.pressed = false
        lockRotation = null
        lookupRotation = null
        mc.timer.timerSpeed = 1f
        shouldGoDown = false
        faceBlock = false
        if (lastSlot != mc.thePlayer!!.inventory.currentItem && autoBlockMode.get()
                .equals("switch", ignoreCase = true)
        ) {
            mc.thePlayer!!.inventory.currentItem = lastSlot
            mc.playerController.updateController()
        }
        if (slot != mc.thePlayer!!.inventory.currentItem && autoBlockMode.get()
                .equals("spoof", ignoreCase = true)
        ) mc.netHandler.addToSendQueue(
            classProvider.createCPacketHeldItemChange(
                mc.thePlayer!!.inventory.currentItem
            )
        )
    }

    /**
     * Entity movement event
     *
     * @param event
     */
    @EventTarget
    fun onMove(event: MoveEvent) {
        if (!safeWalkValue.get() || shouldGoDown) return
        if (airSafeValue.get() || mc.thePlayer!!.onGround) event.isSafeWalk = true
    }

    @EventTarget
    fun onJump(event: JumpEvent) {
        if (towerActivation()) event.cancelEvent()
    }

    /**
     * Scaffold visuals
     *
     * @param event
     */
    @EventTarget
    fun onRender2D(event: Render2DEvent?) {
        progress = (System.currentTimeMillis() - lastMS).toFloat() / 100f
        if (progress >= 1) progress = 1f
        val counterMode = counterDisplayValue.get()
        val scaledResolution = classProvider.createScaledResolution(mc)
        val info = blocksAmount.toString() + " blocks"
        val infoWidth = Fonts.font25.getStringWidth(info)
        val infoWidth2 = Fonts.minecraftFont.getStringWidth(blocksAmount.toString())
        if (counterMode.equals("simple", ignoreCase = true)) {
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2 - 1).toFloat(),
                (scaledResolution.scaledHeight / 2 - 36).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2 + 1).toFloat(),
                (scaledResolution.scaledHeight / 2 - 36).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 - 35).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 - 37).toFloat(),
                -0x1000000,
                false
            )
            Fonts.minecraftFont.drawString(
                blocksAmount.toString(),
                (scaledResolution.scaledWidth / 2 - infoWidth2 / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 - 36).toFloat(),
                -1,
                false
            )
        }
        if (counterMode.equals("advanced", ignoreCase = true)) {
            val canRenderStack =
                slot >= 0 && slot < 9 && mc.thePlayer!!.inventory.mainInventory[slot] != null && mc.thePlayer!!.inventory.mainInventory[slot]!!.item != null && mc.thePlayer!!.inventory.mainInventory[slot]!!.item is ItemBlock
            RenderUtils.drawRect(
                scaledResolution.scaledWidth / 2 - infoWidth / 2 - 4,
                scaledResolution.scaledHeight / 2 - 40,
                scaledResolution.scaledWidth / 2 + infoWidth / 2 + 4,
                scaledResolution.scaledHeight / 2 - 39,
                if (blocksAmount > 1) -0x1 else -0xeff0
            )
            RenderUtils.drawRect(
                scaledResolution.scaledWidth / 2 - infoWidth / 2 - 4,
                scaledResolution.scaledHeight / 2 - 39,
                scaledResolution.scaledWidth / 2 + infoWidth / 2 + 4,
                scaledResolution.scaledHeight / 2 - 26,
                -0x60000000
            )
            if (canRenderStack) {
                RenderUtils.drawRect(
                    scaledResolution.scaledWidth / 2 - infoWidth / 2 - 4,
                    scaledResolution.scaledHeight / 2 - 26,
                    scaledResolution.scaledWidth / 2 + infoWidth / 2 + 4,
                    scaledResolution.scaledHeight / 2 - 5,
                    -0x60000000
                )
                GlStateManager.pushMatrix()
                GlStateManager.translate(
                    (scaledResolution.scaledWidth / 2 - 8).toFloat(),
                    (scaledResolution.scaledHeight / 2 - 25).toFloat(),
                    (scaledResolution.scaledWidth / 2 - 8).toFloat()
                )
                renderItemStack(mc.thePlayer!!.inventory.mainInventory[slot]!!)
                GlStateManager.popMatrix()
            }
            GlStateManager.resetColor()
            Fonts.font25.drawCenteredString(
                info,
                (scaledResolution.scaledWidth / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 - 36).toFloat(),
                -1
            )
        }
        if (counterMode.equals("sigma", ignoreCase = true)) {
            GlStateManager.translate(0f, -14f - progress * 4f, 0f)
            //GL11.glPushMatrix();
            GL11.glEnable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_TEXTURE_2D)
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA)
            GL11.glEnable(GL11.GL_LINE_SMOOTH)
            GL11.glColor4f(0.15f, 0.15f, 0.15f, progress)
            GL11.glBegin(GL11.GL_TRIANGLE_FAN)
            GL11.glVertex2d(
                (scaledResolution.scaledWidth / 2 - 3).toDouble(),
                (scaledResolution.scaledHeight - 60).toDouble()
            )
            GL11.glVertex2d(
                (scaledResolution.scaledWidth / 2).toDouble(),
                (scaledResolution.scaledHeight - 57).toDouble()
            )
            GL11.glVertex2d(
                (scaledResolution.scaledWidth / 2 + 3).toDouble(),
                (scaledResolution.scaledHeight - 60).toDouble()
            )
            GL11.glEnd()
            GL11.glEnable(GL11.GL_TEXTURE_2D)
            GL11.glDisable(GL11.GL_BLEND)
            GL11.glDisable(GL11.GL_LINE_SMOOTH)
            //GL11.glPopMatrix();
            RenderUtils.drawRoundedRect(
                scaledResolution.scaledWidth.toFloat() / 2 - infoWidth / 2 - 4,
                scaledResolution.scaledHeight.toFloat() - 60,
                scaledResolution.scaledWidth.toFloat() / 2 + infoWidth / 2 + 4,
                scaledResolution.scaledHeight.toFloat() - 74,
                2f.toInt().toFloat(),
                Color(0.15f, 0.15f, 0.15f, progress).rgb
            )
            GlStateManager.resetColor()
            Fonts.font25.drawCenteredString(
                info,
                scaledResolution.scaledWidth / 2 + 0.1f,
                (scaledResolution.scaledHeight - 70).toFloat(),
                Color(1f, 1f, 1f, 0.8f * progress).rgb,
                false
            )
            GlStateManager.translate(0f, 14f + progress * 4f, 0f)
        }
        if (counterMode.equals("novoline", ignoreCase = true)) {
            if (slot >= 0 && slot < 9 && mc.thePlayer!!.inventory.mainInventory[slot] != null && mc.thePlayer!!.inventory.mainInventory[slot]!!.item != null && mc.thePlayer!!.inventory.mainInventory[slot]!!.item is ItemBlock) {
                //RenderUtils.drawRect(scaledResolution.getScaledWidth() / 2 - (infoWidth / 2) - 4, scaledResolution.getScaledHeight() / 2 - 26, scaledResolution.getScaledWidth() / 2 + (infoWidth / 2) + 4, scaledResolution.getScaledHeight() / 2 - 5, 0xA0000000);
                GlStateManager.pushMatrix()
                GlStateManager.translate(
                    (scaledResolution.scaledWidth / 2 - 22).toFloat(),
                    (scaledResolution.scaledHeight / 2 + 16).toFloat(),
                    (scaledResolution.scaledWidth / 2 - 22).toFloat()
                )
                renderItemStack(mc.thePlayer!!.inventory.mainInventory[slot]!!)
                GlStateManager.popMatrix()
            }
            GlStateManager.resetColor()
            Fonts.minecraftFont.drawString(
                "$blocksAmount blocks",
                (scaledResolution.scaledWidth / 2).toFloat(),
                (scaledResolution.scaledHeight / 2 + 20).toFloat(),
                -1,
                true
            )
        }
    }

    @EventTarget
    fun onRender3D(event: Render3DEvent?) {
        if (!markValue.get()) return
        val yaw = Math.toRadians(mc.thePlayer!!.rotationYaw.toDouble())
        val x = if (omniDirectionalExpand.get()) Math.round(-Math.sin(yaw))
            .toInt() else mc.thePlayer!!.horizontalFacing.directionVec.x
        val z = if (omniDirectionalExpand.get()) Math.round(Math.cos(yaw))
            .toInt() else mc.thePlayer!!.horizontalFacing.directionVec.z
        for (i in 0 until if (modeValue.get()
                .equals("Expand", ignoreCase = true) && !towerActivation()
        ) expandLengthValue.get() + 1 else 2) {
            val wBlockPos = WBlockPos(
                mc.thePlayer!!.posX + x * i,
                if (!towerActivation()
                    && (sameYValue.get() ||
                            ((autoJumpValue.get() || smartSpeedValue.get() && LiquidBounce.moduleManager.getModule(Speed::class.java).state)
                                    && mc.gameSettings.isKeyDown(mc.gameSettings.keyBindJump))) && launchY <= mc.thePlayer!!.posY
                ) launchY - 1.0 else mc.thePlayer!!.posY - (if (mc.thePlayer!!.posY == mc.thePlayer!!.posY.toInt() + 0.5) 0.0 else 1.0) - if (shouldGoDown) 1.0 else 0.0,
                mc.thePlayer!!.posZ + z * i
            )
            val placeInfo = get(wBlockPos)
            if (isReplaceable(wBlockPos) && placeInfo != null) {
                RenderUtils.drawBlockBox(
                    wBlockPos,
                    Color(redValue.get(), greenValue.get(), blueValue.get(), alphaValue.get()),
                    false
                )
                break
            }
        }
    }

    private fun renderItemStack(stack: IItemStack) {
        GlStateManager.pushMatrix()
        GlStateManager.enableRescaleNormal()
        GlStateManager.enableBlend()
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
        RenderHelper.enableGUIStandardItemLighting()
        mc.renderItem.renderItemAndEffectIntoGUI(stack, 0, 0)
        mc.renderItem.renderItemOverlays(mc.fontRendererObj, stack, 0, 0)
        RenderHelper.disableStandardItemLighting()
        GlStateManager.disableRescaleNormal()
        GlStateManager.disableBlend()
        GlStateManager.popMatrix()
    }

    private fun calcStepSize(range: Double): Double {
        var accuracy = searchAccuracyValue.get().toDouble()
        accuracy += accuracy % 2 // If it is set to uneven it changes it to even. Fixes a bug
        return if (range / accuracy < 0.01) 0.01 else range / accuracy
    }

    private fun search(WBlockPosition: WBlockPos, checks: Boolean): Boolean {
        return search(WBlockPosition, checks, false)
    }

    /**
     * Search for placeable block
     *
     * @param blockPosition pos
     * @param checks        visible
     * @return
     */
    private fun search(blockPosition: WBlockPos, checks: Boolean, towerActive: Boolean): Boolean {
        faceBlock = false
        // SearchRanges
        val xzRV = xzRangeValue.get().toDouble()
        val xzSSV = calcStepSize(xzRV)
        val yRV = yRangeValue.get().toDouble()
        val ySSV = calcStepSize(yRV)
        if (!isReplaceable(blockPosition)) return false
        val staticYawMode = rotationLookupValue.get().equals("AAC", ignoreCase = true) || rotationLookupValue.get()
            .equals("same", ignoreCase = true) && (rotationModeValue.get()
            .equals("AAC", ignoreCase = true) || rotationModeValue.get().contains("Static") && !rotationModeValue.get()
            .equals("static3", ignoreCase = true))
        val eyesPos = WVec3(
            mc.thePlayer!!.posX,
            mc.thePlayer!!.entityBoundingBox.minY + mc.thePlayer!!.eyeHeight,
            mc.thePlayer!!.posZ
        )
        var placeRotation: PlaceRotation? = null
        for (facingType in EnumFacingType.values()) {
            val side = classProvider.getEnumFacing(facingType)
            val neighbor = blockPosition.offset(side)
            if (!canBeClicked(neighbor)) continue
            val dirVec = WVec3(side.directionVec)
            var xSearch = 0.5 - xzRV / 2
            while (xSearch <= 0.5 + xzRV / 2) {
                var ySearch = 0.5 - yRV / 2
                while (ySearch <= 0.5 + yRV / 2) {
                    var zSearch = 0.5 - xzRV / 2
                    while (zSearch <= 0.5 + xzRV / 2) {
                        val posVec = WVec3(blockPosition).addVector(xSearch, ySearch, zSearch)
                        val distanceSqPosVec = eyesPos.squareDistanceTo(posVec)
                        val hitVec = posVec.add(WVec3(dirVec.xCoord * 0.5, dirVec.yCoord * 0.5, dirVec.zCoord * 0.5))
                        if (checks && (eyesPos.squareDistanceTo(hitVec) > 18.0 || distanceSqPosVec > eyesPos.squareDistanceTo(
                                posVec.add(dirVec)
                            ) || mc.theWorld!!.rayTraceBlocks(eyesPos, hitVec, false, true, false) != null)
                        ) {
                            zSearch += xzSSV
                            continue
                        }

                        // face block
                        for (i in 0 until if (staticYawMode) 2 else 1) {
                            val diffX: Double = if (staticYawMode && i == 0) 0.0 else hitVec.xCoord - eyesPos.xCoord
                            val diffY = hitVec.yCoord - eyesPos.yCoord
                            val diffZ: Double = if (staticYawMode && i == 1) 0.0 else hitVec.zCoord - eyesPos.zCoord
                            val diffXZ = MathHelper.sqrt(diffX * diffX + diffZ * diffZ).toDouble()
                            var rotation = Rotation(
                                MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(diffZ, diffX)).toFloat() - 90f),
                                MathHelper.wrapDegrees(-Math.toDegrees(Math.atan2(diffY, diffXZ)).toFloat())
                            )
                            lookupRotation = rotation
                            if (rotationModeValue.get().equals(
                                    "hypixel",
                                    ignoreCase = true
                                ) && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown)
                            ) rotation = Rotation(
                                mc.thePlayer!!.rotationYaw + (if (mc.thePlayer!!.movementInput.moveForward > 0) 180 else 0) + HypixelYawValue.get(),
                                HypixelPitchValue.get().toFloat()
                            )
                            if (rotationModeValue.get().equals(
                                    "static",
                                    ignoreCase = true
                                ) && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown)
                            ) rotation = Rotation(
                                MovementUtils.getScaffoldRotation(
                                    mc.thePlayer!!.rotationYaw, mc.thePlayer!!.moveForward
                                ), staticPitchValue.get()
                            )
                            if ((rotationModeValue.get().equals("static2", ignoreCase = true) || rotationModeValue.get()
                                    .equals(
                                        "static3",
                                        ignoreCase = true
                                    )) && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown)
                            ) rotation = Rotation(rotation.yaw, staticPitchValue.get())
                            if (rotationModeValue.get().equals(
                                    "custom",
                                    ignoreCase = true
                                ) && (keepRotOnJumpValue.get() || !mc.gameSettings.keyBindJump.isKeyDown)
                            ) rotation = Rotation(
                                mc.thePlayer!!.rotationYaw + customYawValue.get(), customPitchValue.get()
                            )
                            val rotationVector = RotationUtils.getVectorForRotation(
                                if (rotationLookupValue.get()
                                        .equals("same", ignoreCase = true)
                                ) rotation else lookupRotation
                            )
                            val vector = eyesPos.addVector(
                                rotationVector.xCoord * 4,
                                rotationVector.yCoord * 4,
                                rotationVector.zCoord * 4
                            )
                            val obj = mc.theWorld!!.rayTraceBlocks(eyesPos, vector, false, false, true)
                            if (!(obj!!.typeOfHit === IMovingObjectPosition.WMovingObjectType.BLOCK && obj!!.blockPos!!.equals(
                                    neighbor
                                ))
                            ) continue
                            if (placeRotation == null || RotationUtils.getRotationDifference(rotation) < RotationUtils.getRotationDifference(
                                    placeRotation.rotation
                                )
                            ) placeRotation = PlaceRotation(PlaceInfo(neighbor, side.opposite, hitVec), rotation)
                        }
                        zSearch += xzSSV
                    }
                    ySearch += ySSV
                }
                xSearch += xzSSV
            }
        }
        if (placeRotation == null) return false
        if (rotationsValue.get()) {
            if (minTurnSpeed.get() < 180) {
                val limitedRotation = RotationUtils.limitAngleChange(
                    RotationUtils.serverRotation,
                    placeRotation.rotation,
                    RandomUtils.nextFloat(minTurnSpeed.get(), maxTurnSpeed.get())
                )
                if ((10 * MathHelper.wrapDegrees(limitedRotation.yaw)).toInt() == (10 * MathHelper.wrapDegrees(
                        placeRotation.rotation.yaw
                    )).toInt()
                    && (10 * MathHelper.wrapDegrees(limitedRotation.pitch)).toInt() == (10 * MathHelper.wrapDegrees(
                        placeRotation.rotation.pitch
                    )).toInt()
                ) {
                    RotationUtils.setTargetRotation(placeRotation.rotation, keepLengthValue.get())
                    lockRotation = placeRotation.rotation
                    faceBlock = true
                } else {
                    RotationUtils.setTargetRotation(limitedRotation, keepLengthValue.get())
                    lockRotation = limitedRotation
                    faceBlock = false
                }
            } else {
                RotationUtils.setTargetRotation(placeRotation.rotation, keepLengthValue.get())
                lockRotation = placeRotation.rotation
                faceBlock = true
            }
            if (rotationLookupValue.get().equals("same", ignoreCase = true)) lookupRotation = lockRotation
        }
        if (towerActive) towerPlace = placeRotation.placeInfo else targetPlace = placeRotation.placeInfo
        return true
    }

    private val blocksAmount: Int
        /**
         * @return hotbar blocks amount
         */
        get() {
            var amount = 0
            for (i in 36..44) {
                val itemStack = mc.thePlayer!!.inventoryContainer.getSlot(i).stack
                if (itemStack != null && classProvider.isItemBlock(itemStack.item)) {
                    val block = itemStack.item!!.asItemBlock().block
                    val heldItem = mc.thePlayer!!.heldItem
                    if (heldItem != null && heldItem == itemStack || !InventoryUtils.BLOCK_BLACKLIST.contains(block) && !classProvider.isBlockBush(
                            block
                        )
                    ) amount += itemStack.stackSize
                }
            }
            return amount
        }
    override val tag: String
        get() = if (towerActivation()) "Tower, " + towerPlaceModeValue.get() else placeModeValue.get()
}