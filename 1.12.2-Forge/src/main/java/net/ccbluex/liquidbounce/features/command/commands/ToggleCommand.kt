/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.LiquidBounce
import net.ccbluex.liquidbounce.features.command.Command

class ToggleCommand : Command("toggle", "t") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            val module = LiquidBounce.moduleManager.getModule(args[1])

            if (module == null) {
                chat("功能 '${args[1]}' 不存在.")
                return
            }

            if (args.size > 2) {
                val newState = args[2].toLowerCase()

                if (newState == "on" || newState == "off") {
                    module.state = newState == "on"
                } else {
                    chatSyntax("提示 <功能> [开启/关闭]")
                    return
                }
            } else {
                module.toggle()
            }

            chat("${if (module.state) "开启" else "关闭"} module §8${module.name}§3.")
            return
        }

        chatSyntax("提示 <功能> [开启/关闭]")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        val moduleName = args[0]

        return when (args.size) {
            1 -> LiquidBounce.moduleManager.modules
                    .map { it.name }
                    .filter { it.startsWith(moduleName, true) }
                    .toList()
            else -> emptyList()
        }
    }

}
