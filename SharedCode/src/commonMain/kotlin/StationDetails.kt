package com.jetbrains.handson.mpp.mobile

import kotlinx.serialization.Serializable

@Serializable
data class StationDetails(
    val stations : List<StationInformation>
)

@Serializable
data class StationInformation(
    val name : String,
    val crs: String?
)
