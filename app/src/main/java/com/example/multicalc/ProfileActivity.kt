package com.example.multicalc

import android.os.Bundle
import android.app.AlertDialog
import android.widget.Button
import android.content.Intent
import android.widget.LinearLayout
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.Kwarg

class ProfileActivity : AppCompatActivity() {
    // Variables de Chaquopy
    private lateinit var authManager: PyObject
    private lateinit var currentUsername: String

    // Variables de la interfaz
    private lateinit var tvUsername: TextView
    private lateinit var ivProfilePic: ImageView
    private lateinit var tvRacha: TextView
    private lateinit var tvAvanceSimbolico: TextView
    private lateinit var tvAvanceMetodos: TextView
    private lateinit var tvAvanceConversores: TextView

    // Nuevas variables de vista
    private lateinit var tvExamStreak: TextView
    private lateinit var tvExamLevels: TextView
    private lateinit var btnEditUser: Button
    private lateinit var btnChangePass: Button
    private lateinit var btnLogout: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        // 1. Obtener el nombre de usuario de la sesión
        // NOTA: Debes pasar el nombre del usuario desde MainActivity/LoginActivity
        currentUsername = intent.getStringExtra("EXTRA_USERNAME") ?: "Usuario Desconocido"

        // 2. Inicializar Vistas (Asegúrate de que los IDs coincidan con tu XML)
        tvUsername = findViewById(R.id.tv_profile_username)
        ivProfilePic = findViewById(R.id.iv_profile_pic)
        tvRacha = findViewById(R.id.tv_racha_dias)
        tvAvanceSimbolico = findViewById(R.id.tv_avance_simbolico)
        tvAvanceMetodos = findViewById(R.id.tv_avance_metodos)
        tvAvanceConversores = findViewById(R.id.tv_avance_conversores)
        // Inicializar nuevas vistas
        tvExamStreak = findViewById(R.id.tv_exam_streak)
        tvExamLevels = findViewById(R.id.tv_exam_levels)
        btnEditUser = findViewById(R.id.btn_edit_username)
        btnChangePass = findViewById(R.id.btn_change_password)
        btnLogout = findViewById(R.id.btn_logout)

        // Listeners
        btnEditUser.setOnClickListener { showEditUsernameDialog() }
        btnChangePass.setOnClickListener { showChangePasswordDialog() }
        btnLogout.setOnClickListener { logout() }
        findViewById<Button>(R.id.btn_back_profile).setOnClickListener {
            finish()
        }


        // 3. Inicializar Chaquopy y AuthManager (similar al LoginActivity)
        val py = Python.getInstance()
        val filesDir = applicationContext.filesDir.absolutePath
        val jsonPath = "$filesDir/usuarios.json"

        // El constructor de AuthManager solo necesita el path si no lo inicializaste en LoginActivity
        authManager = py.getModule("auth_manager").callAttr("AuthManager", jsonPath)

        // 4. Cargar los datos del perfil
        loadProfileData()
    }

    private fun loadProfileData() {
        tvUsername.text = currentUsername // Mostrar el nombre de inmediato

        // Llamada a Python para obtener el diccionario de datos
        val profileData = authManager.callAttr("obtener_datos_perfil", currentUsername)

        if (profileData != null) {
            // Mapeo directo de valores de Python a Kotlin
            val racha = profileData.callAttr("get", "racha_dias").toInt()
            val fotoUrl = profileData.callAttr("get", "foto_url").toString()

            // Obtener el diccionario de avances (cursos)
            val cursos = profileData.callAttr("get", "cursos_completados")
            val statsExamenes = profileData.callAttr("get", "stats_examenes")

            tvRacha.text = "Racha: $racha días"

            // Actualizar el progreso de los cursos
            if (cursos != null) {
                tvAvanceSimbolico.text = "Simbólico: ${cursos.callAttr("get", "Simbolico")} %"
                tvAvanceMetodos.text = "Métodos Numéricos: ${cursos.callAttr("get", "Metodos_Numericos")} %"
                tvAvanceConversores.text = "Conversores: ${cursos.callAttr("get", "Conversores")} %"
            }
            if (statsExamenes != null) {
                val rachaEx = statsExamenes.callAttr("get", "racha_examen").toString()
                val facil = statsExamenes.callAttr("get", "facil_completados").toString()
                val medio = statsExamenes.callAttr("get", "medio_completados").toString()
                val dificil = statsExamenes.callAttr("get", "dificil_completados").toString()

                tvExamStreak.text = "🔥 Racha Exámenes: $rachaEx"
                tvExamLevels.text = "Fácil: $facil | Medio: $medio | Difícil: $dificil"
            }

            // NOTA: Para la foto, usarías una librería de carga de imágenes (como Glide o Picasso)
            // if (fotoUrl.isNotEmpty()) {
            //     Glide.with(this).load(fotoUrl).into(ivProfilePic)
            // }

        } else {
            Toast.makeText(this, "Error al cargar el perfil.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProfilePicture(newUrl: String) {
        // Llamada a Python para actualizar la URL
        val isUpdated = authManager.callAttr(
            "actualizar_foto_perfil",
            currentUsername,
            newUrl
        ).toBoolean()

        if (isUpdated) {
            Toast.makeText(this, "Foto de perfil actualizada.", Toast.LENGTH_SHORT).show()
            // Recargar la pantalla para mostrar la nueva foto
            loadProfileData()
        } else {
            Toast.makeText(this, "Error al actualizar la foto.", Toast.LENGTH_SHORT).show()
        }
    }

    // --- FUNCIÓN CERRAR SESIÓN ---
    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        // Limpiar la pila de actividades para que no pueda volver atrás
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    // --- DIÁLOGO CAMBIAR NOMBRE ---
    private fun showEditUsernameDialog() {
        val input = EditText(this)
        input.hint = "Nuevo nombre de usuario"

        AlertDialog.Builder(this)
            .setTitle("Cambiar Nombre")
            .setView(input)
            .setPositiveButton("Guardar") { _, _ ->
                val newName = input.text.toString().trim()
                if (newName.isNotEmpty()) {
                    val success = authManager.callAttr("cambiar_nombre_usuario", currentUsername, newName).toBoolean()
                    if (success) {
                        Toast.makeText(this, "Nombre actualizado a $newName", Toast.LENGTH_SHORT).show()
                        currentUsername = newName // Actualizamos la variable local
                        loadProfileData() // Recargamos la vista
                    } else {
                        Toast.makeText(this, "Error: El nombre ya existe", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    // --- DIÁLOGO CAMBIAR CONTRASEÑA ---
    private fun showChangePasswordDialog() {
        val layout = LinearLayout(this)
        layout.orientation = LinearLayout.VERTICAL
        layout.setPadding(50, 20, 50, 20)

        val inputOld = EditText(this)
        inputOld.hint = "Contraseña Actual"
        layout.addView(inputOld)

        val inputNew = EditText(this)
        inputNew.hint = "Nueva Contraseña"
        layout.addView(inputNew)

        AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setView(layout)
            .setPositiveButton("Actualizar") { _, _ ->
                val oldPass = inputOld.text.toString().trim()
                val newPass = inputNew.text.toString().trim()

                if (oldPass.isNotEmpty() && newPass.isNotEmpty()) {
                    val success = authManager.callAttr("cambiar_password", currentUsername, oldPass, newPass).toBoolean()
                    if (success) {
                        Toast.makeText(this, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error: Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}
