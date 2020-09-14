package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.*
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.*
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
        val dateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000")
        val timeNow: String = DateTimeTz.nowLocal().format(dateTimeFormat)

        val apiCall = "https://mobile-api-dev.lner.co.uk/v1/fares?originStation=$departureStation&destinationStation=$arrivalStation&noChanges=false&numberOfAdults=1&numberOfChildren=0&journeyType=single&outboundDateTime=$timeNow&outboundIsArriveBy=false"
        val client = HttpClient() {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json.nonstrict)
            }
        }

        launch {
            val jsonString = client.get<DepartureDetails>(apiCall)
            val departures: MutableList<DepartureInformation> = mutableListOf()
            val timeForm = DateFormat("HH:mm")
            for (i in 0..4){
                val formattedDeparture = processTimeForDisplay(jsonString.outboundJourneys[i].departureTime)
                val formattedArrival = processTimeForDisplay(jsonString.outboundJourneys[i].arrivalTime)
                val journeyTime: TimeSpan = formattedArrival - formattedDeparture
                val journeyTimeMinutes: String = "${journeyTime.minutes}m"
                val trainOperator = jsonString.outboundJourneys[i].primaryTrainOperator.name
                val priceInPounds: Double = jsonString.outboundJourneys[i].tickets[0].priceInPennies as Double / 100
                val price: String = "£$priceInPounds"

                departures.add(DepartureInformation(
                    departureTime = formattedDeparture.format(timeForm),
                    arrivalTime = formattedArrival.format(timeForm),
                    journeyTime = journeyTimeMinutes,
                    trainOperator = trainOperator,
                    price = price)
                )
            }
            view.populateDeparturesTable(departures)
        }
    }

    override fun processTimeForDisplay(dateTime: String): DateTimeTz {
        val receivedDateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000z")
        return receivedDateTimeFormat.parse(dateTime)
    }
}
