package com.harjot.salesadmin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class HistoryAdapter(private val items: List<HistoryModel>,var historyInterface: HistoryInterface) :
    RecyclerView.Adapter<HistoryAdapter.TableViewHolder>() {

    class TableViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvVendorName: TextView = view.findViewById(R.id.tvVendorName)
        val card:CardView = view.findViewById(R.id.card)
//        val tvInTime: TextView = view.findViewById(R.id.tvInTime)
//        val tvOutTime: TextView = view.findViewById(R.id.tvOutTime)
        val date:TextView = view.findViewById(R.id.tvDate)
//        val tvProductName: TextView = view.findViewById(R.id.tvProductName)
//        val tvQuantity: TextView = view.findViewById(R.id.tvQuantity)
        var ll: LinearLayout = view.findViewById(R.id.linearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return TableViewHolder(view)
    }

    override fun onBindViewHolder(holder: TableViewHolder, position: Int) {
        val item = items[position]
        holder.tvVendorName.text = item.vendorName
//        holder.tvInTime.text = item.inTime
//        holder.tvOutTime.text = item.outTime
        holder.date.text = item.date
        holder.card.setBackgroundColor(getRandomColor())
//        holder.tvProductName.text = item.productName
//        holder.tvQuantity.text = item.quantity
        holder.ll.setOnClickListener {
            historyInterface.details(position)
        }
    }
    private fun getRandomColor(): Int {
        val random = kotlin.random.Random
        val red = random.nextInt(256) // Random value between 0 and 255
        val green = random.nextInt(256)
        val blue = random.nextInt(256)
        return Color.rgb(red, green, blue)
    }

    override fun getItemCount(): Int = items.size
}
