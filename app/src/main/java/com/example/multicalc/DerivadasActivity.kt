package com.example.multicalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class DerivadasActivity : AppCompatActivity() {

    private lateinit var etFuncion: EditText
    private lateinit var etLiteral: EditText
    private lateinit var btnCalcular: Button
    private lateinit var txtResultado: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_derivadas)

        //Habilitar chaquopy
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }


        // Inicializar vistas
        etFuncion = findViewById(R.id.etFuncion)
        etLiteral = findViewById(R.id.etLiteral)
        btnCalcular = findViewById(R.id.btnCalcular)
        txtResultado = findViewById(R.id.txtResultado)
        //txtResultado = findViewById(R.id.webLatex)

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

        // Validaciones
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

        try {
            val py = com.chaquo.python.Python.getInstance()
            val modulo = py.getModule("calculadora_simbolica")


            val resultado = modulo.callAttr("derivar", funcion, literal)

            // Detectar si es booleano
            val esBoolean = try {
                resultado.toJava(Boolean::class.java) != null
            } catch (e: Exception) {
                false
            }

            if (esBoolean && resultado.toBoolean() == false) {
                txtResultado.text = "Error: entrada inválida"
            } else {
                txtResultado.text = resultado.toString()
            }

        } catch (e: Exception) {
            txtResultado.text = "Error en Python:\n${e.message}"
        }
    }


}