package com.pinterest.pinterestfirebase.ui.Perfil

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.io.File
import coil.load
import com.google.firebase.auth.FirebaseAuth
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.data.repository.UserRepository
import com.pinterest.pinterestfirebase.ui.publicacion.PubliNListActivity

class ProfileActivity: AppCompatActivity() {

    private lateinit var edtNombre: EditText
    private lateinit var edtLastname: EditText
    private lateinit var edtEmail: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnPublicaciones: Button
    private lateinit var btnProductos: Button

    private var imagenUrlActual: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val userRepository = UserRepository()

        edtNombre = findViewById(R.id.et_nombre)
        edtLastname = findViewById(R.id.et_apellido)
        edtEmail = findViewById(R.id.et_email)
        imgPreview = findViewById(R.id.profile_image)
        btnPublicaciones = findViewById(R.id.toolbar)
        btnProductos = findViewById(R.id.btn_productos)

        userRepository.obtenerUsuarioActual { usuario ->
            if (usuario != null) {
                edtNombre.setText(usuario.firstName)
                edtLastname.setText(usuario.lastName)
                edtEmail.setText(usuario.email)

                usuario.imagenUrl?.let { ruta ->
                    val file = File(ruta)
                    if (file.exists()) {
                        imgPreview.load(file)
                    } else {
                        Log.w("ProfileActivity", "No se encontró la imagen local: $ruta")
                    }
                }
            } else {
                Toast.makeText(this, "No se pudo cargar el perfil del usuario", Toast.LENGTH_SHORT).show()
            }
        }

        btnProductos.setOnClickListener {
            val intent = Intent(this, PubliNListActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPublicaciones.setOnClickListener {

        }
    }
}