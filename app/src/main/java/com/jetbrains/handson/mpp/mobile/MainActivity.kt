package com.jetbrains.handson.mpp.mobile

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var departures: MutableList<DepartureInformation>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val presenter = ApplicationPresenter()
        presenter.onViewTaken(this)

        val button = findViewById<Button>(R.id.button)

        button.setOnClickListener {
            showLoadingSpinner()
            presenter.onButtonTapped()
        }
        
        val departureSpinner = findViewById<Spinner>(R.id.departure_spinner)
        departureSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.setDepartureStation(departureSpinner.getItemAtPosition(position) as String)
            }
        }

        val arrivalSpinner = findViewById<Spinner>(R.id.arrival_spinner)
        arrivalSpinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onNothingSelected(parent: AdapterView<*>?) {}
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                presenter.setArrivalStation(arrivalSpinner.getItemAtPosition(position) as String)
            }
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

    override fun populateDeparturesTable(departuresList: List<DepartureInformation>) {
        hideLoadingSpinner()
        departures.clear()
        departures.addAll(departuresList)
        viewAdapter.notifyDataSetChanged()
        recyclerView.smoothScrollToPosition(0);
    }

    override fun createAlertMessage(alertMessage: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(alertMessage)
        alertDialogBuilder.setNeutralButton("Ok") { _, _ -> null}
        alertDialogBuilder.show()
        hideLoadingSpinner()
    }

    private fun showLoadingSpinner() {
        val loadingSpinner = findViewById<ProgressBar>(R.id.loading_spinner)
        val departuresTable = findViewById<RecyclerView>(R.id.departures_table)
        loadingSpinner.visibility = View.VISIBLE
        departuresTable.visibility = View.INVISIBLE
    }

    private fun hideLoadingSpinner() {
        val loadingSpinner = findViewById<ProgressBar>(R.id.loading_spinner)
        val departuresTable = findViewById<RecyclerView>(R.id.departures_table)
        loadingSpinner.visibility = View.INVISIBLE
        departuresTable.visibility = View.VISIBLE
    }
}
