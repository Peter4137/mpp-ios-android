package com.jetbrains.handson.mpp.mobile

import com.soywiz.klock.DateFormat
import com.soywiz.klock.DateTimeTz
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), ApplicationContract.View {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager

    private lateinit var departures: MutableList<DepartureInformation>
    private lateinit var departureTime: String

    private lateinit var presenter: ApplicationPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = ApplicationPresenter()
        presenter.onViewTaken(this)

        setListeners()
        setUpDeparturesTable()
    }

    private fun setListeners() {
        val searchButton = findViewById<Button>(R.id.button)
        searchButton.setOnClickListener {
            showLoadingSpinner(true)
            presenter.onButtonTapped()
        }

        val advancedSearchButton = findViewById<TextView>(R.id.advanced_search)
        advancedSearchButton.setOnClickListener {
            val intent = Intent(this, AdvancedSearchActivity::class.java)
            startActivityForResult(intent, 2)
        }

        val adultsButton = findViewById<Button>(R.id.selected_adults)
        adultsButton.setOnClickListener() {
            presenter.setNumAdults(0)
            adultsButton.visibility = View.INVISIBLE
            searchButton.performClick()
        }
        val childrenButton = findViewById<Button>(R.id.selected_children)
        childrenButton.setOnClickListener() {
            presenter.setNumChildren(0)
            childrenButton.visibility = View.INVISIBLE
            searchButton.performClick()
        }
        val departureTimeButton = findViewById<Button>(R.id.selected_time)
        departureTimeButton.setOnClickListener() {
            presenter.setDepartureTime(departureTime)//timeNow?
            departureTimeButton.visibility = View.INVISIBLE
            searchButton.performClick()
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
    }

    private fun setUpDeparturesTable() {
        departures = mutableListOf()

        viewManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
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
                val numAdults = data.getIntExtra("numAdults",1)
                presenter.setNumAdults(numAdults)
                val adultsButton = findViewById<Button>(R.id.selected_adults)
                adultsButton.text = "Adults: $numAdults"
                adultsButton.visibility = View.VISIBLE
                val numChildren = data.getIntExtra("numChildren", 0)
                presenter.setNumChildren(numChildren)
                val childrenButton = findViewById<Button>(R.id.selected_children)
                childrenButton.text = "Children: $numChildren"
                childrenButton.visibility = View.VISIBLE
                departureTime = data.getStringExtra("time")
                presenter.setDepartureTime(departureTime)
                val departureTimeButton = findViewById<Button>(R.id.selected_time)
                departureTimeButton.text = data.getStringExtra("timeText")
                departureTimeButton.visibility = View.VISIBLE
                val searchButton = findViewById<Button>(R.id.button)
                searchButton.performClick()
            }
        }
    }

}
