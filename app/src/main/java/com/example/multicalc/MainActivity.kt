package com.example.multicalc

import android.widget.Button
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
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
        val btnProfile = findViewById<ImageButton>(R.id.btn_profile_access)
        btnProfile.setOnClickListener {
            // 3. Llamar a la función de navegación
            navigateToProfile()
        }

        // Referencias a las tarjetas
        val cardCalculadora = findViewById<CardView>(R.id.cardCalculadora)
        val cardConversiones = findViewById<CardView>(R.id.cardConversiones)
        val cardFormulario = findViewById<CardView>(R.id.cardFormulario)
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

        cardFormulario.setOnClickListener {
            val intent = Intent(this, FormularioActivity::class.java)
            startActivity(intent)
        }

        cardGraficador.setOnClickListener {
            val intent = Intent(this, GraficadorActivity::class.java)
            startActivity(intent)
        }

        cardExamenes.setOnClickListener {
            Toast.makeText(this, "Abriendo Exámenes", Toast.LENGTH_SHORT).show()
        }

        cardHistorial.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }
    }
    private fun navigateToProfile() {
        val intent = Intent(this, ProfileActivity::class.java)

        // 4. Pasar el nombre de usuario a ProfileActivity
        intent.putExtra("EXTRA_USERNAME", currentUsername)

        startActivity(intent)
    }

    fun launchGraficador() {
        val intent = Intent(this, GraficadorActivity::class.java)
        // Asumiendo que ya tienes currentUsername como variable de clase en MainActivity
        intent.putExtra("EXTRA_USERNAME", currentUsername)
        startActivity(intent)
    }
}