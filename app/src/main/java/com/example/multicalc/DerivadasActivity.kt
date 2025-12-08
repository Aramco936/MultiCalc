package com.example.multicalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class DerivadasActivity : AppCompatActivity() {

    private lateinit var etFuncion: EditText
    private lateinit var etLiteral: EditText
    private lateinit var btnCalcular: Button
    private lateinit var txtResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_derivadas)

        // Inicializar vistas
        etFuncion = findViewById(R.id.etFuncion)
        etLiteral = findViewById(R.id.etLiteral)
        btnCalcular = findViewById(R.id.btnCalcular)
        txtResultado = findViewById(R.id.txtResultado)

        // Botón volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Botón calcular
        btnCalcular.setOnClickListener {
            calcularDerivada()
        }
    }

    private fun calcularDerivada() {
        val funcion = etFuncion.text.toString().trim()
        val literal = etLiteral.text.toString().trim()

        // Validaciones básicas
        if (funcion.isEmpty()) {
            Toast.makeText(this, "Ingresa una función", Toast.LENGTH_SHORT).show()
            return
        }

        if (literal.isEmpty()) {
            Toast.makeText(this, "Ingresa un literal", Toast.LENGTH_SHORT).show()
            return
        }

        if (literal.length != 1) {
            Toast.makeText(this, "El literal debe ser una sola letra", Toast.LENGTH_SHORT).show()
            return
        }

        // 1. Inicializar Python (Chaquopy)
        // 2. Llamar al módulo Python con la función y literal
        // 3. Mostrar el resultado en txtResultado

        // Por ahora, mensaje temporal
        txtResultado.text = "Esperando implementación de Python...\n\nFunción: $funcion\nLiteral: $literal"
        Toast.makeText(this, "Lógica pendiente - Orlando", Toast.LENGTH_SHORT).show()
    }
}