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
    private lateinit var btnIntegrales: Button
    private lateinit var btnDerivadas: Button
    private lateinit var btnMetodosNumericos: Button

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
        btnIntegrales = findViewById(R.id.btnIntegrales)
        btnDerivadas = findViewById(R.id.btnDerivadas)
        btnMetodosNumericos = findViewById(R.id.btnMetodosNumericos)

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
        btnIntegrales.setOnClickListener { cambiarCategoria("integrales", "Integrales") }
        btnDerivadas.setOnClickListener { cambiarCategoria("derivadas", "Derivadas") }
        btnMetodosNumericos.setOnClickListener { cambiarCategoria("metodos numericos", "Métodos Numéricos") }

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

    // Datos de fórmulas
    private fun obtenerFormulas(categoria: String): List<FormulaSimple> {
        return when (categoria) {
            "fisica" -> listOf(
                FormulaSimple("Velocidad", R.drawable.formula_fisica_velocidad, "La velocidad es la distancia recorrida entre el tiempo transcurrido"),
                FormulaSimple("Aceleración", R.drawable.formula_fisica_aceleracion, "La aceleración es el cambio de velocidad entre el tiempo"),
                FormulaSimple("Fuerza", R.drawable.formula_fisica_fuerza, "Segunda ley de Newton: Fuerza = masa × aceleración")
            )
            "algebra" -> listOf(
                FormulaSimple("Producto de potencias", R.drawable.formula_algebra_productopotencias, "Al multiplicar potencias de igual base, se suman los exponentes"),
                FormulaSimple("Cociente de potencias", R.drawable.formula_algebra_cocientepotencias, "Al dividir potencias de igual base, se restan los exponentes"),
                FormulaSimple("Diferencia de cuadrados", R.drawable.formula_algebra_difcuadrados, "Factorización: diferencia de cuadrados perfectos")
            )
            "geometria" -> listOf(
                FormulaSimple("Área del círculo", R.drawable.formula_geo_areacirculo, "El área del círculo es pi por el radio al cuadrado"),
                FormulaSimple("Teorema de Pitágoras", R.drawable.formula_geo_pitagoras, "En un triángulo rectángulo, la suma de los cuadrados de los catetos es igual al cuadrado de la hipotenusa")
            )
            "trigonometria" -> listOf(
                FormulaSimple("Seno", R.drawable.formula_trig_seno, "Razón trigonométrica: cateto opuesto entre hipotenusa"),
                FormulaSimple("Coseno", R.drawable.formula_trig_coseno, "Razón trigonométrica: cateto adyacente entre hipotenusa")
            )
            "integrales" -> listOf(
                FormulaSimple("Integral de una Constante", R.drawable.formula_integral_constante, "La integral de una constante es la constante multiplicada por la variable de integración."),
                FormulaSimple("Integral de Potencia", R.drawable.formula_integral_potencia, "Regla de la potencia para integrales: se aumenta el exponente en uno y se divide por el nuevo exponente."),
                FormulaSimple("Integral de la Suma", R.drawable.formula_integral_suma, "La integral de una suma es igual a la suma de las integrales. Propiedad lineal de la integral."),
                FormulaSimple("Integral por Partes", R.drawable.formula_integracion_partes, "Técnica para integrar productos de funciones. Se elige u y dv estratégicamente para simplificar la integral."),
                FormulaSimple("Integral Definida (Teorema Fundamental)", R.drawable.formula_teorema_fundamental, "El teorema fundamental del cálculo relaciona la integral definida con la antiderivada. Permite calcular áreas bajo curvas.")
            )
            "derivadas" -> listOf(
                FormulaSimple("Derivada de una Constante", R.drawable.formula_derivada_constante, "La derivada de cualquier constante es siempre cero, ya que las constantes no cambian."),
                FormulaSimple("Derivada de Potencia", R.drawable.formula_derivada_potencia, "Regla de la potencia: se multiplica por el exponente y se reduce el exponente en uno."),
                FormulaSimple("Derivada de la Suma", R.drawable.formula_derivada_suma, "La derivada de una suma es igual a la suma de las derivadas. Esta propiedad simplifica muchos cálculos."),
                FormulaSimple("Regla del Producto", R.drawable.formula_regla_producto, "Para derivar el producto de dos funciones, se deriva la primera y se multiplica por la segunda, más la primera por la derivada de la segunda."),
                FormulaSimple("Regla del Cociente", R.drawable.formula_regla_cociente, "Para derivar un cociente, se usa: derivada del numerador por denominador menos numerador por derivada del denominador, todo sobre el denominador al cuadrado."),
                FormulaSimple("Regla de la Cadena", R.drawable.formula_regla_cadena, "Para derivar funciones compuestas: se deriva la función exterior evaluada en la interior, multiplicada por la derivada de la función interior."
                )
            )
            "metodos numericos" -> listOf(
                FormulaSimple("Método de Newton-Raphson", R.drawable.formula_metodo_newtonraphson, "Método iterativo para encontrar raíces de funciones. Usa la derivada para aproximarse rápidamente a la solución."),
                FormulaSimple("Método de Bisección", R.drawable.formula_metodo_biseccion, "Método iterativo para encontrar raíces de ecuaciones. Reduce repetidamente el intervalo a la mitad para aproximar la solución.")
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