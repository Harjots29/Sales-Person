package com.harjot.salesperson

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TableAdapter(private val items: List<TableModel>,var historyInterface: HistoryInterface) :
    RecyclerView.Adapter<TableAdapter.TableViewHolder>() {

    class TableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvVendorName: TextView = view.findViewById(R.id.tvVendorName)
        val tvInTime: TextView = view.findViewById(R.id.tvInTime)
        val tvOutTime: TextView = view.findViewById(R.id.tvOutTime)
        val date : TextView = view.findViewById(R.id.tvDate)
        var cardView : CardView = view.findViewById(R.id.cv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_table_row, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val item = items[position]
        holder.tvVendorName.text = item.vendorName
        holder.tvInTime.text = item.inTime
        holder.tvOutTime.text = item.outTime
        holder.date.text = item.date
        holder.cardView.setOnClickListener {
            historyInterface.details(position)
        }
    }

    override fun getItemCount(): Int = items.size
}
