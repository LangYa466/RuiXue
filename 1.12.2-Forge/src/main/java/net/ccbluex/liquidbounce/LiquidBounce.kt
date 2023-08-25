/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce

import liying.MethodObfuscator
import net.ccbluex.liquidbounce.api.Wrapper
import net.ccbluex.liquidbounce.api.minecraft.util.IResourceLocation
import net.ccbluex.liquidbounce.cape.CapeAPI.registerCapeService
import net.ccbluex.liquidbounce.event.ClientShutdownEvent
import net.ccbluex.liquidbounce.event.EventManager
import net.ccbluex.liquidbounce.features.command.CommandManager
import net.ccbluex.liquidbounce.features.module.ModuleManager
import net.ccbluex.liquidbounce.features.special.AntiForge
import net.ccbluex.liquidbounce.features.special.BungeeCordSpoof
import net.ccbluex.liquidbounce.features.special.DonatorCape
import net.ccbluex.liquidbounce.file.FileManager
import net.ccbluex.liquidbounce.script.ScriptManager
import net.ccbluex.liquidbounce.script.remapper.Remapper.loadSrg
import net.ccbluex.liquidbounce.tabs.BlocksTab
import net.ccbluex.liquidbounce.tabs.ExploitsTab
import net.ccbluex.liquidbounce.tabs.HeadsTab
import net.ccbluex.liquidbounce.ui.client.altmanager.GuiAltManager
import net.ccbluex.liquidbounce.ui.client.clickgui.ClickGui
import net.ccbluex.liquidbounce.ui.client.hud.HUD
import net.ccbluex.liquidbounce.ui.client.hud.HUD.Companion.createDefault
import net.ccbluex.liquidbounce.ui.font.Fonts
import net.ccbluex.liquidbounce.utils.ClassUtils.hasForge
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.CombatManager
import net.ccbluex.liquidbounce.utils.InventoryUtils
import net.ccbluex.liquidbounce.utils.RotationUtils
import skid.AnimationHandler
import javax.swing.JOptionPane

object LiquidBounce {

    // Client information
    const val CLIENT_NAME = "瑞雪"
    const val CLIENT_VERSION = 1.1
    const val CLIENT_TIME = 2023
    const val CLIENT_CREATOR = "CCbluex,Nelly"
    const val CLIENT_CLOUD = "https://cloud.liquidbounce.net/LiquidBounce"

    var isStarting = false

    // Managers
    lateinit var moduleManager: ModuleManager
    lateinit var combatManager: CombatManager
    lateinit var commandManager: CommandManager
    lateinit var eventManager: EventManager
    lateinit var fileManager: FileManager

    lateinit var scriptManager: ScriptManager
    var isLoadingConfig = true

    // HUD & ClickGUI
    lateinit var hud: HUD
    lateinit var animationHandler: AnimationHandler
    lateinit var clickGui: ClickGui

    // Update information
    var latestVersion = 0

    // Menu Background
    var background: IResourceLocation? = null

    lateinit var wrapper: Wrapper

    /**
     * Execute if client will be started
     */
@MethodObfuscator
    fun startClient() {
        isStarting = true
        JOptionPane.showMessageDialog(null, "本水影QQ交流群！348091392 ")
        JOptionPane.showMessageDialog(null, "本水影完全免费！")
        JOptionPane.showMessageDialog(null, "如果发现本水影出现在其他群举报可以得到金钱奖励！")
        JOptionPane.showMessageDialog(null, "如果发现本水影出现在其他群举报可以得到金钱奖励！")
        ClientUtils.getLogger().info("Starting $CLIENT_NAME b$CLIENT_VERSION, by $CLIENT_CREATOR")

        // Create file manager
        fileManager = FileManager()

        // Crate event manager
        eventManager = EventManager()

        // Register listeners
        eventManager.registerListener(RotationUtils())
        eventManager.registerListener(AntiForge())
        eventManager.registerListener(BungeeCordSpoof())
        eventManager.registerListener(DonatorCape())
        eventManager.registerListener(InventoryUtils())

        // Create command manager
        commandManager = CommandManager()

        // Load client fonts
        Fonts.loadFonts()

        // Setup module manager and register modules
        moduleManager = ModuleManager()
        moduleManager.registerModules()
        animationHandler = AnimationHandler()
        // Remapper
        try {
            loadSrg()

            // ScriptManager
            scriptManager = ScriptManager()
            scriptManager.loadScripts()
            scriptManager.enableScripts()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to load scripts.", throwable)
        }

        // Register commands
        commandManager.registerCommands()

        // Load configs
        fileManager.loadConfigs(
            *arrayOf(
                fileManager.modulesConfig,
                fileManager.valuesConfig,
                fileManager.accountsConfig,
                fileManager.friendsConfig,
                fileManager.xrayConfig,
                fileManager.shortcutsConfig
            )
        )

        // ClickGUI
        clickGui = ClickGui()
        fileManager.loadConfig(fileManager.clickGuiConfig)

        // Tabs (Only for Forge!)
        if (hasForge()) {
            BlocksTab()
            ExploitsTab()
            HeadsTab()
        }

        // Register capes service
        try {
            registerCapeService()
        } catch (throwable: Throwable) {
            ClientUtils.getLogger().error("Failed to register cape service", throwable)
        }

        // Set HUD
        hud = createDefault()
        fileManager.loadConfig(fileManager.hudConfig)

        // Disable optifine fastrender
        ClientUtils.disableFastRender()

        // Load generators
        GuiAltManager.loadGenerators()
        // Set is starting status
        isStarting = false
        ClientUtils.getLogger().info(" ____        ___  __                ____               _   _      _ _             _____              _   _            _    \n" +
                "|  _ \\ _   _(_) \\/ /   _  ___      |  _ \\  _____   ___| \\ | | ___| | |_   _      |  ___| __ ___  ___| | | | __ _  ___| | __\n" +
                "| |_) | | | | |\\  / | | |/ _ \\_____| | | |/ _ \\ \\ / (_)  \\| |/ _ \\ | | | | |_____| |_ | '__/ _ \\/ _ \\ |_| |/ _` |/ __| |/ /\n" +
                "|  _ <| |_| | |/  \\ |_| |  __/_____| |_| |  __/\\ V / _| |\\  |  __/ | | |_| |_____|  _|| | |  __/  __/  _  | (_| | (__|   < \n" +
                "|_| \\_\\\\__,_|_/_/\\_\\__,_|\\___|     |____/ \\___| \\_/ (_)_| \\_|\\___|_|_|\\__, |     |_|  |_|  \\___|\\___|_| |_|\\__,_|\\___|_|\\_\\\n" +
                "                                                                      |___/                                                \n" +
                " __  __           _ \n" +
                "|  \\/  | ___   __| |\n" +
                "| |\\/| |/ _ \\ / _` |\n" +
                "| |  | | (_) | (_| |\n" +
                "|_|  |_|\\___/ \\__,_|\n" +
                "                    ")
    }

    /**
     * Execute if client will be stopped
     */
    fun stopClient() {
        // Call client shutdown
        eventManager.callEvent(ClientShutdownEvent())

        // Save all available configs
        fileManager.saveAllConfigs()
    }
}
