package com.harjot.salesadmin

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class VendorAdapter(var arrayList:ArrayList<VendorModel>, var interfaces: Interfaces):
    RecyclerView.Adapter<VendorAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        var name = view.findViewById<TextView>(R.id.tvName)
        var email = view.findViewById<TextView>(R.id.tvEmail)
        var contact = view.findViewById<TextView>(R.id.tvContact)
        var lv = view.findViewById<CardView>(R.id.lv)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.vendor_list_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return arrayList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.setText(arrayList[position].name)
        holder.email.setText(arrayList[position].email)
        holder.contact.setText(arrayList[position].contact)
//        holder.card.setBackgroundColor(getRandomColor())
        holder.lv.setOnClickListener {
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