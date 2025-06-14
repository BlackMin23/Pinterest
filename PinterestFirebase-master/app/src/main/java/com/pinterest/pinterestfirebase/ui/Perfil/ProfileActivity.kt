package com.pinterest.pinterestfirebase.ui.Perfil

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
    private lateinit var btnGuardar: Button
    private lateinit var btnNewImage: Button

    private var imagenUrlActual: String? = null
    private var imagenOriginal: String? = null

    private var nombreOriginal = ""
    private var apellidoOriginal = ""
    private var emailOriginal = ""


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
        btnGuardar = findViewById(R.id.btn_editar)
        btnNewImage = findViewById(R.id.btn_new_profile)


        btnGuardar.visibility = Button.GONE

        userRepository.obtenerUsuarioActual { usuario ->
            if (usuario != null) {
                edtNombre.setText(usuario.firstName)
                edtLastname.setText(usuario.lastName)
                edtEmail.setText(usuario.email)

                // Guardamos valores originales
                nombreOriginal = usuario.firstName ?: ""
                apellidoOriginal = usuario.lastName ?: ""
                emailOriginal = usuario.email ?: ""
                imagenOriginal = usuario.imagenUrl
                imagenUrlActual = usuario.imagenUrl

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

            // IMPORTANTE: activar los listeners solo después de tener datos cargados
            setupChangeListeners()
        }

        btnProductos.setOnClickListener {
            val intent = Intent(this, PubliNListActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnPublicaciones.setOnClickListener {
            // Acciones para publicaciones
        }
    }


    private fun setupChangeListeners() {
        val checkCambios: () -> Unit = {
            val nombreCambiado = edtNombre.text.toString() != nombreOriginal
            val apellidoCambiado = edtLastname.text.toString() != apellidoOriginal
            val emailCambiado = edtEmail.text.toString() != emailOriginal
            val imagenCambiada = imagenUrlActual != imagenOriginal

            btnGuardar.visibility = if (nombreCambiado || apellidoCambiado || emailCambiado || imagenCambiada) {
                Button.VISIBLE
            } else {
                Button.GONE
            }
        }

        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                checkCambios()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        edtNombre.addTextChangedListener(watcher)
        edtLastname.addTextChangedListener(watcher)
        edtEmail.addTextChangedListener(watcher)

        // Si cambias la imagen manualmente, deberías llamar a checkCambios()
        checkCambios()
    }
}