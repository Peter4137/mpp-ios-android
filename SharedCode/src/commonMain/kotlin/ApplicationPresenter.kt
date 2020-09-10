package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class ApplicationPresenter: ApplicationContract.Presenter() {

    private val dispatchers = AppDispatchersImpl()
    private var view: ApplicationContract.View? = null
    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job

    override fun onViewTaken(view: ApplicationContract.View) {
        this.view = view
        val stationList = listOf("KGX", "WNS", "WKM", "GLD", "WOK")
        view.setLabel(createApplicationScreenMessage())
        view.setDepartureDropdown(stationList)
        view.setArrivalDropdown(stationList)
    }

    override fun onButtonTapped(departureStation: String, arrivalStation: String, view: ApplicationContract.View) {
        this.view = view
        //val apiCall = "https://mobile-api-dev.lner.co.uk/v1/fares?originStation=$departureStation&destinationStation=$arrivalStation&noChanges=false&numberOfAdults=1&numberOfChildren=0&journeyType=single&outboundDateTime=2020-07-14T19%3A30%3A00.000%2B01%3A00&outboundIsArriveBy=false"
        //val linkToOpen = "https://www.lner.co.uk/travel-information/travelling-now/live-train-times/depart/$departureStation/$arrivalStation/#LiveDepResults"
    }
}
