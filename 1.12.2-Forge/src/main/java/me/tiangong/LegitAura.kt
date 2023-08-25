package me.tiangong

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntity
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityLivingBase
import net.ccbluex.liquidbounce.api.minecraft.util.WBlockPos
import net.ccbluex.liquidbounce.event.*
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.features.module.modules.combat.NoFriends
import net.ccbluex.liquidbounce.features.module.modules.misc.AntiBot
import net.ccbluex.liquidbounce.features.module.modules.player.Reach
import net.ccbluex.liquidbounce.features.module.modules.hyt.Teams
import net.ccbluex.liquidbounce.utils.EntityUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import net.ccbluex.liquidbounce.utils.createOpenInventoryPacket
import net.ccbluex.liquidbounce.utils.extensions.getDistanceToEntityBox
import net.ccbluex.liquidbounce.utils.extensions.isAnimal
import net.ccbluex.liquidbounce.utils.extensions.isClientFriend
import net.ccbluex.liquidbounce.utils.extensions.isMob
import net.ccbluex.liquidbounce.utils.misc.RandomUtils
import net.ccbluex.liquidbounce.utils.timer.MSTimer
import net.ccbluex.liquidbounce.utils.timer.TimeUtils
import net.ccbluex.liquidbounce.value.BoolValue
import net.ccbluex.liquidbounce.value.FloatValue
import net.ccbluex.liquidbounce.value.IntegerValue
import net.ccbluex.liquidbounce.value.ListValue
import net.minecraft.client.settings.KeyBinding
import net.minecraft.network.play.client.CPacketPlayerDigging
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.BlockPos
import java.awt.Robot
import java.awt.event.InputEvent
import java.util.*
import kotlin.math.max

