package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.CoroutineScope

interface ApplicationContract {
    interface View {
        fun setLabel(text: String)
        fun setDepatureDropdown()
        fun setArrivalDropdown()
    }

    abstract class Presenter: CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun onButtonTapped(departureDropdown: String, arrivalDropdown: String)
    }
}
