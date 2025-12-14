package com.example.multicalc

import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast // Agregamos Toast para errores
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class HistoryActivity : AppCompatActivity() {

    private lateinit var adapter: ImagenAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        // 1. RECUPERAR EL NOMBRE DE USUARIO
        val currentUsername = intent.getStringExtra("EXTRA_USERNAME")

        // Validación de seguridad
        if (currentUsername == null) {
            Toast.makeText(this, "Error: No se identificó al usuario.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val btnBack = findViewById<ImageButton>(R.id.btnBack)
        btnBack.setOnClickListener {
            finish()
        }

        val recycler = findViewById<RecyclerView>(R.id.recyclerHistory)
        recycler.layoutManager = LinearLayoutManager(this)

        // 2. CAMBIO DE RUTA: Buscar en la carpeta PRIVADA del usuario
        // Ruta: .../files/[Usuario]/graficas
        val baseDir = getExternalFilesDir(null)
        val carpetaUsuario = File(baseDir, currentUsername)
        val carpeta = File(carpetaUsuario, "graficas")

        // Si la carpeta no existe, la creamos (para evitar errores si es la primera vez)
        if (!carpeta.exists()) carpeta.mkdirs()

        val imagenes = carpeta.listFiles()?.toMutableList() ?: mutableListOf()

        // Si la lista está vacía, es bueno avisar (opcional, para depuración)
        if (imagenes.isEmpty()) {
            Toast.makeText(this, "No hay gráficas guardadas para $currentUsername", Toast.LENGTH_SHORT).show()
        }

        adapter = ImagenAdapter(imagenes) { archivo ->
            archivo.delete()
            adapter.eliminarImagen(archivo)
        }

        recycler.adapter = adapter
    }
}
