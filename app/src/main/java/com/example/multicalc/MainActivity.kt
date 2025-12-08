package com.example.multicalc

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            Toast.makeText(this, "Abriendo Historial", Toast.LENGTH_SHORT).show()
        }
    }
}