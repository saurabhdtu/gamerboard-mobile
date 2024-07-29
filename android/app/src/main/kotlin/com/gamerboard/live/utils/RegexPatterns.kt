package com.gamerboard.live.utils

object RegexPatterns {
    val digitsOnly = Regex("[^0-9]")
    val alphaNumeric = Regex("[^A-Za-z0-9 ]")
    val alphabetsOnly = Regex("[^A-Za-z]")
    val numbers = Regex("[0-9]+")
    val whiteSpace = "\\s".toRegex()
    val whiteSpaceContinuous = "\\s+".toRegex()
}