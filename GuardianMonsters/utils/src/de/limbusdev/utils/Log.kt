package de.limbusdev.utils

enum class LogLevel { OFF, ERROR, WARNING, INFO, VERBOSE, DEBUG }

object Log
{
    var logLevel = LogLevel.OFF
}

inline fun log(tag: String = "[LOG]", message: () -> String)
{
    if(Log.logLevel != LogLevel.OFF) { println("$tag ${message()}") }
}

inline fun logError(tag: String = "[ERROR]", message: () -> String)
{
    if(Log.logLevel == LogLevel.ERROR) { println("$tag ${message()}") }
}

inline fun logWarning(tag: String = "[WARNING]", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.ERROR,
        LogLevel.WARNING -> println("$tag ${message()}")
        else             -> {}
    }
}

inline fun logInfo(tag: String = "[INFO]", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.ERROR,
        LogLevel.WARNING,
        LogLevel.INFO    -> println("$tag ${message()}")
        else             -> {}
    }
}

inline fun logVerbose(tag: String = "[VERBOSE]", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.ERROR,
        LogLevel.WARNING,
        LogLevel.INFO,
        LogLevel.VERBOSE -> println("$tag ${message()}")
        else             -> {}
    }
}

inline fun logDebug(tag: String = "[DEBUG]", message: () -> String)
{
    when(Log.logLevel)
    {
        LogLevel.ERROR,
        LogLevel.WARNING,
        LogLevel.INFO,
        LogLevel.VERBOSE,
        LogLevel.DEBUG   -> println("$tag ${message()}")
        LogLevel.OFF     -> {}
    }
}