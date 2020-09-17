package com.jetbrains.handson.mpp.mobile

import kotlinx.coroutines.CoroutineScope

interface AdvancedSearchContract {
    interface View {

    }

    abstract class Presenter: CoroutineScope {
        abstract fun onViewTaken(view: View)

    }
}