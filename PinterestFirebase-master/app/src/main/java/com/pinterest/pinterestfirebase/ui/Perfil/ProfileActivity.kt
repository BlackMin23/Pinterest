package com.pinterest.pinterestfirebase.ui.Perfil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import java.io.File
import java.io.FileOutputStream

class ProfileActivity: AppCompatActivity() {

    private lateinit var viewModel: ProfileView

    private lateinit var edtName: EditText
    private lateinit var edtType: EditText
    private lateinit var edtAge: EditText
    private lateinit var imgPreview: ImageView
    private lateinit var btnSeleccionarImagen: Button
    private lateinit var btnGuardar: Button
    private lateinit var btnCancelar: Button

    private var imagenUri: Uri? = null
    private var imagenUrlActual: String? = null
    private var mascotaId: String? = null

    private val seleccionarImagenLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            imagenUri = uri
            imgPreview.setImageURI(uri)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mascota_add_edit)

        viewModel = ViewModelProvider(this)[MascotaAddEditViewModel::class.java]

        edtName = findViewById(R.id.etNombre)
        edtType = findViewById(R.id.etTipo)
        edtAge = findViewById(R.id.etEdad)
        imgPreview = findViewById(R.id.imagePreview)
        btnSeleccionarImagen = findViewById(R.id.btnSelectImage)
        btnGuardar = findViewById(R.id.btnRegistrar)
        btnCancelar = findViewById(R.id.btnCancelar)

        // Cargar datos si es edición
        mascotaId = intent.getStringExtra("id")
        val name = intent.getStringExtra("name")
        val type = intent.getStringExtra("type")
        val age = intent.getStringExtra("age")
        imagenUrlActual = intent.getStringExtra("imagenUrl")

        if (mascotaId != null) {
            edtName.setText(name)
            edtType.setText(type)
            edtAge.setText(age.toString())
            imagenUrlActual?.let {
                imgPreview.load(File(it))
            }
        }

        btnSeleccionarImagen.setOnClickListener {
            seleccionarImagenLauncher.launch("image/*")
        }

        btnCancelar.setOnClickListener {
            val intent = Intent(this, MascotaListActivity::class.java)
            startActivity(intent)
            finish() // (opcional) cierra la actividad actual para que no quede en el historial
        }


        btnGuardar.setOnClickListener {
            val nameMascota = edtName.text.toString()
            val typeMascota = edtType.text.toString()
            val ageMascota = edtAge.text.toString()

            if (nameMascota.isNotBlank() && typeMascota.isNotBlank() && ageMascota.isNotBlank()) {
                // Guardar imagen seleccionada localmente
                val rutaImagenLocal = imagenUri?.let { guardarImagenLocal(it) }

                val mascota = Mascota(
                    id = mascotaId ?: "",
                    name = nameMascota,
                    type = typeMascota,
                    age = ageMascota,
                    imagenUrl = rutaImagenLocal ?: imagenUrlActual ?: ""
                )

                viewModel.agregarMascota(mascota, null) { exito ->
                    if (exito) {
                        Toast.makeText(this, if (mascotaId == null) "Mascota guardado" else "Mascota actualizado", Toast.LENGTH_SHORT).show()
                        setResult(Activity.RESULT_OK)
                        finish()
                    } else {
                        Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Función para copiar imagen al almacenamiento interno
    private fun guardarImagenLocal(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val fileName = "mascota_${System.currentTimeMillis()}.jpg"
            val file = File(filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.copyTo(outputStream)
            inputStream?.close()
            outputStream.close()

            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}