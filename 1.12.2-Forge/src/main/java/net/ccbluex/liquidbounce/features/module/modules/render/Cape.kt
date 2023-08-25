
package net.ccbluex.liquidbounce.features.module.modules.render

import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.value.ListValue

import net.minecraft.util.ResourceLocation

@ModuleInfo(name = "Cape", description = "Inside Custom capes.", chinesename = "披风", category = ModuleCategory.RENDER)
class Cape : Module() {

    val styleValue = ListValue(
        "Style",
        arrayOf(
            "瑞雪",
            "Dark",
            "Astolfo",
            "Azrael",
            "Light",
            "Special1",
            "Special2",
            "BiliBili",
            "JiaRan",
            "Paimon",
            "lunar",
            "NekoCat",
            "NekoCat2",
            "NekoCat3",
            "NekoCat4",
            "Arona",
            "Mika1",
            "Mika2",
            "Planetarium",
            "Rise6",
            "Novoline",
            "MiaSakura"
        ),
        "瑞雪"
    )

    fun getCapeLocation(value: String): ResourceLocation {
        return try {
            CapeStyle.valueOf(value.toUpperCase()).location
        } catch (e: IllegalArgumentException) {
            CapeStyle.DARK.location
        }
    }

    enum class CapeStyle(val location: ResourceLocation) {
        瑞雪(ResourceLocation("liquidbounce/capes/ruixue.png")),
        DARK(ResourceLocation("liquidbounce/capes/dark.png")),
        ASTOLFO(ResourceLocation("liquidbounce/capes/astolfo.png")),
        LIGHT(ResourceLocation("liquidbounce/capes/light.png")),
        AZRAEL(ResourceLocation("liquidbounce/capes/azrael.png")),
        SPECIAL1(ResourceLocation("liquidbounce/capes/special1.png")),
        SPECIAL2(ResourceLocation("liquidbounce/capes/special2.png")),
        BILIBILI(ResourceLocation("liquidbounce/capes/bilibili.png")),
        JIARAN(ResourceLocation("liquidbounce/capes/jiaran.png")),
        PAIMON(ResourceLocation("liquidbounce/capes/paimon.png")),
        LUNAR(ResourceLocation("liquidbounce/capes/lunar.png")),
        NEKOCAT(ResourceLocation("liquidbounce/capes/nekocat.png")),
        NEKOCAT2(ResourceLocation("liquidbounce/capes/nekocat2.png")),
        NEKOCAT3(ResourceLocation("liquidbounce/capes/nekocat3.png")),
        NEKOCAT4(ResourceLocation("liquidbounce/capes/nekocat4.png")),
        RISE6(ResourceLocation("liquidbounce/capes/rise6.png")),
        MIASAKURA(ResourceLocation("liquidbounce/capes/miasakurajima.png")),
        NOVOLINE(ResourceLocation("liquidbounce/capes/novoline.png")),
        ARONA(ResourceLocation("liquidbounce/capes/arona.png")),
        MIKA1(ResourceLocation("liquidbounce/capes/mika1.png")),
        MIKA2(ResourceLocation("liquidbounce/capes/mika2.png")),
        PLANETARIUM(ResourceLocation("liquidbounce/capes/planetarium.png")),
    }

    override val tag: String
        get() = styleValue.get()

}