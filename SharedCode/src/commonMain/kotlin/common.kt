package com.jetbrains.handson.mpp.mobile

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import kotlin.math.round

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
    return "Kotlin Rocks on ${platformName()}"
}

fun roundToDecimalPlace(arg: Double, decimalPlaces: Int): String {
    var multiplier = 1.0
    repeat(decimalPlaces) { multiplier *= 10 }
    var roundedArg = (round(arg * multiplier) / multiplier).toString()
    val numberOfDecimalPlaces: Int = roundedArg.length - roundedArg.indexOf(".") - 1
    val zerosToAppend = decimalPlaces - numberOfDecimalPlaces - 1
    for (i in 0..zerosToAppend) {
        roundedArg = roundedArg.plus("0")
    }
    return roundedArg
}