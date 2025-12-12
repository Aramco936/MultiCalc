package com.example.multicalc

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class BiseccionAdapter(private val lista: List<BiseccionRow>)
    : RecyclerView.Adapter<BiseccionAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tv_i: TextView = view.findViewById(R.id.tv_i)
        val tv_Xa: TextView = view.findViewById(R.id.tv_Xa)
        val tv_Xb: TextView = view.findViewById(R.id.tv_Xb)
        val tv_fXa: TextView = view.findViewById(R.id.tv_fXa)
        val tv_fXb: TextView = view.findViewById(R.id.tv_fXb)
        val tv_m: TextView = view.findViewById(R.id.tv_m)
        val tv_f_m: TextView = view.findViewById(R.id.tv_f_m)
        val tv_error: TextView = view.findViewById(R.id.tv_error)
        val layout: LinearLayout = view as LinearLayout
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_biseccion, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = lista[position]

        holder.tv_i.text = row.i.toString()
        holder.tv_Xa.text = row.Xa.toString()
        holder.tv_Xb.text = row.Xb.toString()
        holder.tv_fXa.text = row.fXa.toString()
        holder.tv_fXb.text = row.fXb.toString()
        holder.tv_m.text = row.m.toString()
        holder.tv_f_m.text = row.f_m.toString()
        holder.tv_error.text = row.error.toString()

        if (position % 2 == 0)
            holder.layout.setBackgroundColor(Color.parseColor("#FFFFFF"))
        else
            holder.layout.setBackgroundColor(Color.parseColor("#F0F0F0"))
    }

    override fun getItemCount() = lista.size
}
