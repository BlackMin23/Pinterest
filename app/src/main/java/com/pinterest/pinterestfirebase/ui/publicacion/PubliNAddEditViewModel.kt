package com.pinterest.pinterestfirebase.ui.publicacion

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinterest.pinterestfirebase.data.model.PublicacionN
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.data.repository.PubliNRepository
import kotlinx.coroutines.launch

class PubliNAddEditViewModel (
    private val publiRepository: PubliNRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    // LiveData para observar el resultado de la operación de guardar (añadir o actualizar)
    private val _saveResult = MutableLiveData<Result<Boolean>>()
    val saveResult: LiveData<Result<Boolean>> = _saveResult

    /**
     * Guarda una Publicacion. Si la Publicacion tiene un ID, se actualizará. Si no tiene ID, se añadirá.
     * @param publiN La mascota a guardar.
     */
    fun savePubliN(publiN: PublicacionN) {
        val currentUserId = authRepository.getCurrentUserId()
        if (currentUserId == null) {
            _saveResult.postValue(Result.failure(Exception("Usuario no autenticado. Cannot save pet.")))
            return
        }

        // Asignamos el ID del propietario antes de guardar la mascota
        val pToSave = publiN.copy(ownerId = currentUserId)

        viewModelScope.launch {
            val result = if (pToSave.id.isEmpty()) {
                // Si el ID está vacío, es una nueva mascota, la añadimos
                publiRepository.addPubliN(pToSave)
            } else {
                // Si el ID no está vacío, es una mascota existente, la actualizamos
                publiRepository.updatePubliN(pToSave)
            }
            _saveResult.postValue(result) // Publica el resultado en el LiveData
        }
    }
}