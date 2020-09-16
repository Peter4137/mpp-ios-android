package com.jetbrains.handson.mpp.mobile

data class SearchInformation(
    var departureStation: String,
    var arrivalStation: String,
    var departureTime: String,
    var numAdults: Int,
    var numChildren: Int
)