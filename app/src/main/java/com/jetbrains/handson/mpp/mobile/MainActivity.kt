package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var departures: MutableList<DepartureInformation>

    private var numAdults = 1
    private var numChildren = 0
    private val dateTimeFormat = DateFormat("yyyy-MM-ddTHH:mm:ss.000")
    private val timeNow: String = DateTimeTz.nowLocal().format(dateTimeFormat)
    private var departureTime = timeNow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val presenter = ApplicationPresenter()
        presenter.onViewTaken(this)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener {
            showLoadingSpinner(true)
            presenter.setNumAdults(numAdults)
            presenter.setNumChildren(numChildren)
            if (departureTime=="Unset"){
                departureTime=timeNow
            }
            presenter.setDepartureTime(departureTime)
            presenter.onButtonTapped()
        }

        val advancedSearchButton = findViewById<TextView>(R.id.advanced_search)
        advancedSearchButton.setOnClickListener {
            val intent = Intent(this, PopUpWindow::class.java)
            startActivityForResult(intent, 2)
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
        showLoadingSpinner(false)
        departures.clear()
        departures.addAll(departuresList)
        viewAdapter.notifyDataSetChanged()
        recyclerView.smoothScrollToPosition(0);
    }

    override fun showAlertMessage(alertMessage: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(alertMessage)
        alertDialogBuilder.setNeutralButton("Ok") { _, _ -> null}
        alertDialogBuilder.show()
        showLoadingSpinner(false)
    }

    private fun showLoadingSpinner(visible: Boolean) {
        val loadingSpinner = findViewById<ProgressBar>(R.id.loading_spinner)
        val departuresTable = findViewById<RecyclerView>(R.id.departures_table)
        if (visible) {
            loadingSpinner.visibility = View.VISIBLE
            departuresTable.visibility = View.INVISIBLE
        } else {
            loadingSpinner.visibility = View.INVISIBLE
            departuresTable.visibility = View.VISIBLE
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 2) {
            if (data != null) {
                numAdults = data.getIntExtra("numAdults",1)
                numChildren = data.getIntExtra("numChildren", 0)
                departureTime = data.getStringExtra("time")
                val searchButton = findViewById<Button>(R.id.button)
                searchButton.performClick()
            }
        }
    }

}
