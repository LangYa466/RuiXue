package net.ccbluex.liquidbounce.features.command.commands

import com.google.gson.Gson
import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.Notification
import net.ccbluex.liquidbounce.ui.client.hud.element.elements.NotifyType
import net.ccbluex.liquidbounce.utils.ClientUtils
import net.ccbluex.liquidbounce.utils.SettingsUtils
import java.io.File
import java.net.URL

class CloudConfigCommand : Command("cloudconfig", "cc") {

    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when (args[1].toLowerCase()) {
                "load" -> {
                    if (args.size > 2) {
                        val configName = args[2]

                        try {
                            val url = URL("https://gitcode.net/m0_62964839/瑞雪/-/raw/master/cloudconfig/$configName.json")

                            val config = url.readText()

                            SettingsUtils.executeScript(config)

                            ClientUtils.displayChatMessage("§6$configName 配置文件加载成功！")
                            LiquidBounce.hud.addNotification(Notification("Configs", "$configName Config Loaded", NotifyType.INFO))
                            playEdit()
                        } catch (e: Exception) {
                            ClientUtils.displayChatMessage("§c加载配置文件 $configName 失败: ${e.message}")
                        }

                        return
                    }
//1
                    chatSyntax("cloudconfig load <name>")
                    return
                }
            }
        }
        chatSyntax("cloudconfig load <name>")
    }

    /**
     * Provides tab-completion options for the command arguments.
     */
    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("load").filter { it.startsWith(args[0], true) }
            2 -> {
                when (args[0].toLowerCase()) {
                    "load" -> {
                        if (args.size > 2) {
                            val configName = args[2]
                            try {
                                val url =
                                    URL("https://gitcode.net/m0_62964839/瑞雪/-/raw/master/cloudconfig/$configName.json")
                                val json = url.readText()
                                val files = Gson().fromJson(json, Array<String>::class.java)

                                return files.filter { it.startsWith(args[1], true) }
                            } catch (e: Exception) {
                                ClientUtils.getLogger().error("Failed to load cloud config index.", e)
                            }
                        }
                    }
                }
                return emptyList()
            }
            else -> emptyList()
        }
    }
}
