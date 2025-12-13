package com.example.multicalc

import java.io.Serializable // Importante para pasar objetos
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class ExamenesActivity : AppCompatActivity() {

    // Checkboxes de temas
    private lateinit var checkIntegrales: CheckBox
    private lateinit var checkDerivadas: CheckBox
    private lateinit var checkConversiones: CheckBox
    private lateinit var checkSistemasNumericos: CheckBox
    private lateinit var checkFisica: CheckBox

    // Spinners de dificultad y modo
    private lateinit var spinnerIntegrales: Spinner
    private lateinit var spinnerDerivadas: Spinner
    private lateinit var spinnerConversiones: Spinner
    private lateinit var spinnerSistemasNumericos: Spinner
    private lateinit var spinnerFisica: Spinner
    private lateinit var spinnerModo: Spinner

    // Botones
    private lateinit var btnComenzar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_examenes)

        // Inicializar vistas
        inicializarVistas()

        // Configurar spinners de dificultad y modo
        configurarSpinners()

        // Botón volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Botón comenzar
        btnComenzar.setOnClickListener {
            iniciarExamen()
        }
    }

    private fun inicializarVistas() {
        // Checkboxes
        checkIntegrales = findViewById(R.id.checkIntegrales)
        checkDerivadas = findViewById(R.id.checkDerivadas)
        checkConversiones = findViewById(R.id.checkConversiones)
        checkSistemasNumericos = findViewById(R.id.checkSistemasNumericos)
        checkFisica = findViewById(R.id.checkFisica)

        // Spinners
        spinnerIntegrales = findViewById(R.id.spinnerDificultadIntegrales)
        spinnerDerivadas = findViewById(R.id.spinnerDificultadDerivadas)
        spinnerConversiones = findViewById(R.id.spinnerDificultadConversiones)
        spinnerSistemasNumericos = findViewById(R.id.spinnerDificultadSistemasNumericos)
        spinnerFisica = findViewById(R.id.spinnerDificultadFisica)
        spinnerModo = findViewById(R.id.spinnerModo)

        // Botones
        btnComenzar = findViewById(R.id.btnComenzar)
    }

    private fun configurarSpinners() {
        // Spinners de dificultad (Corregidos acentos visuales)
        val dificultades = arrayOf("Fácil", "Medio", "Difícil")
        val adapterDificultad = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, dificultades)

        spinnerIntegrales.adapter = adapterDificultad
        spinnerDerivadas.adapter = adapterDificultad
        spinnerConversiones.adapter = adapterDificultad
        spinnerSistemasNumericos.adapter = adapterDificultad
        spinnerFisica.adapter = adapterDificultad

        // Spinner de modo (Textos limpios)
        val modos = arrayOf(
            "Seleccionar modo",
            "Normal cronometrado",
            "Contra reloj"
        )
        val adapterModo = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, modos)
        spinnerModo.adapter = adapterModo
    }

    private fun iniciarExamen() {
        // Validar que al menos un tema esté seleccionado
        val temasSeleccionados = obtenerTemasSeleccionados()

        if (temasSeleccionados.isEmpty()) {
            Toast.makeText(this, "Selecciona al menos un tema", Toast.LENGTH_SHORT).show()
            return
        }

        // Validar que se haya seleccionado un modo
        val modoSeleccionado = spinnerModo.selectedItem.toString()
        if (modoSeleccionado == "Seleccionar modo") {
            Toast.makeText(this, "Selecciona un modo de examen", Toast.LENGTH_SHORT).show()
            return
        }

        // Crear configuración del examen
        val configuracion = crearConfiguracionExamen(temasSeleccionados, modoSeleccionado)

        // Mostrar resumen antes de empezar
        mostrarResumenExamen(configuracion)
    }

    private fun obtenerTemasSeleccionados(): List<TemaExamen> {
        val temas = mutableListOf<TemaExamen>()

        if (checkIntegrales.isChecked) {
            temas.add(TemaExamen("Integrales", spinnerIntegrales.selectedItem.toString()))
        }

        if (checkDerivadas.isChecked) {
            temas.add(TemaExamen("Derivadas", spinnerDerivadas.selectedItem.toString()))
        }

        if (checkConversiones.isChecked) {
            temas.add(TemaExamen("Conversiones", spinnerConversiones.selectedItem.toString()))
        }


        if (checkSistemasNumericos.isChecked) {
            temas.add(TemaExamen("Sistemas numéricos", spinnerSistemasNumericos.selectedItem.toString()))
        }

        if (checkFisica.isChecked) {
            temas.add(TemaExamen("Física", spinnerFisica.selectedItem.toString()))
        }

        return temas
    }

    private fun crearConfiguracionExamen(temas: List<TemaExamen>, modo: String): ConfiguracionExamen {
        return ConfiguracionExamen(
            temas = temas,
            modo = modo,
            numeroPreguntas = calcularNumeroPreguntas(temas),
            // Ahora pasamos la lista de temas para calcular el tiempo total
            tiempoLimite = obtenerTiempoLimite(modo, temas)
        )
    }

    private fun calcularNumeroPreguntas(temas: List<TemaExamen>): Int {
        return temas.size * 5
    }

    private fun obtenerTiempoLimite(modo: String, temas: List<TemaExamen>): Int? {
        // Si NO es contra reloj (es decir, es "Normal cronometrado"), devolvemos null
        if (modo != "Contra reloj") {
            return null
        }

        // Lógica para Contra reloj: Sumar minutos según dificultad
        var tiempoTotalMinutos = 0
        val preguntasPorTema = 5

        for (tema in temas) {
            val minutosPorPregunta = when (tema.dificultad) {
                "Fácil" -> 1    // 1 min por pregunta
                "Medio" -> 2    // 2 min por pregunta
                "Difícil" -> 3  // 3 min por pregunta
                else -> 1
            }
            tiempoTotalMinutos += (preguntasPorTema * minutosPorPregunta)
        }

        return tiempoTotalMinutos
    }

    private fun mostrarResumenExamen(config: ConfiguracionExamen) {
        val resumen = buildString {
            append("Configuración del examen:\n")
            append("Modo: ${config.modo}\n")
            append("Temas:\n")
            config.temas.forEach { tema ->
                append("  • ${tema.nombre} (${tema.dificultad})\n")
            }
            append("\nPreguntas totales: ${config.numeroPreguntas}")
            config.tiempoLimite?.let {
                append("\nTiempo límite: $it minutos")
            } ?: run {
                append("\nTiempo: Libre (Cronómetro)")
            }
        }

        AlertDialog.Builder(this)
            .setTitle("Resumen")
            .setMessage(resumen)
            .setPositiveButton("Comenzar") { _, _ ->
                val intent = Intent(this, PreguntasActivity::class.java)
                intent.putExtra("CONFIGURACION", config)
                startActivity(intent)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // Clases de datos con Serializable
    data class TemaExamen(
        val nombre: String,
        val dificultad: String
    ) : Serializable

    data class ConfiguracionExamen(
        val temas: List<TemaExamen>,
        val modo: String,
        val numeroPreguntas: Int,
        val tiempoLimite: Int?
    ) : Serializable
}