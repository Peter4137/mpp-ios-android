package com.jetbrains.handson.mpp.mobile

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

class PopUpWindow : AppCompatActivity(), DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {
        lateinit var textView: TextView
        lateinit var button: Button
        var day = 0
        var month: Int = 0
        var year: Int = 0
        var hour: Int = 0
        var minute: Int = 0
        var myDay = 0
        var myMonth: Int = 0
        var myYear: Int = 0
        var myHour: Int = 0
        var myMinute: Int = 0
        var timeChanged = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pop_up_window)

        val exitPopUpButton = findViewById<Button>(R.id.submit_advanced_search)
        exitPopUpButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.putExtra("numAdults", getNumAdults() )
            intent.putExtra("numChildren", getNumChildren())
            if (timeChanged){
                intent.putExtra("time", "$myYear-${makeNumberTwoDigits(myMonth)}-${makeNumberTwoDigits(myDay)}T${makeNumberTwoDigits(myHour)}:${makeNumberTwoDigits(myMinute)}:00.000")
            } else {
                intent.putExtra("time", "Unset")
            }
            setResult(2, intent)
            finish()
        }

        textView = findViewById(R.id.in_date)
        button = findViewById(R.id.btn_date)
        button.setOnClickListener {
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(this@PopUpWindow, this@PopUpWindow, year, month,day)
            datePickerDialog.show()
        }

        val numAdultsView = findViewById<TextView>(R.id.in_num_adults)
        numAdultsView.text = "1"
        val numChildrenView = findViewById<TextView>(R.id.in_num_children)
        numChildrenView.text = "0"
        val timeView = findViewById<TextView>(R.id.in_date)
        timeView.text = "Today, Now"

        add_num_adults.setOnClickListener {
            numAdultsView.text = (getNumAdults() + 1).toString()
        }
        add_num_children.setOnClickListener {
            numChildrenView.text = (getNumChildren() + 1).toString()
        }
        minus_num_adults.setOnClickListener {
            val currentNumAdults = getNumAdults()
            if (currentNumAdults>0) {
                numAdultsView.text = (currentNumAdults - 1).toString()
            }
        }
        minus_num_children.setOnClickListener {
            val currentNumChildren = getNumChildren()
            if (currentNumChildren>0) {
                numChildrenView.text = (currentNumChildren - 1).toString()
            }
        }
    }
    private fun getNumAdults(): Int {
        val numAdultsView = findViewById<TextView>(R.id.in_num_adults)
        return (numAdultsView.text as String).toInt()
    }
    private fun getNumChildren(): Int {
        val numChildrenView = findViewById<TextView>(R.id.in_num_children)
        return (numChildrenView.text as String).toInt()
    }
    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        myDay = dayOfMonth
        myYear = year
        myMonth = month+1
        val calendar: Calendar = Calendar.getInstance()
        hour = calendar.get(Calendar.HOUR)
        minute = calendar.get(Calendar.MINUTE)
        val timePickerDialog = TimePickerDialog(this@PopUpWindow, this@PopUpWindow, hour, minute,
            DateFormat.is24HourFormat(this))
        timePickerDialog.show()
    }
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        myHour = hourOfDay
        myMinute = minute
        val timeText = "$myDay/${myMonth}, $myHour:$myMinute"
        timeChanged = true
        textView.text = timeText
    }
    fun makeNumberTwoDigits(number: Int): String {
        if (number.toString().length==1){
            return ("0$number")
        }
        return number.toString()
    }
}

