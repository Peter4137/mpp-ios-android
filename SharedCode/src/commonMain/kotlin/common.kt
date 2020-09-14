package com.jetbrains.handson.mpp.mobile

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse

expect fun platformName(): String

fun createApplicationScreenMessage(): String {
    return "TrainBoard"
}