package com.example.multicalc

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.tabs.TabLayout

class ConversionesActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private lateinit var spinnerOrigen: Spinner
    private lateinit var spinnerDestino: Spinner
    private lateinit var etValor: EditText
    private lateinit var btnConvertir: Button
    private lateinit var txtResultado: TextView

    private var tipoActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversiones)

        // Inicializar vistas
        tabLayout = findViewById(R.id.tabLayout)
        spinnerOrigen = findViewById(R.id.spinnerOrigen)
        spinnerDestino = findViewById(R.id.spinnerDestino)
        etValor = findViewById(R.id.etValor)
        btnConvertir = findViewById(R.id.btnConvertir)
        txtResultado = findViewById(R.id.txtResultado)

        // Configurar tabs
        configurarTabs()

        // Listener del botón convertir
        btnConvertir.setOnClickListener {
            realizarConversion()
        }

        // Botón atrás
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    private fun configurarTabs() {
        // Agregar tabs
        tabLayout.addTab(tabLayout.newTab().setText("Masa"))
        tabLayout.addTab(tabLayout.newTab().setText("Longitud"))
        tabLayout.addTab(tabLayout.newTab().setText("Volumen"))
        tabLayout.addTab(tabLayout.newTab().setText("Temperatura"))
        tabLayout.addTab(tabLayout.newTab().setText("Sistema Numérico"))

        // Cargar unidades del primer tab
        actualizarUnidades(0)

        // Listener cuando cambia de tab
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tipoActual = tab?.position ?: 0
                actualizarUnidades(tipoActual)
                txtResultado.text = ""
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun actualizarUnidades(tipoPosition: Int) {
        val unidades = when (tipoPosition) {
            0 -> arrayOf("Kilogramo", "Gramo", "Miligramo", "Tonelada", "Libra", "Onza")
            1 -> arrayOf("Metro", "Kilómetro", "Centímetro", "Milímetro", "Milla", "Pie", "Pulgada")
            2 -> arrayOf("Litro", "Mililitro", "Metro cúbico", "Galón", "Onza líquida")
            3 -> arrayOf("Celsius", "Fahrenheit", "Kelvin")
            4 -> arrayOf("Decimal", "Binario", "Hexadecimal", "Octal")
            else -> arrayOf("")
        }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, unidades)
        spinnerOrigen.adapter = adapter
        spinnerDestino.adapter = adapter
    }

    private fun realizarConversion() {
        val valorStr = etValor.text.toString()

        if (valorStr.isEmpty()) {
            Toast.makeText(this, "Ingresa un valor", Toast.LENGTH_SHORT).show()
            return
        }

        val valor = valorStr.toDoubleOrNull()
        if (valor == null) {
            Toast.makeText(this, "Valor inválido", Toast.LENGTH_SHORT).show()
            return
        }

        val origen = spinnerOrigen.selectedItem.toString()
        val destino = spinnerDestino.selectedItem.toString()

        // Aquí Orlando llamará a Python
        val resultado = convertir(valor, tipoActual, origen, destino)

        txtResultado.text = "$valor $origen =\n$resultado $destino"
    }

    // Función temporal - Orlando la reemplazará con Python
    private fun convertir(valor: Double, tipo: Int, origen: String, destino: String): String {
        return when (tipo) {
            0 -> convertirPeso(valor, origen, destino)
            1 -> convertirLongitud(valor, origen, destino)
            2 -> convertirVolumen(valor, origen, destino)
            3 -> convertirTemperatura(valor, origen, destino)
            4 -> convertirSistemaNumericos(valor.toInt(), origen, destino)
            else -> "0"
        }
    }

    private fun convertirPeso(valor: Double, origen: String, destino: String): String {
        val kg = when (origen) {
            "Kilogramo" -> valor
            "Gramo" -> valor / 1000
            "Miligramo" -> valor / 1000000
            "Tonelada" -> valor * 1000
            "Libra" -> valor * 0.453592
            "Onza" -> valor * 0.0283495
            else -> valor
        }

        val resultado = when (destino) {
            "Kilogramo" -> kg
            "Gramo" -> kg * 1000
            "Miligramo" -> kg * 1000000
            "Tonelada" -> kg / 1000
            "Libra" -> kg / 0.453592
            "Onza" -> kg / 0.0283495
            else -> kg
        }

        return String.format("%.4f", resultado)
    }

    private fun convertirLongitud(valor: Double, origen: String, destino: String): String {
        val metros = when (origen) {
            "Metro" -> valor
            "Kilómetro" -> valor * 1000
            "Centímetro" -> valor / 100
            "Milímetro" -> valor / 1000
            "Milla" -> valor * 1609.34
            "Pie" -> valor * 0.3048
            "Pulgada" -> valor * 0.0254
            else -> valor
        }

        val resultado = when (destino) {
            "Metro" -> metros
            "Kilómetro" -> metros / 1000
            "Centímetro" -> metros * 100
            "Milímetro" -> metros * 1000
            "Milla" -> metros / 1609.34
            "Pie" -> metros / 0.3048
            "Pulgada" -> metros / 0.0254
            else -> metros
        }

        return String.format("%.4f", resultado)
    }

    private fun convertirVolumen(valor: Double, origen: String, destino: String): String {
        val litros = when (origen) {
            "Litro" -> valor
            "Mililitro" -> valor / 1000
            "Metro cúbico" -> valor * 1000
            "Galón" -> valor * 3.78541
            "Onza líquida" -> valor * 0.0295735
            else -> valor
        }

        val resultado = when (destino) {
            "Litro" -> litros
            "Mililitro" -> litros * 1000
            "Metro cúbico" -> litros / 1000
            "Galón" -> litros / 3.78541
            "Onza líquida" -> litros / 0.0295735
            else -> litros
        }

        return String.format("%.4f", resultado)
    }

    private fun convertirTemperatura(valor: Double, origen: String, destino: String): String {
        val celsius = when (origen) {
            "Celsius" -> valor
            "Fahrenheit" -> (valor - 32) * 5 / 9
            "Kelvin" -> valor - 273.15
            else -> valor
        }

        val resultado = when (destino) {
            "Celsius" -> celsius
            "Fahrenheit" -> (celsius * 9 / 5) + 32
            "Kelvin" -> celsius + 273.15
            else -> celsius
        }

        return String.format("%.2f", resultado)
    }

    private fun convertirSistemaNumericos(valor: Int, origen: String, destino: String): String {
        val decimal = when (origen) {
            "Decimal" -> valor
            "Binario" -> valor.toString().toInt(2)
            "Hexadecimal" -> valor.toString().toInt(16)
            "Octal" -> valor.toString().toInt(8)
            else -> valor
        }

        return when (destino) {
            "Decimal" -> decimal.toString()
            "Binario" -> decimal.toString(2)
            "Hexadecimal" -> decimal.toString(16).uppercase()
            "Octal" -> decimal.toString(8)
            else -> decimal.toString()
        }
    }
}