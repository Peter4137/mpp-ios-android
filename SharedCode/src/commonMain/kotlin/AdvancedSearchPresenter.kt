package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlin.coroutines.CoroutineContext

class AdvancedSearchPresenter: AdvancedSearchContract.Presenter() {

    private val dispatchers = AppDispatchersImpl()
    private var view: AdvancedSearchContract.View? = null
    private val job: Job = SupervisorJob()

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job

    override fun onViewTaken(view: AdvancedSearchContract.View) {
        this.view = view
    }

    override fun submitSearch(numAdults: Int, numChildren: Int, date: String) {
        val maxNumPassengers = 9
        val minNumPassengers = 1
        val totalPassengersSelected = numAdults + numChildren

        val today = DateTime.now()
        val dateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000")
        val dateAsDateTimeTz: DateTimeTz = dateTimeFormat.parse(date)
        val dateAsDateTime: DateTime = dateAsDateTimeTz.local

        if (minNumPassengers>totalPassengersSelected || totalPassengersSelected>maxNumPassengers) {
            view!!.showAlertMessage("Number of passengers must be between $minNumPassengers and $maxNumPassengers")
        } else if (dateAsDateTime < today) {
            view!!.showAlertMessage("Departure time must be in the future")
        } else {
            view!!.submitAdvancedSearch()
        }
    }


}