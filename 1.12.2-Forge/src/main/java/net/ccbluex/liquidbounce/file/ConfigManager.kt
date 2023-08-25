package net.ccbluex.liquidbounce.file

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.utils.ClientUtils
import java.io.File
import java.util.*
import kotlin.concurrent.schedule

class ConfigManager {
    private val configSetFile = File(LiquidBounce.fileManager.dir, "config-settings.json")

    private val sections = mutableListOf<ConfigSection>()

    var nowConfig = "default"
    private var nowConfigInFile = "default"
    var configFile = File(LiquidBounce.fileManager.configsDir, "$nowConfig.json")
    var needSave = false

    init {

        // add an interval timer to save the config every 30 seconds
        Timer().schedule(30000, 30000) {
            saveTicker()
        }
    }

    fun load(name: String, save: Boolean = true) {
        LiquidBounce.isLoadingConfig = true
        if (save && nowConfig != name) {
            save(true, true) // 保存老配置
        }

        nowConfig = name
        configFile = File(LiquidBounce.fileManager.configsDir, "$nowConfig.json")

        val json = if (configFile.exists()) {
            JsonParser().parse(configFile.reader(Charsets.UTF_8)).asJsonObject
        } else {
            JsonObject() // 这样方便一点,虽然效率会低
        }

        for (section in sections) {
            section.load(
                if (json.has(section.sectionName)) {
                    json.getAsJsonObject(section.sectionName)
                } else {
                    JsonObject()
                }
            )
        }

        if (!configFile.exists()) {
            save(forceSave = true)
        }

        if (save) {
            saveConfigSet()
        }

        ClientUtils.displayChatMessage("Config $nowConfig.json loaded.")
        LiquidBounce.isLoadingConfig = false
    }

    fun save(saveConfigSet: Boolean = nowConfigInFile != nowConfig, forceSave: Boolean = false) {
        if (LiquidBounce.isLoadingConfig && !forceSave) {
            return
        }

        val config = JsonObject()

        for (section in sections) {
            config.add(section.sectionName, section.save())
        }

        configFile.writeText(FileManager.PRETTY_GSON.toJson(config), Charsets.UTF_8)

        if (saveConfigSet || forceSave) {
            saveConfigSet()
        }
        needSave = false

        ClientUtils.displayChatMessage("Config $nowConfig.json saved.")
    }

    private fun saveTicker() {
        if (!needSave) {
            return
        }
        save()
    }

    fun saveConfigSet() {
        val configSet = JsonObject()

        configSet.addProperty("file", nowConfig)

        configSetFile.writeText(FileManager.PRETTY_GSON.toJson(configSet), Charsets.UTF_8)
    }

}