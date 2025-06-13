package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.data.repository.PubliNRepository
import com.pinterest.pinterestfirebase.databinding.ActivityPubliNaddEditBinding

class PubliNAddEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPubliNaddEditBinding
    private lateinit var addEditPubliNViewModel: PubliNAddEditViewModel
    private var publinId: String? = null // Para almacenar el ID de la mascota si estamos editando


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPubliNaddEditBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Inicializa Firebase Auth y Firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Inicializa los repositorios
        val authRepository = AuthRepository(firebaseAuth, firestore)
        val publiNRepository = PubliNRepository (firestore)

        // Inicializa el ViewModel con una Factory
        addEditPubliNViewModel = ViewModelProvider(this, AddEditPubliNViewModelFactory(publiNRepository, authRepository))
            .get(PubliNAddEditViewModel::class.java)

        //Boton para cancelar y volver a la lista de mascotas
        binding.btnCancel.setOnClickListener {
            val intent = Intent(this, PubliNListActivity::class.java)
            startActivity(intent)
        }

        // Comprueba si estamos en modo edición (si se pasa un ID de mascota)
        publinId = intent.getStringExtra("PET_ID")
        if (publinId != null) {

            // Si hay un ID, estamos editando una mascota existente
            binding.tvTitle.text = "Editar Publicacion"
            // Rellena los campos con los datos existentes
            binding.nProducto.setText(intent.getStringExtra("PUBLICACION_NAME"))
            binding.aDescripcion.setText(intent.getStringExtra("PUBLICACION_DESCRIPCION"))

        } else {
            // Si no hay ID, estamos añadiendo una nueva mascota
            binding.tvTitle.text = "Añadir Nueva Publicacion"
        }

        // Observa el resultado de la operación de guardar (añadir/actualizar)
        addEditPubliNViewModel.saveResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Publicacion guardada exitosamente", Toast.LENGTH_SHORT).show()
                finish() // Cierra la actividad y vuelve a la lista de mascotas
            }.onFailure { exception ->
                Toast.makeText(this, "Error al guardar Publicacion: ${exception.message}", Toast.LENGTH_LONG).show()
            }
        }

        // Configura el listener para el botón de guardar mascota

        binding.btnPublicar.setOnClickListener {
            savePet()
        }

    }

    /**
     * Recopila los datos de los campos de entrada y llama al ViewModel para guardar la mascota.
     */
    private fun savePet() {

        val name = binding.nProducto.text.toString().trim()
        val descripcion = binding.aDescripcion.text.toString().trim()


        if (name.isEmpty() || descripcion.isEmpty()) {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        // Crea un objeto Pet. Si petId es nulo, se añadirá una nueva mascota.
        // Si petId no es nulo, se actualizará la mascota existente.
        val publiN = PublicacionN(
            id = publinId ?: "", // Si petId es nulo, se usa una cadena vacía (para nueva mascota)
            name = name,
            descripcion = descripcion,
            ownerId = "" // El ViewModel se encargará de asignar el ownerId
        )

        addEditPubliNViewModel.savePubliN(publiN)

    }
}


/**
 * Factory para crear instancias de AddEditPetViewModel.
 * Necesario para inyectar los repositorios en el ViewModel.
 */
class AddEditPubliNViewModelFactory(
    private val publiNRepository: PubliNRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PubliNAddEditViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PubliNAddEditViewModel(publiNRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}