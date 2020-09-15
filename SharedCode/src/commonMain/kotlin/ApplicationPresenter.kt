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
import kotlinx.coroutines.*

class ApplicationPresenter: ApplicationContract.Presenter() {

    private val dispatchers = AppDispatchersImpl()
    private var view: ApplicationContract.View? = null
    private val job: Job = SupervisorJob()

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
        val stationCodes = listOf("KGX", "WNS", "WKM", "GLD", "WOK")
        val stationNames = buildStationList(stationCodes)
        view.setLabel(createApplicationScreenMessage())
        view.setDepartureDropdown(stationNames)
        view.setArrivalDropdown(stationNames)
    }

    override fun onButtonTapped() {
        if (chosenDepartureStation == chosenArrivalStation) return
        val dateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000")
        val timeNow: String = DateTimeTz.nowLocal().format(dateTimeFormat)

        val apiCall = "https://mobile-api-dev.lner.co.uk/v1/fares?originStation=$chosenDepartureStation&destinationStation=$chosenArrivalStation&noChanges=false&numberOfAdults=1&numberOfChildren=0&journeyType=single&outboundDateTime=$timeNow&outboundIsArriveBy=false"

        runBlocking {
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
        chosenDepartureStation = matchStationCodeAndName(stationName = departureStation).crs!!

    }

    override fun setArrivalStation(arrivalStation: String) {
        chosenArrivalStation = matchStationCodeAndName(stationName = arrivalStation).crs!!
    }

    private fun buildStationList(stationCodes: List<String>): List<String> {
        val stationList = mutableListOf<String>()
        for (stationCode in stationCodes) {
            stationList.add(matchStationCodeAndName(stationCode = stationCode).name)
        }
        return stationList
    }

    private fun matchStationCodeAndName(stationCode: String = "", stationName: String = ""): StationInformation {
        val apiCall = "https://mobile-api-dev.lner.co.uk/v1/stations"
        var matchedStation: StationInformation = StationInformation("", "")
        runBlocking {
            val stationDetails: StationDetails = client.get(apiCall)
            for (stationInformation in stationDetails.stations) {
                if (stationInformation.crs == stationCode) {
                    matchedStation = stationInformation
                    break
                }
                else if (stationInformation.name == stationName) {
                    matchedStation = stationInformation
                    break
                }
            }
        }
        return matchedStation
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
