package com.ssafy.firstproject.util

object TextUtil {
    fun parseSpellCheckedText(text: String): String {
        var cleanedText = text.replace("[\"',\n]".toRegex(), "")
        cleanedText = cleanedText.replace("다.", "다. ")
        return cleanedText
    }
}