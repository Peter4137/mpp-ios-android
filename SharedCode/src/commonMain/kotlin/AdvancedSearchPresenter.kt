package com.jetbrains.handson.mpp.mobile

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

    override fun stepValueXbyY(x: Int, y: Int): Int {
        val newValue = x+y
        if (newValue>=0){
            return newValue
        }
        return x
    }
}