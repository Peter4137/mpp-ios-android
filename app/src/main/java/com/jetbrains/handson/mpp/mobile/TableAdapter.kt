package com.jetbrains.handson.mpp.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(private val departures: MutableList<DepartureInformation>):
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val departureTime: TextView = itemView.findViewById(R.id.time_text_view)
        val journeyDuration: TextView = itemView.findViewById(R.id.duration_text_view)
        val journeyOperator: TextView = itemView.findViewById(R.id.trainOperator_text_view)
        val journeyPrice: TextView = itemView.findViewById(R.id.price_text_view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TableAdapter.TableViewHolder {
        val cellView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_view, parent, false)
        return TableViewHolder(cellView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val timeValue = departures[position].departureTime + " - " + departures[position].arrivalTime
        val timeTextView = holder.departureTime
        timeTextView.text = timeValue
        val durationValue = departures[position].journeyTime
        val durationTextView = holder.journeyDuration
        durationTextView.text = durationValue
        val trainOperatorValue = departures[position].trainOperator
        val trainOperatorTextView = holder.journeyOperator
        trainOperatorTextView.text = trainOperatorValue
        val priceValue = departures[position].price
        val priceTextView = holder.journeyPrice
        priceTextView.text = priceValue
    }

    override fun getItemCount(): Int {
        return departures.size
    }

}