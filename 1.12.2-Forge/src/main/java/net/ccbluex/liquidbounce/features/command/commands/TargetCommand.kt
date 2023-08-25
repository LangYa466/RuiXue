/*
 * LiquidBounce Base
 * God SkidBounce
 * Conding
 */
package net.ccbluex.liquidbounce.features.command.commands

import net.ccbluex.liquidbounce.features.command.Command
import net.ccbluex.liquidbounce.utils.EntityUtils

class TargetCommand : Command("target") {
    /**
     * Execute commands with provided [args]
     */
    override fun execute(args: Array<String>) {
        if (args.size > 1) {
            when {
                args[1].equals("玩家", ignoreCase = true) -> {
                    EntityUtils.targetPlayer = !EntityUtils.targetPlayer
                    chat("§7玩家目标提示 ${if (EntityUtils.targetPlayer) "开启" else "关闭"}.")
                    playEdit()
                    return
                }

                args[1].equals("怪物", ignoreCase = true) -> {
                    EntityUtils.targetMobs = !EntityUtils.targetMobs
                    chat("§7怪物目标提示 ${if (EntityUtils.targetMobs) "开启" else "关闭"}.")
                    playEdit()
                    return
                }

                args[1].equals("动物", ignoreCase = true) -> {
                    EntityUtils.targetAnimals = !EntityUtils.targetAnimals
                    chat("§7动物目标提示 ${if (EntityUtils.targetAnimals) "开启" else "关闭"}.")
                    playEdit()
                    return
                }

                args[1].equals("隐身", ignoreCase = true) -> {
                    EntityUtils.targetInvisible = !EntityUtils.targetInvisible
                    chat("§7隐身目标提示 ${if (EntityUtils.targetInvisible) "开启" else "关闭"}.")
                    playEdit()
                    return
                }
            }
        }

        chatSyntax("target <players/mobs/animals/invisible>")
    }

    override fun tabComplete(args: Array<String>): List<String> {
        if (args.isEmpty()) return emptyList()

        return when (args.size) {
            1 -> listOf("玩家", "怪物", "动物", "隐身")
                .filter { it.startsWith(args[0], true) }
            else -> emptyList()
        }
    }
}