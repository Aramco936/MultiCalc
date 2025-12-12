/*
package com.example.multicalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyObject
import com.chaquo.python.Python

class BiseccionActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnCalcular: Button
    private lateinit var etFuncion: EditText
    private lateinit var etLiteral: EditText
    private lateinit var etA: EditText
    private lateinit var etB: EditText
    private lateinit var etIter: EditText
    private lateinit var etError: EditText
    private lateinit var recyclerBiseccion: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biseccion)

        btnBack = findViewById(R.id.btnBack)
        btnCalcular = findViewById(R.id.btnCalcular)
        etFuncion = findViewById(R.id.etFuncion)
        etLiteral = findViewById(R.id.etLiteral)
        etA = findViewById(R.id.etA)
        etB = findViewById(R.id.etB)
        etIter = findViewById(R.id.etIter)
        etError = findViewById(R.id.etError)
        recyclerBiseccion = findViewById(R.id.recyclerBiseccion)

        recyclerBiseccion.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        btnCalcular.setOnClickListener {
            ejecutarBiseccion()
        }
    }

    private fun ejecutarBiseccion() {

        Thread {
            try {
                val py = Python.getInstance()
                val module = py.getModule("biseccion")

                val result = module.callAttr(
                    "biseccion",
                    etFuncion.text.toString(),
                    etLiteral.text.toString(),
                    etA.text.toString().toDouble(),
                    etB.text.toString().toDouble(),
                    etIter.text.toString().toInt(),
                    etError.text.toString().toDouble()
                )

                val lista = mutableListOf<BiseccionRow>()

                for (item in result.asList()) {
                    val map: Map<PyObject, PyObject> = item.asMap()

                    lista.add(
                        BiseccionRow(
                            i = map[PyObject.fromJava("i")]!!.toInt(),
                            Xa = map[PyObject.fromJava("a")]!!.toDouble(),
                            Xb = map[PyObject.fromJava("b")]!!.toDouble(),
                            fXa = map[PyObject.fromJava("fa")]!!.toDouble(),
                            fXb = map[PyObject.fromJava("fb")]!!.toDouble(),
                            m = map[PyObject.fromJava("m")]!!.toDouble(),
                            f_m = map[PyObject.fromJava("fm")]!!.toDouble(),
                            error = map[PyObject.fromJava("error")]!!.toDouble()
                        )
                    )
                }

                runOnUiThread {
                    recyclerBiseccion.adapter = BiseccionAdapter(lista)
                }

            } catch (e: Exception) {
                runOnUiThread {
                    Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
*/
package com.example.multicalc

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.chaquo.python.PyObject
import com.chaquo.python.Python

class BiseccionActivity : AppCompatActivity() {

    private lateinit var btnBack: Button
    private lateinit var btnCalcular: Button
    private lateinit var etFuncion: EditText
    private lateinit var etLiteral: EditText
    private lateinit var etA: EditText
    private lateinit var etB: EditText
    private lateinit var etIter: EditText
    private lateinit var etError: EditText
    private lateinit var recyclerBiseccion: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_biseccion)

        btnBack = findViewById(R.id.btnBack)
        btnCalcular = findViewById(R.id.btnCalcular)
        etFuncion = findViewById(R.id.etFuncion)
        etLiteral = findViewById(R.id.etLiteral)
        etA = findViewById(R.id.etA)
        etB = findViewById(R.id.etB)
        etIter = findViewById(R.id.etIter)
        etError = findViewById(R.id.etError)
        recyclerBiseccion = findViewById(R.id.recyclerBiseccion)

        recyclerBiseccion.layoutManager = LinearLayoutManager(this)

        btnBack.setOnClickListener {
            finish()
        }

        btnCalcular.setOnClickListener {
            ejecutarBiseccion()
        }
    }

    private fun ejecutarBiseccion() {

        recyclerBiseccion.adapter = null

        try {
            val py = Python.getInstance()
            val module = py.getModule("biseccion")

            val result = module.callAttr(
                "biseccion",
                etFuncion.text.toString(),
                etLiteral.text.toString(),
                etA.text.toString().toDouble(),
                etB.text.toString().toDouble(),
                etIter.text.toString().toInt(),
                etError.text.toString().toDouble()
            )

            val resultMap = result.asMap()

            val okKey = PyObject.fromJava("ok")
            val msgKey = PyObject.fromJava("message")
            val dataKey = PyObject.fromJava("data")

            val ok = resultMap[okKey]!!.toBoolean()
            val message = resultMap[msgKey]!!.toString()

            // ---------- CASO ERROR ----------
            if (!ok) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                return
            }

            // ---------- CASO OK ----------
            val data = resultMap[dataKey]!!.asList()
            val lista = mutableListOf<BiseccionRow>()

            for (item in data) {
                val row = item.asMap()

                lista.add(
                    BiseccionRow(
                        i = row[PyObject.fromJava("i")]!!.toInt(),
                        Xa = row[PyObject.fromJava("a")]!!.toDouble(),
                        Xb = row[PyObject.fromJava("b")]!!.toDouble(),
                        fXa = row[PyObject.fromJava("fa")]!!.toDouble(),
                        fXb = row[PyObject.fromJava("fb")]!!.toDouble(),
                        m = row[PyObject.fromJava("m")]!!.toDouble(),
                        f_m = row[PyObject.fromJava("fm")]!!.toDouble(),
                        error = row[PyObject.fromJava("error")]!!.toDouble()
                    )
                )
            }

            recyclerBiseccion.adapter = BiseccionAdapter(lista)

        } catch (e: Exception) {
            recyclerBiseccion.adapter = null
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}
