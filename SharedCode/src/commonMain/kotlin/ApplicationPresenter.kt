package com.jetbrains.handson.mpp.mobile

import java.time.LocalDateTime
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*
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
        //change time to now
        val timeNow = LocalDateTime.now()
        val apiCall = "https://mobile-api-dev.lner.co.uk/v1/fares?originStation=$departureStation&destinationStation=$arrivalStation&noChanges=false&numberOfAdults=1&numberOfChildren=0&journeyType=single&outboundDateTime=$timeNow&outboundIsArriveBy=false"
        val client = HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json.nonstrict)
            }
        }

        launch {
            val jsonString = client.get<DepartureDetails>(apiCall)
            view.setLabel(jsonString.outboundJourneys[0].arrivalTime)
        }
    }
}

//val serializer = Json(JsonConfiguration(ignoreUnknownKeys = true))

@Serializable
data class DepartureDetails(
    val outboundJourneys : List<JourneyDetails>
)
@Serializable
data class JourneyDetails(
    val arrivalTime: String
)