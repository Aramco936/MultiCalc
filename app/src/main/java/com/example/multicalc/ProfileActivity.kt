package com.example.multicalc

import android.os.Bundle
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

            tvRacha.text = "Racha: $racha días"

            // Actualizar el progreso de los cursos
            if (cursos != null) {
                tvAvanceSimbolico.text = "Simbólico: ${cursos.callAttr("get", "Simbolico")} %"
                tvAvanceMetodos.text = "Métodos Numéricos: ${cursos.callAttr("get", "Metodos_Numericos")} %"
                tvAvanceConversores.text = "Conversores: ${cursos.callAttr("get", "Conversores")} %"
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


}
