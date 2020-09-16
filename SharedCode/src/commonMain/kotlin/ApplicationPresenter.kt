package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.parse
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

    private val stationCodes = listOf("KGX", "WNS", "WKM", "GLD", "WOK")
    private val stationNames = MutableList<String>(stationCodes.size) { _ -> "" }

    private var chosenDepartureStation: String = ""
    private var chosenArrivalStation: String = ""

    private val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict)
        }
    }

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job

    override fun onViewTaken(view: ApplicationContract.View) {
        this.view = view
        buildStationDropdowns()
        view.setLabel(createApplicationScreenMessage())
    }

    override fun onButtonTapped() {
        if (chosenDepartureStation == chosenArrivalStation) return
        val dateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000")
        val timeNow: String = DateTimeTz.nowLocal().format(dateTimeFormat)

        val apiCall = "https://mobile-api-dev.lner.co.uk/v1/fares?originStation=$chosenDepartureStation&destinationStation=$chosenArrivalStation&noChanges=false&numberOfAdults=1&numberOfChildren=0&journeyType=single&outboundDateTime=$timeNow&outboundIsArriveBy=false"

        launch {
            try {
                var departureDetails: DepartureDetails = client.get(apiCall)
                val departures: MutableList<DepartureInformation> = mutableListOf()
                for (journey in departureDetails.outboundJourneys) {
                    departures.add(buildDepartureInformation(journey))
                }
                view!!.populateDeparturesTable(departures)
            } catch (e: Exception) {
                view!!.setLabel("API Call Failed")
            }
        }
    }

    override fun setDepartureStation(departureStation: String) {
        chosenDepartureStation = matchStationNameToCode(departureStation)
    }

    override fun setArrivalStation(arrivalStation: String) {
        chosenArrivalStation = matchStationNameToCode(arrivalStation)
    }

    private fun buildStationDropdowns() {
        launch {
            val allStationDetails = getAllStationDetails()
            buildStationNamesList(allStationDetails)
            view!!.setDepartureDropdown(stationNames)
            view!!.setArrivalDropdown(stationNames)
        }
    }

    private fun buildStationNamesList(allStationDetails: StationDetails) {
        for ((codeIndex, stationCode) in stationCodes.withIndex()) {
            for (stationInformation in allStationDetails.stations) {
                if (stationInformation.crs == stationCode) {
                    stationNames[codeIndex] = stationInformation.name
                }
            }
        }
    }

    private suspend fun getAllStationDetails(): StationDetails {
        val apiCall = "https://mobile-api-dev.lner.co.uk/v1/stations"
        return client.get(apiCall)
    }

    private fun matchStationNameToCode(stationName: String): String {
        return stationCodes[stationNames.indexOf(stationName)]
    }

    private fun buildDepartureInformation(journeyDetails: JourneyDetails): DepartureInformation {
        val timeForm = DateFormat("HH:mm")
        val departureDateTime = processTimeForDisplay(journeyDetails.departureTime)
        val arrivalDateTime = processTimeForDisplay(journeyDetails.arrivalTime)
        val journeyTime: TimeSpan = arrivalDateTime - departureDateTime
        val journeyTimeMinutes: String = "${journeyTime.minutes}m"
        val trainOperator = journeyDetails.primaryTrainOperator.name
        var price = try {
            val priceInPounds = journeyDetails.tickets[0].priceInPennies.toDouble() / 100
            val roundedPriceInPounds = roundToDecimalPlace(priceInPounds, 2)
            "£$roundedPriceInPounds"
        } catch (e: Exception) {
            "N/A"
        }
        return DepartureInformation(
            departureTime = departureDateTime.format(timeForm),
            arrivalTime = arrivalDateTime.format(timeForm),
            journeyTime = journeyTimeMinutes,
            trainOperator = trainOperator,
            price = price
        )
    }

    private fun processTimeForDisplay(dateTime: String): DateTimeTz {
        val receivedDateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000z")
        return receivedDateTimeFormat.parse(dateTime)
    }
}
