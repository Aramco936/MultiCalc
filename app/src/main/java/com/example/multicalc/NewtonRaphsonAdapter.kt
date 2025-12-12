/*
package com.example.multicalc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewtonRaphsonAdapter(private val items: List<NewtonRaphsonRow>) :
    RecyclerView.Adapter<NewtonRaphsonAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIteracion: TextView = view.findViewById(R.id.tvIteracion)
        val tvXi: TextView = view.findViewById(R.id.tvXi)
        val tvFx: TextView = view.findViewById(R.id.tvFx)
        val tvDFx: TextView = view.findViewById(R.id.tvDFx)
        val tvError: TextView = view.findViewById(R.id.tvError)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_newton_rahpson, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = items[position]
        holder.tvIteracion.text = row.iteracion.toString()
        holder.tvXi.text = "%.6f".format(row.xi)
        holder.tvFx.text = "%.6f".format(row.fx)
        holder.tvDFx.text = "%.6f".format(row.dfx)
        holder.tvError.text = "%.6f".format(row.error)
    }
}
*/
package com.example.multicalc

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewtonRaphsonAdapter(
    private val items: List<NewtonRaphsonRow>
) : RecyclerView.Adapter<NewtonRaphsonAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIteracion: TextView = view.findViewById(R.id.tvIteracion)
        val tvXi: TextView = view.findViewById(R.id.tvXi)
        val tvFx: TextView = view.findViewById(R.id.tvFx)
        val tvXi1: TextView = view.findViewById(R.id.tvXi1)
        val tvError: TextView = view.findViewById(R.id.tvError)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_newton_rahpson, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = items.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val row = items[position]
        holder.tvIteracion.text = row.iteracion.toString()
        holder.tvXi.text = "%.6f".format(row.xi)
        holder.tvFx.text = "%.6f".format(row.fx)
        holder.tvXi1.text = "%.6f".format(row.xi1)
        holder.tvError.text = "%.6f".format(row.error)
    }
}
