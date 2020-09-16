package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.parse
import io.ktor.client.HttpClient
import io.ktor.client.features.HttpTimeout
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
    private val stationNameToCodeMapping = mutableMapOf<String, String>()

    private var chosenDepartureStation: String = ""
    private var chosenArrivalStation: String = ""

    private val client = HttpClient() {
        install(JsonFeature) {
            serializer = KotlinxSerializer(Json.nonstrict)
        }
    }

    private val baseURL = "https://mobile-api-dev.lner.co.uk/v1/"

    override val coroutineContext: CoroutineContext
        get() = dispatchers.main + job

    override fun onViewTaken(view: ApplicationContract.View) {
        this.view = view
        buildStationDropdowns()
        view.setLabel(createApplicationScreenMessage())
    }

    override fun onButtonTapped() {
        if (chosenDepartureStation == chosenArrivalStation) {
            view!!.showAlertMessage("Stations cannot match")
            return
        }
        val dateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000")
        val timeNow: String = DateTimeTz.nowLocal().format(dateTimeFormat)

        val apiCall = baseURL + "fares?originStation=$chosenDepartureStation&destinationStation=$chosenArrivalStation&noChanges=false&numberOfAdults=1&numberOfChildren=0&journeyType=single&outboundDateTime=$timeNow&outboundIsArriveBy=false"

        launch {
            try {
                var departureDetails: DepartureDetails = client.get(apiCall)
                val departures: MutableList<DepartureInformation> = mutableListOf()
                for (journey in departureDetails.outboundJourneys) {
                    departures.add(buildDepartureInformation(journey))
                }
                view!!.populateDeparturesTable(departures)
            } catch (e: Exception) {
                view!!.showAlertMessage("API call failed")
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
            view!!.setDepartureDropdown(stationNameToCodeMapping.keys.toList())
            view!!.setArrivalDropdown(stationNameToCodeMapping.keys.toList())
        }
    }

    private fun buildStationNamesList(allStationDetails: StationDetails) {
        for (stationCode in stationCodes) {
            for (stationInformation in allStationDetails.stations) {
                if (stationInformation.crs == stationCode) {
                    stationNameToCodeMapping[stationInformation.name] = stationCode
                }
            }
        }
    }

    private suspend fun getAllStationDetails(): StationDetails {
        val apiCall = baseURL + "stations"
        return client.get(apiCall)
    }

    private fun matchStationNameToCode(stationName: String): String {
        var matchedCode = ""
        try {
            matchedCode = stationNameToCodeMapping[stationName]!!
        } catch (e: Exception) {
            view!!.showAlertMessage("Invalid station name")
        }
        return matchedCode
    }

    private fun buildDepartureInformation(journeyDetails: JourneyDetails): DepartureInformation {
        val timeForm = DateFormat("HH:mm")
        val departureDateTime = processTimeForDisplay(journeyDetails.departureTime)
        val arrivalDateTime = processTimeForDisplay(journeyDetails.arrivalTime)
        val journeyTime: TimeSpan = arrivalDateTime - departureDateTime
        val journeyTimeMinutes: String = "${journeyTime.minutes.toInt()} min"
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

