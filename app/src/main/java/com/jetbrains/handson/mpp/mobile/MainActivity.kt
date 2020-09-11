package com.jetbrains.handson.mpp.mobile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.TextView
import android.widget.Spinner
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var departures: MutableList<departureInformation>

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
            presenter.onButtonTapped(departureStation, arrivalStation, this)
        }

        departures = mutableListOf()

        viewManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL, false)
        viewAdapter = TableAdapter(departures)
        recyclerView = findViewById<RecyclerView>(R.id.departures_table).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
    }

    override fun setLabel(text: String) {
        findViewById<TextView>(R.id.main_text).text = text
    }

    override fun setDepartureDropdown(stationList: List<String>) {
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

    override fun setArrivalDropdown(stationList: List<String>) {
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

    override fun populateDeparturesTable(departuresList: List<departureInformation>) {
        departures.addAll(departuresList)
        viewAdapter.notifyDataSetChanged()
        return
    }

}
