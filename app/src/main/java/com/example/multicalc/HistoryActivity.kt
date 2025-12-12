package com.example.multicalc

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class HistoryActivity : AppCompatActivity() {

    private lateinit var adapter: ImagenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerHistory)
        recycler.layoutManager = LinearLayoutManager(this)

        // Carpeta donde SÍ estás guardando las gráficas
        val carpeta = File(getExternalFilesDir(null), "graficas")

        if (!carpeta.exists()) carpeta.mkdirs()

        val imagenes = carpeta.listFiles()?.toMutableList() ?: mutableListOf()

        adapter = ImagenAdapter(imagenes) { archivo ->
            archivo.delete()
            adapter.eliminarImagen(archivo)
        }

        recycler.adapter = adapter

    }
}
