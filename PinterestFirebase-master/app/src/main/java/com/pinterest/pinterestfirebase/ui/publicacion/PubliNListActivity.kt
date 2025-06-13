package com.pinterest.pinterestfirebase.ui.publicacion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.pinterest.pinterestfirebase.R
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.data.repository.PubliNRepository
import com.pinterest.pinterestfirebase.databinding.ActivityPubliNlistBinding
import com.pinterest.pinterestfirebase.ui.auth.LoginActivity

class PubliNListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPubliNlistBinding
    private lateinit var publiNListViewModel: PubliNListViewModel
    private lateinit var publicacionNAdapter: PublicacionNAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPubliNlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Inicializa Firebase Auth y Firestore
        val firebaseAuth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()

        // Inicializa los repositorios
        val authRepository = AuthRepository(firebaseAuth, firestore)
        val publiNRepository = PubliNRepository(firestore)

        // Inicializa el ViewModel con una Factory
        publiNListViewModel = ViewModelProvider(
            this,
            PubliNListViewModelFactory(publiNRepository, authRepository)
        ).get(PubliNListViewModel::class.java)

        // Configura el RecyclerView y el Adapter
        setupRecyclerView()

        // Observa los datos del ViewModel
        observeViewModel()

        // Configura los listeners de los botones
        setupButtonListeners()

        initUI()
    }

    private fun initUI() {

        val bottomNavigation = findViewById<BottomNavigationView>(R.id.bottom_navigation)

//        bottomNavigation.selectedItemId = R.id.nav_profile

        // funcion para ir a agregar desde el nav
        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {

                R.id.nav_add -> {
                    startActivity(Intent(this, ElegirTipoActivity ::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupRecyclerView() {
        // Inicializa el adapter con los callbacks para editar y eliminar
        publicacionNAdapter = PublicacionNAdapter(
            onEditClick = { mascota ->
                // Navega a la actividad de edición con los datos de la mascota
                editPet(mascota)
            },
            onDeleteClick = { petId ->
                // Muestra un diálogo de confirmación antes de eliminar
                showDeleteConfirmationDialog(petId)
            }
        )

        // Configura el RecyclerView
        binding.rvMascotas.apply {
            adapter = publicacionNAdapter

            //organiza los elementos en una lista vertical.
            layoutManager = LinearLayoutManager(this@PubliNListActivity)

            // agrega animaciones por defecto cuando se agregan,
            // eliminan o mueven ítems.
            itemAnimator = DefaultItemAnimator()

        }
    }

    private fun observeViewModel() {
        // Observa la lista de mascotas
        publiNListViewModel.publin.observe(this) { result ->
            result.onSuccess { publin ->
                // Actualiza el adapter con la nueva lista
                publicacionNAdapter.submitList(publin)

                // Muestra u oculta la vista vacía según sea necesario
                if (publin.isEmpty()) {
                    showEmptyState()
                } else {
                    hideEmptyState()
                }
            }.onFailure { exception ->
                Toast.makeText(
                    this,
                    "Mostrando Publicaciones",
                    Toast.LENGTH_LONG
                ).show()
                Log.e("MascotaListActivity", "Error loading Publicacion", exception)
            }
        }

        // Observa el resultado de eliminación
        publiNListViewModel.deleteResult.observe(this) { result ->
            result.onSuccess {
                Toast.makeText(this, "Publicacion eliminada exitosamente", Toast.LENGTH_SHORT).show()
            }.onFailure { exception ->
                Toast.makeText(
                    this,
                    "Error al eliminar la Publicacion: ${exception.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Observa si el usuario no está autenticado
        publiNListViewModel.isUserNotAuthenticated.observe(this) { isNotAuthenticated ->
            if (isNotAuthenticated) {
                Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
                navigateToLogin()
            }
        }
    }

    private fun setupButtonListeners() {
        // Configura el listener para cerrar sesión
        binding.btnCerrarSesion.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            navigateToLogin()
        }

        // Configura botón para ir a la vista de agregar
        //Proximo boton para ir a la vista del foro
//        binding.fabAddPet.setOnClickListener {
//            val intent = Intent(this, PubliNAddEditActivity::class.java)
//            startActivity(intent)
//        }
    }

    private fun editPet(publiN: PublicacionN) {
        val intent = Intent(this, PubliNAddEditActivity::class.java).apply {
            putExtra("PUBLICACION_ID", publiN.id)
            putExtra("PUBLICACION_NAME", publiN.name)
            putExtra("PUBLICACION_DESCRIPCION", publiN.descripcion)
        }
        startActivity(intent)
    }

    private fun showDeleteConfirmationDialog(publinNId: String) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar Publicacion")
            .setMessage("¿Estás seguro de que quieres eliminar esta Publicacion? Esta acción no se puede deshacer.")
            .setPositiveButton("Eliminar") { _, _ ->
                publiNListViewModel.deletePubliN(publinNId)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun showEmptyState() {
        // Si tienes una vista para estado vacío, muéstrala aquí
        binding.rvMascotas.visibility = View.GONE
        // binding.emptyStateView.visibility = View.VISIBLE (si tienes una)
    }

    private fun hideEmptyState() {
        binding.rvMascotas.visibility = View.VISIBLE
        // binding.emptyStateView.visibility = View.GONE (si tienes una)
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onResume() {
        super.onResume()
        // Recarga las mascotas cuando la actividad se reanuda
        // Esto asegura que se muestren las mascotas recién añadidas
        publiNListViewModel.loadPubliN()
    }
}

/**
 * Factory para crear instancias de MascotaListViewModel.
 */
class PubliNListViewModelFactory(
    private val publiNRepository: PubliNRepository,
    private val authRepository: AuthRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PubliNListViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return PubliNListViewModel(publiNRepository, authRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}