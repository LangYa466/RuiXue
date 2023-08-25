
package net.ccbluex.liquidbounce.ui.client.hud.element.elements

import com.google.common.collect.Iterables
import com.google.common.collect.Lists
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.api.minecraft.scoreboard.IScoreObjective
import net.ccbluex.liquidbounce.api.minecraft.util.WEnumChatFormatting
import net.ccbluex.liquidbounce.feng.FontLoaders
import net.ccbluex.liquidbounce.ui.client.hud.element.Border
import net.ccbluex.liquidbounce.ui.client.hud.element.Element
import net.ccbluex.liquidbounce.ui.client.hud.element.ElementInfo
import net.ccbluex.liquidbounce.ui.client.hud.element.Side
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.render.ColorUtils
import net.ccbluex.liquidbounce.utils.render.RenderUtils
import net.ccbluex.liquidbounce.value.*
import net.minecraft.client.gui.Gui
import net.minecraft.client.renderer.GlStateManager
import org.lwjgl.opengl.GL11
import skid.ShadowUtils
import java.awt.Color

/**
 * CustomHUD scoreboard
 *
 * Allows to move and customize minecraft scoreboard
 */
@ElementInfo(name = "计分板")
class ScoreboardElement(
    x: Double = 5.0,
    y: Double = 0.0,
    scale: Float = 1F,
    side: Side = Side(Side.Horizontal.RIGHT, Side.Vertical.MIDDLE)
) : Element(x, y, scale, side) {

    private val textRedValue = IntegerValue("Text-R", 255, 0, 255)
    private val textGreenValue = IntegerValue("Text-G", 255, 0, 255)
    private val textBlueValue = IntegerValue("Text-B", 255, 0, 255)

    private val edittextvalue = TextValue("EditText","EDITTEXT可以自定义")

    private val backgroundColorRedValue = IntegerValue("Background-R", 0, 0, 255)
    private val backgroundColorGreenValue = IntegerValue("Background-G", 0, 0, 255)
    private val backgroundColorBlueValue = IntegerValue("Background-B", 0, 0, 255)
    private val backgroundColorAlphaValue = IntegerValue("Background-Alpha", 95, 0, 255)


    private val shadowShaderValue = BoolValue("Shadow", false)

    private val shadowColorMode = ListValue("Shadow-Color", arrayOf("Background", "Custom"), "Background")

    private val shadowColorRedValue = IntegerValue("Shadow-Red", 0, 0, 255)
    private val shadowColorGreenValue = IntegerValue("Shadow-Green", 111, 0, 255)
    private val shadowColorBlueValue = IntegerValue("Shadow-Blue", 255, 0, 255)
    private val rectValue = BoolValue("Rect", false)
    private val rectColorModeValue = ListValue("Rect-Color", arrayOf("Custom", "Rainbow"), "Custom")
    private val rectColorRedValue = IntegerValue("Rect-R", 0, 0, 255)
    private val rectColorGreenValue = IntegerValue("Rect-G", 111, 0, 255)
    private val rectColorBlueValue = IntegerValue("Rect-B", 255, 0, 255)
    private val rectColorBlueAlpha = IntegerValue("Rect-Alpha", 255, 0, 255)

    private val shadowValue = BoolValue("Shadow", false)
    private val serverValue = ListValue("ServerIp", arrayOf("None", "ClientName"), "ClientName")
    private val noPointValue = BoolValue("NoPoints", false)
    private val fontValue = FontValue("Font", Fonts.font35)

    private val allowedDomains = arrayOf(".ac", ".academy", ".accountant", ".accountants", ".actor", ".adult", ".ag", ".agency", ".ai", ".airforce", ".am", ".amsterdam", ".apartments", ".app", ".archi", ".army", ".art", ".asia", ".associates", ".at", ".attorney", ".au", ".auction", ".auto", ".autos", ".baby", ".band", ".bar", ".barcelona", ".bargains", ".bayern", ".be", ".beauty", ".beer", ".berlin", ".best", ".bet", ".bid", ".bike", ".bingo", ".bio", ".biz", ".biz.pl", ".black", ".blog", ".blue", ".boats", ".boston", ".boutique", ".build", ".builders", ".business", ".buzz", ".bz", ".ca", ".cab", ".cafe", ".camera", ".camp", ".capital", ".car", ".cards", ".care", ".careers", ".cars", ".casa", ".cash", ".casino", ".catering", ".cc", ".center", ".ceo", ".ch", ".charity", ".chat", ".cheap", ".church", ".city", ".cl", ".claims", ".cleaning", ".clinic", ".clothing", ".cloud", ".club", ".cn", ".co", ".co.in", ".co.jp", ".co.kr", ".co.nz", ".co.uk", ".co.za", ".coach", ".codes", ".coffee", ".college", ".com", ".com.ag", ".com.au", ".com.br", ".com.bz", ".com.cn", ".com.co", ".com.es", ".com.mx", ".com.pe", ".com.ph", ".com.pl", ".com.ru", ".com.tw", ".community", ".company", ".computer", ".condos", ".construction", ".consulting", ".contact", ".contractors", ".cooking", ".cool", ".country", ".coupons", ".courses", ".credit", ".creditcard", ".cricket", ".cruises", ".cymru", ".cz", ".dance", ".date", ".dating", ".de", ".deals", ".degree", ".delivery", ".democrat", ".dental", ".dentist", ".design", ".dev", ".diamonds", ".digital", ".direct", ".directory", ".discount", ".dk", ".doctor", ".dog", ".domains", ".download", ".earth", ".education", ".email", ".energy", ".engineer", ".engineering", ".enterprises", ".equipment", ".es", ".estate", ".eu", ".events", ".exchange", ".expert", ".exposed", ".express", ".fail", ".faith", ".family", ".fan", ".fans", ".farm", ".fashion", ".film", ".finance", ".financial", ".firm.in", ".fish", ".fishing", ".fit", ".fitness", ".flights", ".florist", ".fm", ".football", ".forsale", ".foundation", ".fr", ".fun", ".fund", ".furniture", ".futbol", ".fyi", ".gallery", ".games", ".garden", ".gay", ".gen.in", ".gg", ".gifts", ".gives", ".glass", ".global", ".gmbh", ".gold", ".golf", ".graphics", ".gratis", ".green", ".gripe", ".group", ".gs", ".guide", ".guru", ".hair", ".haus", ".health", ".healthcare", ".hockey", ".holdings", ".holiday", ".homes", ".horse", ".hospital", ".host", ".house", ".idv.tw", ".immo", ".immobilien", ".in", ".inc", ".ind.in", ".industries", ".info", ".info.pl", ".ink", ".institute", ".insure", ".international", ".investments", ".io", ".irish", ".ist", ".istanbul", ".it", ".jetzt", ".jewelry", ".jobs", ".jp", ".kaufen", ".kim", ".kitchen", ".kiwi", ".kr", ".la", ".land", ".law", ".lawyer", ".lease", ".legal", ".lgbt", ".life", ".lighting", ".limited", ".limo", ".live", ".llc", ".loan", ".loans", ".london", ".love", ".ltd", ".ltda", ".luxury", ".maison", ".makeup", ".management", ".market", ".marketing", ".mba", ".me", ".me.uk", ".media", ".melbourne", ".memorial", ".men", ".menu", ".miami", ".mobi", ".moda", ".moe", ".money", ".monster", ".mortgage", ".motorcycles", ".movie", ".ms", ".mx", ".nagoya", ".name", ".navy", ".ne.kr", ".net", ".net.ag", ".net.au", ".net.br", ".net.bz", ".net.cn", ".net.co", ".net.in", ".net.nz", ".net.pe", ".net.ph", ".net.pl", ".net.ru", ".network", ".news", ".ninja", ".nl", ".no", ".nom.co", ".nom.es", ".nom.pe", ".nrw", ".nyc", ".okinawa", ".one", ".onl", ".online", ".org", ".org.ag", ".org.au", ".org.cn", ".org.es", ".org.in", ".org.nz", ".org.pe", ".org.ph", ".org.pl", ".org.ru", ".org.uk", ".page", ".paris", ".partners", ".parts", ".party", ".pe", ".pet", ".ph", ".photography", ".photos", ".pictures", ".pink", ".pizza", ".pl", ".place", ".plumbing", ".plus", ".poker", ".porn", ".press", ".pro", ".productions", ".promo", ".properties", ".protection", ".pub", ".pw", ".quebec", ".quest", ".racing", ".re.kr", ".realestate", ".recipes", ".red", ".rehab", ".reise", ".reisen", ".rent", ".rentals", ".repair", ".report", ".republican", ".rest", ".restaurant", ".review", ".reviews", ".rich", ".rip", ".rocks", ".rodeo", ".ru", ".run", ".ryukyu", ".sale", ".salon", ".sarl", ".school", ".schule", ".science", ".se", ".security", ".services", ".sex", ".sg", ".sh", ".shiksha", ".shoes", ".shop", ".shopping", ".show", ".singles", ".site", ".ski", ".skin", ".soccer", ".social", ".software", ".solar", ".solutions", ".space", ".storage", ".store", ".stream", ".studio", ".study", ".style", ".supplies", ".supply", ".support", ".surf", ".surgery", ".sydney", ".systems", ".tax", ".taxi", ".team", ".tech", ".technology", ".tel", ".tennis", ".theater", ".theatre", ".tienda", ".tips", ".tires", ".today", ".tokyo", ".tools", ".tours", ".town", ".toys", ".top", ".trade", ".training", ".travel", ".tube", ".tv", ".tw", ".uk", ".university", ".uno", ".us", ".vacations", ".vegas", ".ventures", ".vet", ".viajes", ".video", ".villas", ".vin", ".vip", ".vision", ".vodka", ".vote", ".voto", ".voyage", ".wales", ".watch", ".webcam", ".website", ".wedding", ".wiki", ".win", ".wine", ".work", ".works", ".world", ".ws", ".wtf", ".xxx", ".xyz", ".yachts", ".yoga", ".yokohama", ".zone", "花雨庭", "855712180")

    /**
     * Draw element
     */
    override val drawElement: Border?
        get() {
            val fontRenderer = fontValue.get()
            val textColor = textColor().rgb
            val backColor = backgroundColor().rgb

            val rectColorMode = rectColorModeValue.get()
            val rectCustomColor = Color(
                rectColorRedValue.get(), rectColorGreenValue.get(), rectColorBlueValue.get(),
                rectColorBlueAlpha.get()
            ).rgb

            val worldScoreboard = mc.theWorld!!.scoreboard
            var currObjective: IScoreObjective? = null
            val playerTeam = worldScoreboard.getPlayersTeam(mc.thePlayer!!.name)


            if (playerTeam != null) {
                val colorIndex = playerTeam.chatFormat.colorIndex

                if (colorIndex >= 0)
                    currObjective = worldScoreboard.getObjectiveInDisplaySlot(3 + colorIndex)
            }

            val objective = currObjective ?: worldScoreboard.getObjectiveInDisplaySlot(1) ?: return null

            val scoreboard = objective.scoreboard
            var scoreCollection = scoreboard.getSortedScores(objective)
            val scores = Lists.newArrayList(Iterables.filter(scoreCollection) { input ->
                input?.playerName != null && !input.playerName.startsWith("#")
            })

            scoreCollection = if (scores.size > 15) {
                Lists.newArrayList(Iterables.skip(scores, scoreCollection.size - 15))
            } else {
                scores
            }

            var maxWidth = fontRenderer.getStringWidth(objective.displayName)

            for (score in scoreCollection) {
                val scorePlayerTeam = scoreboard.getPlayersTeam(score.playerName)
                val width = "${
                    functions.scoreboardFormatPlayerName(
                        scorePlayerTeam,
                        score.playerName
                    )
                }: ${WEnumChatFormatting.RED}${score.scorePoints}"
                maxWidth = maxWidth.coerceAtLeast(fontRenderer.getStringWidth(width))
            }

            val maxHeight = scoreCollection.size * fontRenderer.fontHeight
            val l1 = -maxWidth - 3 - if (rectValue.get()) 3 else 0
            if (shadowShaderValue.get()) {
                GL11.glTranslated(-renderX, -renderY, 0.0)
                GL11.glScalef(1F, 1F, 1F)
                GL11.glPushMatrix()
                ShadowUtils.shadow(20f, {
                    GL11.glPushMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                    GL11.glScalef(scale, scale, scale)
                    RenderUtils.newDrawRect(
                        l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                        -2F,
                        if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                        (maxHeight + fontRenderer.fontHeight).toFloat(),
                        if (shadowColorMode.get().equals("background", true))
                            Color(
                                backgroundColorRedValue.get(),
                                backgroundColorGreenValue.get(),
                                backgroundColorBlueValue.get()
                            ).rgb
                        else
                            Color(
                                shadowColorRedValue.get(),
                                shadowColorGreenValue.get(),
                                shadowColorBlueValue.get()
                            ).rgb
                    )
                    GL11.glPopMatrix()
                }, {
                    GL11.glPushMatrix()
                    GL11.glTranslated(renderX, renderY, 0.0)
                    GL11.glScalef(scale, scale, scale)
                    GlStateManager.enableBlend()
                    GlStateManager.disableTexture2D()
                    GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0)
                    RenderUtils.quickDrawRect(
                        l1.toFloat() + if (side.horizontal == Side.Horizontal.LEFT) 2F else -2F,
                        -2F,
                        if (side.horizontal == Side.Horizontal.LEFT) -5F else 5F,
                        (maxHeight + fontRenderer.fontHeight).toFloat()
                    )
                    GlStateManager.enableTexture2D()
                    GlStateManager.disableBlend()
                    GL11.glPopMatrix()
                })
                GL11.glPopMatrix()
                GL11.glScalef(scale, scale, scale)
                GL11.glTranslated(renderX, renderY, 0.0)
            }
            //Blur

            Gui.drawRect(l1 - 7, -5, 9, maxHeight + fontRenderer.fontHeight + 5, backColor)


            scoreCollection.forEachIndexed { index, score ->
                val team = scoreboard.getPlayersTeam(score.playerName)

                var name = functions.scoreboardFormatPlayerName(team, score.playerName)
                val scorePoints = "${WEnumChatFormatting.RED}${score.scorePoints}"

                val width = 5 - if (rectValue.get()) 4 else 0
                val height = maxHeight - index * fontRenderer.fontHeight

                GlStateManager.resetColor()

                var listColor = textColor
                if (!serverValue.equals("none")) {
                    for (domain in allowedDomains) {
                        if (name.contains(domain, true)) {
                            name = when (serverValue.get().toLowerCase()) {
                                "clientname" -> LiquidBounce.CLIENT_NAME
                                "eidt" -> edittextvalue.get()
                                "null" -> "null"
                                else -> ({}).toString()
                            }
                            listColor = Color(148, 87, 235).rgb
                            break
                        }
                    }
                }

                FontLoaders.C16.drawString(name, l1.toFloat(), height.toFloat(), listColor, shadowValue.get())
                if (!noPointValue.get()) {
                    FontLoaders.C16.drawString(
                        scorePoints,
                        (width - fontRenderer.getStringWidth(scorePoints)).toFloat(),
                        height.toFloat(),
                        textColor,
                        shadowValue.get()
                    )
                }

                if (index == scoreCollection.size - 1) {
                    val displayName = objective.displayName

                    GlStateManager.resetColor()

                    FontLoaders.C18.drawString(
                        displayName,
                        (l1 + maxWidth / 2 - fontRenderer.getStringWidth(displayName) / 2).toFloat(),
                        (height -
                                fontRenderer.fontHeight).toFloat(),
                        textColor,
                        shadowValue.get()
                    )
                }

                if (rectValue.get()) {
                    val rectColor = when {
                        rectColorMode.equals("Rainbow", ignoreCase = true) -> ColorUtils.rainbow(index).rgb
                        else -> rectCustomColor
                    }

                    RenderUtils.drawRect(
                        2F,
                        if (index == scoreCollection.size - 1) -2F else height.toFloat(),
                        5F,
                        if (index == 0) fontRenderer.fontHeight.toFloat() else height.toFloat() + fontRenderer.fontHeight
                                * 2F,
                        rectColor
                    )
                }
            }

            return Border(
                -maxWidth.toFloat() - 10f - if (rectValue.get()) 3 else 0,
                -5F,
                9F,
                maxHeight.toFloat() + fontRenderer.fontHeight + 5
            )
        }

    private fun backgroundColor() = Color(backgroundColorRedValue.get(), backgroundColorGreenValue.get(),
        backgroundColorBlueValue.get(), backgroundColorAlphaValue.get())

    private fun textColor() = Color(textRedValue.get(), textGreenValue.get(),
        textBlueValue.get())
}