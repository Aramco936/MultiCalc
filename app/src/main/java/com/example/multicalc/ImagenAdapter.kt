package com.example.multicalc

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class ImagenAdapter(
    private val lista: MutableList<File>,
    private val onDelete: (File) -> Unit
) : RecyclerView.Adapter<ImagenAdapter.Holder>() {

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img: ImageView = itemView.findViewById(R.id.imgGrafica)
        val btnBorrar: ImageButton = itemView.findViewById(R.id.btnBorrar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_imagen, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val archivo = lista[position]

        val bitmap = BitmapFactory.decodeFile(archivo.absolutePath)
        holder.img.setImageBitmap(bitmap)

        holder.btnBorrar.setOnClickListener {
            onDelete(archivo)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun eliminarImagen(file: File) {
        val index = lista.indexOf(file)
        if (index != -1) {
            lista.removeAt(index)
            notifyItemRemoved(index)
        }
    }
}