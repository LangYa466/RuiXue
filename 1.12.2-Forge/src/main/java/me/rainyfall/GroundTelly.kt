package me.rainyfall

import net.ccbluex.liquidbounce.LiquidBounce.moduleManager
import net.ccbluex.liquidbounce.api.enums.BlockType
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.EventTarget
import net.ccbluex.liquidbounce.event.TickEvent
import net.ccbluex.liquidbounce.event.UpdateEvent
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold
import net.ccbluex.liquidbounce.features.module.modules.world.Scaffold4
import net.ccbluex.liquidbounce.features.module.modules.world.Timer
import net.ccbluex.liquidbounce.utils.MovementUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.ListValue
import kotlin.jvm.internal.Intrinsics

@ModuleInfo(name = "GroundTelly", description = "GroundTelly", category = ModuleCategory.WORLD)
class GroundTelly : Module() {
    private val scaffoldModule =
        ListValue("ScaffoldModule", arrayOf("Scaffold", "Scaffold2", "Scaffold3", "Scaffold4"), "Scaffold")
    private val autoJumpValue = BoolValue("AutoJump", false)
    private val autoJumpHelper =
        ListValue("JumpHelper", arrayOf("Parkour", "Eagle", "Test"), "Parkour")
    private val autoJumpMode = ListValue(
        "AutoJumpMode", arrayOf(
            "MCInstanceJump",
            "MCInstance2Jump",
            "ClientMotionY"
        ), "MCInstanceJump"
    )
    private val eventTargetSelector = ListValue(
        "EventSelect", arrayOf(
            "onUpdate",
            "onTick"
        ), "onUpdate"
    )

    private val noBobValue = BoolValue("NoBob", false)

    companion object {
        @JvmStatic
        val noFovValue = BoolValue("NoFov", false)

        @JvmStatic
        var fovValue = FloatValue("FOV", 1f, 0f, 1.5f)
    }

    private val autoTimerValue = BoolValue("AutoTimer", false)
    private val autoPitchValue = BoolValue("setBestPitch", false)
    private val alwaysPitchValue = BoolValue("setPitch-onUpdate", false)
    private val customPitchValue = FloatValue("CustomPitch",26.5F,0F,90F)
    private val autoYawValue = ListValue("setYawMode", arrayOf("None", "onEnable", "onUpdate"), "None")
    private val disableAllOnEnable = BoolValue("Enable-DisableAll", false)
    private val disableAllOnDisable = BoolValue("Disable-DisableAll", false)

    override fun onEnable() {
        if (autoPitchValue.get()) {
            mc.thePlayer!!.rotationPitch = customPitchValue.get()
        }
        if (autoYawValue.get().equals("onEnable")) setYaw()

        if (disableAllOnEnable.get()) disableAll()
    }

    @EventTarget
    fun onUpdate(event: UpdateEvent?) {
        val thePlayer = mc.thePlayer!!

        if (autoPitchValue.get() && alwaysPitchValue.get()) {
            mc.thePlayer!!.rotationPitch = customPitchValue.get()
        }
        if (autoYawValue.get().equals("onUpdate")) setYaw()
        if (noBobValue.get()) mc.thePlayer!!.distanceWalkedModified = 0f
        if (!thePlayer.sneaking) {
            val thePlayer2 = mc.thePlayer
            if (thePlayer2 == null) {
                Intrinsics.throwNpe()
            }
            if (thePlayer2!!.onGround) {
                scaffoldChange(false)
                if (autoTimerValue.get()) if (moduleManager.getModule(Timer::class.java).state) moduleManager.getModule(
                    Timer::class.java
                ).state = false
            } else {
                scaffoldChange(true)
                if (autoTimerValue.get()) if (!moduleManager.getModule(Timer::class.java).state) moduleManager.getModule(
                    Timer::class.java
                ).state = true
            }
        }
        if (autoJumpValue.get() && eventTargetSelector.get().equals("onUpdate", true)) tryJump()
    }

    private fun jump() {
        when (autoJumpMode.get().toLowerCase()) {
            "mcinstancejump" -> mc.thePlayer!!.jump()
            "mcinstance2jump" -> mc2.player.jump()
            "clientmotiony" -> mc.thePlayer!!.motionY = 0.42
        }
    }

    @EventTarget
    fun onTick(event: TickEvent) {
        if (autoJumpValue.get() && eventTargetSelector.get().equals("onTick", true)) tryJump()
    }

    @EventTarget
    override fun onDisable() {
        scaffoldChange(false)
        if (autoTimerValue.get()) if (moduleManager.getModule(Timer::class.java).state) moduleManager.getModule(Timer::class.java).state =
            false
        if (disableAllOnDisable.get()) disableAll()
    }

    private fun scaffoldChange(state: Boolean) {
        when (scaffoldModule.get().toLowerCase()) {
            "scaffold" -> moduleManager.getModule(Scaffold::class.java).state = state
            "scaffold2" -> moduleManager.getModule(Scaffold2::class.java).state = state
            "scaffold3" -> moduleManager.getModule(Scaffold3::class.java).state = state
            "scaffold4" -> moduleManager.getModule(Scaffold4::class.java).state = state
        }
    }

    private fun tryJump() {
        val thePlayer = mc.thePlayer!!
        when (autoJumpHelper.get().toLowerCase()) {
            "parkour" -> if (MovementUtils.isMoving && thePlayer.onGround && !thePlayer.sneaking && !mc.gameSettings.keyBindSneak.isKeyDown && !mc.gameSettings.keyBindJump.isKeyDown &&
                mc.theWorld!!.getCollidingBoundingBoxes(
                    thePlayer, thePlayer.entityBoundingBox
                        .offset(0.0, -0.5, 0.0).expand(-0.001, 0.0, -0.001)
                ).isEmpty()
            ) {
                jump()
            }

            "eagle" -> {
                if (mc.theWorld!!.getBlockState(
                        WBlockPos(
                            thePlayer.posX,
                            thePlayer.posY - 1.0,
                            thePlayer.posZ
                        )
                    ).block == classProvider.getBlockEnum(
                        BlockType.AIR
                    ) && thePlayer.onGround
                ) jump()
            }

            "test" -> {
                if (thePlayer.onGround && MovementUtils.isMoving && thePlayer.sprinting) {
                    jump()
                }
            }
        }
    }

    private fun disableAll() {
        moduleManager.getModule(Scaffold::class.java).state = false
        moduleManager.getModule(Scaffold2::class.java).state = false
        moduleManager.getModule(Scaffold3::class.java).state = false
        moduleManager.getModule(Scaffold4::class.java).state = false
    }

    private fun setYaw() {
        val thePlayer = mc.thePlayer!!
        if (autoYawValue.get().toLowerCase().equals("none")) return
        val x = java.lang.Double.valueOf(thePlayer.motionX)
        val y = java.lang.Double.valueOf(thePlayer.motionZ)
        if (mc.gameSettings.keyBindForward.isKeyDown) {
            if (y != null &&
                y.toDouble() > 0.1
            ) {
                thePlayer.rotationYaw = 0.0f
            }
            if (y != null &&
                y.toDouble() < -0.1
            ) {
                thePlayer.rotationYaw = 180.0f
            }
            if (x != null &&
                x.toDouble() > 0.1
            ) {
                thePlayer.rotationYaw = -90.0f
            }
            if (x != null &&
                x.toDouble() < -0.1
            ) {
                thePlayer.rotationYaw = 90.0f
            }
        }

    }
}