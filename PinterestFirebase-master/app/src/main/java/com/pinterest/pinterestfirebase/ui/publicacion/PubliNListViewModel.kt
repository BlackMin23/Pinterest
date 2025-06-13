package com.pinterest.pinterestfirebase.ui.publicacion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.data.repository.PubliNRepository
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PubliNListViewModel (
    private val publiNRepository: PubliNRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // LiveData para observar la lista de mascotas
    private val _publiN = MutableLiveData<Result<List<PublicacionN>>>()
    val publin: LiveData<Result<List<PublicacionN>>> = _publiN

    // LiveData para observar el resultado de una operación de eliminación
    private val _deleteResult = MutableLiveData<Result<Boolean>>()
    val deleteResult: LiveData<Result<Boolean>> = _deleteResult

    // LiveData para observar si el usuario no está autenticado
    private val _isUserNotAuthenticated = MutableLiveData<Boolean>()
    val isUserNotAuthenticated: LiveData<Boolean> = _isUserNotAuthenticated

    init {
        // Al inicializar el ViewModel, cargamos las mascotas
        loadPubliN()
    }

    /**
     * Carga la lista de mascotas para el usuario actualmente autenticado.
     * Observa los cambios en tiempo real a través del Flow del repositorio.
     */
    fun loadPubliN() {
        val currentUserId = authRepository.getCurrentUserId()

        // --- INICIO DEPURACIÓN ---
        Log.d("PetListViewModel", "Cargando mascotas para ownerId: $currentUserId")
        // --- FIN DEPURACIÓN ---

        if (currentUserId == null) {
            // Si no hay usuario autenticado, notificar a la UI
            _isUserNotAuthenticated.value = true
            _publiN.value = Result.failure(Exception("User not authenticated"))
            Log.e("PetListViewModel", "Usuario no autenticado al cargar mascotas.")
            return
        }

        // Lanzamos una corrutina en el ámbito del ViewModel
        viewModelScope.launch {
            // collectLatest cancela la corrutina anterior si se emite un nuevo valor
            publiNRepository.getPubliNForOwner(currentUserId).collectLatest { result ->
                result.onSuccess { publin ->
                    Log.d("PetListViewModel", "Flow emitió SUCCESS: ${publin.size} Publicacion.")
                    // Asegúrate de que el ID del documento se mapee a la propiedad 'id' de Pet
                    // Esto ya se hace en publiNRepository, pero es bueno recordarlo.
                    _publiN.postValue(Result.success(publin)) // Publica la lista de mascotas en el LiveData
                }.onFailure { exception ->
                    Log.e(
                        "PetListViewModel",
                        "Flow emitió FAILURE: ${exception.message}",
                        exception
                    )
                    _publiN.postValue(Result.failure(exception)) // Publica el error en el LiveData
                }
            }
        }
    }


    /**
     * Elimina una mascota de la base de datos.
     * @param publiNId El ID de la mascota a eliminar.
     */
    fun deletePubliN(publiNId: String) {
        viewModelScope.launch {
            val result = publiNRepository.deletePubliN(publiNId)

            result.onSuccess {
                Log.d("PetListViewModel", "Publicacion eliminada exitosamente: $publiNId")
            }.onFailure { exception ->
                Log.e("PetListViewModel", "Error al eliminar mascota: ${exception.message}", exception)
            }

            _deleteResult.postValue(result) // Publica el resultado de la eliminación
            // Después de eliminar, recargamos la lista para reflejar el cambio
            loadPubliN()
        }
    }

    companion object
}