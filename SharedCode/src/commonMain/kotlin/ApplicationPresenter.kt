package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import com.soywiz.klock.TimeSpan
import com.soywiz.klock.parse
import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.text.DecimalFormat
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
        if (departureStation == arrivalStation) return
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
            lateinit var jsonString: DepartureDetails
            try{
                jsonString = client.get(apiCall)
            } catch (e: Exception){
                view.setLabel("API Call Failed")
            }
            val departures: MutableList<departureInformation> = mutableListOf()
            val receivedDateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000z")
            val timeForm = DateFormat("HH:mm")
            var numOfResults = 5
            if (jsonString.outboundJourneys.count() < numOfResults){
                numOfResults = jsonString.outboundJourneys.count()
            }
            val maxIndexForResults = numOfResults-1
            for (i in 0..maxIndexForResults){
                val jsonDepartureTime = jsonString.outboundJourneys[i].departureTime
                val jsonArrivalTime = jsonString.outboundJourneys[i].arrivalTime
                val formattedDeparture = receivedDateTimeFormat.parse(jsonDepartureTime)
                val formattedArrival = receivedDateTimeFormat.parse(jsonArrivalTime)
                val journeyTime: TimeSpan = formattedArrival - formattedDeparture
                val journeyTimeMinutes = "${journeyTime.minutes.toUInt()}m"
                val trainOperator = jsonString.outboundJourneys[i].primaryTrainOperator.name
                lateinit var price: String
                price = try{
                    val priceInPounds = jsonString.outboundJourneys[i].tickets[0].priceInPennies.toDouble() / 100
                    val df = DecimalFormat("#.00")
                    val roundedPriceInPounds = df.format(priceInPounds)
                    "£$roundedPriceInPounds"
                }catch (e: Exception){
                    "N/A"
                }
                departures.add(departureInformation(
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
}

@Serializable
data class DepartureDetails(
    val outboundJourneys : List<JourneyDetails>
)
@Serializable
data class JourneyDetails(
    val departureTime: String,
    val arrivalTime: String,
    val primaryTrainOperator: TrainOperatorDetails,
    val tickets: List<TicketDetails>
)

@Serializable
data class TrainOperatorDetails(
    val name: String
)

@Serializable
data class TicketDetails(
    val priceInPennies: Int
)

data class departureInformation(
    val departureTime: String,
    val arrivalTime: String,
    val journeyTime: String,
    val trainOperator: String,
    val price: String
)