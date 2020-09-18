package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.CoroutineScope

interface AdvancedSearchContract {
    interface View {
        fun showAlertMessage(alertMessage: String)
        fun submitAdvancedSearch()
    }

    abstract class Presenter: CoroutineScope {
        abstract fun onViewTaken(view: View)
        abstract fun submitSearch(numAdults: Int, numChildren: Int, date: String)
    }
}