/*
package com.example.multicalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.Python
import com.chaquo.python.PyObject

class NewtonRaphsonActivity : AppCompatActivity() {

    private lateinit var etFuncion: EditText
    private lateinit var etAproxInicial: EditText
    private lateinit var etErrorPermitido: EditText
    private lateinit var etIteraciones: EditText
    private lateinit var btnCalcular: Button
    private lateinit var btnBack: Button
    private lateinit var rvNewtonRaphson: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // VERIFICA EL NOMBRE EXACTO DE TU ARCHIVO XML
        setContentView(R.layout.activity_newton_rahpson) // O activity_newton_raphson

        etFuncion = findViewById(R.id.etFuncion)
        etAproxInicial = findViewById(R.id.etAproxInicial)
        etErrorPermitido = findViewById(R.id.etErrorPermitido)
        etIteraciones = findViewById(R.id.etIteraciones)
        btnCalcular = findViewById(R.id.btnCalcular)
        btnBack = findViewById(R.id.btnBack)
        rvNewtonRaphson = findViewById(R.id.rvNewtonRaphson)
        rvNewtonRaphson.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        btnCalcular.setOnClickListener {
            calcularNewtonRaphson()
        }
    }

    private fun calcularNewtonRaphson() {
        val funcion = etFuncion.text.toString()
        val aproxInicial = etAproxInicial.text.toString().toDoubleOrNull()
        val errorPermitido = etErrorPermitido.text.toString().toDoubleOrNull()
        val iterMax = etIteraciones.text.toString().toIntOrNull()
        val literal = findViewById<EditText>(R.id.etLiteral).text.toString()

        if (funcion.isEmpty() || aproxInicial == null || errorPermitido == null || iterMax == null) {
            Toast.makeText(this, "Por favor llena todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val py = Python.getInstance()
            val pyObj: PyObject = py.getModule("newton_raphson")

            val resultado: PyObject = pyObj.callAttr(
                "newton_raphson",
                funcion,
                literal,
                aproxInicial,
                errorPermitido,
                iterMax
            )

            // Convertir el resultado de Python a un mapa
            val resultMap = resultado.toMap() as Map<String, Any>
            val ok = (resultMap["ok"] as? Boolean) ?: false
            val message = (resultMap["message"] as? String) ?: ""

            if (!ok) {
                Toast.makeText(this, "Error: $message", Toast.LENGTH_SHORT).show()
                return
            }

            val dataList = resultMap["data"] as? List<Map<String, Any>>

            if (dataList == null || dataList.isEmpty()) {
                Toast.makeText(this, "No se obtuvieron resultados", Toast.LENGTH_SHORT).show()
                return
            }

            val iteraciones = mutableListOf<NewtonRaphsonRow>()
            for (item in dataList) {
                // SOLUCIÓN 1: Si NewtonRaphsonRow tiene 5 parámetros (sin xi1)
                iteraciones.add(
                    NewtonRaphsonRow(
                        (item["i"] as? Int) ?: 0,
                        (item["xi"] as? Double) ?: 0.0,
                        (item["fxi"] as? Double) ?: 0.0,
                        (item["dfxi"] as? Double) ?: 0.0,
                        (item["error"] as? Double) ?: 0.0
                    )
                )

            }

            rvNewtonRaphson.adapter = NewtonRaphsonAdapter(iteraciones)

        } catch (e: Exception) {
            Toast.makeText(this, "Error en el cálculo: ${e.message}", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }
}
*/
package com.example.multicalc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.chaquo.python.PyObject

class NewtonRaphsonActivity : AppCompatActivity() {

    private lateinit var etFuncion: EditText
    private lateinit var etLiteral: EditText
    private lateinit var etAproxInicial: EditText
    private lateinit var etErrorPermitido: EditText
    private lateinit var etIteraciones: EditText
    private lateinit var btnCalcular: Button
    private lateinit var btnBack: Button
    private lateinit var rvNewtonRaphson: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_newton_rahpson)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        etFuncion = findViewById(R.id.etFuncion)
        etLiteral = findViewById(R.id.etLiteral)
        etAproxInicial = findViewById(R.id.etAproxInicial)
        etErrorPermitido = findViewById(R.id.etErrorPermitido)
        etIteraciones = findViewById(R.id.etIteraciones)
        btnCalcular = findViewById(R.id.btnCalcular)
        btnBack = findViewById(R.id.btnBack)
        rvNewtonRaphson = findViewById(R.id.rvNewtonRaphson)

        rvNewtonRaphson.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener { finish() }
        btnCalcular.setOnClickListener { calcularNewtonRaphson() }
    }

    private fun calcularNewtonRaphson() {

        val funcion = etFuncion.text.toString()
        val literal = etLiteral.text.toString()
        val x0 = etAproxInicial.text.toString().toDoubleOrNull()
        val tol = etErrorPermitido.text.toString().toDoubleOrNull()
        val iterMax = etIteraciones.text.toString().toIntOrNull()

        if (funcion.isEmpty() || literal.isEmpty() || x0 == null || tol == null || iterMax == null) {
            Toast.makeText(this, "Llena todos los campos correctamente", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val py = Python.getInstance()
            val module = py.getModule("newton_raphson")

            val result = module.callAttr(
                "newton_raphson",
                funcion,
                literal,
                x0,
                tol,
                iterMax
            )

            val map = result.asMap()
            val ok = map[PyObject.fromJava("ok")]!!.toBoolean()

            if (!ok) {
                val msg = map[PyObject.fromJava("message")]!!.toString()
                Toast.makeText(this, msg, Toast.LENGTH_LONG).show()
                return
            }

            val data = map[PyObject.fromJava("data")]!!.asList()
            val lista = mutableListOf<NewtonRaphsonRow>()

            for (row in data) {
                val m = row.asMap()
                lista.add(
                    NewtonRaphsonRow(
                        m[PyObject.fromJava("i")]!!.toInt(),
                        m[PyObject.fromJava("xi")]!!.toDouble(),
                        m[PyObject.fromJava("fxi")]!!.toDouble(),
                        m[PyObject.fromJava("xi1")]!!.toDouble(),
                        m[PyObject.fromJava("error")]!!.toDouble()
                    )
                )
            }

            rvNewtonRaphson.adapter = NewtonRaphsonAdapter(lista)

        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }
}
