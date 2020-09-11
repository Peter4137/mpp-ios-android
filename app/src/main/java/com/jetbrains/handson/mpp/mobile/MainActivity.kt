package com.jetbrains.handson.mpp.mobile

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Spinner
import android.widget.Button

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val presenter = ApplicationPresenter()
        presenter.onViewTaken(this)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {

            val departureSpinner: Spinner = findViewById(R.id.departure_spinner)
            val departureStation = departureSpinner.selectedItem.toString()
            val arrivalSpinner: Spinner = findViewById(R.id.arrival_spinner)
            val arrivalStation = arrivalSpinner.selectedItem.toString()

//            val openURL = Intent(Intent.ACTION_VIEW)
//            val linkToOpen = "https://www.lner.co.uk/travel-information/travelling-now/live-train-times/depart/$departureStation/$arrivalStation/#LiveDepResults"
//            openURL.data = Uri.parse(linkToOpen)
//            startActivity(openURL)

            presenter.onButtonTapped(departureStation, arrivalStation, this)
        }
    }

    override fun setLabel(text: String) {
        findViewById<TextView>(R.id.main_text).text = text
    }

    override fun setDepartureDropdown(stationList: List<String>){
        val spinner: Spinner = findViewById(R.id.departure_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
         ArrayAdapter(this, android.R.layout.simple_spinner_item, stationList
         ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    override fun setArrivalDropdown(stationList: List<String>){
        val spinner: Spinner = findViewById(R.id.arrival_spinner)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter(this, android.R.layout.simple_spinner_item, stationList
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }

    override fun populateDeparturesTable(departuresList: List<String>){
        setLabel(departuresList[0])
    }

}
