package com.example.multicalc

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class FormularioActivity : AppCompatActivity() {

    private lateinit var btnFisica: Button
    private lateinit var btnAlgebra: Button
    private lateinit var btnGeometria: Button
    private lateinit var btnTrigonometria: Button

    private lateinit var txtCategoriaActual: TextView
    private lateinit var spinnerFormulas: Spinner
    private lateinit var imgFormula: ImageView
    private lateinit var txtDescripcion: TextView

    private var categoriaActual = "fisica"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_formulario)

        // Inicializar vistas
        btnFisica = findViewById(R.id.btnFisica)
        btnAlgebra = findViewById(R.id.btnAlgebra)
        btnGeometria = findViewById(R.id.btnGeometria)
        btnTrigonometria = findViewById(R.id.btnTrigonometria)

        txtCategoriaActual = findViewById(R.id.txtCategoriaActual)
        spinnerFormulas = findViewById(R.id.spinnerFormulas)
        imgFormula = findViewById(R.id.imgFormula)
        txtDescripcion = findViewById(R.id.txtDescripcion)

        // Botón atrás
        findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            finish()
        }

        // Configurar botones de categorías
        btnFisica.setOnClickListener { cambiarCategoria("fisica", "Física") }
        btnAlgebra.setOnClickListener { cambiarCategoria("algebra", "Álgebra") }
        btnGeometria.setOnClickListener { cambiarCategoria("geometria", "Geometría") }
        btnTrigonometria.setOnClickListener { cambiarCategoria("trigonometria", "Trigonometría") }

        // Listener del spinner
        spinnerFormulas.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                mostrarFormula(position)
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        // Cargar primera categoría
        cambiarCategoria("fisica", "Física")
    }

    private fun cambiarCategoria(categoria: String, nombre: String) {
        categoriaActual = categoria
        txtCategoriaActual.text = nombre

        // Obtener fórmulas de la categoría
        val formulas = obtenerFormulas(categoria)
        val nombresFormulas = formulas.map { it.nombre }

        // Actualizar spinner
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, nombresFormulas)
        spinnerFormulas.adapter = adapter
    }

    private fun mostrarFormula(position: Int) {
        val formulas = obtenerFormulas(categoriaActual)
        if (position < formulas.size) {
            val formula = formulas[position]
            imgFormula.setImageResource(formula.imagenResId)
            txtDescripcion.text = formula.descripcion
        }
    }

    // Datos de fórmulas (puedes moverlo a un archivo separado después)
    private fun obtenerFormulas(categoria: String): List<FormulaSimple> {
        return when (categoria) {
            "fisica" -> listOf(
                FormulaSimple("Velocidad", R.drawable.prueba, "La velocidad es la distancia recorrida entre el tiempo transcurrido"),
                FormulaSimple("Aceleración", R.drawable.prueba, "La aceleración es el cambio de velocidad entre el tiempo"),
                FormulaSimple("Fuerza", R.drawable.prueba, "Segunda ley de Newton: Fuerza = masa × aceleración")
            )
            "algebra" -> listOf(
                FormulaSimple("Producto de potencias", R.drawable.prueba, "Al multiplicar potencias de igual base, se suman los exponentes"),
                FormulaSimple("Cociente de potencias", R.drawable.prueba, "Al dividir potencias de igual base, se restan los exponentes"),
                FormulaSimple("Diferencia de cuadrados", R.drawable.prueba, "Factorización: diferencia de cuadrados perfectos")
            )
            "geometria" -> listOf(
                FormulaSimple("Área del círculo", R.drawable.prueba, "El área del círculo es pi por el radio al cuadrado"),
                FormulaSimple("Teorema de Pitágoras", R.drawable.prueba, "En un triángulo rectángulo, la suma de los cuadrados de los catetos es igual al cuadrado de la hipotenusa")
            )
            "trigonometria" -> listOf(
                FormulaSimple("Seno", R.drawable.prueba, "Razón trigonométrica: cateto opuesto entre hipotenusa"),
                FormulaSimple("Coseno", R.drawable.prueba, "Razón trigonométrica: cateto adyacente entre hipotenusa")
            )
            else -> emptyList()
        }
    }

    // Clase de datos simple
    data class FormulaSimple(
        val nombre: String,
        val imagenResId: Int,
        val descripcion: String
    )
}