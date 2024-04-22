package com.chrsrns.codingencyclopedia.utils

import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

object Utilities {
    fun validateEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePhoneNumber(number: String): Boolean {
        val phoneNumberRegex = Regex("^(09|\\+639)\\d{9}\$")
        return phoneNumberRegex.matches(number)
    }

    fun cleanFileName(relativePath: String): String {
        val baseName = relativePath.substringAfterLast('/')
        val fileName = baseName.substringBeforeLast('.', baseName)
        val cleanedFileNameWithSpaces =
            fileName.replace(Regex("[^a-zA-Z0-9]"), " ").toLowerCase(Locale.ROOT)

        return cleanedFileNameWithSpaces.replace(" ", "")
    }
}

fun JSONObject.toMap(): Map<String, *> = keys().asSequence().associateWith { s ->
    when (val value = this[s]) {
        is JSONArray -> {
            val map = (0 until value.length()).associate { Pair(it.toString(), value[it]) }
            JSONObject(map).toMap().values.toList()
        }

        is JSONObject -> value.toMap()
        JSONObject.NULL -> null
        else -> value
    }
}