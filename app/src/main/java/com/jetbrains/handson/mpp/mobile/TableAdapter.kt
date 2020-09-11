package com.jetbrains.handson.mpp.mobile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(private val departureTimes: MutableList<String>):
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(listItemView: View) : RecyclerView.ViewHolder(listItemView) {
        val departureTime: TextView = itemView.findViewById<TextView>(R.id.text_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): TableAdapter.TableViewHolder {
        val cellView = LayoutInflater.from(parent.context)
            .inflate(R.layout.cell_view, parent, false)
        return TableViewHolder(cellView)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        // get actual data from shared here
        val cellValue = departureTimes[position]
        val textView = holder.departureTime
        textView.text = cellValue
    }

    override fun getItemCount(): Int {
        return departureTimes.size
    }

}