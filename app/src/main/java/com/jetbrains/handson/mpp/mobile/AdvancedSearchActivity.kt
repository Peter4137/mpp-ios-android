package com.jetbrains.handson.mpp.mobile

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.text.format.DateFormat
import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import kotlinx.android.synthetic.main.activity_pop_up_window.*
import java.util.*

class AdvancedSearchActivity : AppCompatActivity(), AdvancedSearchContract.View, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

        private lateinit var presenter: AdvancedSearchPresenter

        var day = 0
        var month: Int = 0
        var year: Int = 0
        var hour: Int = 0
        var minute: Int = 0
        var timeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up_window)

        presenter = AdvancedSearchPresenter()
        presenter.onViewTaken(this)

        setInitialValues()
        setListeners()

    }

    private fun setInitialValues() {
        val numAdultsView = findViewById<TextView>(R.id.in_num_adults)
        numAdultsView.text = "1"
        val numChildrenView = findViewById<TextView>(R.id.in_num_children)
        numChildrenView.text = "0"
        val timeView = findViewById<TextView>(R.id.in_date)
        timeView.text = "Today, Now"
    }

    override fun showAlertMessage(alertMessage: String) {
        val alertDialogBuilder = AlertDialog.Builder(this)
        alertDialogBuilder.setMessage(alertMessage)
        alertDialogBuilder.setNeutralButton("Ok") { _, _ -> null}
        alertDialogBuilder.show()
    }

    private fun setListeners() {
        val exitPopUpButton = findViewById<Button>(R.id.submit_advanced_search)
        exitPopUpButton.setOnClickListener {
            presenter.submitSearch(getNumAdults(), getNumChildren(), getDateInAPIFormat())
        }

        val dateButton = findViewById<Button>(R.id.btn_date)
        dateButton.setOnClickListener {
            showDatePicker()
        }

        val numAdultsView = findViewById<TextView>(R.id.in_num_adults)
        val numChildrenView = findViewById<TextView>(R.id.in_num_children)
        add_num_adults.setOnClickListener {
            numAdultsView.text = stepValueXbyY(getNumAdults(),1).toString()
        }
        add_num_children.setOnClickListener {
            numChildrenView.text = stepValueXbyY(getNumChildren(),1).toString()
        }
        minus_num_adults.setOnClickListener {
            numAdultsView.text = stepValueXbyY(getNumAdults(),-1).toString()
        }
        minus_num_children.setOnClickListener {
            numChildrenView.text = stepValueXbyY(getNumChildren(),-1).toString()
        }
    }
    override fun submitAdvancedSearch() {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("numAdults", getNumAdults() )
        intent.putExtra("numChildren", getNumChildren())
        if (timeChanged){
            intent.putExtra("time", getDateInAPIFormat())
        } else {
            intent.putExtra("time", "Unset")
        }
        setResult(2, intent)
        finish()
    }
    private fun getDateInAPIFormat(): String {
        return "$year-${makeNumberTwoDigits(month)}-${makeNumberTwoDigits(day)}T${makeNumberTwoDigits(hour)}:${makeNumberTwoDigits(minute)}:00.000"
    }
    private fun showDatePicker() {
        val calendar: Calendar = Calendar.getInstance()
        day = calendar.get(Calendar.DAY_OF_MONTH)
        month = calendar.get(Calendar.MONTH)
        year = calendar.get(Calendar.YEAR)
        val datePickerDialog =
            DatePickerDialog(this@AdvancedSearchActivity, this@AdvancedSearchActivity, year, month,day)
        datePickerDialog.show()
    }
    private fun stepValueXbyY(x: Int, y: Int): Int {
        val newValue = x+y
        if (newValue>=0){
            return newValue
        }
        return x
    }
    private fun getNumAdults(): Int {
        val numAdultsView = findViewById<TextView>(R.id.in_num_adults)
        return (numAdultsView.text as String).toInt()
    }
    private fun getNumChildren(): Int {
        val numChildrenView = findViewById<TextView>(R.id.in_num_children)
        return (numChildrenView.text as String).toInt()
    }
    override fun onDateSet(view: DatePicker?, selectedYear: Int, selectedMonth: Int, selectedDay: Int) {
        day = selectedDay
        month = selectedMonth+1
        year = selectedYear
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this@AdvancedSearchActivity, this@AdvancedSearchActivity, hour, minute,
            DateFormat.is24HourFormat(this))
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, selectedHour: Int, selectedMinute: Int) {
        hour = selectedHour
        minute = selectedMinute
        val timeText = "$day/${month}, $hour:${makeNumberTwoDigits(minute)}"
        timeChanged = true
        val timeView = findViewById<TextView>(R.id.in_date)
        timeView.text = timeText
    }
    private fun makeNumberTwoDigits(number: Int): String {
        if (number.toString().length==1){
            return ("0$number")
        }
        return number.toString()
    }
}

