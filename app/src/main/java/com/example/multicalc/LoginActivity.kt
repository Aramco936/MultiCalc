package com.example.multicalc

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
// Importaciones de Chaquopy
import com.chaquo.python.Python
import com.chaquo.python.PyObject
import com.chaquo.python.android.AndroidPlatform // Necesaria para iniciar Python

class LoginActivity : AppCompatActivity() {

    // Variables de Chaquopy
    private lateinit var py: Python
    private lateinit var authManager: PyObject // Objeto que representa la clase AuthManager de Python

    private lateinit var etUsuario: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button // Asumiendo que también quieres registrar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 1. Inicialización de Vistas
        etUsuario = findViewById(R.id.etUsuario) // Asegúrate que uses el ID correcto
        etPassword = findViewById(R.id.etPassword) // Asegúrate que uses el ID correcto
        btnLogin = findViewById(R.id.btnLogin) // Asegúrate que uses el ID correcto
        btnRegister = findViewById(R.id.btnRegister) // Añadir el botón de registro si existe

        // 2. Inicialización de Chaquopy (El "puente" a Python)
        if (!Python.isStarted()) {
            // Se debe inicializar con la plataforma Android (necesita el contexto)
            Python.start(AndroidPlatform(this))
        }
        py = Python.getInstance()

        // 3. Obtener la ruta de almacenamiento interno (writable location)
        val filesDir = applicationContext.filesDir.absolutePath
        val jsonPath = "$filesDir/usuarios.json"

        // 4. Cargar la clase Python y crear la instancia, PASANDO LA RUTA
        // El módulo es 'auth_manager' (nombre del archivo sin .py)
        // La clase es 'AuthManager'
        authManager = py.getModule("auth_manager").callAttr("AuthManager", jsonPath)

        // 5. Configurar el click de los botones
        btnLogin.setOnClickListener {
            handleLogin()
        }

        btnRegister.setOnClickListener {
            handleRegister()
        }
    }

    // Función de LOGIN que llama a Python
    private fun handleLogin() {
        val usuario = etUsuario.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Por favor, completa ambos campos.", Toast.LENGTH_SHORT).show()
            return
        }

        // LLAMADA A PYTHON: Ejecutar el método iniciar_sesion(usuario, password)
        // .toBoolean() convierte el resultado booleano de Python (True/False) a Kotlin.
        val isSuccess = authManager.callAttr("iniciar_sesion", usuario, password).toBoolean()

        if (isSuccess) {
            // Login exitoso
            Toast.makeText(this, "¡Bienvenido $usuario!", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Login fallido (Usuario no existe o contraseña incorrecta)
            Toast.makeText(this, "Usuario o contraseña incorrectos", Toast.LENGTH_LONG).show()
        }
    }

    // Función de REGISTRO que llama a Python
    private fun handleRegister() {
        val usuario = etUsuario.text.toString().trim()
        val password = etPassword.text.toString().trim()

        if (usuario.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this,
                "Por favor, completa ambos campos para registrarte.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        // LLAMADA A PYTHON: Ejecutar el método registrar_usuario(usuario, password)
        val isSuccess = authManager.callAttr("registrar_usuario", usuario, password).toBoolean()

        if (isSuccess) {
            Toast.makeText(this, "Usuario '$usuario' registrado exitosamente.", Toast.LENGTH_LONG)
                .show()
            // Opcional: limpiar campos tras el registro
            etUsuario.setText("")
            etPassword.setText("")
        } else {
            Toast.makeText(
                this,
                "Error: El usuario ya existe o la entrada es inválida.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
