package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.CoroutineScope

interface ApplicationContract {
    interface View {
        fun setLabel(text: String)
        fun setDepartureDropdown(stationList: List<String>)
        fun setArrivalDropdown(stationList: List<String>)
    }

    abstract class Presenter: CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun onButtonTapped(departureStation: String, arrivalStation: String, view: View)
    }
}
