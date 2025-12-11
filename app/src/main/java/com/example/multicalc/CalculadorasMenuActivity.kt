package com.example.multicalc
import android.widget.Button
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class CalculadorasMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculadoras_menu)

        // Botón volver
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Referencias a los botones
        val btnDerivadas = findViewById<Button>(R.id.btnDerivadas)
        val btnIntegrales = findViewById<Button>(R.id.btnIntegrales)
        val btnOtro1 = findViewById<Button>(R.id.btnOtro1)
        val btnOtro2 = findViewById<Button>(R.id.btnOtro2)
        val btnOtro3 = findViewById<Button>(R.id.btnOtro3)

        // Click listeners
        btnDerivadas.setOnClickListener {
            val intent = Intent(this, DerivadasActivity::class.java)
            startActivity(intent)
        }

        btnIntegrales.setOnClickListener {
            val intent = Intent(this, IntegralesActivity::class.java)
            startActivity(intent)
        }

        btnOtro1.setOnClickListener {
            Toast.makeText(this, "Calculadora en desarrollo", Toast.LENGTH_SHORT).show()
        }

        btnOtro2.setOnClickListener {
            Toast.makeText(this, "Calculadora en desarrollo", Toast.LENGTH_SHORT).show()
        }

        btnOtro3.setOnClickListener {
            Toast.makeText(this, "Calculadora en desarrollo", Toast.LENGTH_SHORT).show()
        }
    }
}