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

    private val dateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000")
    private val defaultTime: String = DateTimeTz.nowLocal().format(dateTimeFormat)
    private var searchInformation = SearchInformation("", "", defaultTime, 1, 0)

    private val stationCodes = listOf("KGX", "WNS", "WKM", "GLD", "WOK")
    private val allStations = mutableListOf<StationInformation>()

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
        if (searchInformation.departureStation == searchInformation.arrivalStation) {
            view!!.showAlertMessage("Stations cannot match")
            return
        }
        
        val apiCall = baseURL + "fares?" +
                "originStation=${searchInformation.departureStation}&" +
                "destinationStation=${searchInformation.arrivalStation}&" +
                "noChanges=false&" +
                "numberOfAdults=${searchInformation.numAdults}&" +
                "numberOfChildren=${searchInformation.numChildren}&" +
                "journeyType=single&" +
                "outboundDateTime=${searchInformation.departureTime}&" +
                "outboundIsArriveBy=false"

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
        searchInformation.departureStation = matchStationNameToCode(departureStation)
    }

    override fun setArrivalStation(arrivalStation: String) {
        searchInformation.arrivalStation = matchStationNameToCode(arrivalStation)
    }

    override fun setDepartureTime(departureTime: String) {
        searchInformation.departureTime = departureTime
    }

    override fun setNumAdults(numAdults: Int) {
        searchInformation.numAdults = numAdults
    }

    override fun setNumChildren(numChildren: Int) {
        searchInformation.numChildren = numChildren
    }

    private fun buildStationDropdowns() {
        launch {
            val allStationDetails = getAllStationDetails()
            buildStationNamesList(allStationDetails)
            view!!.setDepartureDropdown(List(allStations.size) {i -> allStations[i].name})
            view!!.setArrivalDropdown(List(allStations.size) {i -> allStations[i].name})
        }
    }

    private fun buildStationNamesList(allStationDetails: StationDetails) {
        for (stationCode in stationCodes) {
            for (stationInformation in allStationDetails.stations) {
                if (stationInformation.crs == stationCode) {
                    allStations.add(stationInformation)
                }
            }
        }
    }

    private suspend fun getAllStationDetails(): StationDetails {
        val apiCall = baseURL + "stations"
        return client.get(apiCall)
    }

    private fun matchStationNameToCode(stationName: String): String {
        for (station in allStations) {
            if (stationName == station.name) {
                return station.crs!!
            }
        }
        view!!.showAlertMessage("Invalid station name")
        return ""
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

