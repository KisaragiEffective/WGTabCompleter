package com.github.kisaragieffective.wgtabcompleter

import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.server.TabCompleteEvent

object Listeners : Listener {
    @EventHandler
    fun onTabComplete(e: TabCompleteEvent) {
        val c = Regex("^/([^ ]*) ?(.*)")
        val p = e.buffer.endsWith(" ")
        val w = c.find(e.buffer)!!.groupValues
        val args: Array<out String> = w[2].split(" ").filter { it.isNotEmpty() }.toTypedArray()
        val command: String = w[1]

        val z = WGTabCompleter.instance.value.logger
        with (z) {
            info("TabCompleteEvent: command=$command,args=${args.contentDeepToString()},compl=${e.completions.joinToString { it.toString() }}")
            // info(WorldGuardPlugin.inst().flagRegistry.map { it.name }.toTypedArray().contentDeepToString())
        }

        val t = listOf("rg", "region", "regions")
        val subCommand = args.getOrElse(0) { "" }.toLowerCase()
        if (t.contains(command.toLowerCase()) || t.map { "worldguard:$it" }.contains(command)) {
            // /rg flag [claim-name] [flag-name]

            z.info("rg")
            args.forEachIndexed { i: Int, s: String ->
                z.info("args[$i] --> $s")
            }
            z.info("$subCommand is SubCommand")

            val flags = WorldGuardPlugin.inst().flagRegistry.map { it.name }
            val players = Bukkit.getOnlinePlayers().map { it.name }
            val suggest  = e.completions
            when (args.size) {
                0 -> {
                    suggest.plusAssign(arrayOf(
                            "flag", "select", "migrateuuid", "setpriority", "teleport",
                            "save", "addowner", "removeowner", "list", "remove",
                            "addmember", "redefine", "load", "removemember", "define",
                            "claim", "setparent", "info", "migratedb"
                    ))
                }

                1 -> {
                    val q = e.sender
                    if (q is Player) {
                        val zp = WorldGuardPlugin.inst().regionContainer.get(q.world) ?: run {
                            z.info("${q.world.name} does not have RegionManager.")
                            return
                        }

                        val tc = zp.regions.values.map { it.id }
                        suggest.plusAssign(tc)
                    }
                }

                2 -> when (subCommand) {
                    "f", "flag" -> {
                        val q = e.sender
                        if (q is Player) {
                            val zp = WorldGuardPlugin.inst().regionContainer.get(q.world) ?: run {
                                z.info("${q.world.name} does not have RegionManager.")
                                return
                            }

                            val tc = zp.regions.values.map { it.id }
                            if (tc.contains(args[1])) {
                                suggest.plusAssign(flags)
                            } else {
                                suggest.plusAssign(tc.filter { it.startsWith(args[1]) })
                            }
                        }
                    }
                }

                3 -> when (subCommand) {
                    "f", "flag" -> {
                        suggest.plusAssign(flags.filter { it.startsWith(args[2]) })
                    }
                }
            }
            suggest.minusAssign(players)
        }
    }
}