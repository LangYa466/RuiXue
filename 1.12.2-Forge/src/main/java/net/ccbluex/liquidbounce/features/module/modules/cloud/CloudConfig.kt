package net.ccbluex.liquidbounce.features.module.modules.cloud

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.module.Module
import net.ccbluex.liquidbounce.features.module.ModuleCategory
import net.ccbluex.liquidbounce.features.module.ModuleInfo
import net.ccbluex.liquidbounce.file.FileConfig
import net.ccbluex.liquidbounce.file.configs.ClickGuiConfig
import net.ccbluex.liquidbounce.file.configs.HudConfig
import net.ccbluex.liquidbounce.file.configs.ModulesConfig
import net.ccbluex.liquidbounce.file.configs.ValuesConfig
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.value.ListValue
import java.io.File

@ModuleInfo(name = "CloudConfig", description = "CloudConfig.", category = ModuleCategory.VULGAR)
class CloudConfig : Module() {
    private val modeValue = ListValue("Mode", arrayOf("Test", "Null"), "Null")
    val cloudconfigFile = LiquidBounce.fileManager.cloudconfigDir
    val cloudhudConfig: FileConfig = HudConfig(File(cloudconfigFile, "hud.json"))
    val modulesCloudConfig: FileConfig = ModulesConfig(File(cloudconfigFile, "modules.json"))
    val valuesCloudConfig: FileConfig = ValuesConfig(File(cloudconfigFile, "values.json"))
    val clickGuiCloudConfig: FileConfig = ClickGuiConfig(File(cloudconfigFile,"clickgui.json"))
    override fun onEnable() {
        when (modeValue.get().toLowerCase()) {
            "test" -> {
                LiquidBounce.fileManager.loadConfigs(modulesCloudConfig, valuesCloudConfig)
                LiquidBounce.fileManager.loadConfig(clickGuiCloudConfig)
                LiquidBounce.fileManager.loadConfig(cloudhudConfig)
                ClientUtils.displayChatMessage(LiquidBounce.CLIENT_NAME + " | 切换云参数成功")
            }
            "null" -> {
                LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.hudConfig)
                LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.clickGuiConfig)
                LiquidBounce.fileManager.loadConfigs(LiquidBounce.fileManager.modulesConfig, LiquidBounce.fileManager.valuesConfig, LiquidBounce.fileManager.accountsConfig, LiquidBounce.fileManager.friendsConfig, LiquidBounce.fileManager.xrayConfig, LiquidBounce.fileManager.shortcutsConfig)
                ClientUtils.displayChatMessage(LiquidBounce.CLIENT_NAME + " | 切换本地参数成功")
            }
        }
    }
    override fun onDisable() {
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.hudConfig)
        LiquidBounce.fileManager.loadConfig(LiquidBounce.fileManager.clickGuiConfig)
        LiquidBounce.fileManager.loadConfigs(LiquidBounce.fileManager.modulesConfig, LiquidBounce.fileManager.valuesConfig, LiquidBounce.fileManager.accountsConfig, LiquidBounce.fileManager.friendsConfig, LiquidBounce.fileManager.xrayConfig, LiquidBounce.fileManager.shortcutsConfig)
        ClientUtils.displayChatMessage(LiquidBounce.CLIENT_NAME + " | 自动切换本地参数")
    }
}