@ModuleInfo(name = "LegitAura", description = "It allows you to hit people safely.", category = ModuleCategory.COMBAT)
class LegitAura : Module() {
    private val maxCPSValue: IntegerValue = object : IntegerValue("MaxCPS", 8, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val minCPS = minCPSValue.get()
            if (minCPS > newValue)
                set(minCPS)
        }
    }
    private val minCPSValue: IntegerValue = object : IntegerValue("MinCPS", 5, 1, 20) {
        override fun onChanged(oldValue: Int, newValue: Int) {
            val maxCPS = maxCPSValue.get()
            if (maxCPS < newValue)
                set(maxCPS)
        }
    }
    private val reach = FloatValue("Range", 3.5f, 3f, 7F)
    private val throughWallsRangeValue = FloatValue("ThroughWallsRange", 1.5f, 0f, 8f)
    private val targetModeValue = ListValue("TargetMode", arrayOf("Single", "Switch", "Multi"), "Switch")

    // Turn Speed
    private val maxTurnSpeed: FloatValue = object : FloatValue("MaxTurnSpeed", 120f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minTurnSpeed.get()
            if (v > newValue) set(v)
        }
    }

    private val minTurnSpeed: FloatValue = object : FloatValue("MinTurnSpeed", 90f, 0f, 180f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxTurnSpeed.get()
            if (v < newValue) set(v)
        }
    }

    // Predict
    private val predictValue = BoolValue("Predict", true)

    private val maxPredictSize: FloatValue = object : FloatValue("MaxPredictSize", 1.65f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = minPredictSize.get()
            if (v > newValue) set(v)
        }
    }

    private val minPredictSize: FloatValue = object : FloatValue("MinPredictSize", 1.2f, 0.1f, 5f) {
        override fun onChanged(oldValue: Float, newValue: Float) {
            val v = maxPredictSize.get()
            if (v < newValue) set(v)
        }
    }
    private val priorityValue =
        ListValue("Priority", arrayOf("Health", "Distance", "Direction", "LivingTime"), "Distance")
    val hurtTimeValue = IntegerValue("HurtTime", 10, 0, 10)
    private val canrotation = BoolValue("CanRotation", false)
    private val turnSpeedValue = FloatValue("TurnSpeed", 2F, 1F, 180F)
    private val fovValue = FloatValue("FOV", 180F, 1F, 180F)
    private val centerValue = BoolValue("Center", false)
    private val rotations = ListValue("RotationMode", arrayOf("None", "New", "BackTrack", "HytRotation"), "HytRotation")
    private val outborderValue = BoolValue("Outborder", false)
    private val hytValue = BoolValue("HytRotaTionBypassNew", true)
    private val randomCenterValue = BoolValue("RandomCenter", true)
    private val silentRotationValue = BoolValue("SilentRotation", true)
    private var leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
    private var leftLastSwing = 0L
    private val prevTargetEntities = mutableListOf<Int>()
    private val switchTimer = MSTimer()
    var target: IEntityLivingBase? = null
    private var currentTarget: IEntityLivingBase? = null

    override fun onEnable() {
        val Reach = LiquidBounce.moduleManager.getModule(Reach::class.java) as Reach
        Reach.combatReachValue.set(reach.get())
        Reach.state = true
        updateTarget()
    }

    override fun onDisable() {
        target = null
        currentTarget = null
        val Reach = LiquidBounce.moduleManager.getModule(Reach::class.java) as Reach
        Reach.state = false
    }

    private fun updateTarget() {
        var target: IEntityLivingBase? = null
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
            if (!classProvider.isEntityLivingBase(entity) || !isEnemy(entity) || (switchMode && prevTargetEntities.contains(
                    entity.entityId
                ))
            )
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
    fun update() {

        // Update target
        updateTarget()

        // Target
        currentTarget = target

        if (!targetModeValue.get().equals("Switch", ignoreCase = true) && isEnemy(currentTarget))
            target = currentTarget
    }
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
    @EventTarget
    fun onUpdate(event: UpdateEvent) {
       // updateTarget()
        LegitClick()
    }
    @EventTarget
    fun onMotion(event: MotionEvent) {
        if (event.eventState == EventState.PRE) {
            update()
        }
    }
    @EventTarget
    fun onStrafe(event: StrafeEvent) {
        val range = reach.get()
        val player = mc.thePlayer ?: return
        if (canrotation.get()) {
            val entity = mc.theWorld!!.loadedEntityList
                .filter {
                    EntityUtils.isSelected(it, true) && player.canEntityBeSeen(it) &&
                            player.getDistanceToEntityBox(it) <= range && RotationUtils.getRotationDifference(it) <= fovValue.get()
                }
                .minBy { RotationUtils.getRotationDifference(target) } ?: return

            val boundingBox = entity.entityBoundingBox ?: return

            val destinationRotation = if (centerValue.get()) {
                RotationUtils.toRotation(RotationUtils.getCenter(boundingBox) ?: return, true)
            } else {
                RotationUtils.searchCenter(boundingBox, false, false, true, false, range).rotation ?: return
            }

            val rotation = RotationUtils.limitAngleChange(
                RotationUtils.targetRotation,
                destinationRotation,
                (turnSpeedValue.get() + Math.random()).toFloat()
            )
            rotation.toPlayer(player)
        }
    }
    private fun updateRotations(entity: IEntity): Boolean {
        var boundingBox = entity.entityBoundingBox
        val attackTimer = MSTimer()
        var attackDelay = 0L
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

            val limitedRotation = RotationUtils.limitAngleChange(
                RotationUtils.serverRotation,
                rotation,
                (Math.random() * (maxTurnSpeed.get() - minTurnSpeed.get()) + minTurnSpeed.get()).toFloat()
            )

            if (silentRotationValue.get())
                RotationUtils.setTargetRotation(limitedRotation, if (hytValue.get()) 15 else 0)
            else
                limitedRotation.toPlayer(mc.thePlayer!!)

            return true
        }
        return false
    }

    fun LegitClick() {
        val range = reach.get()
        val player = mc.thePlayer ?: return
        val entity = mc.theWorld!!.loadedEntityList.filter {
            EntityUtils.isSelected(
                it,
                true
            ) && player.canEntityBeSeen(it) && player.getDistanceToEntityBox(it) <= range
        }.minBy { RotationUtils.getRotationDifference(player) } ?: return
        if (entity != mc.thePlayer && player.getDistanceToEntityBox(entity) <= range) {
            if (!entity.isDead) {
                if (System.currentTimeMillis() - leftLastSwing >= leftDelay && mc.playerController.curBlockDamageMP == 0F) {
                    KeyBinding.onTick(mc.gameSettings.keyBindAttack.keyCode)
                    leftLastSwing = System.currentTimeMillis()
                    leftDelay = TimeUtils.randomClickDelay(minCPSValue.get(), maxCPSValue.get())
                }
            }
        }
    }

    private val maxRange: Float
        get() = max(reach.get(), throughWallsRangeValue.get())
    private fun isAlive(entity: IEntityLivingBase) = entity.entityAlive && entity.health > 0 ||
            hytValue.get() && entity.hurtTime > 5
}

// By BingLiang Fix TG
