package de.limbusdev.utils

enum class LogLevel { OFF, ERROR, WARNING, INFO, VERBOSE, DEBUG }

object Log
{
    var logLevel = LogLevel.DEBUG
}

inline fun log(message: () -> String)
{
    if(Log.logLevel != LogLevel.OFF) { println(message()) }
}

inline fun logError(tag: String? = "", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.DEBUG,
        LogLevel.VERBOSE,
        LogLevel.INFO,
        LogLevel.WARNING,
        LogLevel.ERROR-> println("[ERROR][$tag] ${message()}")
        else             -> {}
    }
}

inline fun logWarning(tag: String? = "", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.DEBUG,
        LogLevel.VERBOSE,
        LogLevel.INFO,
        LogLevel.WARNING -> println("[WARNING][$tag] ${message()}")
        else             -> {}
    }
}

inline fun logInfo(tag: String? = "[INFO]", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.DEBUG,
        LogLevel.VERBOSE,
        LogLevel.INFO    -> println("[$tag] ${message()}")
        else             -> {}
    }
}

inline fun logVerbose(tag: String? = "[VERBOSE]", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.DEBUG,
        LogLevel.VERBOSE -> println("[$tag] ${message()}")
        else             -> {}
    }
}

inline fun logDebug(tag: String? = "[DEBUG]", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.DEBUG   -> println("[$tag] ${message()}")
        else             -> {}
    }
}