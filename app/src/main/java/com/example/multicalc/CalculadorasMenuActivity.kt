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
        val btnBiseccion = findViewById<Button>(R.id.btnBiseccion)
        val btnNewtonRaphson = findViewById<Button>(R.id.btnNewtonRaphson)
        val btnEcNoLi = findViewById<Button>(R.id.EcNoLi)

        // Click listeners
        btnDerivadas.setOnClickListener {
            val intent = Intent(this, DerivadasActivity::class.java)
            startActivity(intent)
        }

        btnIntegrales.setOnClickListener {
            val intent = Intent(this, IntegralesActivity::class.java)
            startActivity(intent)
        }

        btnBiseccion.setOnClickListener {
            val intent = Intent(this, BiseccionActivity::class.java)
            startActivity(intent)
        }

        btnNewtonRaphson.setOnClickListener {
            val intent = Intent(this, NewtonRaphsonActivity::class.java)
            startActivity(intent)
        }

        btnEcNoLi.setOnClickListener {
            val intent = Intent(this, EcNoLinealesActivity::class.java)
            startActivity(intent)
        }
    }
}