package com.github.kisaragieffective.wgtabcompleter

import com.sk89q.worldguard.bukkit.WorldGuardPlugin
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.plugin.java.JavaPlugin

class WGTabCompleter : JavaPlugin(), TabCompleter {

    init {
        instance = ImmutableValue(this)
    }

    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(Listeners, this)
    }

    override fun onDisable() {
        // Plugin shutdown logic
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        with(logger) {
            info(sender.toString())
            info(command.toString())
            info(alias)
            info(args.contentToString())
        }
        val t = listOf("rg", "region", "regions")
        if (t.contains(command.name.toLowerCase()) || t.map { "worldguard:$it" }.contains(command.name)) {
            // /rg flag [claim-name] [flag-name]
            if (args.size <= 2) {
                return emptyList()
            }

            when (args[0]) {
                "f", "flag" -> {
                    return WorldGuardPlugin.inst().flagRegistry.map { it.name }.filter { it.startsWith(args[2]) }
                }
            }
        }
        return emptyList()
    }

    companion object {
        lateinit var instance: ImmutableValue<WGTabCompleter>
    }
}
