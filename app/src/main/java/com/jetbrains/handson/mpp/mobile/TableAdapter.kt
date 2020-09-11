package com.jetbrains.handson.mpp.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(private val departures: MutableList<departureInformation>):
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val departureTime: TextView = itemView.findViewById<TextView>(R.id.time_text_view)
        val departureDuration: TextView = itemView.findViewById(R.id.duration_text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TableAdapter.TableViewHolder {
        val cellView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_view, parent, false)
        return TableViewHolder(cellView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        // get actual data from shared here
        val timeValue = departures[position].departureTime + " - " + departures[position].arrivalTime
        val timeTextView = holder.departureTime
        timeTextView.text = timeValue
        val durationValue = departures[position].journeyTime
        val durationTextView = holder.departureDuration
        durationTextView.text = durationValue

    }

    override fun getItemCount(): Int {
        return departures.size
    }

}