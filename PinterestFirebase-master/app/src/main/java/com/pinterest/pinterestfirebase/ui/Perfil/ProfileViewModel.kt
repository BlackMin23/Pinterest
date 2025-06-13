package com.pinterest.pinterestfirebase.ui.Perfil

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import kotlinx.coroutines.launch

// ProfileViewModel.kt
class ProfileViewModel() : ViewModel() {

    private val repository = AuthRepository()

    fun agregarMascota(mascota: Mascota, imagenUri: Uri?, onResult: (Boolean) -> Unit) {
        repository.agregarLibro(mascota, imagenUri, onResult)
    }
}