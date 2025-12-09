package com.example.multicalc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {
    // Variable para almacenar el nombre del usuario logeado
    private lateinit var currentUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 1. Recuperar el nombre de usuario del Intent
        // El Intent debe venir desde LoginActivity
        currentUsername = intent.getStringExtra("EXTRA_USERNAME") ?: "Usuario Desconocido"

        // 2. Ejemplo: Configurar un botón de perfil
        val btnProfile = findViewById<Button>(R.id.btn_profile_access)
        btnProfile.setOnClickListener {
            // 3. Llamar a la función de navegación
            navigateToProfile()
        }

        // Referencias a las tarjetas
        val cardCalculadora = findViewById<CardView>(R.id.cardCalculadora)
        val cardConversiones = findViewById<CardView>(R.id.cardConversiones)
        val cardFormulas = findViewById<CardView>(R.id.cardFormulas)
        val cardGraficador = findViewById<CardView>(R.id.cardGraficador)
        val cardExamenes = findViewById<CardView>(R.id.cardExamenes)
        val cardHistorial = findViewById<CardView>(R.id.cardHistorial)

        // Click listeners
        cardCalculadora.setOnClickListener {
            val intent = Intent(this, CalculadorasMenuActivity::class.java)
            startActivity(intent)
        }

        cardConversiones.setOnClickListener {
            val intent = Intent(this, ConversionesActivity::class.java)
            startActivity(intent)
        }

        cardFormulas.setOnClickListener {
            Toast.makeText(this, "Abriendo Fórmulas", Toast.LENGTH_SHORT).show()
        }

        cardGraficador.setOnClickListener {
            val intent = Intent(this, GraficadorActivity::class.java)
            startActivity(intent)
        }

        cardExamenes.setOnClickListener {
            Toast.makeText(this, "Abriendo Exámenes", Toast.LENGTH_SHORT).show()
        }

        cardHistorial.setOnClickListener {
            Toast.makeText(this, "Abriendo Historial", Toast.LENGTH_SHORT).show()
        }
    }
    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)

        // 4. Pasar el nombre de usuario a ProfileActivity
        intent.putExtra("EXTRA_USERNAME", currentUsername)

        startActivity(intent)
    }
}