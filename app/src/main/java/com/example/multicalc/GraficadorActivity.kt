package com.example.multicalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class GraficadorActivity : AppCompatActivity() {

    private lateinit var idImageview: ImageView
    private lateinit var etFuncion: EditText
    private lateinit var etLiteral: EditText
    private lateinit var etIntervaloA: EditText
    private lateinit var etIntervaloB: EditText
    private lateinit var btnCalcular: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graficador)

        // Inicializar vistas
        idImageview = findViewById(R.id.idImageview)
        etFuncion = findViewById(R.id.etFuncion)
        etLiteral = findViewById(R.id.etLiteral)
        etIntervaloA = findViewById(R.id.etIntervaloA)
        etIntervaloB = findViewById(R.id.etIntervaloB)
        btnCalcular = findViewById(R.id.btnCalcular)

        // Botón volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Botón calcular
        btnCalcular.setOnClickListener {
            generarGrafica()
        }
    }

    private fun generarGrafica() {
        val funcion = etFuncion.text.toString().trim()
        val literal = etLiteral.text.toString().trim()
        val intervaloAStr = etIntervaloA.text.toString().trim()
        val intervaloBStr = etIntervaloB.text.toString().trim()

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

        // Validar intervalos
        val intervaloA = intervaloAStr.toDoubleOrNull()
        val intervaloB = intervaloBStr.toDoubleOrNull()

        if (intervaloA == null || intervaloB == null) {
            Toast.makeText(this, "Intervalos inválidos", Toast.LENGTH_SHORT).show()
            return
        }

        if (intervaloA >= intervaloB) {
            Toast.makeText(this, "El intervalo A debe ser menor que B", Toast.LENGTH_SHORT).show()
            return
        }

        // Ejemplo de lo que debería hacer tu código:
        // val py = Python.getInstance()
        // val module = py.getModule("graficador")
        // val imagePath = module.callAttr("graficar_funcion", funcion, literal, intervaloA, intervaloB).toString()
        // val bitmap = BitmapFactory.decodeFile(imagePath)
        // idImageview.setImageBitmap(bitmap)

        Toast.makeText(
            this,
            "Lógica pendiente - Orlando\n\nFunción: $funcion\nLiteral: $literal\nIntervalo: [$intervaloA, $intervaloB]",
            Toast.LENGTH_LONG
        ).show()
    }
}