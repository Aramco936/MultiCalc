package com.example.multicalc

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
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
import java.io.FileOutputStream

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

        // Validaciones básicas
        if (funcion.isEmpty()) {
            Toast.makeText(this, "Ingresa una función", Toast.LENGTH_SHORT).show()
            return
        }
        if (literal.isEmpty() || literal.length != 1) {
            Toast.makeText(this, "Ingresa un literal válido", Toast.LENGTH_SHORT).show()
            return
        }

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

        generarGraficaConPython(funcion, literal, intervaloA, intervaloB)
    }

    private fun generarGraficaConPython(
        funcion: String,
        literal: String,
        intervaloA: Double,
        intervaloB: Double
    ) {
        try {
            val py = Python.getInstance()
            val modulo = py.getModule("graficador")

            // Crear archivo temporal en cache
            val timestamp = System.currentTimeMillis()
            val archivoTemporal = File(cacheDir, "grafica_temp_$timestamp.png")
            val rutaCompleta = archivoTemporal.absolutePath

            Log.d("Graficador", "Ruta temporal: $rutaCompleta")

            val resultado = modulo.callAttr(
                "graficar_archivo",
                funcion,
                literal,
                intervaloA,
                intervaloB,
                rutaCompleta
            )

            if (resultado.toString() == "False") {
                Toast.makeText(this, "Error al generar la gráfica", Toast.LENGTH_SHORT).show()
                return
            }

            if (!archivoTemporal.exists()) {
                Toast.makeText(this, "No se pudo crear el archivo de gráfica", Toast.LENGTH_SHORT).show()
                return
            }

            // Cargar el bitmap generado
            val bitmap = BitmapFactory.decodeFile(rutaCompleta)
            if (bitmap != null) {
                idImageview.setImageBitmap(bitmap)
                Toast.makeText(this, "Gráfica generada exitosamente", Toast.LENGTH_SHORT).show()

                // Guardar el bitmap en almacenamiento interno persistente
                val nombreGuardado = guardarGrafica(bitmap)
                Log.d("Graficador", "Gráfica guardada como: $nombreGuardado")
            } else {
                Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            }

        } catch (e: Exception) {
            Log.e("Graficador", "Error: ${e.message}", e)
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
/*
    // Función para guardar la gráfica en almacenamiento interno
    private fun guardarGrafica(bitmap: Bitmap): String {
        val timestamp = System.currentTimeMillis()
        val carpeta = getExternalFilesDir(null)!! // Carpeta externa de la app
        if (!carpeta.exists()) carpeta.mkdirs()

        val file = File(carpeta, "grafica_$timestamp.png")
        FileOutputStream(file).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        // Mostrar ruta para verificar
        Toast.makeText(this, "Gráfica guardada en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        Log.d("Graficador", "Gráfica guardada en: ${file.absolutePath}")

        return file.absolutePath
    }
*/
    private fun guardarGrafica(bitmap: Bitmap): String {
        val timestamp = System.currentTimeMillis()

        // Crear carpeta graficas dentro del almacenamiento externo privado
        val carpetaGraficas = File(getExternalFilesDir(null), "graficas")
        if (!carpetaGraficas.exists()) carpetaGraficas.mkdirs()

        val archivo = File(carpetaGraficas, "grafica_$timestamp.png")

        FileOutputStream(archivo).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }

        Toast.makeText(this, "Guardada: ${archivo.absolutePath}", Toast.LENGTH_LONG).show()
        Log.d("Graficador", "Guardada: ${archivo.absolutePath}")

        return archivo.absolutePath
    }

}