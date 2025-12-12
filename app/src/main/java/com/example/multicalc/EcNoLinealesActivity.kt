package com.example.multicalc

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class EcNoLinealesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ec_no_lineales)

        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }

        val etF1 = findViewById<EditText>(R.id.etFuncion1)
        val etF2 = findViewById<EditText>(R.id.etFuncion2)
        val etL1 = findViewById<EditText>(R.id.etLiteral1)
        val etL2 = findViewById<EditText>(R.id.etLiteral2)
        val etX0 = findViewById<EditText>(R.id.etX0)
        val etY0 = findViewById<EditText>(R.id.etY0)
        val etTol = findViewById<EditText>(R.id.etTol)
        val etIter = findViewById<EditText>(R.id.etIter)

        val btn = findViewById<Button>(R.id.btnCalcular)
        val tvResultado = findViewById<TextView>(R.id.tvResultado)

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }

        btn.setOnClickListener {

            // VALIDACIÓN BÁSICA
            if (
                etF1.text.isEmpty() || etF2.text.isEmpty() ||
                etL1.text.isEmpty() || etL2.text.isEmpty() ||
                etX0.text.isEmpty() || etY0.text.isEmpty() ||
                etTol.text.isEmpty() || etIter.text.isEmpty()
            ) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            try {
                val py = Python.getInstance()
                val mod = py.getModule("ec_no_lineales_2x2")

                val res = mod.callAttr(
                    "newton_raphson_2x2",
                    etF1.text.toString(),
                    etF2.text.toString(),
                    etL1.text.toString(),
                    etL2.text.toString(),
                    etX0.text.toString().toDouble(),
                    etY0.text.toString().toDouble(),
                    etTol.text.toString().toDouble(),
                    etIter.text.toString().toInt()
                ).asList()

                val x = res[0].toDouble()
                val y = res[1].toDouble()

                if (x == -999999.0) {
                    tvResultado.text = "Error: Jacobiano singular o no converge"
                } else {
                    tvResultado.text =
                        "Solución:\n${etL1.text} = $x\n${etL2.text} = $y"
                }

            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "Error: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
                e.printStackTrace()
            }
        }
    }
}
