package com.harjot.salesperson

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class InAdapter(var arrayList:ArrayList<Model>, var interfaces: Interfaces):
    RecyclerView.Adapter<InAdapter.ViewHolder>() {
    class ViewHolder(var view: View): RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.tvVendorName)
        var inTime = view.findViewById<TextView>(R.id.tvInTime)
        var outTime = view.findViewById<TextView>(R.id.tvOutTime)
        var date = view.findViewById<TextView>(R.id.tvDate)
        var ll = view.findViewById<LinearLayout>(R.id.linearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.in_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(arrayList[position].vendorName)
        holder.inTime.setText(arrayList[position].inTime)
        holder.outTime.setText(arrayList[position].outTime)
        holder.date.setText(arrayList[position].date)
//        holder.card.setBackgroundColor(getRandomColor())
        holder.ll.setOnClickListener {
            interfaces.onClick(position)
        }
    }
//    private fun getRandomColor(): Int {
//        val random = kotlin.random.Random
//        val red = random.nextInt(256) // Random value between 0 and 255
//        val green = random.nextInt(256)
//        val blue = random.nextInt(256)
//        return Color.rgb(red, green, blue)
//    }
}