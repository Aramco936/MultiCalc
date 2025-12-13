package com.example.multicalc

import android.os.Bundle
import android.os.SystemClock
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.PyObject
import java.io.File
import java.io.FileOutputStream

class PreguntasActivity : AppCompatActivity() {

    // Variables de Python
    private lateinit var py: Python
    private lateinit var generador: PyObject

    // Variables de UI
    private lateinit var tvPreguntaNumero: TextView
    private lateinit var tvEnunciado: TextView
    private lateinit var etRespuesta: EditText
    private lateinit var btnSiguiente: Button
    private lateinit var btnAnterior: Button
    private lateinit var btnFinalizar: Button
    private lateinit var chronometer: Chronometer
    private lateinit var progressBar: ProgressBar
    private lateinit var tvProgreso: TextView

    // Variables del examen
    private var preguntas = mutableListOf<Pregunta>()
    private var respuestasUsuario = mutableMapOf<Int, String>()
    private var preguntaActual = 0
    private var configuracion: ExamenesActivity.ConfiguracionExamen? = null
    private var tiempoInicio: Long = 0

    // Variable para la cuenta regresiva (Nuevo)
    private var countDownTimer: android.os.CountDownTimer? = null

    // Manejador botón atrás
    private val onBackPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            confirmarSalida()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_preguntas)

        onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
        inicializarVistas()

        // Obtener configuración (usando cast seguro para evitar warnings críticos)
        // Nota: getSerializableExtra está deprecated pero funciona.
        // Si usas Android 13+ deberías usar la nueva API, pero esta línea es compatible.
        configuracion = intent.getSerializableExtra("CONFIGURACION") as? ExamenesActivity.ConfiguracionExamen

        if (configuracion == null) {
            Toast.makeText(this, "Error: No se recibió configuración", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        if (!inicializarPython()) {
            finish()
            return
        }

        generarPreguntas()
        configurarCronometro()

        if (preguntas.isNotEmpty()) {
            mostrarPregunta(0)
        } else {
            Toast.makeText(this, "No se pudieron generar preguntas", Toast.LENGTH_LONG).show()
            finish()
        }

        configurarBotones()
    }

    // Aseguramos detener el timer si se sale de la actividad
    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
        chronometer.stop()
    }

    private fun inicializarVistas() {
        tvPreguntaNumero = findViewById(R.id.tvPreguntaNumero)
        tvEnunciado = findViewById(R.id.tvEnunciado)
        etRespuesta = findViewById(R.id.etRespuesta)
        btnSiguiente = findViewById(R.id.btnSiguiente)
        btnAnterior = findViewById(R.id.btnAnterior)
        btnFinalizar = findViewById(R.id.btnFinalizar)
        chronometer = findViewById(R.id.chronometer)
        progressBar = findViewById(R.id.progressBar)
        tvProgreso = findViewById(R.id.tvProgreso)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            confirmarSalida()
        }
    }

    private fun inicializarPython(): Boolean {
        try {
            if (!Python.isStarted()) {
                // Esto debería estar en tu Application class, pero por seguridad:
                // AndroidPlatform(this) etc... asumiendo que ya inicias Python en tu App.
                // Si usas Chaquopy gradle plugin, Python.getInstance() es suficiente.
            }
            py = Python.getInstance()

            val filesDir = applicationContext.filesDir
            val jsonFile = File(filesDir, "banco_preguntas.json")

            if (!jsonFile.exists()) {
                val inputStream = assets.open("banco_preguntas.json")
                val outputStream = FileOutputStream(jsonFile)
                inputStream.copyTo(outputStream)
                inputStream.close()
                outputStream.close()
            }

            val moduloGenerador = py.getModule("generador_preguntas")
            generador = moduloGenerador.callAttr("crear_generador", jsonFile.absolutePath)
            return true

        } catch (e: Exception) {
            Toast.makeText(this, "Error Python: ${e.message}", Toast.LENGTH_LONG).show()
            return false
        }
    }

    private fun generarPreguntas() {
        try {
            val temasConfig = mutableListOf<Map<String, String>>()
            configuracion?.temas?.forEach { tema ->
                temasConfig.add(mapOf(
                    "tema" to tema.nombre,
                    "dificultad" to tema.dificultad
                ))
            }

            val examenPy = generador.callAttr("generar_examen_completo", temasConfig.toTypedArray())
            val listaPy = examenPy.asList()

            listaPy.forEachIndexed { index, preguntaPy ->
                val enunciado = preguntaPy.callAttr("get", "enunciado").toString()
                val respuestaCorrecta = preguntaPy.callAttr("get", "respuesta").toString()
                val tipo = preguntaPy.callAttr("get", "tipo").toString()
                val tema = preguntaPy.callAttr("get", "tema").toString()
                val dificultad = preguntaPy.callAttr("get", "dificultad").toString()
                val id = preguntaPy.callAttr("get", "id").toString()

                val tolerancia = try {
                    preguntaPy.callAttr("get", "tolerancia")?.toDouble() ?: 0.1
                } catch (e: Exception) { 0.1 }

                preguntas.add(Pregunta(
                    numero = index + 1,
                    id = id,
                    enunciado = enunciado,
                    respuestaCorrecta = respuestaCorrecta,
                    tipo = tipo,
                    tema = tema,
                    dificultad = dificultad,
                    tolerancia = tolerancia
                ))
            }

            progressBar.max = preguntas.size
            actualizarProgreso()
            Toast.makeText(this, "Examen generado: ${preguntas.size} preguntas", Toast.LENGTH_SHORT).show()

        } catch (e: Exception) {
            Toast.makeText(this, "Error al generar preguntas: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    private fun configurarCronometro() {
        val limiteMinutos = configuracion?.tiempoLimite

        if (limiteMinutos != null) {
            // --- MODO CONTRA RELOJ (Cuenta Regresiva) ---
            val tiempoTotalMillis = limiteMinutos * 60 * 1000L

            countDownTimer = object : android.os.CountDownTimer(tiempoTotalMillis, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    val segundosTotales = millisUntilFinished / 1000
                    val minutos = segundosTotales / 60
                    val segundos = segundosTotales % 60

                    chronometer.text = String.format("%02d:%02d", minutos, segundos)

                    if (minutos < 1) {
                        chronometer.setTextColor(android.graphics.Color.RED)
                    }
                }

                override fun onFinish() {
                    chronometer.text = "00:00"
                    Toast.makeText(applicationContext, "¡Se acabó el tiempo!", Toast.LENGTH_LONG).show()
                    finalizarExamen()
                }
            }.start()

        } else {
            // --- MODO NORMAL (Cronómetro normal) ---
            tiempoInicio = SystemClock.elapsedRealtime()
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
        }
    }

    private fun configurarBotones() {
        btnAnterior.setOnClickListener {
            if (preguntaActual > 0) {
                guardarRespuestaActual()
                mostrarPregunta(preguntaActual - 1)
            }
        }

        btnSiguiente.setOnClickListener {
            if (preguntaActual < preguntas.size - 1) {
                guardarRespuestaActual()
                mostrarPregunta(preguntaActual + 1)
            }
        }

        btnFinalizar.setOnClickListener {
            confirmarFinalizacion()
        }
    }

    private fun mostrarPregunta(indice: Int) {
        if (indice < 0 || indice >= preguntas.size) return
        preguntaActual = indice
        val pregunta = preguntas[indice]

        tvPreguntaNumero.text = "Pregunta ${pregunta.numero} de ${preguntas.size}"
        tvEnunciado.text = buildString {
            append(pregunta.enunciado)
            append("\nTema: ${pregunta.tema}")
            append("\nDificultad: ${pregunta.dificultad}")
        }
        etRespuesta.setText(respuestasUsuario[indice] ?: "")

        btnAnterior.isEnabled = indice > 0
        btnSiguiente.isEnabled = indice < preguntas.size - 1

        if (indice == preguntas.size - 1) {
            btnFinalizar.visibility = android.view.View.VISIBLE
            btnSiguiente.visibility = android.view.View.GONE
        } else {
            btnFinalizar.visibility = android.view.View.GONE
            btnSiguiente.visibility = android.view.View.VISIBLE
        }
        actualizarProgreso()
    }

    private fun guardarRespuestaActual() {
        val respuesta = etRespuesta.text.toString().trim()
        if (respuesta.isNotEmpty()) {
            respuestasUsuario[preguntaActual] = respuesta
        } else {
            respuestasUsuario.remove(preguntaActual)
        }
    }

    private fun actualizarProgreso() {
        progressBar.progress = respuestasUsuario.size
        tvProgreso.text = "${respuestasUsuario.size} / ${preguntas.size} contestadas"
    }

    private fun confirmarFinalizacion() {
        guardarRespuestaActual()
        val preguntasSinResponder = preguntas.size - respuestasUsuario.size
        val mensaje = if (preguntasSinResponder > 0) {
            "Tienes $preguntasSinResponder pregunta(s) sin responder.\n\n¿Deseas finalizar?"
        } else {
            "Has respondido todo.\n\n¿Finalizar examen?"
        }

        AlertDialog.Builder(this)
            .setTitle("Finalizar Examen")
            .setMessage(mensaje)
            .setPositiveButton("Finalizar") { _, _ -> finalizarExamen() }
            .setNegativeButton("Continuar", null)
            .show()
    }

    private fun finalizarExamen() {
        // Detener cronómetro/timer
        countDownTimer?.cancel()
        chronometer.stop()

        // Calcular tiempo real usado (para mostrar en resultados)
        val tiempoTranscurrido = if (configuracion?.tiempoLimite == null) {
            (SystemClock.elapsedRealtime() - tiempoInicio) / 1000
        } else {
            // En modo contra reloj, podríamos mostrar el tiempo total límite o lo que usaron
            (configuracion!!.tiempoLimite!! * 60).toLong() // Simplificado
        }

        var correctas = 0
        val resultadosDetallados = mutableListOf<ResultadoPregunta>()

        preguntas.forEachIndexed { indice, pregunta ->
            val respuestaUsuario = respuestasUsuario[indice] ?: ""
            val esCorrecta = if (respuestaUsuario.isEmpty()) false else {
                try {
                    generador.callAttr("verificar_respuesta",
                        respuestaUsuario,
                        pregunta.respuestaCorrecta,
                        pregunta.tipo,
                        pregunta.tolerancia
                    ).toBoolean()
                } catch (e: Exception) { false }
            }
            if (esCorrecta) correctas++
            resultadosDetallados.add(ResultadoPregunta(
                pregunta.numero, pregunta.id, pregunta.enunciado,
                respuestaUsuario, pregunta.respuestaCorrecta, esCorrecta,
                pregunta.tema, pregunta.dificultad
            ))
        }

        val calificacion = (correctas.toFloat() / preguntas.size) * 10
        mostrarResultados(calificacion, correctas, preguntas.size, tiempoTranscurrido, resultadosDetallados)
    }

    private fun mostrarResultados(
        calificacion: Float, correctas: Int, totales: Int, tiempo: Long,
        detalles: List<ResultadoPregunta>
    ) {
        val minutos = tiempo / 60
        val segundos = tiempo % 60
        val resumen = buildString {
            append("RESULTADOS\n\n")
            append("Calificación: ${String.format("%.1f", calificacion)}/10\n")
            append("Correctas: $correctas/$totales\n")

            // Solo mostrar tiempo transcurrido si fue modo normal
            if (configuracion?.tiempoLimite == null) {
                append("Tiempo: ${minutos}m ${segundos}s\n\n")
            }

            append("\nDETALLES:\n")
            detalles.forEach { res ->
                val icon = if (res.esCorrecta) "✅" else "❌"
                append("$icon ${res.numero}. ${res.tema}\n")
                if (!res.esCorrecta) append("   Correcta: ${res.respuestaCorrecta}\n")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Examen Finalizado")
            .setMessage(resumen)
            .setPositiveButton("Salir") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    private fun confirmarSalida() {
        AlertDialog.Builder(this)
            .setTitle("Salir")
            .setMessage("¿Salir? Perderás el progreso.")
            .setPositiveButton("Salir") { _, _ -> finish() }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    data class Pregunta(
        val numero: Int,
        val id: String,
        val enunciado: String,
        val respuestaCorrecta: String,
        val tipo: String,
        val tema: String,
        val dificultad: String,
        val tolerancia: Double
    )

    data class ResultadoPregunta(
        val numero: Int,
        val id: String,
        val enunciado: String,
        val respuestaUsuario: String,
        val respuestaCorrecta: String,
        val esCorrecta: Boolean,
        val tema: String,
        val dificultad: String
    )
}