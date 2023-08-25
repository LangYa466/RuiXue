/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */

package net.ccbluex.liquidbounce.injection.backend

import com.google.common.collect.ImmutableMap
import net.ccbluex.liquidbounce.api.minecraft.client.entity.IEntityPlayerSP
import net.ccbluex.liquidbounce.api.minecraft.client.network.IINetHandlerPlayClient
import net.ccbluex.liquidbounce.api.minecraft.util.IIChatComponent
import net.ccbluex.liquidbounce.api.minecraft.util.IMovementInput
import net.minecraft.block.Block
import net.minecraft.block.material.EnumPushReaction
import net.minecraft.block.material.MapColor
import net.minecraft.block.material.Material
import net.minecraft.block.properties.IProperty
import net.minecraft.block.state.BlockFaceShape
import net.minecraft.block.state.IBlockState
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.entity.Entity
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.EnumBlockRenderType
import net.minecraft.util.EnumFacing
import net.minecraft.util.Mirror
import net.minecraft.util.Rotation
import net.minecraft.util.math.AxisAlignedBB
import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.RayTraceResult
import net.minecraft.util.math.Vec3d
import net.minecraft.world.IBlockAccess
import net.minecraft.world.World

open class EntityPlayerSPImpl<T : EntityPlayerSP>(wrapped: T) : AbstractClientPlayerImpl<T>(wrapped), IEntityPlayerSP {
    override var horseJumpPowerCounter: Int
        get() = wrapped.horseJumpPowerCounter
        set(value) {
            wrapped.horseJumpPowerCounter = value
        }
    override var horseJumpPower: Float
        get() = wrapped.horseJumpPower
        set(value) {
            wrapped.horseJumpPower = value
        }
    override val isHandActive: Boolean
        get() = wrapped.isHandActive
    override val sendQueue: IINetHandlerPlayClient
        get() = wrapped.connection.wrap()
    override val movementInput: IMovementInput
        get() = wrapped.movementInput.wrap()
    override val sneaking: Boolean
        get() = wrapped.isSneaking

    override fun onBlockEventReceived(p0: World, p1: BlockPos, p2: Int, p3: Int): Boolean {
        TODO("Not yet implemented")
    }

    override fun neighborChanged(p0: World, p1: BlockPos, p2: Block, p3: BlockPos) {
        TODO("Not yet implemented")
    }

    override fun getMaterial(): Material {
        TODO("Not yet implemented")
    }

