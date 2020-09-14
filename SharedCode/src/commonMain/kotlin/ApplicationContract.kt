package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateTimeTz
import kotlinx.coroutines.CoroutineScope

interface ApplicationContract {
    interface View {
        fun setLabel(text: String)
        fun setDepartureDropdown(stationList: List<String>)
        fun setArrivalDropdown(stationList: List<String>)
        fun populateDeparturesTable(departuresList: List<DepartureInformation>)
    }

    abstract class Presenter: CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun onButtonTapped(departureStation: String, arrivalStation: String, view: View)
        abstract fun processTimeForDisplay(dateTime: String) : DateTimeTz
    }
}
