package com.example.multicalc

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File

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

        // INICIALIZAR PYTHON
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        // Inicializar vistas
        idImageview = findViewById(R.id.idImageview)
        etFuncion = findViewById(R.id.etFuncion)
        etLiteral = findViewById(R.id.etLiteral)
        etIntervaloA = findViewById(R.id.etIntervaloA)
        etIntervaloB = findViewById(R.id.etIntervaloB)
        btnCalcular = findViewById(R.id.btnCalcular)

        // Boton volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Boton calcular
        btnCalcular.setOnClickListener {
            generarGrafica()
        }
    }

    private fun generarGrafica() {
        val funcion = etFuncion.text.toString().trim()
        val literal = etLiteral.text.toString().trim()
        val intervaloAStr = etIntervaloA.text.toString().trim()
        val intervaloBStr = etIntervaloB.text.toString().trim()

        // Validaciones basicas
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

        // Llamar al metodo que usa Python
        generarGraficaConPython(funcion, literal, intervaloA, intervaloB)
    }

    private fun generarGraficaConPython(
        funcion: String,
        literal: String,
        intervaloA: Double,
        intervaloB: Double
    ) {
        try {
            // Obtener instancia de Python
            val py = Python.getInstance()

            // Obtener el módulo
            val modulo = py.getModule("graficador")

            // Crear archivo temporal unico
            val timestamp = System.currentTimeMillis()
            val archivo = File(cacheDir, "grafica_$timestamp.png")
            val rutaCompleta = archivo.absolutePath

            Log.d("Graficador", "Ruta del archivo: $rutaCompleta")

            // Llamar a la funcion Python con la ruta completa
            val resultado = modulo.callAttr(
                "graficar_archivo",
                funcion,
                literal,
                intervaloA,
                intervaloB,
                rutaCompleta
            )

            // Verificar si hubo error
            if (resultado.toString() == "False") {
                Toast.makeText(this, "Error al generar la gráfica", Toast.LENGTH_SHORT).show()
                return
            }

            // Verificar que el archivo se creo
            if (archivo.exists()) {
                val fileSize = archivo.length()
                Log.d("Graficador", "Archivo creado: ${archivo.name}, tamaño: $fileSize bytes")

                // Cargar y mostrar la imagen
                val bitmap = BitmapFactory.decodeFile(rutaCompleta)
                if (bitmap != null) {
                    idImageview.setImageBitmap(bitmap)
                    Toast.makeText(this, "Gráfica generada exitosamente", Toast.LENGTH_SHORT).show()

                }
                else {
                    Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
                }
            }
            else {
                Toast.makeText(this, "No se pudo crear el archivo de gráfica", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("Graficador", "Error: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}