    override fun isFullBlock(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canEntitySpawn(p0: Entity): Boolean {
        TODO("Not yet implemented")
    }

    override fun getLightOpacity(): Int {
        TODO("Not yet implemented")
    }

    override fun getLightOpacity(p0: IBlockAccess, p1: BlockPos): Int {
        TODO("Not yet implemented")
    }

    override fun getLightValue(): Int {
        TODO("Not yet implemented")
    }

    override fun getLightValue(p0: IBlockAccess, p1: BlockPos): Int {
        TODO("Not yet implemented")
    }

    override fun isTranslucent(): Boolean {
        TODO("Not yet implemented")
    }

    override fun useNeighborBrightness(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getMapColor(p0: IBlockAccess, p1: BlockPos): MapColor {
        TODO("Not yet implemented")
    }

    override fun withRotation(p0: Rotation): IBlockState {
        TODO("Not yet implemented")
    }

    override fun withMirror(p0: Mirror): IBlockState {
        TODO("Not yet implemented")
    }

    override fun isFullCube(): Boolean {
        TODO("Not yet implemented")
    }

    override fun hasCustomBreakingProgress(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getRenderType(): EnumBlockRenderType {
        TODO("Not yet implemented")
    }

    override fun getPackedLightmapCoords(p0: IBlockAccess, p1: BlockPos): Int {
        TODO("Not yet implemented")
    }

    override fun getAmbientOcclusionLightValue(): Float {
        TODO("Not yet implemented")
    }

    override fun isBlockNormalCube(): Boolean {
        TODO("Not yet implemented")
    }

    override fun isNormalCube(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canProvidePower(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getWeakPower(p0: IBlockAccess, p1: BlockPos, p2: EnumFacing): Int {
        TODO("Not yet implemented")
    }

    override fun hasComparatorInputOverride(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getComparatorInputOverride(p0: World, p1: BlockPos): Int {
        TODO("Not yet implemented")
    }

    override fun getBlockHardness(p0: World, p1: BlockPos): Float {
        TODO("Not yet implemented")
    }

    override fun getPlayerRelativeBlockHardness(p0: EntityPlayer, p1: World, p2: BlockPos): Float {
        TODO("Not yet implemented")
    }

    override fun getStrongPower(p0: IBlockAccess, p1: BlockPos, p2: EnumFacing): Int {
        TODO("Not yet implemented")
    }

    override fun getMobilityFlag(): EnumPushReaction {
        TODO("Not yet implemented")
    }

    override fun getActualState(p0: IBlockAccess, p1: BlockPos): IBlockState {
        TODO("Not yet implemented")
    }

    override fun getSelectedBoundingBox(p0: World, p1: BlockPos): AxisAlignedBB {
        TODO("Not yet implemented")
    }

    override fun shouldSideBeRendered(p0: IBlockAccess, p1: BlockPos, p2: EnumFacing): Boolean {
        TODO("Not yet implemented")
    }

    override fun isOpaqueCube(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getCollisionBoundingBox(p0: IBlockAccess, p1: BlockPos): AxisAlignedBB? {
        TODO("Not yet implemented")
    }

    override fun addCollisionBoxToList(
        p0: World,
        p1: BlockPos,
        p2: AxisAlignedBB,
        p3: MutableList<AxisAlignedBB>,
        p4: Entity?,
        p5: Boolean
    ) {
        TODO("Not yet implemented")
    }

    override fun getBoundingBox(p0: IBlockAccess, p1: BlockPos): AxisAlignedBB {
        TODO("Not yet implemented")
    }

    override fun collisionRayTrace(p0: World, p1: BlockPos, p2: Vec3d, p3: Vec3d): RayTraceResult {
        TODO("Not yet implemented")
    }

    override fun isTopSolid(): Boolean {
        TODO("Not yet implemented")
    }

    override fun doesSideBlockRendering(p0: IBlockAccess, p1: BlockPos, p2: EnumFacing): Boolean {
        TODO("Not yet implemented")
    }

    override fun isSideSolid(p0: IBlockAccess, p1: BlockPos, p2: EnumFacing): Boolean {
        TODO("Not yet implemented")
    }

    override fun doesSideBlockChestOpening(p0: IBlockAccess, p1: BlockPos, p2: EnumFacing): Boolean {
        TODO("Not yet implemented")
    }

    override fun getOffset(p0: IBlockAccess, p1: BlockPos): Vec3d {
        TODO("Not yet implemented")
    }

    override fun causesSuffocation(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBlockFaceShape(p0: IBlockAccess, p1: BlockPos, p2: EnumFacing): BlockFaceShape {
        TODO("Not yet implemented")
    }

    override fun getPropertyKeys(): MutableCollection<IProperty<*>> {
        TODO("Not yet implemented")
    }

    override fun <T : Comparable<T>?> getValue(p0: IProperty<T>): T {
        TODO("Not yet implemented")
    }

    override fun <T : Comparable<T>?, V : T> withProperty(p0: IProperty<T>, p1: V): IBlockState {
        TODO("Not yet implemented")
    }

    override fun <T : Comparable<T>?> cycleProperty(p0: IProperty<T>): IBlockState {
        TODO("Not yet implemented")
    }

    override fun getProperties(): ImmutableMap<IProperty<*>, Comparable<*>> {
        TODO("Not yet implemented")
    }

    override fun getBlock(): Block {
        TODO("Not yet implemented")
    }

    override var serverSprintState: Boolean
        get() = wrapped.serverSprintState
        set(value) {
            wrapped.serverSprintState = value
        }

    override fun sendChatMessage(msg: String) = wrapped.sendChatMessage(msg)

    override fun respawnPlayer() = wrapped.respawnPlayer()

    override fun addChatMessage(component: IIChatComponent) = wrapped.sendMessage(component.unwrap())

    override fun closeScreen() = wrapped.closeScreen()
}

inline fun IEntityPlayerSP.unwrap(): EntityPlayerSP = (this as EntityPlayerSPImpl<*>).wrapped
inline fun EntityPlayerSP.wrap(): IEntityPlayerSP = EntityPlayerSPImpl(this)