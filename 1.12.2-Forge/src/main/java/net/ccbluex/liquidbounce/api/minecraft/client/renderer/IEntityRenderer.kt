/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */

package net.ccbluex.liquidbounce.api.minecraft.client.renderer

import net.ccbluex.liquidbounce.api.minecraft.client.shader.IShaderGroup
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.minecraft.util.ResourceLocation

interface IEntityRenderer {
    val shaderGroup: IShaderGroup?

    fun disableLightmap()
    fun isShaderActive(): Boolean
    fun loadShader(resourceLocation: IResourceLocation)
    fun loadShader2(resourceLocation: ResourceLocation)
    fun stopUseShader()
    fun setupCameraTransform(partialTicks: Float, i: Int)
    fun setupOverlayRendering()
}