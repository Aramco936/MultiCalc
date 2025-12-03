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
        val cardFormulas = findViewById<CardView>(R.id.cardFormulas)
        val cardGraficador = findViewById<CardView>(R.id.cardGraficador)
        val cardExamenes = findViewById<CardView>(R.id.cardExamenes)
        val cardHistorial = findViewById<CardView>(R.id.cardHistorial)

        // Click listeners
        cardCalculadora.setOnClickListener {
            Toast.makeText(this, "Abriendo Calculadora", Toast.LENGTH_SHORT).show()
            // Después navegarás a CalculadoraActivity
        }

        cardConversiones.setOnClickListener {
            val intent = Intent(this, ConversionesActivity::class.java)
            startActivity(intent)
        }

        cardFormulas.setOnClickListener {
            Toast.makeText(this, "Abriendo Fórmulas", Toast.LENGTH_SHORT).show()
        }

        cardGraficador.setOnClickListener {
            Toast.makeText(this, "Abriendo Graficador", Toast.LENGTH_SHORT).show()
        }

        cardExamenes.setOnClickListener {
            Toast.makeText(this, "Abriendo Exámenes", Toast.LENGTH_SHORT).show()
        }

        cardHistorial.setOnClickListener {
            Toast.makeText(this, "Abriendo Historial", Toast.LENGTH_SHORT).show()
        }
    }
}