package com.pinterest.pinterestfirebase.ui.Perfil

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pinterest.pinterestfirebase.data.model.Usuarios
import com.pinterest.pinterestfirebase.data.repository.AuthRepository
import com.pinterest.pinterestfirebase.data.repository.UserRepository
import kotlinx.coroutines.launch

// ProfileViewModel.kt
class ProfileViewModel() : ViewModel() {

    private val repository = UserRepository()

    fun agregarMascota(mascota: Usuarios, imagenUri: Uri?, onResult: (Boolean) -> Unit) {
//        repository.agregarLibro(mascota, imagenUri, onResult)
    }
}