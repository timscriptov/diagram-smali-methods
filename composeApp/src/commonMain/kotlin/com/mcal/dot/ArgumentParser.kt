package com.mcal.dot

class ArgumentParser(
    private val args: Array<String>
) {
    private val argumentMap = mutableMapOf<String, String>()
    private val argumentHelp = mutableMapOf<String, String>()

    fun addArgument(argName: String, help: String) {
        argumentHelp[argName] = help
    }

    fun parseArgs() {
        var currentArg: String? = null
        var index = 0
        while (index < args.size) {
            val arg = args[index]
            if (arg.startsWith("-")) {
                currentArg = arg
                argumentMap[currentArg] = ""
                index++
            } else {
                currentArg?.let {
                    if (argumentMap[it].isNullOrEmpty()) {
                        argumentMap[it] = arg
                    } else {
                        argumentMap[it] += " $arg"
                    }
                }
                index++
            }
        }
    }

    fun getString(argName: String): String? {
        return argumentMap[argName]
    }

    fun getInt(argName: String): Int? {
        return argumentMap[argName]?.toIntOrNull()
    }

    fun getBoolean(argName: String): Boolean {
        return argumentMap[argName]?.toBoolean() ?: false
    }

    fun getList(argName: String): List<String>? {
        return argumentMap[argName]?.split(" ")
    }

    fun printHelp() {
        println("Usage:")
        argumentHelp.forEach { (argName, help) ->
            println("\t$argName: $help")
        }
    }
}
