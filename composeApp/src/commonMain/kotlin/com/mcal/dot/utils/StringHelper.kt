package com.mcal.dot.utils

import kotlinx.serialization.json.Json
import kotlin.random.Random


object StringHelper {
    @JvmStatic
    fun String.toFileName(): String {
        return this
            .replace(Regex("[<>]"), "")
            .replace(Regex("[/;()]"), "_")
    }

    @JvmStatic
    fun String.isJson(): Boolean {
        return try {
            val json = Json { ignoreUnknownKeys = true }
            json.parseToJsonElement(this)
            true
        } catch (e: Exception) {
            false
        }
    }

    @JvmStatic
    fun String.isNotJson(): Boolean {
        return !this.isJson()
    }

    @JvmStatic
    fun ClosedRange<Char>.randomString(length: Int): String {
        return (1..length)
            .map { (Random.nextInt(endInclusive.code - start.code) + start.code).toChar() }
            .joinToString("")
    }
}